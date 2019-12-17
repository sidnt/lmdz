package lmdz

import java.io.File
import java.nio.ByteBuffer
import zio._
import org.lmdbjava._

/** lmdz imports */
import cfgs._
import LmdzBatteryPackAccessors._

package object apis {

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

}
