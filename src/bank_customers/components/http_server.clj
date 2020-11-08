(ns bank-customers.components.http-server
  (:require [org.httpkit.server :as server]
            [com.stuartsierra.component :as component]
            [ring.middleware.defaults :as rm-defaults]
            [ring.middleware.json :as rm-json]))

(defrecord HttpServer [port http-server service]
  component/Lifecycle

  (start [this]
    (let [app-routes (:service-routes service)
          server (-> app-routes
                     (rm-defaults/wrap-defaults (assoc-in rm-defaults/site-defaults [:security :anti-forgery] false))
                     (rm-json/wrap-json-body {:keywords? true :bigdecimals? true})
                     (server/run-server {:port port}))]

      (println (str "Running webserver at http://localhost:" port "/"))
      (assoc this :http-server server)))

  (stop [this]
    (println "Server stopped")
    (when-let [stop-server (:http-server this)]
      (stop-server :timeout 100))
    (assoc this :http-server nil)))

(defn new-server [port]
  (map->HttpServer {:port port}))