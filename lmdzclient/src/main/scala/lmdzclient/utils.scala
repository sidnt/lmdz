package lmdzclient

import java.io.IOException

import zio.{URIO, ZIO}
import zio.console.{Console, getStrLn, putStr, putStrLn}

object utils {

  /** console conveniences */
  val gsl: ZIO[Console, IOException, String] = getStrLn
  val psl: String => URIO[Console, Unit] = (s:String)  =>  putStrLn(s)
  val ps: String => URIO[Console, Unit] = (s:String)  =>  putStr(s)

}
