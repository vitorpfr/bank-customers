(ns bank-customers.components.service
  (:require [com.stuartsierra.component :as component]
            [compojure.route :as comp-route]
            [compojure.core :as comp-core]
            [bank-customers.adapters :as a]
            [bank-customers.controllers :as c]))

(defn- success [body-content]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body-content})

(defn- bad-request [missing-input]
  {:status  400
   :body    {:error (str "Input " missing-input " was not provided")}})

(defn customers-handler
  [db _]
  (-> (c/get-customers-tax-ids db)
      a/tax-ids-internal->wire
      success))

(defn customer-handler
  [db request]
  (let [{{tax-id :tax-id} :params} request]
    (if tax-id
      (-> (c/get-customer tax-id db)
          a/customer-internal->wire
          success)
      (bad-request "tax-id"))))

; TODO: Handler to add new customers to DB

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