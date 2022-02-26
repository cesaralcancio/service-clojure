(ns servico-clojure.datomic.task
  (:require [datomic.api :as d]))

(def tasks-schema
  [{:db/ident       :task/id
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db/doc         "Task ID"}
   {:db/ident       :task/nome
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Task nome"}
   {:db/ident       :task/status
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "Task status"}])

(defn upsert-one!
  [conn task]
  (d/transact conn [task]))

(defn fetch-all!
  [db]
  (d/q '[:find ?id ?nome ?status
         :keys task/id task/nome task/status
         :where
         [?e :task/id ?id]
         [?e :task/nome ?nome]
         [?e :task/status ?status]]
       db))

(defn delete-ids!
  [conn ids]
  (doseq [id ids]
    (d/transact conn [[:db/retract [:task/id id] :task/id]
                      [:db/retract [:task/id id] :task/nome]
                      [:db/retract [:task/id id] :task/status]])))
