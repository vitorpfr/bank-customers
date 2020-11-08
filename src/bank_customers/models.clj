(ns bank-customers.models
  (:require [schema.core :as s]))

; should this validation be here on in the schemata-in (when receiving data)?
(defn- valid-tax-id?
  [tax-id]
  (and (string? tax-id)
       (every? #(Character/isDigit %) tax-id)
       (= 9 (count tax-id))))

(def TaxId (s/pred valid-tax-id?))

(s/defschema TaxIds
  {:tax-ids [TaxId]})

; should this validation be here on in the schemata-in (when receiving data)?
(defn- valid-email?
  [email]
  (re-find #"@.*?\." email))

(def Email (s/pred valid-email?))

(s/defschema Customer
  {:name s/Str
   :email Email
   :tax-id TaxId})
