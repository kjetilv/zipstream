package no.scienta.alchemy.zipstream;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class ZipStreamImpl<X, Y> implements ZipStream<X, Y> {

    private final Stream<Zip<X, Y>> stream;

    ZipStreamImpl(Stream<Zip<X, Y>> stream) {
        this.stream = stream == null ? Stream.empty() : stream;
    }

    @Override
    public Stream<Zip<X, Y>> stream() {
        return stream;
    }

    @Override
    public ZipStream<Y, X> flip() {
        return ZipStreamImpl.zip(toY(), toX());
    }

    @Override
    public ZipStream<X, Y> filter(BiPredicate<X, Y> p) {
        return zip(stream().filter(o -> p.test(o.x(), o.y())));
    }

    @Override
    public ZipStream<X, Y> filterX(Predicate<X> p) {
        return zip(stream().filter(o -> p.test(o.x())));
    }

    @Override
    public ZipStream<X, Y> filterY(Predicate<Y> p) {
        return zip(stream().filter(o -> p.test(o.y())));
    }

    @Override
    public <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b) {
        return zip(stream().map(o -> ZipStreamImpl.zip(x2a.apply(o.x()), y2b.apply(o.y()))));
    }

    @Override
    public <A> ZipStream<A, Y> mapX(Function<X, A> f) {
        return zip(stream().map(o -> ZipStreamImpl.zip(f.apply(o.x()), o.y())));
    }

    @Override
    public <B> ZipStream<X, B> mapY(Function<Y, B> f) {
        return zip(stream().map(o -> ZipStreamImpl.zip(o.x(), f.apply(o.y()))));
    }

    @Override
    public ZipStream<X, Y> sequential() {
        return zip(stream().sequential());
    }

    @Override
    public ZipStream<X, Y> parallel() {
        return zip(stream().parallel());
    }

    @Override
    public ZipStream<X, Y> unordered() {
        return zip(stream().unordered());
    }

    @Override
    public ZipStream<X, Y> onClose(Runnable closeHandler) {
        return zip(stream().onClose(closeHandler));
    }

    static class ZipImpl<X, Y> implements Zip<X, Y> {

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

    static <A, B> ZipStream<A, B> zip(Stream<A> as, Stream<B> bs) {
        return zip(stream(as, bs, ZipStreamImpl::zip));
    }

    static <A, B> ZipStream<A, B> zip(Stream<Zip<A, B>> stream) {
        return new ZipStreamImpl<>(stream);
    }

    static <A, B> Zip<A, B> zip(A a, B b) {
        return new ZipImpl<>(a, b);
    }

    private static <X, Y, T> Stream<T> stream(Stream<X> xs, Stream<Y> ys, BiFunction<X, Y, T> f) {
        Iterable<T> i = iterable(xs.iterator(), ys.iterator(), f);
        return StreamSupport.stream(i.spliterator(),
                xs.isParallel() || ys.isParallel());
    }

    private static <X, Y, T> Iterable<T> iterable(Iterator<X> xi, Iterator<Y> yi, BiFunction<X, Y, T> f) {
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return xi.hasNext() && yi.hasNext();
            }

            @Override
            public T next() {
                return f.apply(xi.next(), yi.next());
            }
        };
    }
}
