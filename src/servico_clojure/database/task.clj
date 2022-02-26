(ns servico-clojure.database.task)

(defn upsert! [store task]
  (swap! store assoc (:id task) task))

(defn delete [store task]
  (swap! store dissoc (:id task)))

(defn find-all [store]
  (-> @store vals vec))
