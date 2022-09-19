package no.sikt.sws;

import nva.commons.core.JacocoGenerated;

import java.util.Arrays;

import static nva.commons.core.attempt.Try.attempt;

@JacocoGenerated
public class WorkspaceStripper {

    public static String remove(String body, String workspace) {
        if (body == null) {
            return null;
        }
        return body.replaceAll(workspace + "-","");
    }

    public static String add(String body, String workspace, String index) {
        if (body == null) {
            return null;
        }

        return attempt(() -> {
            var strippedIndex = Arrays.stream(index.split("/")).findFirst();
            return body.replaceAll(index, workspace + "-" + strippedIndex);
        }).orElseThrow();
    }
}
