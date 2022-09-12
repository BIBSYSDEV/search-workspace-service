package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.JacocoGenerated;

import java.util.Arrays;

import static no.sikt.sws.constants.ApplicationConstants.SCOPE_IDENTIFIER;
import static nva.commons.apigateway.RequestInfoConstants.SCOPES_CLAIM;
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


    public static String getWorkspace(RequestInfo request) {
        return attempt(() -> {
            var fullScope = request.getRequestContextParameter(SCOPES_CLAIM);
            return fullScope.replaceFirst(SCOPE_IDENTIFIER + "/", "");
        }).orElseThrow();
    }

    public static String addWorkspace(String body, String workspace, String index) {
        if (body == null) {
            return null;
        }

        return attempt(() -> {
            var strippedIndex = Arrays.stream(index.split("/")).findFirst();
            return body.replaceAll(index, workspace + "-" + strippedIndex);
        }).orElseThrow();
    }

}
