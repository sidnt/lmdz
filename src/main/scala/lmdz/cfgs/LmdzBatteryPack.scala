package lmdz.cfgs

import zio._
import org.lmdbjava._
import org.lmdbjava.EnvFlags.MDB_WRITEMAP
import org.lmdbjava.DbiFlags.MDB_CREATE

/** an LmdzBatteryPack is required to get up and running with a minimally configured instance of lmdb */
trait LmdzBatteryPack {
  def batteryPack: LmdzBatteryPack.Service[Any]
}

object LmdzBatteryPack {
  trait Service[R] {
    def DefaultEnvPath: String
    def DefaultEnvMiBs: Int
    def DefaultEnvFlag: EnvFlags
    def DefaultDbiName: String
    def DefaultDbiFlag: DbiFlags
  }

  trait DefaultBatteryPack extends LmdzBatteryPack {
    def batteryPack: Service[Any] = new Service[Any] {
      def DefaultEnvPath = "lmdb-default-env"
      def DefaultEnvMiBs = 10 // size in MiB
      def DefaultEnvFlag = MDB_WRITEMAP
      def DefaultDbiName = null //
      def DefaultDbiFlag = MDB_CREATE
    }
  }
  object DefaultBatteryPack extends DefaultBatteryPack

}

/** accessors */
object LmdzBatteryPackAccessors {
  val DefaultEnvPath: URIO[LmdzBatteryPack, String]      = ZIO.access(_.batteryPack.DefaultEnvPath)
  val DefaultEnvMiBs: URIO[LmdzBatteryPack, Int]         = ZIO.access(_.batteryPack.DefaultEnvMiBs)
  val DefaultEnvFlag: URIO[LmdzBatteryPack, EnvFlags]    = ZIO.access(_.batteryPack.DefaultEnvFlag)
  val DefaultDbiName: URIO[LmdzBatteryPack, String]      = ZIO.access(_.batteryPack.DefaultDbiName)
  val DefaultDbiFlag: URIO[LmdzBatteryPack, DbiFlags]    = ZIO.access(_.batteryPack.DefaultDbiFlag)
}
