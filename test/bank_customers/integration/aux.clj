(ns bank-customers.integration.aux
  (:require [clojure.test :refer :all]
            [bank-customers.components :as components]
            [clojure.data.json :as json]))

(defn test-url
  ([] (test-url "" ""))
  ([endpoint] (test-url endpoint ""))
  ([endpoint arg]
   (let [test-port (get-in components/system-config [:test :server-port])]
     (str "http://localhost:" test-port "/" endpoint arg))))

(defn write-json
  [clj-map]
  (json/write-str clj-map))

(defn read-json
  [json-str]
  (json/read-str json-str :key-fn keyword))