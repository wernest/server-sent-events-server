/*
MIT License

Copyright (c) 2016 William Ernest

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.wernest.sse;

import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import javax.ws.rs.core.MediaType;

/**
 * @author wernest
 */
@Path("sse")
public class ServerSentEventsResource{

    private static EventOutput eventOutput = new EventOutput();

    @GET
    @Path("updates")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(){
        final String subscriptionId = String.valueOf(new Random().nextInt());
        MessageManager.getInstance().addObjectSubscription(subscriptionId, SampleObject.class.getName());
        return subscriptionId;
    }

    @POST
    @Path("updates")
    public void addMessage(final SampleObject message) throws IOException {
        MessageManager.getInstance().addObject(SampleObject.class.getName(), message);
    }

    /**
     * Used to close a subscription from a client.
     * @param subscriptionId subscriptionId to close
     * @throws IOException
     */
    @DELETE
    @Path("updates/{subscriptionId}")
    public void close(@PathParam("subscriptionId") String subscriptionId) throws IOException {
        eventOutput.close();
        ServerSentEventsResource.setEventOutput(new EventOutput());
        MessageManager.getInstance().removeList(subscriptionId);
    }

    @GET
    @Path("updates/{subscriptionId}")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput startChannelCommunication(@PathParam("subscriptionId") final String subscriptionId) {
        final EventOutput seq = new EventOutput();
        final List<Object> list = new LinkedList<>();

        final Thread sseThread = new Thread() {
            public void run() {
                try{
                    while(list.size() > 0){
                        Object object = list.remove(0);
                        Gson gson = new GsonBuilder().create();
                        seq.write(new OutboundEvent.Builder().name(object.getClass().getSimpleName())
                                .data(String.class, gson.toJson(object)).build());
                    }
                } catch (final IOException e) {
                    try {
                        if (!seq.isClosed()) {
                            seq.close();
                            close(subscriptionId);
                            MessageManager.getInstance().removeList(subscriptionId);
                        }
                    }catch(IOException ioe) {
                        System.out.println("problem closing seq " + ioe.toString());
                    }
                }
            }
        };
        sseThread.start();

        MessageManager.getInstance().addList(subscriptionId, sseThread, list);


        return seq;
    }

    private static void setEventOutput(final EventOutput eventOutput) {
        ServerSentEventsResource.eventOutput = eventOutput;
    }
}
