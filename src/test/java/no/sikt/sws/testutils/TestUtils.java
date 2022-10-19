package no.sikt.sws.testutils;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.testutils.HandlerRequestBuilder;

import java.io.InputStream;
import java.util.Map;

import static no.sikt.sws.IndexHandler.RESOURCE_IDENTIFIER;
import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;

public final class TestUtils {

    public static Map<String, String> buildPathParamsForIndex(String index) {
        return Map.of(
                RESOURCE_IDENTIFIER, index
        );
    }

//    public static Map<String, String> buildQueryParamsFromString(String queryParams) {
//        return Map.of(queryParams.split("&="));
//    }

    public static InputStream buildRequest(HttpMethod httpMethod,
                                           Map<String, String> pathParams,
                                           Map<String, String> queryParameters)
            throws JsonProcessingException {

        return new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(httpMethod.toString())
                .withPathParameters(pathParams)
                .withQueryParameters(queryParameters)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .build();
    }

    public static  <T> InputStream buildRequestWithBody(HttpMethod httpMethod,
                                                        Map<String,String> pathParams,
                                                        Map<String, String> queryParameters,
                                                        T body)
            throws JsonProcessingException {

        return new HandlerRequestBuilder<T>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(httpMethod.toString())
                .withPathParameters(pathParams)
                .withQueryParameters(queryParameters)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .withBody(body)
                .build();
    }
}
