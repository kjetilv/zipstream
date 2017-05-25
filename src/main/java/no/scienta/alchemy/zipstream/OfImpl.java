package no.scienta.alchemy.zipstream;

class OfImpl<X, Y> implements ZipStream.Of<X, Y> {

    private final X x;

    private final Y y;

    OfImpl(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public X x() {
        return x;
    }

    @Override
    public Y y() {
        return y;
    }
}
