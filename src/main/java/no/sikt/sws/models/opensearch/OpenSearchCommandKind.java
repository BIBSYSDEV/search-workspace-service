package no.sikt.sws.models.opensearch;

import java.util.ArrayDeque;
import java.util.Arrays;


@SuppressWarnings({"PMD.OnlyOneReturn"})
public enum OpenSearchCommandKind {
    ALIAS("_alias"),
    BULK("_bulk"),
    DOC(".*/_doc.*"),
    MAPPING("_mapping"),
    SEARCH(".*_search.*"),
    SCROLL("_search/scroll"),
    INDEX("index"),
    // the following are not executable
    NOT_IMPLEMENTED("_.+"),
    INVALID("invalid");
    private static final String ALLOWED_INPUT = "[A-ZÆØÅa-zæøå*\\d/_-]*";

    private final String val;

    OpenSearchCommandKind(String val) {
        this.val = val;
    }

    public  boolean isNotValid() {
        return this.compareTo(NOT_IMPLEMENTED) >= 0;
    }

    @Override
    public String toString() {
        return val;
    }

    public static OpenSearchCommandKind fromString(String resourceIdentifier) {
        if (resourceIdentifier == null || resourceIdentifier.isEmpty()) {
            return INVALID;
        }

        if (SCROLL.val.equals(resourceIdentifier)) {
            return SCROLL;
        }

        //find last item in resourceIdentifier, if only one item (none), use resourceIdentifier
        var resource = new ArrayDeque<>(Arrays.asList(resourceIdentifier.split("/"))).getLast();
        String finalResource = (resource.isEmpty() ? resourceIdentifier : resource).split("[?]")[0];

        var result = Arrays.stream(values())
                .filter(p -> finalResource.equals(p.val) || resourceIdentifier.matches(p.val))
                        .findFirst()
                        .orElse(INDEX);

        if (result == INDEX && !resourceIdentifier.matches(ALLOWED_INPUT)) {
            result = INVALID;
        }

        return result;
    }


}
