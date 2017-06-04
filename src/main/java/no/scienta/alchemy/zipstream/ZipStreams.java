package no.scienta.alchemy.zipstream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public final class ZipStreams {

    public static <X, Y> ZipStream<X, Y> from(Stream<X> xs, Stream<Y> ys) {
        return xs.isParallel() || ys.isParallel()
                ? new MergedZipStream<>(zip(xs, ys))
                : new UnmergedZipStream<>(xs, ys);
    }

    public static <X, Y> ZipStream<X, Y> from(Map<X, Y> map) {
        return fromEntries(map.entrySet().stream());
    }

    public static <X, Y> ZipStream<X, Y> fromEntries(Collection<Map.Entry<X, Y>> es) {
        return fromEntries(es.stream());
    }

    public static <X, Y> ZipStream<X, Y> fromEntries(Stream<Map.Entry<X, Y>> es) {
        return new MergedZipStream<>(es.map(e -> new Zip<>(e.getKey(), e.getValue())));
    }

    public static <X, Y> ZipStream<X, Y> computed(Stream<X> xs, Function<X, Y> f) {
        return new MergedZipStream<>(xs.map(x -> new Zip<>(x, f.apply(x))));
    }

    public static <X, Y> ZipStream<X, Y> computed(Function<Y, X> f, Stream<Y> ys) {
        return new MergedZipStream<>(ys.map(y -> new Zip<>(f.apply(y), y)));
    }

    public static <Y> ZipStream<Long, Y> withIndexes(Stream<Y> ys) {
        AtomicLong al = new AtomicLong();
        return new MergedZipStream<>(ys.map(y -> new Zip<>(al.getAndIncrement(), y)));
    }

    static <X, Y> Stream<Zip<X, Y>> combine(Stream<X> xs, Stream<Y> ys) {
        Iterable<Zip<X, Y>> i = iterable(xs.iterator(), ys.iterator());
        return StreamSupport.stream(i.spliterator(), xs.isParallel() || ys.isParallel());
    }

    private static <X, Y> Iterable<Zip<X, Y>> iterable(Iterator<X> xi, Iterator<Y> yi) {
        return () -> new Iterator<Zip<X, Y>>() {
            @Override
            public boolean hasNext() {
                return xi.hasNext() && yi.hasNext();
            }

            @Override
            public Zip<X, Y> next() {
                return new Zip<>(xi.next(), yi.next());
            }
        };
    }

    private static <X, Y> Stream<Zip<X, Y>> zip(Stream<X> xs, Stream<Y> ys) {
        return combine(xs, ys);
    }

    private ZipStreams() {
    }
}
