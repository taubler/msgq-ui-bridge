package com.taubler.bridge

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.Message

class RabbitMessageWatcher extends ScalaVerticle {
  
  override def start(): Unit = {
    vertx.eventBus.registerHandler("service.ui-message", { msg: Message[Object] => 
      println("About to send $msg to browser")  
    })
  }
  
}