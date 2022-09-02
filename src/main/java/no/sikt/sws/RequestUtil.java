package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.JacocoGenerated;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class RequestUtil {

    public static final String HTTP_METHOD = "httpMethod";

    public static HttpMethodName getRequestHttpMethod(RequestInfo requestInfo) {
        return attempt(() ->
                HttpMethodName.valueOf(
                        requestInfo.getOtherProperties().get(HTTP_METHOD).toString()
                )
        ).orElseThrow();
    }
}
