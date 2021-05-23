(ns servico-clojure.principal
  (:require [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database])
  (:use [clojure.pprint]))

(defn system-components []
  (component/system-map
    :database (database/new-database)
    :servidor (component/using (servidor/new-servidor) [:database])))

(def result-components (component/start (system-components)))
(pprint result-components)
(def test-request (-> result-components :servidor :test-request))

(test-request :get "/hello?name=Cesar")
(test-request :post "/tarefa?nome=Correr&status=pendente")
(test-request :post "/tarefa?nome=Ler&status=pendente")
(test-request :post "/tarefa?nome=Estudar&status=feito")
(clojure.edn/read-string (:body (test-request :get "/tarefa")))
(test-request :delete "/tarefa/64b92914-c582-4d35-a52b-f7bbf0651e54")
(test-request :patch "/tarefa/cf277943-a66e-45c4-af6b-3a98c0063e0f?nome=Ler Muito&status=pendente")
