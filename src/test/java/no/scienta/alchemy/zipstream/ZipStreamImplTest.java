package no.scienta.alchemy.zipstream;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ZipStreamImplTest {

    @Test
    public void testZip() {
        ZipStream<Integer, Character> stream = ZipStream.from(
                IntStream.range(0, 10).boxed(),
                chars("ThisIsATest", Integer.MAX_VALUE));

        assertTest(stream);
    }

    @Test
    public void testIndexedZip() {
        ZipStream<Long, Character> stream = ZipStream.withIndexes(chars("ThisIsATest", 10));

        assertTest(stream);
    }

    @Test
    public void testSideEffects() {
        StringBuilder sb = new StringBuilder();
        ZipStream.from(chars("foo"), chars("bar")).forEach((c1, c2) -> {
            sb.insert(0, c2);
            sb.insert(0, "-");
            sb.insert(0, c1);
        });

        assertEquals("o-ro-af-b", sb.toString());
    }

    private Stream<Character> chars(String text) {
        return chars(text, null);
    }

    private Stream<Character> chars(String text, Integer size) {
        return IntStream.range(0, size == null ? text.length() : size).mapToObj(text::charAt);
    }

    private void assertTest(ZipStream<?, ?> stream) {
        Stream<String> map = stream.map((x, y) -> "" + x + y);

        assertEquals(
                "0T1h2i3s4I5s6A7T8e9s",
                map.collect(Collectors.joining())
        );
    }
}
