package no.sikt.sws.models.internal;

import java.util.Comparator;
import java.util.Date;

public class Snapshot implements Comparable<Snapshot> {
    String name;
    Date epochTime;

    public Date getEpochTime() {
        return epochTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = new Date(epochTime);
    }

    @Override
    public int compareTo(Snapshot o) {
        return Comparators.SNAP_COMPARATOR_TIME.compare(this, o);
    }

    public static class Comparators {
        public static final Comparator<Snapshot> SNAP_COMPARATOR_TIME =
                Comparator.comparingLong((Snapshot snap) -> snap.getEpochTime().getTime());
    }
}
