package pl.zimi.repository.example;

import pl.zimi.repository.annotation.Queryable;

@Queryable
public class Bar {

    private String str;

    public Bar() {
    }

    public Bar(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
