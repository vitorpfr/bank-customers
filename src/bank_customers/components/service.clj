(ns bank-customers.components.service
  (:require [com.stuartsierra.component :as component]
            [compojure.route :as comp-route]
            [compojure.core :as comp-core]
            [bank-customers.adapters :as a]
            [bank-customers.controllers :as c]
            [bank-customers.schemata.in :as schemata-in]
            [schema.core :as s]))

(defn- success [body-content]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body-content})

; TODO: move s/validates to a try/catch and returns bad-request if any of them fails
(defn- bad-request [missing-input]
  {:status  400
   :body    {:error (str "Input " missing-input " was not provided")}})

(defn customers-handler
  [db _]
  (-> (c/get-customers-tax-ids db)
      a/tax-ids-internal->wire
      success))

; should I use s/validate here or validate user input somewhere else?
(defn customer-handler
  [db
   {{tax-id :tax-id} :params}]
  (s/validate schemata-in/TaxId tax-id)
  (-> (c/get-customer tax-id db)
      a/customer-internal->wire
      success))

; should I use s/validate here or validate user input somewhere else?
(defn add-customer-handler
  [db
   {{:keys [name email tax-id]} :body}]
  (s/validate schemata-in/Name name)
  (s/validate schemata-in/Email email)
  (s/validate schemata-in/TaxId tax-id)
  (-> (c/add-customer {:customer/name name
                       :customer/email email
                       :customer/tax-id tax-id}
                      db)
      a/customer-internal->wire
      success))

(defn app-routes
  [db]
  (comp-core/routes
    (comp-core/GET "/customers" request (customers-handler db request))
    (comp-core/GET "/customer" request (customer-handler db request))
    (comp-core/POST "/addcustomer" request (add-customer-handler db request))
    (comp-route/not-found "Error, page not found!")))

(defrecord Service [routes db]
  component/Lifecycle
  (start [this]
    (assoc this :service-routes (routes db)))

  (stop [this]
    (assoc this :service-routes nil)))

(defn new-service [] (map->Service {:routes app-routes}))