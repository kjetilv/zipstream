package no.scienta.alchemy.zipstream;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b) {
        return new UnmergedZipStream<>(xs.map(x2a), ys.map(y2b));
    }

    @Override
    public <A> ZipStream<A, Y> mapX(Function<X, A> f) {
        return new UnmergedZipStream<>(xs.map(f), ys);
    }

    @Override
    public <B> ZipStream<X, B> mapY(Function<Y, B> f) {
        return new UnmergedZipStream<>(xs, ys.map(f));
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
    public ZipStream<X, Y> sortedX() {
        return convert().sortedX();
    }

    @Override
    public ZipStream<X, Y> sortedX(Comparator<X> comparator) {
        return convert().sortedX(comparator);
    }

    @Override
    public ZipStream<X, Y> sortedY() {
        return convert().sortedY();
    }

    @Override
    public ZipStream<X, Y> sortedY(Comparator<Y> comparator) {
        return convert().sortedY(comparator);
    }

    @Override
    public ZipStream<Y, X> flip() {
        return new UnmergedZipStream<>(ys, xs);
    }

    @Override
    protected MergedZipStream<X, Y> convert() {
        return new MergedZipStream<>(ZipStreams.combineWithZip(xs, ys));
    }

    @Override
    public boolean isParallel() {
        return xs.isParallel() || ys.isParallel();
    }
}
