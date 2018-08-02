# byteObject
目标: 方便的将`case class`序列化为二进制进行传输或者存储;  
参考了[circe](https://github.com/circe/circe)处理json的方式, 除了标准库和[shapeless](https://github.com/milessabin/shapeless), 没有其他依赖;  
欢迎提出你宝贵的建议和意见;  


## MiddleBuffer
1. `trait MiddleBuffer`是用于进行二进制转换的中间数据数据结构; 
2. `MiddleBuffer`提供了方便的`putXXX()`方法写入方法, 使我们可以将需要传输的对象, 保存到`MiddleBuffer`中; 
3. 它的具体实现提供了`result()`方法, 转换为方便传输的二进制形式, 用于传输; 
   * 在jvm平台上是`MiddleBufferInJvm`, 转换为`Array[Byte]`数组; 
   * 在js平台上是`MiddleBufferInJs`, 转换为`ArrayBuffer`数组; 
4. 同时, 完成传输后, 你也可以通过二进制数据方便的构造`MiddleBuffer`的具体实现; 
5. 最后`MiddleBuffer`提供了`getXXX()`方法来获取数据; 
6. 以上内容，在实际使用时，很多都不需要关注;

## byteObject
1. `byteObject`包提供了三个东西：`ByteEncoder`,`ByteDecoder`和`ByteObject`
2. `ByteEncoder`能够分析对象结构**自动的**将对象序列化到`MiddleBuffer`中;
3. 类似的, `ByteDecoder`能够自动的将`MiddlerBuffer`中的二进制数据解析为指定对象;


## Example
说了这么多, 其实使用起来非常非常简单, 代码在本工程的netSnake_back分支下； 
例子，假设消息定义如下：
 ```
   sealed trait Msg
   case class TextMsg(id: Int, data: String, value: Float) extends Msg
   case class MultiTextMsg(id: Int, b: Option[Boolean], ls: List[TextMsg]) extends Msg

 ```
 目前已经支持全部scala基础类型；
 


需要使用编解码的文件中，引入编解码工具；
 ```
   import org.seekloud.byteobject.ByteObject._
 ```

### In Jvm
* encode  
  ```
  val sendBuffer = MiddleBufferInJvm(2048)
  val msg: Msg = TextMsg(1001, "testMessssage", 6.66f)
  val arr: Array[Byte] = msg.fillMiddleBuffer(sendBuffer).result()
  val strice = BinaryMessage.Strict(ByteString(arr)) // 这个就可以直接通过akka http发送了
  ```
  
* decode  
  ```
  val buffer = MiddleBufferInJvm(bMsg.asByteBuffer)
  val msg: Msg =
    bytesDecode[Msg](buffer) match {
       case Right(v) => v
       case Left(e) =>
         println(s"decode error: ${e.message}")
         TextMsg(-1, "decode error", 5.555f)
    }
  //msg 就可以直接交给Actor处理了;
  ```
  具体可参看jvmExample


### In Js
* encode  
  ```
  val sendBuffer = MiddleBufferInJs(2048)
  val msg: Msg = TextMsg(1001, "testMessssage", 6.66f)
  msg.fillMiddleBuffer(sendBuffer)
  val ab: ArrayBuffer = sendBuffer.result()
  //ab就可以直接用ws发送了，ws.send(ab)
  ```
  
* decode  
  ```
  val blobMsg: Blob = ...
  val fr = new FileReader()
  fr.readAsArrayBuffer(blobMsg)
  fr.onloadend = { _: Event =>
    val buf = fr.result.asInstanceOf[ArrayBuffer]
    val middleDataInJs = MiddleBufferInJs(buf) // get middle data.
    bytesDecode[Msg](middleDataInJs) match { //decode here.
      case Right(data) => data match {
        case m: TextMsg => ... //process m
        case m: MultiTextMsg => ... //process m
      }
      case Left(error) => println(s"got error: ${error.message}")
    }
  }
  ```
  可以参看jsExample
