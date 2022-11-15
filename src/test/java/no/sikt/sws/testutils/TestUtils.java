package no.sikt.sws.testutils;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.core.JsonProcessingException;
import no.unit.nva.commons.json.JsonUtils;
import no.unit.nva.testutils.HandlerRequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static no.sikt.sws.IndexHandler.RESOURCE_IDENTIFIER;
import static no.sikt.sws.constants.ApplicationConstants.EMPTY_STRING;
import static no.sikt.sws.testutils.TestConstants.TEST_SCOPE;
import static no.unit.nva.testutils.HandlerRequestBuilder.SCOPE_CLAIM;

public final class TestUtils {

    public static Map<String, String> buildPathParamsForIndex(String index) {
        return Map.of(
                RESOURCE_IDENTIFIER, index
        );
    }

    @NotNull
    public static Map<String, String> buildQueryParams(URI uri) {
        return Arrays.stream(uri.getQuery().split("&"))
            .map(param -> {
                var pair = param.split("=");
                return Map.entry(pair[0], pair[1]);
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static InputStream buildRequest(HttpMethod httpMethod,
                                           Map<String,String> pathParams)
            throws JsonProcessingException {

        return new HandlerRequestBuilder<Void>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(httpMethod.toString())
                .withPathParameters(pathParams)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .build();
    }

    public static  <T> InputStream buildRequestWithBody(HttpMethod httpMethod,
                                                        Map<String,String> pathParams,
                                                        T body)
            throws JsonProcessingException {

        return new HandlerRequestBuilder<T>(JsonUtils.dtoObjectMapper)
                .withHttpMethod(httpMethod.toString())
                .withPathParameters(pathParams)
                .withAuthorizerClaim(SCOPE_CLAIM, TEST_SCOPE)
                .withBody(body)
                .build();
    }

    public static String readCompact(String body) {
        return body.replaceAll("[\n\r ]", EMPTY_STRING);
    }
}
