package org.seekloud.byteobject.exampleInJVM

import org.seekloud.byteobject.MiddleBufferInJvm

import scala.collection.immutable.Queue

/**
  * User: Taoz
  * Date: 8/2/2018
  * Time: 11:27 AM
  */
object Test {


  sealed trait Msg
  final case class Join(name: String, num: Int, friends: List[String]) extends Msg
  final case class Left(name: String) extends Msg
  final case class QueueTest(name: String, num: Int, resule: Queue[Double]) extends Msg



  def main(args: Array[String]): Unit = {

    val msg: Msg = QueueTest("ceshi", 123, Queue(1.0, 2.0, 3.0, 4.0))
    val bytes = encodeTest(msg)
    println("\n+++++++++++++++++++++\n")
    val decodedMsg = decodeTest(bytes)
    println("DONE.")

  }


  def encodeTest(obj: Msg): Array[Byte] = {
    import org.seekloud.byteobject.ByteObject._
    val buffer = MiddleBufferInJvm(2048)
    val rst = obj.fillMiddleBuffer(buffer).result()
    println(s"object: $obj")
    println(s"length: ${rst.length}")
    println(s"bytes: ${rst.mkString(" ")}")
    rst
  }



  def decodeTest(bytes: Array[Byte]): Unit = {
    import org.seekloud.byteobject.ByteObject._
    val buffer = MiddleBufferInJvm(bytes)
    val obj = bytesDecode[Msg](buffer)
    println(s"bytes: ${bytes.mkString(" ")}")
    println(s"length: ${bytes.length}")
    println(s"obj: $obj")

  }






}
