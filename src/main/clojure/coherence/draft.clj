(ns  coherence.coherence

        )
(import [com.tangosol.net CacheFactory NamedCache]
        [com.tangosol.util.processor AbstractProcessor]
        [com.tangosol.net.cache CacheMap]
        )

(import '(com.tangosol.util.filter AllFilter))
(import '(com.tangosol.util Filter))
(import '(com.tangosol.util.filter AlwaysFilter))
(import '(java.util Set))
(import '(com.tangosol.util.filter AllFilter))


        (def ^{:dynamic true} *cache* nil)

        (defn get-cache
                "Get a NamedCache reference using CacheFactory"
                [name]
                (CacheFactory/getCache name))

        (defn get-val
                "get entry from cache"
                [e-key]
                (.get *cache* e-key))

        (defn put-val
                "Put an entry to cache, returns the old value if any"
                [e-key e-value]
                        (.put *cache* e-key e-value))

        (defmacro with-cache [cache-name & body]
                `(binding [*cache* (get-cache ~cache-name)]
                        ~@body))

(def cache (get-cache "positions"))
(.get cache 1)


(.getCacheName cache)

(.getCacheService cache)

(.put cache 5 {:a 1 :b 2})
(.get cache 5)
;(get-method-names (first (into [] (.entrySet {:a 1}))))
(.put cache 5 (first (into [] (.entrySet {:a 1}))))

(def all-filter (new AlwaysFilter))



(let [all-filter (new AlwaysFilter)
      nodeCache (get-cache "positions")]
  (. (. nodeCache entrySet all-filter) size))

(let [all-filter (new AlwaysFilter)
      nodeCache (get-cache "positions")]
  (into [] (. nodeCache entrySet all-filter)))

(into [] (. cache entrySet all-filter))

(with-cache "positions" (get-val 5))


(import '(java.lang.reflect Modifier))
(defn get-obj-methods [obj]
  (let [obj2methods (fn [obj] (map #(do (.setAccessible % true) %) (into [] (. (. obj getClass) getDeclaredMethods))))
        get-inst-methods (fn [fields] (filter #(not (Modifier/isStatic (.getModifiers %))) fields))
        method2ref (fn [field obj] (.get field obj))
        ]

    (obj2methods obj)))

(defn get-method-names [obj] (map #(.getName %) (get-obj-methods obj)))

(filter #(re-matches #"get.*" %) (get-method-names cache))
