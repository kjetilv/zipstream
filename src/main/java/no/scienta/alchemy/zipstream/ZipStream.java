package no.scienta.alchemy.zipstream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("SameParameterValue")
public interface ZipStream<X, Y> extends BaseStream<ZipStream.Zip<X, Y>, ZipStream<X, Y>> {

    static <X, Y> ZipStream<X, Y> from(Stream<X> xs, Stream<Y> ys) {
        return new UnmergedZipStream<>(xs, ys);
    }

    static <X, Y> ZipStream<X, Y> from(Stream<X> xs, Function<X, Y> f) {
        Stream<Zip<X, Y>> stream = xs.map(x -> new ZipImpl<>(x, f.apply(x)));
        return new MergedZipStream<>(stream);
    }

    static <X, Y> ZipStream<X, Y> from(Map<X, Y> map) {
        Stream<Zip<X, Y>> stream = map.entrySet().stream().map(e -> new ZipImpl<>(e.getKey(), e.getValue()));
        return new MergedZipStream<>(stream);
    }

    static <X, Y> ZipStream<X, Y> fromEntries(Collection<Map.Entry<X, Y>> es) {
        Stream<Zip<X, Y>> stream = es.stream().map(e -> new ZipImpl<>(e.getKey(), e.getValue()));
        return new MergedZipStream<>(stream);
    }

    static <X, Y> ZipStream<X, Y> fromEntries(Stream<Map.Entry<X, Y>> es) {
        Stream<Zip<X, Y>> stream = es.map(e -> new ZipImpl<>(e.getKey(), e.getValue()));
        return new MergedZipStream<>(stream);
    }

    static <Y> ZipStream<Long, Y> withIndexes(Stream<Y> ys) {
        AtomicLong al = new AtomicLong();
        Stream<Zip<Long, Y>> stream = ys.map(y -> new ZipImpl<>(al.getAndIncrement(), y));
        return new MergedZipStream<>(stream);
    }

    ZipStream<Y, X> flip();

    default ZipStream<X, Y> filter(BiPredicate<X, Y> p) {
        Stream<Zip<X, Y>> stream = stream().filter(o -> p.test(o.x(), o.y()));
        return new MergedZipStream<>(stream);
    }

    default ZipStream<X, Y> filterX(Predicate<X> p) {
        Stream<Zip<X, Y>> stream = stream().filter(o -> p.test(o.x()));
        return new MergedZipStream<>(stream);
    }

    default ZipStream<X, Y> filterY(Predicate<Y> p) {
        Stream<Zip<X, Y>> stream = stream().filter(o -> p.test(o.y()));
        return new MergedZipStream<>(stream);
    }

    <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b);

    <A> ZipStream<A, Y> mapX(Function<X, A> f);

    <B> ZipStream<X, B> mapY(Function<Y, B> f);

    Stream<Zip<X, Y>> stream();

    <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b);

    <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a);

    <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b);

    long count();

    ZipStream<X, Y> limit(long limit);

    Stream<X> toX();

    Stream<Y> toY();

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
        toX().forEach(op);
    }

    default void forEachY(Consumer<Y> op) {
        toY().forEach(op);
    }

    default <R> Stream<R> map(BiFunction<X, Y, R> f) {
        return stream().map(o -> f.apply(o.x(), o.y()));
    }

    default <R> Stream<R> flatMap(BiFunction<X, Y, Stream<R>> f) {
        return stream().flatMap(o -> f.apply(o.x(), o.y()));
    }

    default <R> R reduce(R r, Reducer<R, X, Y> fun, BinaryOperator<R> combiner) {
        return stream().reduce(r, (acc, o) -> fun.apply(acc, o.x(), o.y()), combiner);
    }

    default <R> R reduceX(R r, BiFunction<R, X, R> fun, BinaryOperator<R> combiner) {
        return toX().reduce(r, fun, combiner);
    }

    default <R> R reduceY(R r, BiFunction<R, Y, R> fun, BinaryOperator<R> combiner) {
        return toY().reduce(r, fun, combiner);
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

    default boolean anyMatchX(Predicate<X> p) {
        return toX().anyMatch(p);
    }

    default boolean anyMatchY(Predicate<Y> p) {
        return toY().anyMatch(p);
    }

    default boolean allMatch(BiPredicate<X, Y> p) {
        return stream().allMatch(o -> p.test(o.x(), o.y()));
    }

    default boolean allMatchX(Predicate<X> p) {
        return toX().allMatch(p);
    }

    default boolean allMatchY(Predicate<Y> p) {
        return toY().allMatch(p);
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
