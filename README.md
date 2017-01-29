# types

If you think that defrecord is great and you want the same for vectors, sets and lists, you're in the right place.

Alpha quality, contributions are welcome

## Declaration 

```clojure
;; def a new type called Foo that behave exactly like a PersistentVector
(vectype Foo)

;; it can be extended to whatever protocol you need to implement
(vectype FOO 
  IBark 
  (bark [_] "woaf"))
```


## Instantiate

```clojure
;; unary constructor
(foo [1 2 3])

;; variadic constructor
(foo* 1 2 3)

```

## Sets

For set like types you can use `settype` instead of `vectype`
For set like types you can use `listtype`

That's all

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
