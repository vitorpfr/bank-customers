(ns bank-customers.schemata.in
  (:require [schema.core :as s]))

(def TaxId s/Str)
(s/defschema TaxIds [[(s/one TaxId "s")]])

