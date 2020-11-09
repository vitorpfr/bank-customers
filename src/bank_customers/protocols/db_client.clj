(ns bank-customers.protocols.db-client
  (:require [schema.core :as s]))

(defprotocol DatabaseClient
  (query [db data] "Queries the database")
  (query-with-arg [db arg data] "Queries the database with an argument")
  (transact-entity! [db data] "Transact an entity in the database"))

(def IDatabaseClient (s/protocol DatabaseClient))