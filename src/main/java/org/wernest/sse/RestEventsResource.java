package org.wernest.sse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author wernest
 */
@Path("/rest-event")
public class RestEventsResource{

    @POST
    @Path("send/{subscriptionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewSampleObject(@PathParam("subscriptionId") String subscriptionId, SampleObject sampleObject){
        MessageManager.getInstance().addObject(SampleObject.class.getName(), sampleObject);
        return Response.status(200).build();
    }

    @POST
    @Path("send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNewSampleObject(SampleObject sampleObject){
        MessageManager.getInstance().addObject(SampleObject.class.getName(), sampleObject);
        return Response.status(200).build();

    }
}
