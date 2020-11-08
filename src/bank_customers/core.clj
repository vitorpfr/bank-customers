(ns bank-customers.core
  (:require [bank-customers.components :as components]))

(defn -main [& args]
  (components/start-all))
