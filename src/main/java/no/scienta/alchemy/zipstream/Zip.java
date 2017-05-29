package no.scienta.alchemy.zipstream;

@SuppressWarnings("WeakerAccess")
public final class Zip<X, Y> {

    private final X x;

    private final Y y;

    Zip(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X x() {
        return x;
    }

    public Y y() {
        return y;
    }
}
