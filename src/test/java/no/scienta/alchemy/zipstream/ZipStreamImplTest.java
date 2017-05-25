package no.scienta.alchemy.zipstream;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class ZipStreamImplTest {

    @Test
    public void testZip() {
        ZipStream<Integer, Character> stream = ZipStream.of(
                IntStream.range(0, 10).boxed(),
                IntStream.range(0, Integer.MAX_VALUE).mapToObj("ThisIsATest"::charAt));

        assertTest(stream);
    }

    private void assertTest(ZipStream<Integer, Character> stream) {
        Stream<String> map = stream.map((integer, character) ->
                "" + integer + character);

        assertEquals(
                "0T1h2i3s4I5s6A7T8e9s",
                map.collect(Collectors.joining())
        );
    }
}
