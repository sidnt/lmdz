package lmdz.cfgs

import zio._
import org.lmdbjava._
import org.lmdbjava.EnvFlags.MDB_WRITEMAP
import org.lmdbjava.DbiFlags.MDB_CREATE

/** an LmdbBatteryPack is required to get up and running with a minimally configured instance of lmdb */
trait LmdbBatteryPack {
  def batteryPack: LmdbBatteryPack.Service[Any]
}

object LmdbBatteryPack {
  trait Service[R] {
    def DefaultEnvPath: String
    def DefaultEnvMiBs: Int
    def DefaultEnvFlag: EnvFlags
    def DefaultDbiName: String
    def DefaultDbiFlag: DbiFlags
  }

  trait DefaultBatteryPack extends LmdbBatteryPack {
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
object LmdbBatteryPackAccessors {
  val DefaultEnvPath: URIO[LmdbBatteryPack, String]      = ZIO.access(_.batteryPack.DefaultEnvPath)
  val DefaultEnvMiBs: URIO[LmdbBatteryPack, Int]         = ZIO.access(_.batteryPack.DefaultEnvMiBs)
  val DefaultEnvFlag: URIO[LmdbBatteryPack, EnvFlags]    = ZIO.access(_.batteryPack.DefaultEnvFlag)
  val DefaultDbiName: URIO[LmdbBatteryPack, String]      = ZIO.access(_.batteryPack.DefaultDbiName)
  val DefaultDbiFlag: URIO[LmdbBatteryPack, DbiFlags]    = ZIO.access(_.batteryPack.DefaultDbiFlag)
}
