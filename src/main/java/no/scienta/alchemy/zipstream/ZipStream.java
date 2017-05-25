package no.scienta.alchemy.zipstream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ZipStream<X, Y> extends BaseStream<ZipStream.Of<X, Y>, ZipStream<X, Y>> {

    static <X, Y> ZipStream<X, Y> of(Stream<X> xs, Stream<Y> ys) {
        return new ZipStreamImpl<>(xs, ys);
    }

    static <X, Y> ZipStream<X, Y> of(Stream<X> xs, Function<X, Y> f) {
        return new ZipStreamImpl<>(xs.map(x -> new OfImpl<>(x, f.apply(x))));
    }

    static <X, Y> ZipStream<X, Y> of(Map<X, Y> map) {
        return new ZipStreamImpl<>(map.entrySet().stream().map(e -> new OfImpl<>(e.getKey(), e.getValue())));
    }

    static <X, Y> ZipStream<X, Y> ofEntries(Collection<Map.Entry<X, Y>> entries) {
        return new ZipStreamImpl<>(entries.stream().map(e -> new OfImpl<>(e.getKey(), e.getValue())));
    }

    static <X, Y> ZipStream<X, Y> ofEntries(Stream<Map.Entry<X, Y>> entries) {
        return new ZipStreamImpl<>(entries.map(e -> new OfImpl<>(e.getKey(), e.getValue())));
    }

    static <Y> ZipStream<Long, Y> ofIndexed(Stream<Y> ys) {
        AtomicLong al = new AtomicLong();
        return new ZipStreamImpl<>(ys.map(y -> new OfImpl<>(al.getAndIncrement(), y)));
    }

    ZipStream<Y, X> flip();

    ZipStream<X, Y> filter(BiPredicate<X, Y> p);

    ZipStream<X, Y> filterX(Predicate<X> p);

    ZipStream<X, Y> filterY(Predicate<Y> p);

    <A, B> ZipStream<A, B> map(Function<X, A> xa, Function<Y, B> yb);

    <A> ZipStream<A, Y> mapX(Function<X, A> f);

    <B> ZipStream<X, B> mapY(Function<Y, B> f);

    Stream<Of<X, Y>> stream();

    default IntStream mapToInt(BiFunction<X, Y, Integer> f) {
        return stream().mapToInt(o -> f.apply(o.x(), o.y()));
    }

    default LongStream mapToLong(BiFunction<X, Y, Long> f) {
        return stream().mapToLong(o -> f.apply(o.x(), o.y()));
    }

    default DoubleStream mapToDouble(BiFunction<X, Y, Double> f) {
        return stream().mapToDouble(o -> f.apply(o.x(), o.y()));
    }

    default <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> xa, Function<Y, Stream<B>> yb) {
        return of(toX().flatMap(xa), toY().flatMap(yb));
    }

    default <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> xa) {
        return of(toX().flatMap(xa), toY());
    }

    default <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> yb) {
        return new ZipStreamImpl<>(toX(), toY().flatMap(yb));
    }

    default Map<X, Y> toMap() {
        return stream().collect(Collectors.toMap(Of::x, Of::y));
    }

    default Map<X, Y> toMap(BinaryOperator<Y> merge) {
        return stream().collect(Collectors.toMap(Of::x, Of::y, merge));
    }

    default Map<X, Y> toMap(BinaryOperator<Y> merge, Supplier<Map<X, Y>> map) {
        return stream().collect(Collectors.toMap(Of::x, Of::y, merge, map));
    }

    default void forEach(BiConsumer<X, Y> op) {
        stream().forEach(o -> op.accept(o.x(), o.y()));
    }

    default void forEachX(Consumer<X> op) {
        stream().forEach(o -> op.accept(o.x()));
    }

    default void forEachY(Consumer<Y> op) {
        stream().forEach(o -> op.accept(o.y()));
    }

    default <R> Stream<R> map(BiFunction<X, Y, R> f) {
        return stream().map(o -> f.apply(o.x(), o.y()));
    }

    default <R> Stream<R> flatMap(BiFunction<X, Y, Stream<R>> f) {
        return stream().flatMap(o -> f.apply(o.x(), o.y()));
    }

    default Stream<X> toX() {
        return stream().map(Of::x);
    }

    default Stream<Y> toY() {
        return stream().map(Of::y);
    }

    default <R> R reduce(R r, Reducer<R, X, Y> fun, BinaryOperator<R> combiner) {
        return stream().reduce(r, (acc, o) -> fun.apply(acc, o.x(), o.y()), combiner);
    }

    default <R, A> R collect(Collector<Of<X, Y>, A, R> collector) {
        return stream().collect(collector);
    }

    default <R, A> R collectX(Collector<X, A, R> collector) {
        return toX().collect(collector);
    }

    default <R, A> R collectY(Collector<Y, A, R> collector) {
        return toY().collect(collector);
    }

    default <R> R collect(Supplier<R> supplier,
                          BiConsumer<R, Of<X, Y>> accumulator,
                          BiConsumer<R, R> combiner) {
        return stream().collect(supplier, accumulator, combiner);
    }

    @Override
    default boolean isParallel() {
        return stream().isParallel();
    }

    @Override
    default Iterator<Of<X, Y>> iterator() {
        return stream().iterator();
    }

    @Override
    default void close() {
        stream().close();
    }

    @Override
    default Spliterator<Of<X, Y>> spliterator() {
        return stream().spliterator();
    }

    interface Reducer<R, X, Y> {

        R apply(R t, X a, Y b);
    }

    interface Of<X, Y> {

        X x();

        Y y();
    }
}
