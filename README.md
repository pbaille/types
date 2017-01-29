# types

If you think that defrecord is great and you want the same for vectors, sets and lists, you're in the right place.

Alpha quality, contributions are welcome

## Declaration 

```clojure
;; def a new type called Foo that behave exactly like a PersistentVector
(vectype Foo)

;; it can be extended to whatever protocol you need to implement
(vectype Foo
  IBark 
  (bark [_] "woaf"))

;; same with lists
(listtype Bar)

;; and sets 
(settype Baz)

```


## Instantiate

```clojure
;; unary constructors --------

(foo [1 2 3])
;; => Foo[1 2 3]

(bar [1 2 3])
;; => Bar#{3 1 2}

(baz [1 2 3])
;; => Baz(1 2 3)

;; variadic constructors -----

(foo* 1 2 3)
;; => Foo[1 2 3]

(bar* 1 2 3)
;; => Bar#{3 1 2}

(baz* 1 2 3)
;; => Baz(1 2 3)

```

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
