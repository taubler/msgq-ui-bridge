package com.taubler.bridge;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * @author dtaubler
 */
public class RabbitClientFactory {
    
    public static RabbitClientFactory RABBIT_CLIENT_FACTORY_INSTANCE = new RabbitClientFactory();

    private static RabbitMQClient rabbitClient;
    
    private RabbitClientFactory() {}

    public RabbitMQClient getRabbitClient(Vertx vertx) {
        if (rabbitClient == null) {
            JsonObject config = new JsonObject();
             config.put("uri", "amqp://dbuyiruu:X4xvi0JEtb4sbmcMJ-X2o8kzAibBGClm@cat.rmq.cloudamqp.com/dbuyiruu");
             config.put("connectionTimeout", 50000);
             config.put("handshakeTimeout", 50000);
             rabbitClient = RabbitMQClient.create(vertx, config);
        }
        return rabbitClient;
    }

}