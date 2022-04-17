package cz.harag.ds.cv01.server.rest.shuffler;

import cz.harag.ds.cv01.client.server.ApiClient;
import cz.harag.ds.cv01.client.server.ApiException;
import cz.harag.ds.cv01.client.server.Configuration;
import cz.harag.ds.cv01.client.server.api.DefaultApi;
import cz.harag.ds.cv01.client.server.model.Operation;
import cz.harag.ds.cv01.server.Main;
import cz.harag.ds.cv01.server.model.Response;
import cz.harag.ds.cv01.server.model.ShufflerRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/shuffler")
public class ShufflerEndpoint {

    private static final int MAX_DELAY = 200;  // ms

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response process(final ShufflerRequest sequencerRequest) {
        System.out.println("Received " + sequencerRequest);

        for (String path : Main.SERVER_BASE_PATHS) {
            postWithRandomDelay(sequencerRequest, path);
        }
        return new Response("OK");
    }

    private void postWithRandomDelay(final ShufflerRequest sequencerRequest, final String basePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long) (Math.random() * MAX_DELAY));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    post(sequencerRequest, basePath);
                }
            }
        }).start();
    }

    private void post(ShufflerRequest sequencerRequest, String basePath) {
        synchronized (ShufflerEndpoint.class) {
            ApiClient configuration = Configuration.getDefaultApiClient();
            configuration.setBasePath(basePath);
            DefaultApi apiInstance = new DefaultApi(configuration);

            Operation operation = new Operation();
            operation.setValue(sequencerRequest.getValue());
            operation.setId(sequencerRequest.getId());
            try {
                apiInstance.serverPost(operation);
                System.out.println("Posted: " + sequencerRequest + " to " + basePath);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

}
