package lmdz

import java.io.File
import java.nio.ByteBuffer
import zio._
import org.lmdbjava._

/** lmdz imports */
import cfgs._
import LmdbBatteryPackAccessors._

package object apis {

  /** lmdbjava creates the environment directory if it's not there
   * this api returns a ready to use lmdb-env & lmdb-dbi ootb */
  val createOrOpenDefaultEnvAndDbi: RIO[LmdbBatteryPack, (Env[ByteBuffer], Dbi[ByteBuffer])] = for {
    defaultEnvPath  <-  DefaultEnvPath
    defaultEnvSize  <-  DefaultEnvMiBs
    defaultEnvFlag  <-  DefaultEnvFlag
    defaultDbiName  <-  DefaultDbiName
    defaultDbiFlag  <-  DefaultDbiFlag
    env             <-  IO.effect(Env.open(new File(defaultEnvPath), defaultEnvSize, defaultEnvFlag))
    dbi             <-  IO.effect(env.openDbi(defaultDbiName,defaultDbiFlag))
  } yield (env,dbi)

}
