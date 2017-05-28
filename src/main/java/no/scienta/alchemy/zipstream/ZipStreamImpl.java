package no.scienta.alchemy.zipstream;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static no.scienta.alchemy.zipstream.ZipStream.from;

final class ZipStreamImpl<X, Y> implements ZipStream<X, Y> {

    private final AtomicReference<Stream<X>> xs;

    private final AtomicReference<Stream<Y>> ys;

    private final AtomicReference<Stream<Zip<X, Y>>> stream;

    private ZipStreamImpl(Stream<X> xs, Stream<Y> ys, Stream<Zip<X, Y>> stream) {
        this.xs = new AtomicReference<>(xs);
        this.ys = new AtomicReference<>(ys);
        this.stream = new AtomicReference<>(stream);
    }

    @Override
    public Stream<X> toX() {
        return xs.updateAndGet(xs -> {
            if (xs != null) {
                return xs;
            }
            List<Zip<X, Y>> collected = collectZips();
            return set(this.ys, ys(collected), xs(collected));
        });
    }

    @Override
    public Stream<Y> toY() {
        return ys.updateAndGet(ys -> {
            if (ys != null) {
                return ys;
            }
            List<Zip<X, Y>> collected = collectZips();
            return set(this.xs, xs(collected), ys(collected));
        });
    }

    @Override
    public Stream<Zip<X, Y>> stream() {
        return stream.updateAndGet(stream -> {
            if (stream == null) {
                return ZipStreamImpl.lazyZip(xs.get(), ys.get());
            }
            return stream;
        });
    }

    @Override
    public <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b) {
        return from(toX().flatMap(x2a), toY().flatMap(y2b));
    }

    @Override
    public <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a) {
        return from(toX().flatMap(x2a), toY());
    }

    @Override
    public <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b) {
        return from(toX(), toY().flatMap(y2b));
    }

    @Override
    public long count() {
        return Math.min(toX().count(), toY().count());
    }

    @Override
    public ZipStream<X, Y> limit(long limit) {
        return from(toX().limit(limit), toY().limit(limit));
    }

    @Override
    public ZipStream<Y, X> flip() {
        return zip(toY(), toX());
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

    private Stream<Y> ys(List<Zip<X, Y>> zips) {
        return zips.stream().map(Zip::y);
    }

    private Stream<X> xs(List<Zip<X, Y>> zips) {
        return zips.stream().map(Zip::x);
    }

    private List<Zip<X, Y>> collectZips() {
        return stream().collect(Collectors.toList());
    }

    static <A, B> ZipStream<A, B> zip(Stream<A> as, Stream<B> bs) {
        return new ZipStreamImpl<>(as == null ? Stream.empty() : as, bs == null ? Stream.empty() : bs, null);
    }

    static <A, B> ZipStream<A, B> zip(Stream<Zip<A, B>> stream) {
        return new ZipStreamImpl<>(null, null, stream == null ? Stream.empty() : stream);
    }

    static <A, B> Zip<A, B> zip(A a, B b) {
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

    private static <A, B> Stream<A> set(AtomicReference<Stream<B>> that, Stream<B> those, Stream<A> these) {
        that.set(those);
        return these;
    }

    private static <X, Y> Stream<Zip<X, Y>> lazyZip(Stream<X> xs, Stream<Y> ys) {
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
                return zip(xi.next(), yi.next());
            }
        };
    }
}
