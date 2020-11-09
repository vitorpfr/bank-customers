(ns bank-customers.models
  (:require [schema.core :as s]))

(def TaxId s/Str)

(s/defschema TaxIds
  {:tax-ids [TaxId]})

(def Email s/Str)

(s/defschema Customer
  (s/either {:customer/name   s/Str
             :customer/email  Email
             :customer/tax-id TaxId}
            {}))