package org.glassfish.jersey.examples.sse;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.grizzly.http.server.HttpServer;

/**
 * Server sent event example.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class App {

  private static final URI BASE_URI = URI.create("http://localhost:8080/");
  public static final String ROOT_PATH = "server-sent-events";

  public static void main(String[] args) {
    try {

      System.out.println("\"Server-Sent Events\" Jersey Example App");

      final ResourceConfig resourceConfig = new ResourceConfig(RestEventsResource.class, ServerSentEventsResource.class, SseFeature.class);
      resourceConfig.register(JacksonFeature.class);

      final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          server.shutdownNow();
        }
      }));

      HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/WEB-INF/");
      server.getServerConfiguration().addHttpHandler(httpHandler, "/html/");
      server.start();

      System.out.println(String.format("Application started.\nTry out %s%s\nStop the application using CTRL+C",
          BASE_URI, ROOT_PATH));

      Thread.currentThread().join();


    } catch (IOException | InterruptedException ex) {
      Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

