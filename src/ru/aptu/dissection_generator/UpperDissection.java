package ru.aptu.dissection_generator;

public class UpperDissection {
    private Dissection dissection;
    private int after, size;

    public UpperDissection(Dissection dissection, int after, int size) {
        this.dissection = dissection;
        this.after = after;
        this.size = size;
    }

    public LowerDissection createArbitraryLower() {
        Dissection dissection = this.dissection.gluePolygon(after, size);
        return new LowerDissection(dissection, after);
    }
}
