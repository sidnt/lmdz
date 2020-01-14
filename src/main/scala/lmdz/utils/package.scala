package lmdz

import java.io.IOException

import zio._
import zio.console._
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8

package object utils {

  /** we need the ability of interconversion between
   * runtime objects <-> database objects */

  /** #doubt where will be dbb allocated? in the method's stack?
   * will it need GC later? Or will it be wiped after this method returns? */
  def byteArrayToDByteBuffer(ba: Array[Byte]): ByteBuffer = {
    val dbb: ByteBuffer = ByteBuffer.allocateDirect(ba.length).put(ba)
    dbb.flip()
    dbb
    /** #doubt is ↑ concurrent safe?
     * seems so, because it doesn't access any value outside itself
     * even if it mutates something `dbb.flip()` the values affected are local to the method
     * each executing instance will have its own copy
     * and won't be working with any shared state. so looks like fiber safe? */
  }

  /** these apis are #hardcoded to the UTF_8 Charset */
  def stringToUtf8DByteBuffer(s:String): ByteBuffer = byteArrayToDByteBuffer(s.getBytes(UTF_8))
  def dByteBufferToUtf8String(bb:ByteBuffer): String = UTF_8.decode(bb).toString

}
