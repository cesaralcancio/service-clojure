(ns servico-clojure.datomic.task-test
  (:require [clojure.test :refer :all]
            [servico-clojure.datomic.config :as datomic.config]
            [servico-clojure.datomic.task :as datomic.task])
  (:import (java.util UUID)))

(deftest ^:datomic create-and-fetch-all-and-delete-ids-test
  (let [_ (datomic.config/create-database! datomic.config/local-uri datomic.config/db-name-tasks)
        conn (datomic.config/connect! datomic.config/local-uri datomic.config/db-name-tasks)
        _ (datomic.config/create-schema! conn datomic.task/tasks-schema)
        task {:task/id (UUID/randomUUID) :task/nome "Refactor code" :task/status :pending}
        _ (datomic.task/upsert-one! conn task)
        tasks (datomic.task/fetch-all! (datomic.config/db! conn))
        ids (map #(:task/id %) tasks)
        _ (datomic.task/delete-ids! conn ids)
        no-tasks (datomic.task/fetch-all! (datomic.config/db! conn))]
    (is (not (empty? tasks)))
    (is (empty? no-tasks))))
