package lmdzclient

import zio._
import console._
import lmdz.cfgs.LmdzBatteryPack.DefaultBatteryPack

object main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    app1.
      provide(new Console.Live with DefaultBatteryPack).
      fold( (f: Any) => {println(f.toString);1}, (s:Any) => {println(s.toString);0})

}
