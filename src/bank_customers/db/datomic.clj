(ns bank-customers.db.datomic
  (:require [bank-customers.adapters :as a]
            [schema.core :as s]
            [bank-customers.protocols.db-client :as db-client]
            [bank-customers.models :as m]))

(s/defn get-customers-tax-ids :- m/TaxIds
  [db :- db-client/IDatabaseClient]
  (->> '[:find ?tax-id
         :where [_ :customer/tax-id ?tax-id]]
       (db-client/query db)
       (a/tax-ids-wire->internal)))

(s/defn get-customer :- m/Customer
  [tax-id :- m/TaxId
   db :- db-client/IDatabaseClient]
  (->> '[:find ?name ?email ?tax-id-searched
         :in $ ?tax-id-searched
         :where [?e :customer/tax-id ?tax-id-searched]
         [?e :customer/name ?name]
         [?e :customer/email ?email]
         [?e :customer/tax-id ?tax-id-searched]]
       (db-client/query-with-arg db tax-id)
       (a/customer-wire->internal)))

; WORK IN PROGRESS
(s/defn add-customer
  [customer
   db :- db-client/IDatabaseClient]
  (db-client/transact-entity! db [customer]))