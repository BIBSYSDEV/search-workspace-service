package no.sikt.sws.models.opensearch;

import java.util.ArrayDeque;
import java.util.Arrays;


public enum OpenSearchCommand {
    ALIAS("_alias"),
    BULK("_bulk"),
    DOC(".*/_doc.*"),
    MAPPING("_mapping"),
    SEARCH("_search"),
    OTHER("other"),
    // the following are not executable
    NOT_IMPLEMENTED("_"),
    INVALID("invalid"),
    ERROR("error");

    private static final String ALLOWED_INPUT = "[A-ZÆØÅa-zæøå\\d/_-]*";

    private final String val;

    OpenSearchCommand(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    public static OpenSearchCommand fromString(String resourceIdentifier) {
        if (resourceIdentifier == null || resourceIdentifier.isEmpty()) {
            return INVALID;
        }
        //find last item in resourceIdentifier, if only one item (none), use resourceIdentifier
        var resource = new ArrayDeque<>(Arrays.asList(resourceIdentifier.split("/"))).getLast();
        String finalResource = resource.isEmpty() ? resourceIdentifier : resource;

        var result = Arrays.stream(OpenSearchCommand.values())
                .filter(p -> finalResource.equals(p.val) || resourceIdentifier.matches(p.val))
                        .findFirst()
                        .orElse(OpenSearchCommand.OTHER);

        if (result == OTHER && !resourceIdentifier.matches(ALLOWED_INPUT)) {
            result = INVALID;
        }

        return result;
    }


}
