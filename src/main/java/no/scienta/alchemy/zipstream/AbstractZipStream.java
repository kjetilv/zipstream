package no.scienta.alchemy.zipstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

abstract class AbstractZipStream<X, Y> implements ZipStream<X, Y> {

    @Override
    public ZipStream<X, Y> filter(BiPredicate<X, Y> p) {
        return merged(stream().filter(o -> p.test(o.x(), o.y())));
    }

    @Override
    public ZipStream<X, Y> filterX(Predicate<X> p) {
        return merged(stream().filter(o -> p.test(o.x())));
    }

    @Override
    public ZipStream<X, Y> filterY(Predicate<Y> p) {
        return merged(stream().filter(o -> p.test(o.y())));
    }

    @Override
    public IntStream mapToInt(BiFunction<X, Y, Integer> f) {
        return stream().mapToInt(o -> f.apply(o.x(), o.y()));
    }

    @Override
    public LongStream mapToLong(BiFunction<X, Y, Long> f) {
        return stream().mapToLong(o -> f.apply(o.x(), o.y()));
    }

    @Override
    public DoubleStream mapToDouble(BiFunction<X, Y, Double> f) {
        return stream().mapToDouble(o -> f.apply(o.x(), o.y()));
    }

    @Override
    public Map<X, Y> toMap() {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y));
    }

    @Override
    public Map<X, Y> toMap(BinaryOperator<Y> merge) {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y, merge));
    }

    @Override
    public Map<X, Y> toMap(BinaryOperator<Y> merge, Supplier<Map<X, Y>> map) {
        return stream().collect(Collectors.toMap(Zip::x, Zip::y, merge, map));
    }

    @Override
    public void forEach(BiConsumer<X, Y> op) {
        stream().forEach(o -> op.accept(o.x(), o.y()));
    }

    @Override
    public void forEachX(Consumer<X> op) {
        toX().forEach(op);
    }

    @Override
    public void forEachY(Consumer<Y> op) {
        toY().forEach(op);
    }

    @Override
    public <R> Stream<R> map(BiFunction<X, Y, R> f) {
        return stream().map(o -> f.apply(o.x(), o.y()));
    }

    @Override
    public <R> Stream<R> flatMap(BiFunction<X, Y, Stream<R>> f) {
        return stream().flatMap(o -> f.apply(o.x(), o.y()));
    }

    @Override
    public <R> R reduce(R r, Reducer<R, X, Y> fun, BinaryOperator<R> combiner) {
        return stream().reduce(r, (acc, o) -> fun.apply(acc, o.x(), o.y()), combiner);
    }

    @Override
    public <R> R reduceX(R r, BiFunction<R, X, R> fun, BinaryOperator<R> combiner) {
        return toX().reduce(r, fun, combiner);
    }

    @Override
    public <R> R reduceY(R r, BiFunction<R, Y, R> fun, BinaryOperator<R> combiner) {
        return toY().reduce(r, fun, combiner);
    }

    @Override
    public <R, A> R collectX(Collector<X, A, R> collector) {
        return toX().collect(collector);
    }

    @Override
    public <R, A> R collectY(Collector<Y, A, R> collector) {
        return toY().collect(collector);
    }

    @Override
    public boolean anyMatch(BiPredicate<X, Y> p) {
        return stream().anyMatch(o -> p.test(o.x(), o.y()));
    }

    @Override
    public boolean anyMatchX(Predicate<X> p) {
        return toX().anyMatch(p);
    }

    @Override
    public boolean anyMatchY(Predicate<Y> p) {
        return toY().anyMatch(p);
    }

    @Override
    public boolean allMatch(BiPredicate<X, Y> p) {
        return stream().allMatch(o -> p.test(o.x(), o.y()));
    }

    @Override
    public boolean allMatchX(Predicate<X> p) {
        return toX().allMatch(p);
    }

    @Override
    public boolean allMatchY(Predicate<Y> p) {
        return toY().allMatch(p);
    }

    @Override
    public MergedZipStream<X, Y> sequential() {
        return merged(stream().sequential());
    }

    @Override
    public MergedZipStream<X, Y> parallel() {
        return merged(stream().parallel());
    }

    @Override
    public MergedZipStream<X, Y> unordered() {
        Stream<Zip<X, Y>> unordered = stream().unordered();
        return merged(unordered);
    }

    @Override
    public MergedZipStream<X, Y> onClose(Runnable closeHandler) {
        return merged(stream().onClose(closeHandler));
    }

    @Override
    public Iterator<Zip<X, Y>> iterator() {
        return stream().iterator();
    }

    @Override
    public void close() {
        stream().close();
    }

    @Override
    public Spliterator<Zip<X, Y>> spliterator() {
        return stream().spliterator();
    }

    protected abstract ZipStream<X, Y> convert();

    protected abstract Stream<Zip<X, Y>> stream();

    MergedZipStream<X, Y> merged(Stream<Zip<X, Y>> unordered) {
        return new MergedZipStream<>(unordered);
    }
}
