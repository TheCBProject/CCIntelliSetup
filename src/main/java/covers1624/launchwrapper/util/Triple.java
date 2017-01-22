package covers1624.launchwrapper.util;

/**
 * Created by covers1624 on 2/01/2017.
 */
public class Triple<A,B,C> {

    private A a;
    private B b;
    private C c;

    public Triple(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public void setC(C c) {
        this.c = c;
    }
}
