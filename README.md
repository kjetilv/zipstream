# ZipStream

```java
Stream<Foo> foos = foos();
Stream<Bar> bars = bars();

ZipStream.from(foos, bars).forEach((foo, bar) ->
  stuff(foo, bar));

Stream<FooBar> foobars = ZipStream.from(foos(), bars()).map((foo, bar) ->
  foo.and(bar)));
```

TODO: Make work.
