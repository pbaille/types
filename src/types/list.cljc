(ns types.list
  #?(:clj (:require [backtick]
                    [types.helpers :refer [get-options decline-name]])
     :cljs (:require-macros [types.vec :refer [vectype]])))

#?(:clj
   (defmacro listtype [n & body]
     (let [{:keys [cfnsym cfnsym* prsym]} (get-options n body)
           {c :class
            cc :class-constr} (decline-name n)
           body (if (map? (first body)) (next body) body)]
       (if (:ns &env)
         (backtick/template
           (do
             (deftype ~c [xs]
               Object
               (toString [_] (pr-str* xs))
               (equiv [_ o] (and (instance? ~c o) (= (.-xs o) xs)))
               (indexOf [_ x] (-indexOf xs x 0))
               (indexOf [_ x start] (-indexOf xs x start))
               (lastIndexOf [_ x] (-lastIndexOf xs x (count xs)))
               (lastIndexOf [_ x start] (-lastIndexOf xs x start))

               IList

               ICloneable
               (-clone [_] (~cc xs))

               IWithMeta
               (-with-meta [_ meta] (~cc (with-meta xs meta)))

               IMeta
               (-meta [_] (meta xs))

               ASeq
               ISeq
               (-first [_] (-first xs))
               (-rest [_] (~cc (-rest xs)))

               INext
               (-next [_] (~cc (-next xs)))

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

               IReduce
               (-reduce [_ f] (~cc (-reduce xs f)))
               (-reduce [_ f init] (~cc (-reduce xs f init)))
               )

             (defn ~cfnsym [coll] (~cc (list* coll)))
             (defn ~cfnsym* [& args] (~cc (list* args)))))

         (backtick/template
           (do
             (deftype ~n [xs]
               clojure.lang.IPersistentCollection
               (seq [_] (.seq xs))
               (empty [_] (~cc []))
               (equiv [_ o] (and (instance? ~n o) (= (.xs o) xs)))
               (count [_] (.count xs))

               clojure.lang.IPersistentStack
               (pop [_] (~cc (.pop xs)))
               (peek [_] (~cc (.peek xs)))

               clojure.lang.ILookup
               (valAt [_ idx] (nth xs idx))
               (valAt [_ idx not-found] (nth xs idx not-found))

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
             (defn ~cfnsym [xs#] (~cc (list* xs#)))
             (defn ~cfnsym* [& xs#] (~cc (list* xs#)))))))))

(comment
  (listtype Aze)
  (rest (aze [1 2 3]))
  (list* [1 2 3])
  (cljs.pprint/pprint (macroexpand-1 '(vectype Aze))))





