package no.scienta.alchemy.zipstream.test;

import no.scienta.alchemy.zipstream.ZipStream;
import no.scienta.alchemy.zipstream.ZipStreams;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZipStreamTest {

    @Test
    public void testFlatMap() {
        Stream<Character> characterStream = foobar("foo", "bar")
                .flatMap((c1, c2) -> Stream.of(c1, c2));
        assertEquals("fboaor", joinChars(characterStream));
    }

    @Test
    public void testFlatMapFromIndex() {
        Stream<Character> characterStream = indexedBar("bar")
                .flatMap((id, c2) -> Stream.of(c2));
        assertEquals("bar", joinChars(characterStream));
    }

    @Test
    public void testFlatMapXFromIndex() {
        ZipStream<Long, Character> bar = indexedBar("bar")
                .flatMapX(Stream::of);
        assertEquals("0b1a2r", joinZipChars(bar));
    }

    @Test
    public void testFlatMapYFromIndex() {
        ZipStream<Long, Character> bar = indexedBar("bar")
                .flatMapY(Stream::of);
        assertEquals("0b1a2r", joinZipChars(bar));
    }

    @Test
    public void testFlatMap2() {
        ZipStream<Character, Character> map = foobar("foo", "bar").flatMap(Stream::of, Stream::of);
        assertEquals("fboaor", joinZipChars(map));
    }

    @Test
    public void testFlatMapX() {
        ZipStream<Character, Character> map = foobar("foo", "bar").flatMapX(Stream::of);
        assertEquals("fboaor", joinZipChars(map));
    }

    @Test
    public void testFlatMapY() {
        ZipStream<Character, Character> map = foobar("foo", "bar").flatMapY(Stream::of);
        assertEquals("fboaor", joinZipChars(map));
    }

    @Test
    public void testMap() {
        ZipStream<String, String> stream = foobar("foo", "bar").map(c -> c + "X", c -> c + "Y");
        assertEquals("fXbYoXaYoXrY", joinZipChars(stream));
    }

    @SuppressWarnings("RedundantStreamOptionalCall")
    @Test
    public void testMapMerged() {
        ZipStream<Character, Character> flip = ZipStreams.from(chars("foo"), chars("bar"))
                .filter((character, character2) -> true);
        ZipStream<String, String> stream = flip.map(c -> c + "X", c -> c + "Y");
        assertEquals("fXbYoXaYoXrY", joinZipChars(stream));
    }

    @Test
    public void testMapX() {
        ZipStream<String, Character> map = foobar("foo", "bar").mapX(s -> "" + s + s);
        assertEquals("ffbooaoor", joinZipChars(map));
    }

    @Test
    public void testMapY() {
        ZipStream<Character, String> map = foobar("foo", "bar").mapY(s -> "" + s + s);
        assertEquals("fbboaaorr", joinZipChars(map));
    }

    @SuppressWarnings("RedundantStreamOptionalCall")
    @Test
    public void testMapYMerged() {
        ZipStream<Character, String> map = foobar("foo", "bar")
                .filter((c1, c2) -> true)
                .mapY(s -> "" + s + s);
        assertEquals("fbboaaorr", joinZipChars(map));
    }

    @Test
    public void testAllMatch() {
        ZipStream<Character, Character> foobar = foobar("aba", "baa");
        assertTrue(foobar.allMatch((c1, c2) -> c1 == 'a' || c1 == 'b' || c2 == 'a' || c2 == 'b'));
    }

    @Test
    public void testAllMatchX() {
        ZipStream<Character, Character> foobar = foobar("aba", "XYZ");
        assertTrue(foobar.allMatchX(c1 -> c1 == 'a' || c1 == 'b'));
    }

    @Test
    public void testAllMatchY() {
        ZipStream<Character, Character> foobar = foobar("XYZ", "aba");
        assertTrue(foobar.allMatchY(c2 -> c2 == 'a' || c2 == 'b'));
    }

    @Test
    public void testAnyMatch() {
        ZipStream<Character, Character> foobar = foobar("abZa", "baab");
        assertTrue(foobar.anyMatch((c1, c2) -> c1 == 'Z'));
    }

    @Test
    public void testAnyMatchX() {
        ZipStream<Character, Character> foobar = foobar("abZa", "baab");
        assertTrue(foobar.anyMatchX(c1 -> c1 == 'Z'));
    }

    @Test
    public void testAnyMatchY() {
        ZipStream<Character, Character> foobar = foobar("abXa", "baZb");
        assertTrue(foobar.anyMatchY(c2 -> c2 == 'Z'));
    }

    @Test
    public void testFilter() {
        ZipStream<Character, Character> foobar = foobar("aaaab", "bbabb");
        Stream<Character> filtered = foobar.filter(Character::equals).toX();
        assertEquals("ab", joinChars(filtered));
    }

    @Test
    public void testFilterX() {
        ZipStream<Character, Character> foobar = foobar("aaaab", "bbabX");
        ZipStream<Character, Character> filtered = foobar.filterX(c -> c == 'b');
        assertEquals("bX", joinZipChars(filtered));
    }

    @Test
    public void testFilterY() {
        ZipStream<Character, Character> foobar = foobar("XYaZO", "bbabb");
        ZipStream<Character, Character> filtered = foobar.filterY(c -> c == 'b');
        assertEquals("XbYbZbOb", joinZipChars(filtered));
    }

    @Test
    public void testCount() {
        ZipStream<Character, Character> barrr = foobar("foo", "barrr");
        assertEquals(3, barrr.count());
    }

    @Test
    public void testCountFromIndex() {
        ZipStream<Long, Character> barrr = indexedBar("foo");
        assertEquals(3, barrr.count());
    }

    @Test
    public void testLimit() {
        ZipStream<Character, Character> barrr = foobar("foo", "barrr");
        assertEquals("fboa", joinZipChars(barrr.limit(2)));
    }

    @Test
    public void testLimitFromIndex() {
        ZipStream<Long, Character> barrr = indexedBar("fooarr");
        assertEquals("0f1o2o", joinZipChars(barrr.limit(3)));
    }

    @Test
    public void testZip() {
        assertZipped(
                ZipStreams.from(
                        IntStream.range(0, 10).boxed(),
                        chars("ThisIsATest", Integer.MAX_VALUE)),
                "0T1h2i3s4I5s6A7T8e9s");
    }

    @Test
    public void testIndexedZip() {
        assertZipped(ZipStreams.withIndexes(chars("ThisIsATest", 10)),
                "0T1h2i3s4I5s6A7T8e9s");
    }

    @Test
    public void testSideEffects() {
        StringBuilder sb = new StringBuilder();
        foobar("foo", "bar").forEach((c1, c2) -> {
            sb.insert(0, c2);
            sb.insert(0, "-");
            sb.insert(0, c1);
        });

        assertEquals("o-ro-af-b", sb.toString());
    }

    @Test
    public void testSideEffectsX() {
        StringBuilder sb = new StringBuilder();
        foobar("foo", "bar").forEachX(c1 -> {
            sb.insert(0, c1);
            sb.insert(0, "-");
        });

        assertEquals("-o-o-f", sb.toString());
    }

    @Test
    public void testSideEffectsY() {
        StringBuilder sb = new StringBuilder();
        foobar("foo", "bar").forEachY(c2 -> {
            sb.insert(0, c2);
            sb.insert(0, "-");
        });

        assertEquals("-r-a-b", sb.toString());
    }

    @Test
    public void testFlip() {
        String ab = foobar("foo", "bar")
                .map((c1, c2) -> "" + c1 + c2)
                .collect(Collectors.joining());
        assertEquals("fboaor", ab);

        String ba = foobar("foo", "bar")
                .flip()
                .map((c1, c2) -> "" + c1 + c2)
                .collect(Collectors.joining());
        assertEquals("bfaoro", ba);
    }

    @Test
    public void testFlipFromIndex() {
        String ab = indexedBar("foo")
                .map((c1, c2) -> "" + c1 + c2)
                .collect(Collectors.joining());
        assertEquals("0f1o2o", ab);

        String ba = indexedBar("foo")
                .flip()
                .map((c1, c2) -> "" + c1 + c2)
                .collect(Collectors.joining());
        assertEquals("f0o1o2", ba);
    }

    @Test
    public void testReduce() {
        ZipStream<Character, Character> foobar = foobar("foo", "bar");
        String reduce = foobar.reduce("1969", (t, a, b) -> t + a + b, String::concat);
        assertEquals(reduce, "1969fboaor");
    }

    @Test
    public void testReduceX() {
        ZipStream<Character, Character> foobar = foobar("foo", "bar");
        String reduce = foobar.reduceX("1969", (t, a) -> t + a, String::concat);
        assertEquals(reduce, "1969foo");
    }

    @Test
    public void testReduceY() {
        ZipStream<Character, Character> foobar = foobar("foo", "bar");
        String reduce = foobar.reduceY("1969", (t, b) -> t + b, String::concat);
        assertEquals(reduce, "1969bar");
    }

    @Test
    public void testToMap() {
        ZipStream<Character, Character> ab = foobar("12", "ab");
        Map<Character, Character> map = ab.toMap();
        assertEquals(2, map.size());
        assertEquals((int) 'a', (int) map.get('1'));
        assertEquals((int) 'b', (int) map.get('2'));

        ZipStream<Character, Character> from = ZipStreams.from(map);
        ZipStream<Character, Character> rStream = from.flatMap(Stream::of, Stream::of);
        assertEquals("1a2b", joinZipChars(rStream));
    }

    @Test
    public void testFromEntries() {
        Map<Character, Character> ab = foobar("12", "ab").toMap();
        ZipStream<Character, Character> stream = ZipStreams.fromEntries(ab.entrySet());
        assertEquals("1a2b", joinZipChars(stream));
    }

    @Test
    public void testFromEntriesStream() {
        Map<Character, Character> ab = foobar("12", "ab").toMap();
        ZipStream<Character, Character> stream = ZipStreams.fromEntries(ab.entrySet().stream());
        assertEquals("1a2b", joinZipChars(stream));
    }

    @Test
    public void testFromFun() {
        ZipStream<Character, Character> bar = ZipStreams.computed(chars("bar"), Character::toUpperCase);
        assertEquals("bBaArR", joinZipChars(bar));
    }

    @Test
    public void testMapToInt() {
        ZipStream<Integer, Integer> s = ZipStreams.withIndexes(IntStream.range(0, 3).boxed())
                .mapX(Long::intValue);
        IntStream intStream = s.mapToInt((i1, i2) -> i1 + i2);
        assertEquals(6, intStream.sum());
    }

    @Test
    public void testMapToLong() {
        ZipStream<Long, Long> s = ZipStreams.withIndexes(LongStream.range(0, 3).boxed());
        LongStream longStream = s.mapToLong((i1, i2) -> i1 + i2);
        assertEquals(6L, longStream.sum());
    }

    @Test
    public void testMapToDouble() {
        ZipStream<Double, Double> s = ZipStreams.withIndexes(DoubleStream.of(0.0D, 1.0D, 2.0D).boxed())
                .mapX(Long::doubleValue);
        DoubleStream longStream = s.mapToDouble((i1, i2) -> i1 + i2);
        assertEquals(6.0D, longStream.sum(), 0.01D);
    }

    @Test
    public void testCollectX() {
        ZipStream<Long, Character> bar = indexedBar("bar");
        List<Long> longs = bar.collectX(Collectors.toList());
        assertEquals(Arrays.asList(0L, 1L, 2L), longs);
    }

    @Test
    public void testCollectY() {
        ZipStream<Long, Character> bar = indexedBar("bar");
        List<Character> longs = bar.collectY(Collectors.toList());
        assertEquals(Arrays.asList('b', 'a', 'r'), longs);
    }

    @Test
    public void testIsParallel() {
        ZipStream<Character, Character> from = ZipStreams.from(chars("foo"), chars("bar").parallel());
        assertTrue(from.isParallel());
    }

    @Test
    public void testParallel() {
        ZipStream<Character, Character> from = ZipStreams.from(chars("foo"), chars("bar"));
        assertTrue(from.parallel().isParallel());
    }

    @Test
    public void testSortedX() {
        ZipStream<String, Integer> stream =
                ZipStreams.computed(Stream.of("ccc", "bb", "a"), String::length);
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedX()));
    }

    @Test
    public void testSortedXUnmerged() {
        ZipStream<String, Integer> stream =
                ZipStreams.from(Stream.of("ccc", "bb", "a"), IntStream.of(3, 2, 1).boxed());
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedX()));
    }

    @Test
    public void testSortedXComparator() {
        ZipStream<String, Integer> stream =
                ZipStreams.computed(Stream.of("ccc", "bb", "a"), String::length);
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedX(String::compareTo)));
    }

    @Test
    public void testSortedXUnmergedComparator() {
        ZipStream<String, Integer> stream =
                ZipStreams.from(Stream.of("ccc", "bb", "a"), IntStream.of(3, 2, 1).boxed());
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedX(String::compareTo)));
    }

    @Test
    public void testSortedY() {
        ZipStream<String, Integer> stream =
                ZipStreams.computed(Stream.of("ccc", "bb", "a"), String::length);
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedY()));
    }

    @Test
    public void testSortedYUnmerged() {
        ZipStream<String, Integer> stream =
                ZipStreams.from(Stream.of("ccc", "bb", "a"), IntStream.of(3, 2, 1).boxed());
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedY()));
    }

    @Test
    public void testSortedYComparator() {
        ZipStream<String, Integer> stream =
                ZipStreams.computed(Stream.of("ccc", "bb", "a"), String::length);
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedY(Integer::compareTo)));
    }

    @Test
    public void testSortedYUnmergedComparator() {
        ZipStream<String, Integer> stream =
                ZipStreams.from(Stream.of("ccc", "bb", "a"), IntStream.of(3, 2, 1).boxed());
        assertEquals("a1bb2ccc3", joinZipChars(stream.sortedY(Integer::compareTo)));
    }

    private ZipStream<Character, Character> foobar(String foo, String bar) {
        return ZipStreams.from(chars(foo), chars(bar));
    }

    private ZipStream<Long, Character> indexedBar(String bar) {
        return ZipStreams.withIndexes(chars(bar));
    }

    private String joinZipChars(ZipStream<?, ?> map) {
        return joinStrings(map.map((a, b) -> "" + a + b));
    }

    private String joinChars(Stream<Character> characterStream) {
        return joinStrings(characterStream.map(String::valueOf));
    }

    private String joinStrings(Stream<String> stream) {
        return stream.collect(Collectors.joining());
    }

    private Stream<Character> chars(String text) {
        return chars(text, null);
    }

    private Stream<Character> chars(String text, Integer size) {
        return IntStream.range(0, size == null ? text.length() : size).mapToObj(text::charAt);
    }

    @SuppressWarnings("SameParameterValue")
    private void assertZipped(ZipStream<?, ?> stream, String expected) {
        Stream<String> map = stream.map((x, y) -> "" + x + y);
        assertEquals(expected, joinStrings(map));
    }
}
