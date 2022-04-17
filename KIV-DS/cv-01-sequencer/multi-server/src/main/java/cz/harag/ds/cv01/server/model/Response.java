package cz.harag.ds.cv01.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Response {

    public String msg;

    public Response(String msg) {
        this.msg = msg;
    }
}
