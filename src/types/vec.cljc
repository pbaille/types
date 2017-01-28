(ns types.vec
  #?(:clj (:require [backtick]
                    [types.helpers :refer [get-options decline-name]])
     :cljs (:require-macros [types.vec :refer [vectype]])))

#?(:clj
   (defmacro vectype [n & body]
     (let [{:keys [cfnsym cfnsym* prsym]} (get-options n body)
           {c :class
            cc :class-constr
            tc :transient-class
            tcc :transient-constr} (decline-name n)
           body (if (map? (first body)) (next body) body)]
       (if (:ns &env)
         (backtick/template
           (do
             (declare ~c)

             (deftype ~tc [xs]
               ITransientCollection
               (-conj! [_ o] (~tcc (-conj! xs o)))
               (-persistent! [_] (~cc (-persistent! xs)))

               ITransientAssociative
               (-assoc! [_ key val] (~tcc (-assoc! xs key val)))

               ITransientVector
               (-assoc-n! [_ n val] (~tcc (-assoc-n! xs n val)))
               (-pop! [_] (~tcc (-pop! xs)))

               ICounted
               (-count [_] (-count xs))

               IIndexed
               (-nth [_ n] (-nth xs n))
               (-nth [_ n not-found] (-nth xs n not-found))

               ILookup
               (-lookup [_ k] (-lookup xs k))
               (-lookup [_ k not-found] (-lookup xs k not-found))

               IFn
               (-invoke [_ k] (-lookup xs k))
               (-invoke [_ k not-found] (-lookup xs k not-found)))


             (deftype ~c [xs]
               Object
               (toString [_] (pr-str* xs))
               (equiv [_ o] (and #_(instance? ~c o) (= (.-xs o) xs)))
               (indexOf [_ x] (-indexOf xs x 0))
               (indexOf [_ x start] (-indexOf xs x start))
               (lastIndexOf [_ x] (-lastIndexOf xs x (count xs)))
               (lastIndexOf [_ x start] (-lastIndexOf xs x start))

               ICloneable
               (-clone [_] (~cc xs))

               IWithMeta
               (-with-meta [_ meta] (~cc (with-meta xs meta)))

               IMeta
               (-meta [_] (meta xs))

               IStack
               (-peek [_] (~cc (-peek xs)))
               (-pop [_] (~cc (-pop xs)))

               ICollection
               (-conj [_ o] (~cc (-conj xs o)))

               IEmptyableCollection
               (-empty [_] (~cc (-empty xs)))

               ISequential
               IEquiv
               (-equiv [_ o] (~cc (-equiv xs o)))

               IHash
               (-hash [_] (-hash xs))

               ISeqable
               (-seq [_] (-seq xs))

               ICounted
               (-count [_] (-count xs))

               IIndexed
               (-nth [_ n] (-nth xs n))
               (-nth [_ n not-found] (-nth xs n not-found))

               ILookup
               (-lookup [_ k] (-lookup xs k))
               (-lookup [_ k not-found] (-lookup xs k not-found))

               IMapEntry
               (-key [_] (-key xs))
               (-val [_] (-val xs))

               IAssociative
               (-assoc [_ k v] (~cc (-assoc xs k v)))

               IVector
               (-assoc-n [_ n val] (~cc (-assoc-n xs n val)))

               IReduce
               (-reduce [_ f] (~cc (-reduce xs f)))
               (-reduce [_ f init] (~cc (-reduce xs f init)))

               IKVReduce
               (-kv-reduce [_ f init] (~cc (-kv-reduce xs f init)))

               IFn
               (-invoke [_ k] (-nth xs k))
               (-invoke [_ k not-found] (-nth xs k not-found))

               IEditableCollection
               (-as-transient [_] (~tcc xs))

               IReversible
               (-rseq [_] (-rseq xs))

               IIterable
               (-iterator [_] (-iterator xs))

               IPrintWithWriter
               (-pr-writer [_ writer c]
                 (-write writer ~(name prsym))
                 (-pr-writer xs writer c))

               ~@body)

             (defn ~cfnsym [coll] (~cc (vec coll)))
             (defn ~cfnsym* [& args] (~cc (vec args)))))

         (backtick/template
           (do
             (deftype ~n [xs]
               clojure.lang.IPersistentCollection
               (seq [_] (.seq xs))
               (empty [_] (~cc []))
               (equiv [_ o] (and (instance? ~n o) (= (.xs o) xs)))
               (count [_] (.count xs))

               clojure.lang.IPersistentVector
               (cons [this x] (~cc (.cons xs x)))
               (assocN [_ i o] (~cc (.assocN xs i o)))
               (length [this] (.length xs))

               clojure.lang.Associative
               (containsKey [_ k] (boolean (get xs k)))
               (assoc [_ k v] (~cc (.assoc xs k v)))
               (entryAt [this k] (get xs k))

               clojure.lang.Indexed
               (nth [_ idx] (.nth xs idx))
               (nth [_ idx not-found] (.nth xs idx not-found))

               clojure.lang.IPersistentStack
               (pop [_] (~cc (.pop xs)))
               (peek [_] (~cc (.peek xs)))

               clojure.lang.Reversible
               (rseq [this] (~cc (.rseq xs)))

               clojure.lang.ILookup
               (valAt [_ idx] (.valAt xs idx))
               (valAt [_ idx not-found] (.valAt xs idx not-found))

               clojure.lang.IFn
               (invoke [this n] (get this n))

               Object
               (toString [this] (pr-str xs))

               clojure.lang.IObj
               (withMeta [_ met]
                 (~cc (.withMeta xs met)))

               clojure.lang.IMeta
               (meta [_] (.meta xs))

               ~@body)
             (defmethod print-method ~n [o# w#]
               (print-method (symbol (str ~(name prsym) (.xs o#))) w#))
             (defn ~cfnsym [xs#] (~cc (vec xs#)))
             (defn ~cfnsym* [& xs#] (~cc (vec xs#)))))))))

(comment
  (vectype Aze)
  (aze [1 2 3])
  (cljs.pprint/pprint (macroexpand-1 '(vectype Aze))))
