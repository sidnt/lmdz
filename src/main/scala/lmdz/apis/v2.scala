package lmdz.apis

import java.io.File
import java.nio.ByteBuffer

import lmdz.utils.{dByteBufferToUtf8String, stringToUtf8DByteBuffer}
import org.lmdbjava.DbiFlags.MDB_CREATE
import org.lmdbjava.{Dbi, Env, Txn}
import zio._
import zio.blocking.Blocking
import zio.nio.file._

object v2 {

  /** DefaultDB is the default ootb's api.
   * You can DefaultDB.get/put/del but
   * some of the lmdb level operation semantics are a bit augmented underneath, behind that api.
   * in particular, these are the acquire release operations.
   * rwTxn.commit roTxn.commit will be called automatically bracketing each put-del/get operation
   * with a freshly-created-and-then-released-after-use-transaction foreach put-del/get operation.
   * env.close will always be called when the managed use ends.
   * .
   * could DefaultDB be actually the service structure?
   * so that it could be handled via the module pattern? */
  private [lmdz] class DefaultDB (
                              get0: ByteBuffer => ZIO[Any, Throwable, ByteBuffer],
                              put0: (ByteBuffer, ByteBuffer) => ZIO[Any, Throwable, Boolean],
                              del0: ByteBuffer => ZIO[Any, Throwable, Boolean]
                              ) {
    /** interesting. those who follow an invocation of get in an IDE will get here
     * and will not get the implementation of the method.
     * where's the implementation? */
    def get(kbb:ByteBuffer) = get0(kbb)
    def put(kbb:ByteBuffer, vbb:ByteBuffer) = put0(kbb,vbb)
    def del(kbb:ByteBuffer) = del0(kbb)
  }

  val createOrOpenDefaultEnv: ZIO[Blocking, Throwable, Env[ByteBuffer]] = for {
    _         <-  IO.unit
    dbDirName =   "DefaultDB"
    dbMiBs    =   10
    dbDirPath =   FileSystem.default.getPath(dbDirName)
    dbDirFile =   dbDirPath.toFile
    dirExists <-  Files.exists(dbDirPath)
    env       <-   if(dirExists) Task (Env.open(dbDirFile, dbMiBs*1024*1024)) else for {
                      _         <-  Files.createDirectory(dbDirPath)
                      env       =   Env.open(dbDirFile, dbMiBs*1024*1024)
                    } yield env
  } yield env


  /** we don't want to managed.use two mDefaultDBs
   * because that will open again an already open environment, which is bad in lmdb. */
  val createOrOpen_mDefaultDB: ZIO[Blocking, Throwable, Managed[Nothing, DefaultDB]] =
    for {
      env       <- createOrOpenDefaultEnv //likewise createOrOpenDefaultDbi below #improve
      dbi       =  env.openDbi("DefaultDBI", MDB_CREATE) // #unsafe because this dbi might be closed by txns that abort. using dbi as is after a txn has aborted might throw #hardcode
      mRoTxn    =  Managed.make(UIO(env.txnRead()))(roTxn => UIO{println("roTxn.commit()");roTxn.commit()})
      mRwTxn    =  Managed.make(UIO(env.txnWrite()))(rwTxn => UIO{println("rwTxn.commit()");rwTxn.commit()})
      get       =  (kbb:ByteBuffer) => mRoTxn.use(roTxn => Task(dbi.get(roTxn,kbb)))
      put       =  (kbb:ByteBuffer, vbb:ByteBuffer) => mRwTxn.use( rwTxn => Task(dbi.put(rwTxn,kbb, vbb)))
      del       =  (kbb:ByteBuffer) => mRwTxn.use(rwTxn => Task(dbi.delete(rwTxn,kbb)))
      mDefaultDB =  Managed.make(UIO(new DefaultDB(get, put, del)))(_ => UIO {println("closing env");env.close()})
  } yield mDefaultDB
  /** probably we need a concurrent data reference
   * like Ref, where the global thing is kept, eg a managed default db instance
   * we don't want to instantiate that effect a multiple times and have it create that many multiple managed default db instances, it will open the same environment twice in the same process
   * but instead, we want that, even if we run that effect multiple times, it only operates from that global reference,
   * so we protect ourselves from the clients running such an effect concurrently.
   * even if they run, we can return a Ref to that global instance. */

}
