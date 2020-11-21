(ns bank-customers.service
  (:require [com.stuartsierra.component :as component]
            [compojure.route :as comp-route]
            [compojure.core :as comp-core]
            [bank-customers.adapters :as a]
            [bank-customers.controllers :as c]
            [bank-customers.schemata.in :as schemata-in]
            [schema.core :as s]))

(defn ^:private success [body-content]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body-content})

(defn ^:private bad-request [body-content]
  {:status 400
   :body   body-content})

(defn ^:private unprocessable-entity [body-content]
  {:status 422
   :body   body-content})

(defn ^:private validate-user-inputs
  [validation-map]
  (reduce-kv (fn [acc input schema]
               (conj acc (try (s/validate schema input)
                              (catch Exception e))))
             []
             validation-map))

(defn ^:private not-valid-user-inputs?
  [validation-map]
  (some nil? (validate-user-inputs validation-map)))

(defn customers-handler
  [db _]
  (-> (c/get-customers-tax-ids db)
      a/tax-ids-internal->wire
      success))

(defn customer-handler
  [db
   {{tax-id :tax-id} :params}]
  (cond
    (nil? tax-id)
    (bad-request "A customer tax-id was not provided.")

    (not-valid-user-inputs? {tax-id schemata-in/TaxId})
    (unprocessable-entity "The tax-id provided is not valid (it must have 11 numerical digits).")

    :else
    (-> (c/get-customer tax-id db)
        a/customer-operation-internal->wire
        success)))

(defn add-customer-handler
  [db
   {{:keys [name email tax-id]} :body}]
  (cond
    (some nil? [name email tax-id])
    (bad-request "One or more of the required fields (name, email, tax-id) was not provided.")

    (not-valid-user-inputs? {name   schemata-in/Name
                             email  schemata-in/Email
                             tax-id schemata-in/TaxId})
    (unprocessable-entity "One or more of the required fields (name, email, tax-id) was provided in an invalid format.")

    :else
    (-> (c/add-customer {:customer/name   name
                         :customer/email  email
                         :customer/tax-id tax-id}
                        db)
        a/customer-operation-internal->wire
        success)))

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