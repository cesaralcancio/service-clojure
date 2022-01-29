(ns servico-clojure.main
  (:require [servico-clojure.components.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.components.database :as database]
            [servico-clojure.components.rotas :as rotas])
  (:use [clojure.pprint]))

(def my-component-system
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])))

(def component-result (component/start my-component-system))
(def test-request (-> component-result :servidor :test-request))

(test-request :get "/hello?name=Cesar")
(test-request :post "/tarefa?nome=Correr&status=pendente")
(test-request :post "/tarefa?nome=Ler&status=pendente")
(test-request :post "/tarefa?nome=Estudar&status=feito")
(clojure.edn/read-string (:body (test-request :get "/tarefa")))
(def first-tarefa-id (-> (test-request :get "/tarefa")
                         :body
                         clojure.edn/read-string
                         first
                         key
                         str))
(def second-tarefa-id (-> (test-request :get "/tarefa")
                          :body
                          clojure.edn/read-string
                          second
                          key
                          str))
(test-request :delete (str "/tarefa/" first-tarefa-id))
(test-request :patch (str "/tarefa/" second-tarefa-id "?nome=TerminarCursoWebAPI&status=feito"))
