(ns bank-customers.components.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [bank-customers.protocols.db-client :as db-client]))

(def ^:private customer-schema
  [{:db/ident       :customer/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Full name of a customer"}

   {:db/ident       :customer/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "E-mail of a customer"}

   {:db/ident       :customer/tax-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Tax id of a customer"}])

(defn ^:private connect-to-empty-in-memory-db [uri]
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (d/transact conn customer-schema)
    conn))

(defn ^:private connect-to-prod-db [uri]
  (d/connect uri))

(defn ^:private connect-to-db
  [env uri]
  (case env
    :test (connect-to-empty-in-memory-db uri)
    :prod (connect-to-prod-db uri)))

(defrecord Database [env uri connection]
  component/Lifecycle
  (start [this]
    (println "Starting database connection")
    (let [conn (connect-to-db env uri)]
      (assoc this :connection conn)))

  (stop [this]
    (println "Stopping database connection")
    (assoc this :connection nil))

  db-client/DatabaseClient
  (query [this data]
    (into [] (d/q data
                  (d/db (get this :connection)))))

  (query-with-arg [this arg data]
    (into [] (d/q data
                  (d/db (get this :connection))
                  arg)))

  (transact-entity! [this data]
    (d/transact (get this :connection) data)))

(defn new-db [env uri]
  (map->Database {:env env
                  :uri uri}))