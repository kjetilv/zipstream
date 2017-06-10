package no.scienta.alchemy.zipstream;

import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings({"SameParameterValue", "unused"})
public interface ZipStream<X, Y> extends BaseStream<Zip<X, Y>, ZipStream<X, Y>> {

    /**
     * @return X stream
     */
    Stream<X> toX();

    /**
     * @return Y stream
     */
    Stream<Y> toY();

    /**
     * @return Inverted stream
     */
    ZipStream<Y, X> flip();

    /**
     * @param p Predicate
     * @return Filtered stream
     */
    ZipStream<X, Y> filter(BiPredicate<X, Y> p);

    /**
     * @param p Predicate
     * @return Filtered stream
     */
    ZipStream<X, Y> filterX(Predicate<X> p);

    /**
     * @param p Predicate
     * @return Filtered stream
     */
    ZipStream<X, Y> filterY(Predicate<Y> p);

    /**
     * @param x2a X mapper
     * @param y2b Y mapper
     * @param <A> X target type
     * @param <B> Y target type
     * @return Mapped stream
     */
    <A, B> ZipStream<A, B> map(Function<X, A> x2a, Function<Y, B> y2b);

    /**
     * @param f Mapper
     * @param <A> Target type
     * @return Mapped stream
     */
    <A> ZipStream<A, Y> mapX(Function<X, A> f);

    /**
     * @param f Mapper
     * @param <B> Target type
     * @return Mapped stream
     */
    <B> ZipStream<X, B> mapY(Function<Y, B> f);

    /**
     * @param x2a X mapper
     * @param y2b Y mapper
     * @param <A> X target type
     * @param <B> Y target type
     * @return Map {@link ZipStream}
     */
    <A, B> ZipStream<A, B> flatMap(Function<X, Stream<A>> x2a, Function<Y, Stream<B>> y2b);

    /**
     * @param x2a X mapper
     * @param <A> X target type
     * @return Mapped stream
     */
    <A> ZipStream<A, Y> flatMapX(Function<X, Stream<A>> x2a);

    /**
     * @param y2b Y mapper
     * @param <B> Y target type
     * @return Mapped stream
     */
    <B> ZipStream<X, B> flatMapY(Function<Y, Stream<B>> y2b);

    /**
     * @return Count
     */
    long count();

    /**
     * @param limit Limit
     * @return Limited stream
     */
    ZipStream<X, Y> limit(long limit);

    /**
     * @param comparator A big hand for our very own BiComparator! And they said it couldn't be done!
     * @return Sorted stream
     */
    ZipStream<X, Y> sorted(BiComparator<X, Y> comparator);

    /**
     * @return Sorted stream, assuming the X type is a {@link Comparable}!
     */
    ZipStream<X, Y> sortedX();

    /**
     * @param comparator Comparator
     * @return Stream sorted by X
     */
    ZipStream<X, Y> sortedX(java.util.Comparator<X> comparator);

    /**
     * @return Sorted stream, assuming the Y type is a {@link Comparable}!
     */
    ZipStream<X, Y> sortedY();

    /**
     * @param comparator Comparator
     * @return Stream sorted by Y
     */
    ZipStream<X, Y> sortedY(java.util.Comparator<Y> comparator);

    /**
     * @param f Mapper
     * @return IntStream
     */
    IntStream mapToInt(BiFunction<X, Y, Integer> f);

    /**
     * @param f Mapper
     * @return IntStream
     */
    IntStream flatMapToInt(BiFunction<X, Y, IntStream> f);

    /**
     * @param f Mapper
     * @return LongStream
     */
    LongStream mapToLong(BiFunction<X, Y, Long> f);

    /**
     * @param f Mapper
     * @return LongStream
     */
    LongStream flatMapToLong(BiFunction<X, Y, LongStream> f);

    /**
     * @param f Mapper
     * @return DoubleStream (which does sound like a ZipStream of a sort but it is nothing of the kind!)
     */
    DoubleStream mapToDouble(BiFunction<X, Y, Double> f);

    /**
     * @param f Mapper
     * @return DoubleStream (which does sound like a ZipStream of a sort but it is nothing of the kind!)
     */
    DoubleStream flatMapToDouble(BiFunction<X, Y, DoubleStream> f);

    /**
     * @return A map with X's as keys and Y's as values
     */
    Map<X, Y> toMap();

    Map<X, Y> toMap(BinaryOperator<Y> merge);

    Map<X, Y> toMap(BinaryOperator<Y> merge, Supplier<Map<X, Y>> map);

    /**
     * @param op Operator
     */
    void forEach(BiConsumer<X, Y> op);

    /**
     * @param f Pair mapper
     * @param <R> Target type
     * @return Target type stream
     */
    <R> Stream<R> map(BiFunction<X, Y, R> f);

    /**
     * @param f Pair mapper
     * @param <R> Target type
     * @return Target type stream
     */
    <R> Stream<R> flatMap(BiFunction<X, Y, Stream<R>> f);

    /**
     * @param r Starting value
     * @param fun Our very own BiReducer! And they said it couldn't be done!
     * @param combiner A combiner
     * @param <R> Target type
     * @return Reduced value
     */
    <R> R reduce(R r, BiReducer<R, X, Y> fun, BinaryOperator<R> combiner);

    /**
     * @param p Predicate
     * @return True iff any match
     */
    boolean anyMatch(BiPredicate<X, Y> p);

    /**
     * @param p Predicate
     * @return True iff all match
     */
    boolean allMatch(BiPredicate<X, Y> p);

    @FunctionalInterface
    interface BiReducer<R, X, Y> {

        R apply(R t, X a, Y b);
    }

    /**
     * @param <X> X type
     * @param <Y> Y type
     */
    @FunctionalInterface
    interface BiComparator<X, Y> {

        /**
         * @param x1 X1
         * @param y1 Y1
         * @param x2 X2
         * @param y2 Y2
         * @return Standard comparator value, only based on two pairs
         */
        int compare(X x1, Y y1, X x2, Y y2);
    }
}
