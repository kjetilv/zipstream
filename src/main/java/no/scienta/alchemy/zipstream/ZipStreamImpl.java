package no.scienta.alchemy.zipstream;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static no.scienta.alchemy.zipstream.ZipStream.from;

final class ZipStreamImpl<X, Y> implements ZipStream<X, Y> {

    private final Stream<X> xs;

    private final Stream<Y> ys;

    private final Stream<Zip<X, Y>> stream;

    private ZipStreamImpl(Stream<X> xs, Stream<Y> ys, Stream<Zip<X, Y>> stream) {
        this.xs = xs;
        this.ys = ys;
        this.stream = stream;
    }

    @Override
    public Stream<X> toX() {
        return xs == null ? stream.map(Zip::x) : xs;
    }

    @Override
    public Stream<Y> toY() {
        return ys == null ? stream.map(Zip::y) : ys;
    }

    @Override
    public Stream<Zip<X, Y>> stream() {
        return unmerged() ? ZipStreamImpl.combineWithZip(xs, ys) : stream;
    }

    @Override
    public <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b) {
        return unmerged()
                ? newZipStream(xs.flatMap(x2a), ys.flatMap(y2b))
                : convert().flatMap(x2a, y2b);
    }

    private boolean unmerged() {
        return stream == null;
    }

    @Override
    public <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a) {
        return unmerged() ? newZipStream(xs.flatMap(x2a), ys) : convert().flatMapX(x2a);
    }

    @Override
    public <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b) {
        return unmerged() ? newZipStream(xs, ys.flatMap(y2b)) : convert().flatMapY(y2b);
    }

    @Override
    public long count() {
        return unmerged() ? Math.min(xs.count(), ys.count()) : convert().count();
    }

    @Override
    public ZipStream<X, Y> limit(long limit) {
        return unmerged() ? newZipStream(xs.limit(limit), ys.limit(limit)) : convert().limit(limit);
    }

    @Override
    public ZipStream<Y, X> flip() {
        return unmerged() ? newZipStream(ys, xs) : convert().flip();
    }

    @Override
    public <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b) {
        return newZipStream(stream().map(o -> ZipStreamImpl.newZip(x2a.apply(o.x()), y2b.apply(o.y()))));
    }

    @Override
    public <A> ZipStream<A, Y> mapX(Function<X, A> f) {
        return newZipStream(stream().map(o -> ZipStreamImpl.newZip(f.apply(o.x()), o.y())));
    }

    @Override
    public <B> ZipStream<X, B> mapY(Function<Y, B> f) {
        return newZipStream(stream().map(o -> ZipStreamImpl.newZip(o.x(), f.apply(o.y()))));
    }

    @Override
    public ZipStream<X, Y> sequential() {
        return newZipStream(stream().sequential());
    }

    @Override
    public ZipStream<X, Y> parallel() {
        return newZipStream(stream().parallel());
    }

    @Override
    public ZipStream<X, Y> unordered() {
        return newZipStream(stream().unordered());
    }

    @Override
    public ZipStream<X, Y> onClose(Runnable closeHandler) {
        return newZipStream(stream().onClose(closeHandler));
    }

    private ZipStream<X, Y> convert() {
        if (unmerged()) {
            return newZipStream(combineWithZip(xs, ys));
        }
        List<Zip<X, Y>> collect = stream.collect(Collectors.toList());
        return newZipStream(collect.stream().map(Zip::x), collect.stream().map(Zip::y));
    }

    static <A, B> ZipStream<A, B> newZipStream(Stream<A> as, Stream<B> bs) {
        return new ZipStreamImpl<>(
                as == null ? Stream.empty() : as,
                bs == null ? Stream.empty() : bs,
                null);
    }

    static <A, B> ZipStream<A, B> newZipStream(Stream<Zip<A, B>> stream) {
        return new ZipStreamImpl<>(
                null,
                null,
                stream == null ? Stream.empty() : stream);
    }

    static <A, B> Zip<A, B> newZip(A a, B b) {
        return new ZipImpl<>(a, b);
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

    private static <X, Y> Stream<Zip<X, Y>> combineWithZip(Stream<X> xs, Stream<Y> ys) {
        Iterable<Zip<X, Y>> i = iterable(xs.iterator(), ys.iterator());
        return StreamSupport.stream(i.spliterator(),
                xs.isParallel() || ys.isParallel());
    }

    private static <X, Y> Iterable<Zip<X, Y>> iterable(Iterator<X> xi, Iterator<Y> yi) {
        return () -> new Iterator<Zip<X, Y>>() {
            @Override
            public boolean hasNext() {
                return xi.hasNext() && yi.hasNext();
            }

            @Override
            public Zip<X, Y> next() {
                return newZip(xi.next(), yi.next());
            }
        };
    }
}
