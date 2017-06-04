# ZipStream

```java
Stream<Foo> foos = foos();
Stream<Bar> bars = bars();

ZipStreams.from(foos, bars)
  .forEach((foo, bar) -> doStuff(foo, bar));

Stream<FooBar> foobars = ZipStreams.from(foos(), bars())
  .map((foo, bar) -> foo.and(bar)));

Map<Foo, Bar> foobars = ZipStreams.from(foos(), bars())
  .toMap();
```

