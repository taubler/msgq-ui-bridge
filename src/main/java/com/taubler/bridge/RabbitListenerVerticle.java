package com.taubler.bridge;

import static com.taubler.bridge.RabbitClientFactory.RABBIT_CLIENT_FACTORY_INSTANCE;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.rabbitmq.RabbitMQClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitListenerVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RabbitListenerVerticle.class);

    @Override
    public void start(Future<Void> fut) throws InterruptedException {
        try {
            
            RabbitMQClient rClient1 = RABBIT_CLIENT_FACTORY_INSTANCE.getRabbitClient(vertx);
            rClient1.start(sRes -> {
                if (sRes.succeeded()) {
                    rClient1.basicConsume("message.queue", "service.rabbit-message", bcRes -> {
                        if (bcRes.succeeded()) {
                            System.out.println("Listening to message.queue");
                        } else {
                            System.out.println("Message receipt failed: " + bcRes.cause());
                        }
                        System.out.println("Rabbit message client registered");
                    });
                } else {
                    System.out.println("Connection failed: " + sRes.cause());
                }
            });

            RabbitMQClient rClient2 = RABBIT_CLIENT_FACTORY_INSTANCE.getRabbitClient(vertx);
            rClient2.start(sRes -> {
                if (sRes.succeeded()) {
                    rClient2.basicConsume("todo.queue", "service.rabbit-todo", bcRes -> {
                        if (bcRes.succeeded()) {
                            System.out.println("Listening to todo.queue");
                        } else {
                            System.out.println("Message receipt failed: " + bcRes.cause());
                        }
                    });
                    log.info("Rabbit to-do client registered");
                } else {
                    System.out.println("Connection failed: " + sRes.cause());
                }
            });

            log.info("RabbitListenerVerticle started");
            fut.complete();
        } catch (Throwable t) {
            log.error("failed to start RabbitListenerVerticle: " + t.getMessage(), t);
            fut.fail(t);
        }
    }

}