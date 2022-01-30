(ns servico-clojure.endpoints
  (:require [io.pedestal.http.route :as route]
            [servico-clojure.database.task :as db.task])
  (:import (java.util UUID)))

(defn funcao-hello [request]
  {:status 200 :body (str "Bem vindo!!! " (get-in request [:query-params :name] "Everybody!"))})

(defn task
  ([uuid nome status]
   {:id uuid :nome nome :status status})
  ([uuid]
   {:id uuid}))

(defn criar-tarefa [request]
  (let [uuid (UUID/randomUUID)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (task uuid nome status)
        store (:store request)]
    (db.task/upsert! store tarefa)
    {:status 200 :body {:mensagem "Tarefa registrada com sucesso!"
                        :tarefa   tarefa}}))

(defn atualizar-tarefa [request]
  (let [tarefa-id (get-in request [:path-params :id])
        tarefa-id-uuid (UUID/fromString tarefa-id)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (task tarefa-id-uuid nome status)
        store (:store request)]
    (db.task/upsert! store tarefa)
    {:status 200 :body {:mensagem "Tarefa atualizada com sucesso!"
                        :tarefa   tarefa}}))

(defn listar-tarefas [request]
  {:status 200 :body @(:store request)})

(defn remover-tarefa [request]
  (let [store (:store request)
        tarefa-id (get-in request [:path-params :id])
        tarefa-id-uuid (UUID/fromString tarefa-id)
        task (task tarefa-id-uuid)]
    (db.task/delete store task)
    {:status 200 :body {:mensagem "Removida com sucesso"}}))

(def routes (route/expand-routes
              #{["/hello" :get funcao-hello :route-name :hello-world]
                ["/tarefa" :post criar-tarefa :route-name :criar-tarefa]
                ["/tarefa/:id" :patch atualizar-tarefa :route-name :atualizar-tarefa]
                ["/tarefa" :get listar-tarefas :route-name :listar-tarefas]
                ["/tarefa/:id" :delete remover-tarefa :route-name :remover-tarefa]}))
