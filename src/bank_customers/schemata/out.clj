(ns bank-customers.schemata.out
  (:require [schema.core :as s]
            [clojure.data.json :as json]))

(defn json-string?
  [s]
  (try (json/read-str s)
       (catch Exception _)))

(def TaxIds (s/pred json-string?))

(def Customer (s/pred json-string?))

(def CustomerOperation (s/pred json-string?))