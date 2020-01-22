package lmdzclient

import lmdz.utils._
import lmdzclient.utils._
import zio._

object App2 {

  val eg1 = for {
    mdefaultDB  <-  lmdz.apis.v2.createOrOpen_mDefaultDB
    _           <-  mdefaultDB.use(defaultDB => for {
                      key           <-      ps("put an item >> key? ") *> gsl
                      vlu           <-      ps("put an item >> value? ") *> gsl
                      kbb           <-      IO.effect(stringToUtf8DByteBuffer(key))
                      vbb           <-      IO.effect(stringToUtf8DByteBuffer(vlu))
                      _             <-      defaultDB.put(kbb,vbb)
                      key2          <-      ps("get an item >> key? ") *> gsl
                      kbb2          <-      IO.effect(stringToUtf8DByteBuffer(key2))
                      vbb2          <-      defaultDB.get(kbb2)
                      vlu2          <-      IO.effect(dByteBufferToUtf8String(vbb2)) orElse IO("not found")
                      _             <-      psl(vlu2)
                      key3          <-      ps("delete an item >> key? ") *> gsl
                      kbb3          <-      IO.effect(stringToUtf8DByteBuffer(key3))
                      _             <-      defaultDB.del(kbb3).map(b => println(if(b) "deleted" else "not found"))
                    } yield ()
  )} yield ()

}
