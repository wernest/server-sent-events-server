/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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
@Path("server-sent-events")
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
