package no.sikt.sws;

import com.amazonaws.Request;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import nva.commons.core.JacocoGenerated;

import static no.sikt.sws.constants.ApplicationConstants.ELASTICSEARCH_REGION;

public class AwsSignerWrapper {
    private static final String ELASTIC_SEARCH_SERVICE_NAME = "es";

    public void signRequest(Request<Void> request) {
        var awsSigner = getAws4Signer();
        var credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        awsSigner.sign(request, credentials);
    }

    @JacocoGenerated
    private AWS4Signer getAws4Signer() {
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(ELASTIC_SEARCH_SERVICE_NAME);
        signer.setRegionName(ELASTICSEARCH_REGION);
        return signer;
    }


}
