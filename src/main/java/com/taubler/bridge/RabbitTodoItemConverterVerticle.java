package com.taubler.bridge;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class RabbitTodoItemConverterVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) throws InterruptedException {
        
        vertx.eventBus().consumer("service.rabbit-todo", msg -> {
            JsonObject m = (JsonObject) msg.body();  // JsonObject, "body":msg
            if (m.containsKey("body")) {
                String payloadStr = m.getString("body");
                JsonObject payload = new JsonObject(payloadStr);
                String partyGuid = payload.getString("guid");
                String taskId = payload.getString("taskId");
                String taskStatus = payload.getString("taskStatus");
                JsonObject browserPayload = new JsonObject();
                browserPayload.put("taskId", taskId);
                browserPayload.put("completed", "ACTIVE".equals(taskStatus));
                vertx.eventBus().publish("service.ui-taskitem-" + partyGuid, browserPayload);
            }
        });
        
        fut.complete();
    }

}

/*

{
 "guid": "11026159-fddd-47e4-9183-df9fac68453b",
 "taskId": "bank-account",
 "taskStatus": "ACTIVE"
}

*/