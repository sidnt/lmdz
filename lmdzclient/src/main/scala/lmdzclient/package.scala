import java.nio.ByteBuffer

import lmdz.apis._
import lmdz.utils._
import lmdz.cfgs.LmdzBatteryPack
import org.lmdbjava._
import zio._
import zio.console.Console

package object lmdzclient {

  import utils._
  val app0: ZIO[Console with LmdzBatteryPack, Throwable, Unit] = for {
    (env,dbi)     <-      openDefaultEnvAndDbi
    _             <-      use(env,dbi) // whatever happens in use, but do release the handles.
    _             <-      release(env,dbi) // #untested
  } yield ()

  def use(env:Env[ByteBuffer], dbi:Dbi[ByteBuffer]) = for {
    key           <-      psl("key? ") *> gsl
    vlu           <-      psl("value? ") *> gsl
    kbb           <-      IO.effect(stringToUtf8DByteBuffer(key))
    vbb           <-      IO.effect(stringToUtf8DByteBuffer(vlu))
    _             <-      crud.put(vbb,dbi,kbb)
    key2          <-      psl("key? ") *> gsl
    kbb2          <-      IO.effect(stringToUtf8DByteBuffer(key2))
    vbb2          <-      crud.get(kbb2,dbi,env.txnRead())
    vlu2          <-      IO.effect(dByteBufferToUtf8String(vbb2))
    _             <-      psl(vlu2)
  } yield ()

  /** release is not effectTotal and we don't want a release to fail and leak handles */
  def release(env:Env[ByteBuffer], dbi:Dbi[ByteBuffer]) = IO.effect {
    dbi.close()
    env.close()
  }

}
