package io.quarkus.rest.client.reactive.registerclientheaders;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.TestJacksonBasicMessageBodyReader;

@RegisterRestClient
@RegisterProvider(TestJacksonBasicMessageBodyReader.class)
public interface HeaderSettingClient {

    String HEADER = "my-header";

    @Path("/with-incoming-header")
    @GET
    RequestData setHeaderValue(@HeaderParam(HEADER) String headerName);

    @Path("/with-incoming-header/no-passing")
    @GET
    RequestData setHeaderValueNoPassing(@HeaderParam(HEADER) String headerName);

}
