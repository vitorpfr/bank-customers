(ns bank-customers.components.service
  (:require [com.stuartsierra.component :as component]
            [compojure.route :as comp-route]
            [compojure.core :as comp-core]
            [bank-customers.adapters :as a]
            [bank-customers.controllers :as c]
            [clojure.data.json :as json]))

(defn ^:private success [body-content]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body-content})

(defn customers-handler
  [db _]
  (let [])
  (-> (c/get-customers-tax-ids db)
      a/tax-ids-internal->wire
      success))

(defn customer-handler
  [db request]
  (let [{{tax-id :tax-id} :params} request]
    (-> (c/get-customer tax-id db)
        (clojure.data.json/write-str)
        success)))

(defn app-routes
  [db]
  (comp-core/routes
    (comp-core/GET "/customers" request (customers-handler db request))
    (comp-core/GET "/customer" request (customer-handler db request))
    ;(comp-core/POST "/add" request (add-customer-handler db request))
    (comp-route/not-found "Error, page not found!")))

(defrecord Service [routes db]
  component/Lifecycle
  (start [this]
    (assoc this :service-routes (routes db)))

  (stop [this]
    (assoc this :service-routes nil)))

(defn new-service [] (map->Service {:routes app-routes}))