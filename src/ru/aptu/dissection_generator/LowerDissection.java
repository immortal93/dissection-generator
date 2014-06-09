package ru.aptu.dissection_generator;

public class LowerDissection {
    private Dissection dissection;
    private int after;

    public LowerDissection(Dissection dissection, int after) {
        this.dissection = dissection;
        this.after = after;
    }

    public Dissection getUnderlyingDissection() {
        return dissection;
    }

    boolean isParentFor(Dissection dissection) {
        return dissection.isCanonicalRoot(after);
    }
}
