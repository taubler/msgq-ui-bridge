package com.taubler.bridge;

import scala.reflect.api.TypeTags;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.impl.RouterImpl;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.lang.scala.ScalaVerticle;

public class Main extends AbstractVerticle {
    
    TemplateHandler hbsTemplateHandler = TemplateHandler.create(HandlebarsTemplateEngine.create());

    @Override
    public void start() {
        
        HttpServer server = vertx.createHttpServer();
        
        Router router = new RouterImpl(vertx);
        router.get("/rsc/*").handler(this::resourceHandler);
        
        String hbsPath = ".+\\.hbs";
        router.getWithRegex(hbsPath).handler(hbsTemplateHandler);
        
        router.get("/").handler(this::homeHandler);
        
        SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        BridgeOptions bo = new BridgeOptions()
            .addInboundPermitted(new PermittedOptions().setAddress("/client.register"))
            .addOutboundPermitted(new PermittedOptions().setAddress("service.ui-message"));
        sockJSHandler.bridge(bo, event -> {
            System.out.println("A websocket event occurred: " + event.type() + "; " + event.getRawMessage());
            event.complete(true);
        });
        router.route("/client.register" + "/*").handler(sockJSHandler);
        
        deployVerticles();
        
        int port = 8088;
        server.requestHandler(router::accept).listen(port);
        System.out.printf("Deployed server on port %s \n", port);
    }
    
    // handlers
    
    protected void homeHandler(RoutingContext ctx) {
        ctx.reroute("/index.hbs");
    }
    
    protected void resourceHandler(RoutingContext ctx) {
        String filename = ctx.request().path().substring(1);
        vertx.fileSystem().exists(filename, b -> {
            if (b.result()) {
                ctx.response().sendFile(filename);
            } else {
                System.out.println("Could not find file: " + filename);
                ctx.fail(404);
            }
        });
    }
    
    // verticles

    protected void deployVerticles() {
        boolean workers = true;
        deployVerticle(RabbitListenerVerticle.class.getName(), workers);
        deployVerticle(RabbitMessageConverterVerticle.class.getName(), workers);
        deployVerticle("scala:com.taubler.bridge.RabbitMessageWatcher", workers);
    }
    
    protected void deployVerticle(String className, boolean worker) {
        DeploymentOptions options = new DeploymentOptions().setWorker(worker);
        vertx.deployVerticle(className, options, res -> {
            if (res.succeeded()) {
                System.out.printf("Deployed %s verticle \n", className);
            } else {
                System.out.printf("Error deploying %s verticle:%s \n", className, res.cause());
            }
        });
    }

}