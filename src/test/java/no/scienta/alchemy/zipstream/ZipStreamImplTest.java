package no.scienta.alchemy.zipstream;

import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZipStreamImplTest {

    @Test
    public void testFlatMap() {
        Stream<Character> characterStream = foobar("foo", "bar")
                .flatMap((c1, c2) -> Stream.of(c1, c2));
        assertEquals("fboaor", joinChars(characterStream));
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
    public void testLimit() {
        ZipStream<Character, Character> barrr = foobar("foo", "barrr");
        assertEquals("fboa", joinZipChars(barrr.limit(2)));
    }

    @Test
    public void testZip() {
        assertZipped(
                ZipStream.from(
                        IntStream.range(0, 10).boxed(),
                        chars("ThisIsATest", Integer.MAX_VALUE)),
                "0T1h2i3s4I5s6A7T8e9s");
    }

    @Test
    public void testIndexedZip() {
        assertZipped(ZipStream.withIndexes(chars("ThisIsATest", 10)),
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
        assertEquals((int)'a', (int)map.get('1'));
        assertEquals((int)'b', (int)map.get('2'));
    }

    private ZipStream<Character, Character> foobar(String foo, String bar) {
        return ZipStream.from(chars(foo), chars(bar));
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

    private void assertZipped(ZipStream<?, ?> stream, String expected) {
        Stream<String> map = stream.map((x, y) -> "" + x + y);
        assertEquals(expected, joinStrings(map));
    }
}
