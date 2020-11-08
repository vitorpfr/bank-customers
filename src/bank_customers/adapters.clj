(ns bank-customers.adapters
  (:require [bank-customers.models :as m]
            [bank-customers.schemata.in :as schemata-in]
            [bank-customers.schemata.out :as schemata-out]
            [schema.core :as s]
            [clojure.data.json :as json]))

(s/set-fn-validation! true)

(s/defn tax-ids-wire->internal :- m/TaxIds
  [tax-ids :- schemata-in/TaxIds]
  {:tax-ids (flatten tax-ids)})

(s/defn tax-ids-internal->wire :- schemata-out/TaxIds
  [tax-ids :- m/TaxIds]
  (json/write-str tax-ids))