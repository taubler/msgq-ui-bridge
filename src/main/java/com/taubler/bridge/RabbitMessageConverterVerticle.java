package com.taubler.bridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class RabbitMessageConverterVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws InterruptedException {
        
        vertx.eventBus().consumer("service.rabbit", msg -> {
            JsonObject m = (JsonObject) msg.body();  // JsonObject, "body":msg
            if (m.containsKey("body")) {
                vertx.eventBus().publish("service.ui-message", m.getString("body"));
            }
        });
    }

}