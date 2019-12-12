import java.nio.ByteBuffer

import lmdz.apis._
import lmdz.cfgs.LmdzBatteryPack
import org.lmdbjava.{Dbi, Env}
import zio._

package object lmdzclient0 {

  val app0: RIO[LmdzBatteryPack, (Env[ByteBuffer], Dbi[ByteBuffer])] = for {
    (env,dbi)     <-      createOrOpenDefaultEnvAndDbi
  } yield (env, dbi)

}
