package com.taubler.bridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class RabbitMessageConverterVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws InterruptedException {
        
        vertx.eventBus().consumer("service.rabbit-message", msg -> {
            JsonObject m = (JsonObject) msg.body();  // JsonObject, "body":msg
            if (m.containsKey("body")) {
                String text = m.getString("body");
                System.out.println("Received message from queue: " + text);
                vertx.eventBus().publish("service.ui-message", text);
            }
        });
        
        fut.complete();
    }

}