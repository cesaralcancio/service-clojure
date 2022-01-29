(ns servico-clojure.components.rotas
  (:require [servico-clojure.endpoints :as endpoints]
            [com.stuartsierra.component :as component]))

(defrecord Rotas []
  component/Lifecycle

  (start [this]
    (println "Start rotas")
    (assoc this :endpoints endpoints/routes))

  (stop [this]
    (println "Stop rotas")
    (assoc this :endpoints nil)))

(defn new-rotas []
  (->Rotas))
