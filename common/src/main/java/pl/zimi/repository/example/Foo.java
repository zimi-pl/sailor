package pl.zimi.repository.example;

import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Queryable
public class Foo {

    private String id;
    private String abc;
    private String def;
    private Bar bar;
    private String test;
    private Integer value;
    private Integer seq;
    private Integer version;
    private Instant date;

    public Foo() {
    }

    public Foo(String id, String abc, String def, Bar bar, String test, Integer value, Integer seq, Integer version, Instant date) {
        this.id = id;
        this.abc = abc;
        this.def = def;
        this.bar = bar;
        this.test = test;
        this.value = value;
        this.seq = seq;
        this.version = version;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getAbc() {
        return abc;
    }

    public String getDef() {
        return def;
    }

    public Bar getBar() {
        return bar;
    }

    public String getTest() {
        return test;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getSeq() {
        return seq;
    }

    public Integer getVersion() {
        return version;
    }

    public Instant getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public static class FooBuilder {

        private String id;
        private String abc;
        private String def;
        private Bar bar;
        private String test;
        private Integer value;
        private Integer seq;
        private Integer version;
        private Instant date;

        public FooBuilder id( String id) {
            this.id = id;
            return this;
        }
        public FooBuilder abc( String abc) {
            this.abc = abc;
            return this;
        }
        public FooBuilder def( String def) {
            this.def = def;
            return this;
        }
        public FooBuilder bar( Bar bar) {
            this.bar = bar;
            return this;
        }
        public FooBuilder test( String test) {
            this.test = test;
            return this;
        }
        public FooBuilder value( Integer value) {
            this.value = value;
            return this;
        }
        public FooBuilder seq( Integer seq) {
            this.seq = seq;
            return this;
        }
        public FooBuilder version( Integer version) {
            this.version = version;
            return this;
        }
        public FooBuilder date( Instant date) {
            this.date = date;
            return this;
        }

        public Foo build() {
            return new Foo(id, abc, def, bar, test, value, seq, version, date);
        }

    }

    public static FooBuilder builder() {
        return new FooBuilder();
    }
}