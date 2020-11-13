(ns bank-customers.schemata.in
  (:require [schema.core :as s]))

(defn- valid-tax-id?
  [tax-id]
  (and (string? tax-id)
       (every? #(Character/isDigit %) tax-id)
       (= 11 (count tax-id))))

(def TaxId (s/pred valid-tax-id?))

(s/defschema TaxIds (s/both [[(s/one TaxId "s")]]
                            (s/pred vector?)))

(defn- valid-email?
  [email]
  (re-find #"@.*?\." email))

(def Name s/Str)
(def Email (s/pred valid-email?))

(s/defschema Customer (s/both [[(s/one Name "name")
                                (s/one Email "email")
                                (s/one TaxId "tax-id")]]
                              (s/pred vector?)
                              (s/pred #(<= (count %) 1))))
