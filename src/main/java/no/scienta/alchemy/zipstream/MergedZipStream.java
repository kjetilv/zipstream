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
}
