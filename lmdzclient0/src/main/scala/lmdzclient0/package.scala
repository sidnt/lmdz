import java.nio.ByteBuffer

import lmdz.apis._
import lmdz.utils._
import lmdz.cfgs.LmdzBatteryPack
import zio._
import zio.console.Console

package object lmdzclient0 {

  val app0: ZIO[Console with LmdzBatteryPack, Throwable, Unit] = for {
    (env,dbi)     <-      openDefaultEnvAndDbi
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

}
