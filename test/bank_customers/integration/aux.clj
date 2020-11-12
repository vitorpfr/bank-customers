(ns bank-customers.integration.aux
  (:require [clojure.test :refer :all]
            [bank-customers.components :as components]))

(defn test-url
  ([] (test-url "" ""))
  ([endpoint] (test-url endpoint ""))
  ([endpoint arg]
   (let [test-port (get-in components/system-config [:test :server-port])]
     (str "http://localhost:" test-port "/" endpoint arg))))