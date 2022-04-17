package cz.harag.ds.cv01.server.rest.server;

import cz.harag.ds.cv01.server.model.Response;
import cz.harag.ds.cv01.server.model.ServerRequest;

import java.util.PriorityQueue;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/server")
public class ServerEndpoint {

    private static final PriorityQueue<ServerRequest> requests = new PriorityQueue<>();
    private static long lastId = -1;
    private static long balance = 0;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response process(ServerRequest request) {
        synchronized (requests) {
            System.out.println("Received " + request);

            requests.add(request);

            boolean hasNext = hasNext();
            if (hasNext) {
                do {
                    ServerRequest r = requests.poll();
                    balance += r.getValue();
                    lastId = r.getId();
                    System.out.println(" | Executed " + r + " balance: " + balance);
                } while (hasNext());
            } else {
                System.out.println(" | Waiting for op with id = " + (lastId + 1) + "; in queue: " + requests.size());
            }
            return new Response("OK");
        }
    }

    private boolean hasNext() {
        return !requests.isEmpty() && (lastId + 1) == (long) requests.peek().getId();
    }

}
