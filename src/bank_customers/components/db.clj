(ns bank-customers.components.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [bank-customers.protocols.db-client :as db-client]))

(def db-url "datomic:dev://localhost:4334/bank-customers")

(defrecord Database [data]
  component/Lifecycle
  (start [this]
    (println "Starting database connection")
    (assoc this :conn (d/connect db-url)))

  (stop [this]
    (println "Stopping and deleting database connection")
    (assoc this :conn nil))

  db-client/DatabaseClient
  (get-customers-tax-ids [this]
    (into [] (d/q '[:find ?tax-id
                    :where [_ :customer/tax-id ?tax-id]]
                  (d/db (get this :conn))))))

(defn new-db []
  (map->Database {}))