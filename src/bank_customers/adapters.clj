(ns bank-customers.adapters
  (:require [bank-customers.models :as m]
            [bank-customers.schemata.in :as schemata-in]
            [bank-customers.schemata.out :as schemata-out]
            [schema.core :as s]
            [clojure.data.json :as json]))

(s/defn tax-ids-wire->internal :- m/TaxIds
  [tax-ids :- schemata-in/TaxIds]
  {:tax-ids (flatten tax-ids)})

(s/defn tax-ids-internal->wire :- schemata-out/TaxIds
  [tax-ids :- m/TaxIds]
  (json/write-str tax-ids))

(s/defn customer-wire->internal :- m/Customer
  [customer :- schemata-in/Customer]
  (zipmap [:customer/name
           :customer/email
           :customer/tax-id]
          (first customer)))

(s/defn customer-internal->wire :- schemata-out/Customer
  [customer :- m/Customer]
  (json/write-str customer))

(s/defn customer-operation-internal->wire :- schemata-out/CustomerOperation
  [customer-op :- m/CustomerOperation]
  (json/write-str customer-op))