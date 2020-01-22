package lmdzclient

import java.nio.ByteBuffer

import lmdz.apis._
import lmdzclient.utils._
import lmdz.utils._
import lmdz.cfgs.LmdzBatteryPack
import lmdz.cfgs.LmdzBatteryPack.DefaultBatteryPack
import org.lmdbjava._
import zio._
import zio.console.Console

object App1 {

  /** bad api design though
   * the apis are not semantic
   * `use` doesn't convey that it's actually a write thingy app
   * `use` does convey meaning, in that, it's just the continuation */
  val app0: ZIO[Console with LmdzBatteryPack, Throwable, Unit] = for {
    (env,dbi)     <-      v1.openDefaultEnvAndDbi
    _             <-      use(env,dbi) // whatever happens in use, but do release the handles.
    _             <-      release(env,dbi) // #untested
  } yield ()

  /** bad api design though
   * lots of lower level things, present in this layer
   * why do i have convert all those buffer things
   * it should be hidden underneath api calls
   * and the api calls do the wiring underneath */
  def use(env:Env[ByteBuffer], dbi:Dbi[ByteBuffer]) = for {
    key           <-      psl("key? ") *> gsl
    vlu           <-      psl("value? ") *> gsl
    kbb           <-      IO.effect(stringToUtf8DByteBuffer(key))
    vbb           <-      IO.effect(stringToUtf8DByteBuffer(vlu))
    _             <-      v1.put(vbb,dbi,kbb)
    key2          <-      psl("key? ") *> gsl
    kbb2          <-      IO.effect(stringToUtf8DByteBuffer(key2))
    vbb2          <-      v1.get(kbb2,dbi,env.txnRead())
    vlu2          <-      IO.effect(dByteBufferToUtf8String(vbb2))
    _             <-      psl(vlu2)
  } yield ()

  /** release is not effectTotal and we don't want a release to fail and leak handles */
  def release(env:Env[ByteBuffer], dbi:Dbi[ByteBuffer]) = IO.effect {
    dbi.close()
    env.close()
  }

  def eg1 = app0.provide(new Console.Live with DefaultBatteryPack)

}
