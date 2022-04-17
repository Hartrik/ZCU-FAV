package cz.harag.ds.cv02.server.rest;

import cz.harag.ds.cv02.server.Main;
import cz.harag.ds.cv02.server.Snapshot;
import cz.harag.ds.cv02.server.model.Response;

import java.util.Random;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ServiceEndpoint {

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("snapshot")
    public Response snapshot() {
        Random random = new Random();
        int marker = Math.abs(random.nextInt());
        Main.BANK.scheduleSnapshot(marker);
        return new Response("Snapshot scheduled with id=" + marker);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("snapshot/{marker}")
    public Response snapshot(@PathParam("marker") int marker) {
        Main.BANK.scheduleSnapshot(marker);
        return new Response("Snapshot scheduled with id=" + marker);
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("snapshot/{marker}/status")
    public Response snapshotStatus(@PathParam("marker") int marker) {
        Snapshot snapshot = Main.BANK.getSnapshot(marker);
        if (snapshot != null) {
            return new Response(snapshot.toString());
        } else {
            return new Response("Snapshot " + marker + " not found");
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("status")
    public Response status() {
        return new Response("Balance: " + Main.BANK.getBalance() + ", "
                + "Snapshots: " + Main.BANK.getSnapshots());
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("shutdown")
    public Response shutdown() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                System.exit(0);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return new Response("Ok");
    }

}
