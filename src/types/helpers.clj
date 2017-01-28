(ns types.helpers)

(defn decline-name [n]
  {:map-constr (symbol (str "map->" (name n)))
   :transient-class (symbol (str "Transient" (name n)))
   :transient-constr (symbol (str "Transient" (name n) "."))
   :constr (symbol (str "->" (name n)))
   :class n
   :class-constr (symbol (str (name n) "."))})

(defn camel->dash [s]
  (let [[f & n] (re-seq #"[A-Za-z][a-z]*" s)]
    (apply str (clojure.string/lower-case f)
           (map #(str "-" (clojure.string/lower-case %)) n))))

(defn get-options [n [x & xs]]
  (merge
    {:cfnsym
     (symbol (camel->dash (name n)))
     :cfnsym*
     (symbol (str (camel->dash (name n)) "*"))
     :prsym n}
    (if (map? x) x {})))
