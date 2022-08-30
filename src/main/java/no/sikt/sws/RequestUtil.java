package no.sikt.sws;

import com.amazonaws.HttpMethod;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.JacocoGenerated;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class RequestUtil {

    public static final String HTTP_METHOD = "httpMethod";

    public static HttpMethod getRequestHttpMethod(RequestInfo requestInfo) {
        return attempt(() ->
                HttpMethod.valueOf(
                        requestInfo.getOtherProperties().get(HTTP_METHOD).toString()
                )
        ).orElseThrow();
    }
}
