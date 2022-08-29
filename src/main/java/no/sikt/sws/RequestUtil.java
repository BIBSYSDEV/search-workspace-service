package no.sikt.sws;

import nva.commons.apigateway.RequestInfo;
import nva.commons.core.JacocoGenerated;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class RequestUtil {

    public static final String HTTP_METHOD = "httpMethod";

    public static String getRequestHttpMethod(RequestInfo requestInfo) {
        return attempt(() -> requestInfo.getRequestContext()
                .get(HTTP_METHOD).asText())
                .orElseThrow();
    }

}
