(ns servico-clojure.components.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database []
  component/Lifecycle

  (start [this]
    (println "Start database")
    (assoc this :store (atom {})))

  (stop [this]
    (println "Stop database")
    (assoc this :store nil)))

(defn new-database []
  (->Database))
