package no.sikt.sws;

import com.amazonaws.http.HttpMethodName;
import nva.commons.apigateway.RequestInfo;
import nva.commons.core.JacocoGenerated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static no.sikt.sws.constants.ApplicationConstants.SCOPE_IDENTIFIER;
import static nva.commons.apigateway.RequestInfoConstants.SCOPES_CLAIM;
import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class RequestUtil {

    public static final String HTTP_METHOD = "httpMethod";

    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

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
            logger.info("fullScope:" + fullScope);

            return fullScope.replaceFirst(SCOPE_IDENTIFIER + "/", "");
        }).orElseThrow();
    }



}
