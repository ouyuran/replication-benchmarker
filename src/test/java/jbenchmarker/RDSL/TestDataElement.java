package jbenchmarker.RDSL;

import java.io.Serializable;

public class TestDataElement implements Serializable {
    private String content;
    private int pos;
    public TestDataElement(String content, int pos) {
        this.content = content;
        this.pos = pos;
    }

    public String getContent() {
        return this.content;
    }

    public int getPos() {
        return this.pos;
    }
}
