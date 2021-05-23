(ns servico-clojure.main
  (:require [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database]
            [servico-clojure.rotas :as rotas])
  (:use [clojure.pprint]))

(defn my-component-system []
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])))

(def component-result (component/start (my-component-system)))
(def test-request (-> component-result :servidor :test-request))

(test-request :get "/hello?name=Cesar")
(test-request :post "/tarefa?nome=Correr&status=pendente")
(test-request :post "/tarefa?nome=Ler&status=pendente")
(test-request :post "/tarefa?nome=Estudar&status=feito")
(clojure.edn/read-string (:body (test-request :get "/tarefa")))
(test-request :delete "/tarefa/cbc57cfa-490d-47cd-8643-089a66e0eb42")
(test-request :patch "/tarefa/455279fb-92bc-4c61-9f80-be4e611abf08?nome=TerminarCursoWebAPI&status=feito")
