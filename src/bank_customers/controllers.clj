(ns bank-customers.controllers
  (:require [bank-customers.protocols.db-client :as db-client]
            [schema.core :as s]
            [bank-customers.db.datomic :as ddb]
            [bank-customers.models :as m]))

(s/defn get-customers-tax-ids :- m/TaxIds
  [db :- db-client/IDatabaseClient]
  (ddb/get-customers-tax-ids db))

(s/defn get-customer :- m/Customer
  [tax-id :- m/TaxId
   db :- db-client/IDatabaseClient]
  (ddb/get-customer tax-id db))

(s/defn add-customer :- m/Customer
  [customer :- m/Customer
   db :- db-client/IDatabaseClient]
  (ddb/add-customer customer db)
  customer)