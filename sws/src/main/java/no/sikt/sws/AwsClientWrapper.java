package no.sikt.sws;

import com.amazonaws.*;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import no.sikt.sws.exception.OpenSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class AwsClientWrapper {

    private final boolean passError;

    private static final List<Integer> FORWARDED_ES_ERROR_CODES = Arrays.asList(HTTP_BAD_REQUEST, HTTP_NOT_FOUND, HTTP_BAD_METHOD, HTTP_NOT_ACCEPTABLE, HTTP_INTERNAL_ERROR);

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsClientWrapper.class);

    public AwsClientWrapper(boolean passError) {
        this.passError = passError;
    }


    private final HttpResponseHandler<String> responseHandler = new HttpResponseHandler<>() {
        @Override
        public String handle(HttpResponse response) throws Exception {
            var bytes = response.getContent().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    };

    private final HttpResponseHandler<SdkBaseException> errorHandler = new HttpResponseHandler<>() {

        @SuppressWarnings({"PMD.OnlyOneReturn"})
        @Override
        public AmazonClientException handle(HttpResponse response) throws Exception {

            var responseCode = response.getStatusCode();
            var bytes = response.getContent().readAllBytes();
            var bodyString = new String(bytes, StandardCharsets.UTF_8);

            if (passError && FORWARDED_ES_ERROR_CODES.contains(responseCode)) {
                return new OpenSearchException(responseCode, bodyString);
            }


            LOGGER.error("Handling error: {} {}", responseCode, bodyString);

            return new AmazonClientException("OpenSearchError: " + " " + responseCode + " " + bodyString);
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    };

    public Response<String> execute(Request<Void> request) {
        return new AmazonHttpClient(new ClientConfiguration())
            .requestExecutionBuilder()
            .executionContext(new ExecutionContext(true))
            .errorResponseHandler(errorHandler)
            .request(request)
            .execute(responseHandler);
    }
}
