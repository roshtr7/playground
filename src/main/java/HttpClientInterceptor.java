import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

import java.io.IOException;

public class HttpClientInterceptor implements IClientInterceptor {

    //    @Override
//    public void interceptRequest(IHttpRequest iHttpRequest) {
//        stopWatch.startTask("test");
//    }
//
//    @Override
//    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
//        stopWatch.endCurrentTask();
//    }
    private StopWatch stopWatch;

    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {
        this.stopWatch = new StopWatch();
        stopWatch.startTask("get patient details");
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {
        String timing = " in " + iHttpResponse.getRequestStopWatch().toString();
        System.out.println("timing taken to execute: " + iHttpResponse.getRequestStopWatch().toString());
    }
}
