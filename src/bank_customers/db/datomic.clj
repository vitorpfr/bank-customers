(ns bank-customers.db.datomic
  (:require [bank-customers.adapters :as a]
            [schema.core :as s]
            [bank-customers.protocols.db-client :as db-client]
            [bank-customers.models :as m]))

(s/defn get-customers-tax-ids :- m/TaxIds
  [db :- db-client/IDatabaseClient]
  (a/tax-ids-wire->internal (db-client/get-customers-tax-ids db)))