package no.scienta.alchemy.zipstream;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class MergedZipStream<X, Y> extends AbstractZipStream<X, Y> {

    private final Stream<Zip<X, Y>> stream;

    MergedZipStream(Stream<Zip<X, Y>> stream) {
        this.stream = stream == null ? Stream.empty() : stream;
    }

    @Override
    public Stream<X> toX() {
        return stream.map(Zip::x);
    }

    @Override
    public Stream<Y> toY() {
        return stream.map(Zip::y);
    }

    @Override
    public Stream<Zip<X, Y>> stream() {
        return stream;
    }

    @Override
    public ZipStream<X, Y> sorted(BiComparator<X, Y> comparator) {
        return merged(stream.sorted((o1, o2) -> comparator.compare(o1.x(), o1.y(), o2.x(), o2.y())));
    }

    @Override
    public ZipStream<X, Y> sortedX(java.util.Comparator<X> comparator) {
        return merged(stream.sorted((o1, o2) -> comparator.compare(o1.x(), o2.x())));
    }

    @Override
    public ZipStream<X, Y> sortedY(java.util.Comparator<Y> comparator) {
        return merged(stream.sorted((o1, o2) -> comparator.compare(o1.y(), o2.y())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ZipStream<X, Y> sortedX() {
        return merged(stream.sorted((o1, o2) -> ((Comparable<X>) o1.x()).compareTo(o2.x())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ZipStream<X, Y> sortedY() {
        return merged(stream.sorted((o1, o2) -> ((Comparable<Y>) o1.y()).compareTo(o2.y())));
    }

    @Override
    public <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b) {
        return new MergedZipStream<>(stream.map(zip ->
                new Zip<>(x2a.apply(zip.x()), y2b.apply(zip.y()))));
    }

    @Override
    public <A> ZipStream<A, Y> mapX(Function<X, A> f) {
        return new MergedZipStream<>(stream().map(zip ->
                new Zip<>(f.apply(zip.x()), zip.y())));
    }

    @Override
    public <B> ZipStream<X, B> mapY(Function<Y, B> f) {
        return new MergedZipStream<>(stream().map(zip ->
                new Zip<>(zip.x(), f.apply(zip.y()))));
    }

    @Override
    public <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b) {
        return convert().flatMap(x2a, y2b);
    }

    @Override
    public <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a) {
        return convert().flatMapX(x2a);
    }

    @Override
    public <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b) {
        return convert().flatMapY(y2b);
    }

    @Override
    public long count() {
        return convert().count();
    }

    @Override
    public ZipStream<X, Y> limit(long limit) {
        return convert().limit(limit);
    }

    @Override
    public ZipStream<Y, X> flip() {
        return convert().flip();
    }

    @Override
    protected ZipStream<X, Y> convert() {
        List<Zip<X, Y>> collect = stream.collect(Collectors.toList());
        return new UnmergedZipStream<>(collect.stream().map(Zip::x), collect.stream().map(Zip::y));
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }
}
