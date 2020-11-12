(ns bank-customers.integration.integration-test
  (:require [clojure.test :refer :all]
            [bank-customers.components :as components]
            [com.stuartsierra.component :as component]
            [clojure.data.json :as json]
            [schema.test :as s.test]
            [org.httpkit.client :as client]
            [bank-customers.db.datomic :as ddb]
            [bank-customers.integration.aux :as aux]))

(def test-server (atom nil))

(defn with-test-server [f]
  (reset! test-server (components/start-system :test))
  (f)
  (component/stop @test-server))

(use-fixtures :each with-test-server)
(use-fixtures :once s.test/validate-schemas)

(deftest add-and-get-all-customers
  (testing "customers http request on empty db returns JSON with no tax-ids"
    (let [response (-> (client/request {:url (aux/test-url "customers")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {"tax-ids" []}
             (json/read-str (:body response))))))

  (testing "valid customer is added to database"
    (let [valid-customer {:name "Peter Parker"
                          :email "peter@gmail.com"
                          :tax-id "12345655599"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (json/write-str valid-customer)})
                       deref)]
      (println response)
      (is (= 200
             (:status response)))
      (is (= {"name" "Peter Parker"
              "email" "peter@gmail.com"
              "tax-id" "12345655599"}
             (json/read-str (:body response))))))

  (testing "customers http request on db returns JSON with one tax-id"
    (let [response (-> (client/request {:url (aux/test-url "customers")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {"tax-ids" ["12345655599"]}
             (json/read-str (:body response)))))))

(deftest add-and-get-specific-customer
  (testing "get customer http request returns empty JSON for non-existing customer"
    (let [response (-> (client/request {:url (aux/test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {}
             (json/read-str (:body response))))))

  (testing "valid customer is added to database"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (json/write-str valid-customer)})
                       deref)]
      (println response)
      (is (= 200
             (:status response)))
      (is (= {"name" "John"
              "email" "john@gmail.com"
              "tax-id" "12345678912"}
             (json/read-str (:body response))))))

  (testing "get customer HTTP request returns data of existing tax-id asked"
    (let [response (-> (client/request {:url (aux/test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {"email"  "john@gmail.com"
              "name"   "John"
              "tax-id" "12345678912"}
             (json/read-str (:body response)))))))