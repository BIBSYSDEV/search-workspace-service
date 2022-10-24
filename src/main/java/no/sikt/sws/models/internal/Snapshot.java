package no.sikt.sws.models.internal;

public class Snapshot {
    String name;
    Integer epochTime;

    public Integer getEpochTime() {
        return epochTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEpochTime(Integer epochTime) {
        this.epochTime = epochTime;
    }
}
