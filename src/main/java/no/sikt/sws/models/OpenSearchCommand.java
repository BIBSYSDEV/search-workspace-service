package no.sikt.sws.models;

import java.util.Arrays;


public enum OpenSearchCommand {
    ALIAS("_alias"),
    BULK("_bulk"),
    OTHER("other"),
    // the following are not executable
    NOT_IMPLEMENTED("_%"),
    INVALID("invalid");

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
        var result = Arrays.stream(OpenSearchCommand.values())
                .filter(p -> resourceIdentifier.endsWith(p.val) || resourceIdentifier.startsWith(p.val))
                        .findFirst()
                        .orElse(OpenSearchCommand.OTHER);
        if (result == OTHER && !resourceIdentifier.matches(ALLOWED_INPUT)) {
            result = INVALID;
        }
        return result;
    }


}
