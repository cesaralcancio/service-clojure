(ns servico-clojure.api-test
  (:require [clojure.test :refer :all]
            [servico-clojure.components.servidor :as servidor]
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

(deftest tarefa-api-test
  (testing "Hello World Testing"
    (let [path "/hello?name=Cesar"
          response (-> :get (test-request path) :body)]
      (is (= "Bem vindo!!! Cesar" response))))

  (testing "CRUD Testing"
    (let [;; post to create
          post-path "/tarefa?nome=Correr&status=pendente"
          post-resp (-> :post (test-request post-path))
          post-resp-body (-> post-resp :body clojure.edn/read-string)
          post-resp-status (-> post-resp :status)
          ;; get after post
          get-path "/tarefa"
          get-resp (-> :get (test-request get-path))
          get-resp-body (-> get-resp :body clojure.edn/read-string)
          first-task-id (:id (first get-resp-body))
          first-task-name (:nome (first get-resp-body))
          ;; put and get
          put-path (str "/tarefa/" first-task-id "?nome=CorrerAtualizado&status=feito")
          put-resp (-> :patch (test-request put-path))
          get-after-put-body (-> :get (test-request get-path))
          get-after-put-resp-body (-> get-after-put-body :body clojure.edn/read-string)
          first-task-name-updated (:nome (first get-after-put-resp-body))
          ;; delete and get
          delete-path (str "/tarefa/" first-task-id)
          delete-resp (-> :delete (test-request delete-path))
          get-after-delete-body (-> :get (test-request get-path))
          get-after-delete-resp-body (-> get-after-delete-body :body clojure.edn/read-string)]
      (is (= (:mensagem post-resp-body) "Tarefa registrada com sucesso!"))
      (is (= 200 post-resp-status))
      (is (= first-task-name "Correr"))
      (is (= first-task-name-updated "CorrerAtualizado"))
      (is (empty? get-after-delete-resp-body)))))
