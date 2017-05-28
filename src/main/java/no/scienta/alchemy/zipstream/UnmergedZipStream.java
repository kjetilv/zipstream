package no.scienta.alchemy.zipstream;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class UnmergedZipStream<X, Y> extends AbstractZipStream<X, Y> {

    private final Stream<X> xs;

    private final Stream<Y> ys;

    UnmergedZipStream(Stream<X> xs, Stream<Y> ys) {
        this.xs = xs == null ? Stream.empty() : xs;
        this.ys = ys == null ? Stream.empty() : ys;
    }

    @Override
    public Stream<X> toX() {
        return xs;
    }

    @Override
    public Stream<Y> toY() {
        return ys;
    }

    @Override
    public Stream<Zip<X, Y>> stream() {
        return convert().stream();
    }

    @Override
    public <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b) {
        return new UnmergedZipStream<>(xs.flatMap(x2a), ys.flatMap(y2b));
    }

    @Override
    public <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a) {
        return new UnmergedZipStream<>(xs.flatMap(x2a), ys);
    }

    @Override
    public <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b) {
        return new UnmergedZipStream<>(xs, ys.flatMap(y2b));
    }

    @Override
    public long count() {
        return Math.min(xs.count(), ys.count());
    }

    @Override
    public ZipStream<X, Y> limit(long limit) {
        return new UnmergedZipStream<>(xs.limit(limit), ys.limit(limit));
    }

    @Override
    public ZipStream<Y, X> flip() {
        return new UnmergedZipStream<>(ys, xs);
    }

    @Override
    protected ZipStream<X, Y> convert() {
        return new MergedZipStream<>(combineWithZip(xs, ys));
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
                return new ZipImpl<>(xi.next(), yi.next());
            }
        };
    }
}
