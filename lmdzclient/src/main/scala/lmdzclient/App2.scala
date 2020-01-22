package lmdzclient

import lmdz.utils._
import lmdzclient.utils._
import zio._

object App2 {

  val eg1 = for {
    mdefaultDB  <-  lmdz.apis.v2.createOrOpen_mDefaultDB
    _           <-  mdefaultDB.use(defaultDB => for {
                      key           <-      psl("key? ") *> gsl
                      vlu           <-      psl("value? ") *> gsl
                      kbb           <-      IO.effect(stringToUtf8DByteBuffer(key))
                      vbb           <-      IO.effect(stringToUtf8DByteBuffer(vlu))
                      _             <-      defaultDB.put(kbb,vbb)
                      key2          <-      psl("key? ") *> gsl
                      kbb2          <-      IO.effect(stringToUtf8DByteBuffer(key2))
                      vbb2          <-      defaultDB.get(kbb2)
                      vlu2          <-      IO.effect(dByteBufferToUtf8String(vbb2))
                      _             <-      psl(vlu2)
                    } yield ()
  )} yield ()

}
