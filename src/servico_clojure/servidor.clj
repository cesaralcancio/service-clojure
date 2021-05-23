(ns servico-clojure.servidor
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [io.pedestal.interceptor :as i]
            [com.stuartsierra.component :as component]))

(defrecord Servidor [database]
  component/Lifecycle
  (start [this]
    (println "Start servidor")

    (defn assoc-store [context]
      (update context :request assoc :store (:store database)))

    (def db-interceptor
      {:name  :db-interceptor
       :enter assoc-store})

    (defn listar-tarefas [request]
      {:status 200 :body @(:store request)})

    (defn criar-tarefa-mapa [uuid nome status]
      {:id uuid :nome nome :status status})

    (defn criar-tarefa [request]
      (let [uuid (java.util.UUID/randomUUID)
            nome (get-in request [:query-params :nome])
            status (get-in request [:query-params :status])
            tarefa (criar-tarefa-mapa uuid nome status)
            store (:store request)]
        (swap! store assoc uuid tarefa)
        {:status 200 :body {:mensagem "Tarefa registrada com sucesso!"
                            :tarefa   tarefa}}))

    (defn funcao-hello [request]
      {:status 200 :body (str "Bem vindo!!! " (get-in request [:query-params :name] "Everybody!"))})

    (defn remover-tarefa [request]
      (let [store (:store request)
            tarefa-id (get-in request [:path-params :id])
            tarefa-id-uuid (java.util.UUID/fromString tarefa-id)]
        (swap! store dissoc tarefa-id-uuid)
        {:status 200 :body {:mensagem "Removida com sucesso"}}))

    (defn atualizar-tarefa [request]
      (let [tarefa-id (get-in request [:path-params :id])
            tarefa-id-uuid (java.util.UUID/fromString tarefa-id)
            nome (get-in request [:query-params :nome])
            status (get-in request [:query-params :status])
            tarefa (criar-tarefa-mapa tarefa-id-uuid nome status)
            store (:store request)]
        (swap! store assoc tarefa-id-uuid tarefa)
        {:status 200 :body {:mensagem "Tarefa atualizada com sucesso!"
                            :tarefa   tarefa}}))

    (def routes (route/expand-routes
                  #{["/hello" :get funcao-hello :route-name :hello-world]
                    ["/tarefa" :post criar-tarefa :route-name :criar-tarefa]
                    ["/tarefa" :get listar-tarefas :route-name :listar-tarefas]
                    ["/tarefa/:id" :delete remover-tarefa :route-name :remover-tarefa]
                    ["/tarefa/:id" :patch atualizar-tarefa :route-name :atualizar-tarefa]}))

    (def service-map-base {::http/routes routes
                           ::http/port   9999
                           ::http/type   :jetty
                           ::http/join?  false})

    (def service-map (-> service-map-base
                         (http/default-interceptors)
                         (update ::http/interceptors conj (i/interceptor db-interceptor))))

    (defonce server (atom nil))

    (defn start-server []
      (reset! server (http/start (http/create-server service-map))))

    (defn test-request [verb url]
      (test/response-for (::http/service-fn @server) verb url))

    (defn stop-server []
      (http/stop @server))

    (defn restart-server []
      (stop-server)
      (start-server))

    (defn start []
      (try (start-server) (catch Exception e (println "Erro ao executar start" (.getMessage e))))
      (try (restart-server) (catch Exception e (println "Erro ao executar restart" (.getMessage e)))))

    (start)
    (assoc this :test-request test-request))

  (stop [this]
    (println "Stop servidor")
    (assoc this :test-request nil)))

(defn new-servidor []
  (map->Servidor {}))
