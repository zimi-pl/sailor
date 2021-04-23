package pl.zimi.testing;

import ann.Queryable;

@Queryable
public class Foo {

    private String abc;
    private String bar;
    private String test;
    private Integer value;

    public String getAbc() {
        return abc;
    }

    public String getBar() {
        return bar;
    }

    public String getTest() {
        return test;
    }

    public Integer getValue() {
        return value;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
