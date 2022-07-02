(ns servico-clojure.api-test
  (:require [clojure.test :refer :all]
            [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database]
            [servico-clojure.rotas :as rotas])
  (:use [clojure.pprint]))

(def my-component-system
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])))

(def component-result (component/start my-component-system))
(def test-request (-> component-result :servidor :test-request))

(deftest tarefa-api-test
  (testing "Hello World Test"
    (let [path "/hello?name=Cesar"
          response (test-request :get path)
          body (:body response)]
      (is (= "Bem vindo!!! Cesar" body))))

  (testing "CRUD Test"
    (let [_ (test-request :post "/tarefa?nome=Correr&status=pendente")
          _ (test-request :post "/tarefa?nome=Ler&status=pendente")
          tasks (clojure.edn/read-string (:body (test-request :get "/tarefa")))
          task1 (-> tasks first second)
          task1-id (:id task1)
          task2 (-> tasks second second)
          task2-id (:id task2)
          _ (test-request :delete (str "/tarefa/" task1-id))
          _ (test-request :patch (str "/tarefa/" task2-id "?nome=TerminarCursoWebAPI&status=feito"))
          tasks-processed (clojure.edn/read-string (:body (test-request :get "/tarefa")))
          task-updated (-> tasks-processed vals first)]
      (is (= 2 (count tasks)))
      (is (= "Correr" (:nome task1)))
      (is (= "pendente" (:status task1)))
      (is (= "Ler" (:nome task2)))
      (is (= "pendente" (:status task2)))
      (is (= 1 (count tasks-processed)))
      (is (= "TerminarCursoWebAPI" (:nome task-updated)))
      (is (= "feito" (:status task-updated))))))

;(test-request :post "/tarefa?nome=Correr&status=pendente")
;(test-request :post "/tarefa?nome=Ler&status=pendente")
;(test-request :post "/tarefa?nome=Estudar&status=feito")
; (clojure.edn/read-string (:body (test-request :get "/tarefa")))
;(test-request :delete "/tarefa/53ec303f-4a84-4750-8909-a9301f0dd1d3")
;(test-request :patch "/tarefa/c4be7283-a976-454a-b997-9702a433e822?nome=TerminarCursoWebAPI&status=feito")
