(ns bank-customers.protocols.db-client
  (:require [schema.core :as s]))

(defprotocol DatabaseClient
  (get-customers-tax-ids [database] "Return the customers tax-ids present in the database")
  (get-customer [database] "Returns data of a specific tax-id"))

(def IDatabaseClient (s/protocol DatabaseClient))