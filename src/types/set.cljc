(ns types.set
  #?(:clj (:require [backtick]
                    [types.helpers :refer [get-options decline-name]])
     :cljs (:require-macros [types.set :refer [settype]])))

#?(:clj
   (defmacro settype [n & body]
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

               ITransientSet
               (-disjoin [_ key val] (~tcc (-disjoin xs key val)))

               ICounted
               (-count [_] (-count xs))

               ILookup
               (-lookup [_ k] (-lookup xs k))
               (-lookup [_ k not-found] (-lookup xs k not-found))

               IFn
               (-invoke [_ k] (-lookup xs k))
               (-invoke [_ k not-found] (-lookup xs k not-found)))


             (deftype ~c [xs]
               Object
               (toString [_] (pr-str* xs))
               (equiv [_ o] (and (instance? ~c o) (= (.-xs o) xs)))
               (keys [coll] (~cc (keys xs)))
               (entries [coll] (~cc (entries xs)))
               (values [coll] (~cc (values xs)))
               (has [coll k] (~cc (has xs)))
               (forEach [coll f] (~cc (forEach xs)))

               ICloneable
               (-clone [_] (~cc xs))

               IWithMeta
               (-with-meta [_ meta] (~cc (with-meta xs meta)))

               IMeta
               (-meta [_] (meta xs))

               ICollection
               (-conj [_ o] (~cc (-conj xs o)))

               IEmptyableCollection
               (-empty [_] (~cc (-empty xs)))

               IEquiv
               (-equiv [_ o] (~cc (-equiv xs o)))

               IHash
               (-hash [_] (-hash xs))

               ISet
               (-disjoin [_ v] (~cc (-disjoin xs v)))

               ISeqable
               (-seq [_] (-seq xs))

               ICounted
               (-count [_] (-count xs))

               ILookup
               (-lookup [_ k] (-lookup xs k))
               (-lookup [_ k not-found] (-lookup xs k not-found))

               IFn
               (-invoke [_ k] (-nth xs k))
               (-invoke [_ k not-found] (-nth xs k not-found))

               IEditableCollection
               (-as-transient [_] (~tcc xs))

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

               clojure.lang.IPersistentSet
               (get [_ n] (.get xs n))
               (contains [_ n] (.contains xs n))
               (disjoin [_ that] (~cc (.disjoin xs that)))

               clojure.lang.IPersistentCollection
               (cons [_ v] (~cc (.cons xs v)))
               (empty [_] (~cc #{}))
               (equiv [_ o] (and (instance? ~n o)(= (.xs o) xs)))
               (count [_] (.count xs))

               clojure.lang.Seqable
               (seq [_] (seq xs))

               clojure.lang.IFn
               (invoke [_ n] (get xs n))

               Object
               (toString [_] (pr-str xs))

               clojure.lang.IObj
               (withMeta [_ met]
                 (~cc (with-meta xs met)))

               clojure.lang.IMeta
               (meta [_] (meta xs))

               ~@body)

             (defmethod print-method ~n [o# w#]
               (print-method (list ~prsym (.xs o#)) w#))
             (defn ~cfnsym [xs#] (~cc (set xs#)))
             (defn ~cfnsym* [& xs#] (~cc (set xs#)))))))))

(macroexpand-1 '(settype Aze))

(comment
  (settype Aze)
  (aze [1 2 3])
  (cljs.pprint/pprint (macroexpand-1 '(settype Aze))))

