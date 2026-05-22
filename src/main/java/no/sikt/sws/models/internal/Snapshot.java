package no.sikt.sws.models.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Comparator;

@SuppressWarnings("PMD.OverrideBothEqualsAndHashCodeOnComparable")
public class Snapshot implements Comparable<Snapshot> {
    @JsonProperty
    private String name;

    @JsonProperty
    private Instant epochTime;

    public Instant getEpochTime() {
        return epochTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = Instant.ofEpochMilli(epochTime);
    }

    @Override
    public int compareTo(Snapshot o) {
        return Comparators.SNAP_COMPARATOR_TIME.compare(this, o);
    }

    public static class Comparators {
        public static final Comparator<Snapshot> SNAP_COMPARATOR_TIME =
                Comparator.comparing(Snapshot::getEpochTime);
    }
}
