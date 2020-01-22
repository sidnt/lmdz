package lmdz.apis

import java.io.File

import zio._
import java.nio.ByteBuffer

import lmdz.cfgs.LmdzBatteryPack
import lmdz.cfgs.LmdzBatteryPackAccessors.{DefaultDbiFlag, DefaultDbiName, DefaultEnvFlag, DefaultEnvMiBs, DefaultEnvPath}
import org.lmdbjava.{Dbi, Env, Txn}

object v1 {

  /** lmdbjava doesn't create the environment directory if it's not there
   * this effect will fail if the default directory path isn't already there #unsafe
   * this api returns a ready to use lmdb-env & lmdb-dbi ootb
   * .
   * #tip calling this effect again & again to get (env,dbi) at multiple places
   * isn't a good idea because `Env.open` & `env.openDbi` will be run again
   * which is redundant computation within the scope of a running lmdz
   * (env,dbi) references must be shared/reused if these defaults are to be reused
   * via concurrent accesses handled properly */
  val openDefaultEnvAndDbi: RIO[LmdzBatteryPack, (Env[ByteBuffer], Dbi[ByteBuffer])] = for {
    defaultEnvPath  <-  DefaultEnvPath
    defaultEnvSize  <-  DefaultEnvMiBs
    defaultEnvFlag  <-  DefaultEnvFlag
    defaultDbiName  <-  DefaultDbiName
    defaultDbiFlag  <-  DefaultDbiFlag
    env             <-  IO.effect(Env.open(new File(defaultEnvPath), defaultEnvSize, defaultEnvFlag))
    dbi             <-  IO.effect(env.openDbi(defaultDbiName, defaultDbiFlag))
  } yield (env,dbi)


  /** crud apis don't yet abstract over lmdbjava's buffer type
   * these are currently #hardcoded to ByteBuffer
   * #todo we'll need appropriate type bounds to introduce that type parameter */

  /** Dbi.put() internally begins and commits a transaction (Txn). */
  def put(vlu:ByteBuffer, inDbi:Dbi[ByteBuffer], atKey: ByteBuffer): Task[Unit] =
    IO.effect(inDbi.put(atKey,vlu))

  def get(atKey:ByteBuffer, fromDbi:Dbi[ByteBuffer], within:Txn[ByteBuffer]): Task[ByteBuffer] =
    IO.effect(fromDbi.get(within, atKey))

  def delete(fromDbi:Dbi[ByteBuffer], atKey:ByteBuffer): Task[Boolean] =
    IO.effect(fromDbi.delete(atKey))

}
