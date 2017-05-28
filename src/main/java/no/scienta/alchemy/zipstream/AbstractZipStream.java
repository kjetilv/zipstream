package no.scienta.alchemy.zipstream;

import java.util.function.Function;

abstract class AbstractZipStream<X, Y> implements ZipStream<X, Y> {

    public <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b) {
        return new MergedZipStream<>(stream().map(zip ->
                new ZipImpl<>(x2a.apply(zip.x()), y2b.apply(zip.y()))));
    }

    @Override
    public <A> ZipStream<A, Y> mapX(Function<X, A> f) {
        return new MergedZipStream<>(stream().map(zip ->
                new ZipImpl<>(f.apply(zip.x()), zip.y())));
    }

    @Override
    public <B> ZipStream<X, B> mapY(Function<Y, B> f) {
        return new MergedZipStream<>(stream().map(zip ->
                new ZipImpl<>(zip.x(), f.apply(zip.y()))));
    }

    @Override
    public ZipStream<X, Y> sequential() {
        return new MergedZipStream<>(stream().sequential());
    }

    @Override
    public ZipStream<X, Y> parallel() {
        return new MergedZipStream<>(stream().parallel());
    }

    @Override
    public ZipStream<X, Y> unordered() {
        return new MergedZipStream<>(stream().unordered());
    }

    @Override
    public ZipStream<X, Y> onClose(Runnable closeHandler) {
        return new MergedZipStream<>(stream().onClose(closeHandler));
    }

    protected abstract ZipStream<X, Y> convert();
}
