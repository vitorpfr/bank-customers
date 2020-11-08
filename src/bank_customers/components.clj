(ns bank-customers.components
  (:require [com.stuartsierra.component :as component]
            [bank-customers.components.db :as db]
            [bank-customers.components.http-server :as http-server]
            [bank-customers.components.service :as service]))

(def system-config
  {:port 4000})

(defn new-system
  [{:keys [port]}]
  (component/system-map
    :db (db/new-db)
    :service (component/using (service/new-service) [:db])
    :http-server (component/using (http-server/new-server port) [:service])))

(def system (new-system system-config))

(defn start-all []
  (component/start system))
