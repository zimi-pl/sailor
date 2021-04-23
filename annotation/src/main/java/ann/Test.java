//package ann;
//
//public class Test {
//
//    Foo foo;
//
//    static  class Foo {
//        Bar bar;
//
//
//    }
//
//    static class Bar {
//        String str;
//    }
//
//    static class STest extends Descriptor {
//        static STest test = new STest(null, "");
//
//        SFoo foo = new SFoo(this, "foo");
//
//        public STest(Descriptor parent, String path) {
//            super(parent, path);
//        }
//    }
//
//    static class SFoo extends Descriptor {
//        static SFoo foo = new SFoo(null, "");
//
//        SBar bar = new SBar(this, "bar");
//
//        public SFoo(Descriptor parent, String path) {
//            super(parent, path);
//        }
//    }
//
//    static class SBar extends Descriptor {
//        static SBar bar = new SBar(null, "");
//
//        Descriptor str = new Descriptor(this, "str");
//
//        public SBar(Descriptor parent, String path) {
//            super(parent, path);
//        }
//    }
//
//    public static void main(String[] args) {
//        System.out.println(STest.test.foo.bar.str.getPath());
//        System.out.println(SFoo.foo.bar.str.getPath());
//        System.out.println(SBar.bar.str.getPath());
//    }
//}
