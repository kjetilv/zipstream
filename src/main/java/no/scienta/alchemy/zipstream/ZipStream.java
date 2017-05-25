package no.scienta.alchemy.zipstream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ZipStream<X, Y> extends BaseStream<ZipStream.Zip<X, Y>, ZipStream<X, Y>> {

    static <X, Y> ZipStream<X, Y> from(Stream<X> xs, Stream<Y> ys) {
        return ZipStreamImpl.zip(xs, ys);
    }

    static <X, Y> ZipStream<X, Y> from(Stream<X> xs, Function<X, Y> f) {
        return ZipStreamImpl.zip(xs.map(x -> ZipStreamImpl.zip(x, f.apply(x))));
    }

    static <X, Y> ZipStream<X, Y> from(Map<X, Y> map) {
        return ZipStreamImpl.zip(map.entrySet().stream().map(e -> ZipStreamImpl.zip(e.getKey(), e.getValue())));
    }

    static <X, Y> ZipStream<X, Y> fromEntries(Collection<Map.Entry<X, Y>> es) {
        return ZipStreamImpl.zip(es.stream().map(e -> ZipStreamImpl.zip(e.getKey(), e.getValue())));
    }

    static <X, Y> ZipStream<X, Y> fromEntries(Stream<Map.Entry<X, Y>> es) {
        return ZipStreamImpl.zip(es.map(e -> ZipStreamImpl.zip(e.getKey(), e.getValue())));
    }

    static <Y> ZipStream<Long, Y> withIndexes(Stream<Y> ys) {
        AtomicLong al = new AtomicLong();
        return ZipStreamImpl.zip(ys.map(y -> ZipStreamImpl.zip(al.getAndIncrement(), y)));
    }

    ZipStream<Y, X> flip();

    ZipStream<X, Y> filter(BiPredicate<X, Y> p);

    ZipStream<X, Y> filterX(Predicate<X> p);

    ZipStream<X, Y> filterY(Predicate<Y> p);

    <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b);

    <A> ZipStream<A, Y> mapX(Function<X, A> f);

    <B> ZipStream<X, B> mapY(Function<Y, B> f);

    Stream<Zip<X, Y>> stream();

    default <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b) {
        return from(toX().flatMap(x2a), toY().flatMap(y2b));
    }

    default <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a) {
        return from(toX().flatMap(x2a), toY());
    }

    default <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b) {
        return ZipStreamImpl.zip(toX(), toY().flatMap(y2b));
    }

    default IntStream mapToInt(BiFunction<X, Y, Integer> f) {
        return stream().mapToInt(o -> f.apply(o.x(), o.y()));
    }

    default LongStream mapToLong(BiFunction<X, Y, Long> f) {
        return stream().mapToLong(o -> f.apply(o.x(), o.y()));
    }

    default DoubleStream mapToDouble(BiFunction<X, Y, Double> f) {
        return stream().mapToDouble(o -> f.apply(o.x(), o.y()));
    }

    default Map<X, Y> toMap() {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y));
    }

    default Map<X, Y> toMap(BinaryOperator<Y> merge) {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y, merge));
    }

    default Map<X, Y> toMap(BinaryOperator<Y> merge, Supplier<Map<X, Y>> map) {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y, merge, map));
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
        return stream().map(Zip::x);
    }

    default Stream<Y> toY() {
        return stream().map(Zip::y);
    }

    default <R> R reduce(R r, Reducer<R, X, Y> fun, BinaryOperator<R> combiner) {
        return stream().reduce(r, (acc, o) -> fun.apply(acc, o.x(), o.y()), combiner);
    }

    default <R, A> R collect(Collector<Zip<X, Y>, A, R> collector) {
        return stream().collect(collector);
    }

    default <R, A> R collectX(Collector<X, A, R> collector) {
        return toX().collect(collector);
    }

    default <R, A> R collectY(Collector<Y, A, R> collector) {
        return toY().collect(collector);
    }

    default <R> R collect(Supplier<R> supplier,
                          BiConsumer<R, Zip<X, Y>> accumulator,
                          BiConsumer<R, R> combiner) {
        return stream().collect(supplier, accumulator, combiner);
    }

    default boolean anyMatch(BiPredicate<X, Y> p) {
        return stream().anyMatch(o -> p.test(o.x(), o.y()));
    }

    default boolean allMatch(BiPredicate<X, Y> p) {
        return stream().allMatch(o -> p.test(o.x(), o.y()));
    }

    default long count() {
        return Math.min(toX().count(), toY().count());
    }

    default ZipStream<X, Y> limit(long limit) {
        return ZipStream.from(toX().limit(limit), toY().limit(limit));
    }

    @Override
    default boolean isParallel() {
        return stream().isParallel();
    }

    @Override
    default Iterator<Zip<X, Y>> iterator() {
        return stream().iterator();
    }

    @Override
    default void close() {
        stream().close();
    }

    @Override
    default Spliterator<Zip<X, Y>> spliterator() {
        return stream().spliterator();
    }

    interface Reducer<R, X, Y> {

        R apply(R t, X a, Y b);
    }

    interface Zip<X, Y> {

        X x();

        Y y();
    }
}
