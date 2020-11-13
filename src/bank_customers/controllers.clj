(ns bank-customers.controllers
  (:require [bank-customers.protocols.db-client :as db-client]
            [schema.core :as s]
            [bank-customers.db.datomic :as ddb]
            [bank-customers.models :as m]))

(s/defn get-customers-tax-ids :- m/TaxIds
  [db :- db-client/IDatabaseClient]
  (ddb/get-customers-tax-ids db))

(s/defn get-customer :- m/CustomerOperation
  [tax-id :- m/TaxId
   db :- db-client/IDatabaseClient]
  (let [customer (ddb/get-customer tax-id db)
        response {:customer customer}]
    (if (empty? customer)
      (assoc response :result :customer-not-found)
      (assoc response :result :is-customer))))

(s/defn add-customer :- m/CustomerOperation
  [customer :- m/Customer
   db :- db-client/IDatabaseClient]
  (let [response {:customer customer}]
    (if (ddb/add-customer customer db)
      (assoc response :result :customer-added-to-db)
      (assoc response :result :customer-already-exists-in-db))))