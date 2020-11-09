(ns bank-customers.components.db
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [bank-customers.protocols.db-client :as db-client]))

(defrecord Database [conn]
  component/Lifecycle
  (start [this]
    (println "Starting database connection")
    this)

  (stop [_]
    (println "Stopping database connection")
    nil)

  db-client/DatabaseClient
  (query [this data]
    (into [] (d/q data
                  (d/db (get this :conn)))))

  (query-with-arg [this arg data]
    (into [] (d/q data
                  (d/db (get this :conn))
                  arg)))

  (transact-entity! [this data]
    (d/transact (get this :conn) data)))

(def customer-schema
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

(defn create-empty-in-memory-db [uri schema]
  (d/delete-database uri)
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (d/transact conn schema)
    conn))

(defn new-db [db-uri env]
  (case env
    :test (->Database (create-empty-in-memory-db db-uri customer-schema))
    :prod (->Database (d/connect db-uri))))