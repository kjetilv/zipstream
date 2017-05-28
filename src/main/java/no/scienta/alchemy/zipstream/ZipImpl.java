package no.scienta.alchemy.zipstream;

class ZipImpl<X, Y> implements ZipStream.Zip<X, Y> {

    private final X x;

    private final Y y;

    ZipImpl(X x, Y y) {
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
