(ns bank-customers.models
  (:require [schema.core :as s]))

(def Name s/Str)
(def Email s/Str)
(def TaxId s/Str)

(s/defschema TaxIds
  {:tax-ids [TaxId]})

(s/defschema Customer
  (s/either {:customer/name   Name
             :customer/email  Email
             :customer/tax-id TaxId}
            {}))

(s/defschema CustomerOperation
  {:customer Customer
   :result   s/Keyword})