package cz.harag.ds.cv01.server.rest.seqencer;

import cz.harag.ds.cv01.client.shuffler.ApiClient;
import cz.harag.ds.cv01.client.shuffler.ApiException;
import cz.harag.ds.cv01.client.shuffler.Configuration;
import cz.harag.ds.cv01.client.shuffler.api.DefaultApi;
import cz.harag.ds.cv01.client.shuffler.model.Operation;
import cz.harag.ds.cv01.server.Main;
import cz.harag.ds.cv01.server.model.Response;
import cz.harag.ds.cv01.server.model.SequencerRequest;

import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/sequencer")
public class SequencerEndpoint {

    private static final AtomicLong atomicLong = new AtomicLong();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response process(SequencerRequest sequencerRequest) {
        System.out.println("Received " + sequencerRequest);

        ApiClient configuration = Configuration.getDefaultApiClient();
        configuration.setBasePath(Main.SHUFFLER_BASE_PATH);
        DefaultApi apiInstance = new DefaultApi(configuration);

        Operation operation = new Operation();
        operation.setValue(sequencerRequest.getValue());
        operation.setId(atomicLong.getAndIncrement());
        try {
            apiInstance.shufflerPost(operation);
            return new Response("OK");
        } catch (ApiException e) {
            e.printStackTrace();
            return new Response(e.getMessage());
        }
    }

}
