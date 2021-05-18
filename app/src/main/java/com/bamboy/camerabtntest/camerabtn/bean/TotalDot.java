package com.bamboy.camerabtntest.camerabtn.bean;

public class TotalDot {
    public DotBean dotStart;
    public DotBean dotCenter;
    public DotBean dotEnd;
    public DotBean dotAuxiliary;

    public TotalDot() {
    }

    public TotalDot(DotBean dotStart, DotBean dotCenter, DotBean dotEnd, DotBean dotAuxiliary) {
        this.dotStart = dotStart;
        this.dotCenter = dotCenter;
        this.dotEnd = dotEnd;
        this.dotAuxiliary = dotAuxiliary;
    }
}
