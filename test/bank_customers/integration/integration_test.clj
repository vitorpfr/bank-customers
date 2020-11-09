(ns bank-customers.integration.integration-test
  (:require [clojure.test :refer :all]
            [bank-customers.components :as components]
            [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [schema.test :as s.test]
            [org.httpkit.client :as client]
            [bank-customers.db.datomic :as ddb]))

(def test-server (atom nil))

(defn with-test-server [f]
  (reset! test-server (components/start-system :test))
  (f)
  (component/stop @test-server))

(use-fixtures :each with-test-server)
(use-fixtures :once s.test/validate-schemas)

(defn test-url
  ([] (test-url "" ""))
  ([endpoint] (test-url endpoint ""))
  ([endpoint arg]
   (let [test-port (get-in components/system-config [:test :port])]
     (str "http://localhost:" test-port "/" endpoint arg))))

(deftest customers-endpoint-test
  (testing "customers http request returns JSON with tax-ids"
    (let [response (-> (client/request {:url (test-url "customers")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {"tax-ids" []}
             (json/read-str (:body response)))))))

(deftest get-customer-endpoint-test
  (testing "get customer http request returns JSON data of a specific customer"
    (let [response (-> (client/request {:url (test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {}
             (json/read-str (:body response)))))

    ; TODO: When add-customer endpoint is implemented, remove ddb ns reference and use endpoint to add customer instead
    (ddb/add-customer {:customer/name   "John"
                       :customer/email  "john@gmail.com"
                       :customer/tax-id "12345678912"}
                      (-> test-server
                          deref
                          :db))

    (let [response (-> (client/request {:url (test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {"email"  "john@gmail.com"
              "name"   "John"
              "tax-id" "12345678912"}
             (json/read-str (:body response)))))))