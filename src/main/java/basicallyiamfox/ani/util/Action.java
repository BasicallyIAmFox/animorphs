package basicallyiamfox.ani.util;

@FunctionalInterface
public interface Action<P1, P2> {
    void apply(P1 p1, P2 p2);

    default void applyObj(Object p1, Object p2) {
        apply((P1) p1, (P2) p2);
    }
}
