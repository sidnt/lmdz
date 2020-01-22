package lmdzclient

import zio._
import console._
import lmdz.cfgs.LmdzBatteryPack.DefaultBatteryPack

object Main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    App1.eg1
      .fold( (f: Any) => {println(f.toString);1}, (s:Any) => {println(s.toString);0})

}
