package basicallyiamfox.ani.util;

@FunctionalInterface
public interface Func2<P, R> {
    R apply(P p);

    default R applyObj(Object p) {
        return apply((P)p);
    }
}
