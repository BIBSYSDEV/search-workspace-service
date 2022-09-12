package no.sikt.sws;

import nva.commons.core.JacocoGenerated;

@JacocoGenerated
public class ResponseUtil {

    public static String stripWorkspace(String body, String workspace) {
        if (body == null) {
            return null;
        }
        return body.replaceAll(workspace + "-","");
    }
}
