package lmdz.apis

import zio._
import java.nio.ByteBuffer

import org.lmdbjava.{Dbi, Txn}

object crud {

  /** crud apis don't yet abstract over lmdbjava's buffer type
   * these are currently #hardcoded to ByteBuffer
   * #todo we'll need appropriate type bounds to introduce that type parameter */

  /** Dbi.put() internally begins and commits a transaction (Txn). */
  def put(vlu:ByteBuffer, inDbi:Dbi[ByteBuffer], atKey: ByteBuffer): Task[Unit] =
    IO.effect(inDbi.put(atKey,vlu))

  def get(atKey:ByteBuffer, fromDbi:Dbi[ByteBuffer], within:Txn[ByteBuffer]): Task[ByteBuffer] =
    IO.effect(fromDbi.get(within, atKey))

  def del(fromDbi:Dbi[ByteBuffer], atKey:ByteBuffer): Task[Boolean] =
    IO.effect(fromDbi.delete(atKey))

}
