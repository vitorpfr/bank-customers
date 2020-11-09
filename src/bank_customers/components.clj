(ns bank-customers.components
  (:require [com.stuartsierra.component :as component]
            [bank-customers.components.db :as db]
            [bank-customers.components.http-server :as http-server]
            [bank-customers.components.service :as service]))

(defn new-system
  [env {:keys [port db-uri]}]
  (component/system-map
    :db (db/new-db db-uri env)
    :service (component/using (service/new-service) [:db])
    :http-server (component/using (http-server/new-server port) [:service])))

(defn system [env system-config]
  (new-system env (get system-config (keyword env))))

(def system-config
  {:prod {:port 4000
          :db-uri "datomic:dev://localhost:4334/bank-customers"}
   :test {:port 8080
          :db-uri "datomic:mem://bank-customers-test-db"}})

(defn start-system [env]
  (component/start (system env system-config)))
