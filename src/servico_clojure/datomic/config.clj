(ns servico-clojure.datomic.config
  (:require [datomic.api :as d]))

(def local-uri "datomic:dev://localhost:4334/")
(def db-name-tasks "tasks")

(defn create-database! [uri db-name]
  (d/create-database (str uri db-name)))

(defn connect!
  [uri db-name]
  (d/connect (str uri db-name)))

(defn db!
  [conn]
  (d/db conn))

(defn create-schema!
  [conn schema]
  (d/transact conn schema))
