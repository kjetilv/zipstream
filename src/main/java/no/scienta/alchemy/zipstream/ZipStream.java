package no.scienta.alchemy.zipstream;

import java.util.Comparator;
import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings({"SameParameterValue", "unused"})
public interface ZipStream<X, Y> extends BaseStream<Zip<X, Y>, ZipStream<X, Y>> {

    ZipStream<Y, X> flip();

    ZipStream<X, Y> filter(BiPredicate<X, Y> p);

    ZipStream<X, Y> filterX(Predicate<X> p);

    ZipStream<X, Y> filterY(Predicate<Y> p);

    <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b);

    <A> ZipStream<A, Y> mapX(Function<X, A> f);

    <B> ZipStream<X, B> mapY(Function<Y, B> f);

    <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b);

    <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a);

    <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b);

    long count();

    ZipStream<X, Y> limit(long limit);

    ZipStream<X, Y> sortedX();

    ZipStream<X, Y> sortedX(Comparator<X> comparator);

    ZipStream<X, Y> sortedY();

    ZipStream<X, Y> sortedY(Comparator<Y> comparator);

    Stream<X> toX();

    Stream<Y> toY();

    IntStream mapToInt(BiFunction<X, Y, Integer> f);

    LongStream mapToLong(BiFunction<X, Y, Long> f);

    DoubleStream mapToDouble(BiFunction<X, Y, Double> f);

    Map<X, Y> toMap();

    Map<X, Y> toMap(BinaryOperator<Y> merge);

    Map<X, Y> toMap(BinaryOperator<Y> merge, Supplier<Map<X, Y>> map);

    void forEach(BiConsumer<X, Y> op);

    void forEachX(Consumer<X> op);

    void forEachY(Consumer<Y> op);

    <R> Stream<R> map(BiFunction<X, Y, R> f);

    <R> Stream<R> flatMap(BiFunction<X, Y, Stream<R>> f);

    <R> R reduce(R r, Reducer<R, X, Y> fun, BinaryOperator<R> combiner);

    <R> R reduceX(R r, BiFunction<R, X, R> fun, BinaryOperator<R> combiner);

    <R> R reduceY(R r, BiFunction<R, Y, R> fun, BinaryOperator<R> combiner);

    <R, A> R collectX(Collector<X, A, R> collector);

    <R, A> R collectY(Collector<Y, A, R> collector);

    boolean anyMatch(BiPredicate<X, Y> p);

    boolean anyMatchX(Predicate<X> p);

    boolean anyMatchY(Predicate<Y> p);

    boolean allMatch(BiPredicate<X, Y> p);

    boolean allMatchX(Predicate<X> p);

    boolean allMatchY(Predicate<Y> p);

    @FunctionalInterface
    interface Reducer<R, X, Y> {

        R apply(R t, X a, Y b);
    }
}
