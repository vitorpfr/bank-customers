(ns bank-customers.integration.integration-test
  (:require [clojure.test :refer :all]
            [bank-customers.components :as components]
            [com.stuartsierra.component :as component]
            [schema.test :as s.test]
            [org.httpkit.client :as client]
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
    (let [response (-> (client/request {:url    (aux/test-url "customers")
                                        :method :get})
                       deref)]
      (println (:http-server @test-server))
      (is (= 200
             (:status response)))
      (is (= {:tax-ids []}
             (aux/read-json (:body response))))))

  (testing "valid customer is added to database"
    (let [valid-customer {:name   "Peter Parker"
                          :email  "peter@gmail.com"
                          :tax-id "12345655599"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (aux/write-json valid-customer)})
                       deref)]
      (println response)
      (is (= 200
             (:status response)))
      (is (= {:customer {:name   "Peter Parker"
                         :email  "peter@gmail.com"
                         :tax-id "12345655599"}
              :result   "customer-added-to-db"}
             (aux/read-json (:body response))))))

  (testing "customers http request on db returns JSON with one tax-id"
    (let [response (-> (client/request {:url    (aux/test-url "customers")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {:tax-ids ["12345655599"]}
             (aux/read-json (:body response)))))))

(deftest add-and-get-specific-customer
  (testing "get customer http request returns empty JSON for non-existing customer"
    (let [response (-> (client/request {:url    (aux/test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {:customer {}
              :result   "customer-not-found"}
             (aux/read-json (:body response))))))

  (testing "valid customer is added to database"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (aux/write-json valid-customer)})
                       deref)]
      (println response)
      (is (= 200
             (:status response)))
      (is (= {:customer {:name   "John"
                         :email  "john@gmail.com"
                         :tax-id "12345678912"}
              :result   "customer-added-to-db"}
             (aux/read-json (:body response))))))

  (testing "get customer HTTP request returns data of existing tax-id asked"
    (let [response (-> (client/request {:url    (aux/test-url "customer" "?tax-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 200
             (:status response)))
      (is (= {:customer {:email  "john@gmail.com"
                         :name   "John"
                         :tax-id "12345678912"}
              :result   "is-customer"}
             (aux/read-json (:body response)))))))


(deftest invalid-customer-consult
  (testing "trying to consult customer without providing a tax-id returns error"
    (let [response (-> (client/request {:url    (aux/test-url "customer" "?tx-id=12345678912")
                                        :method :get})
                       deref)]
      (is (= 400
             (:status response)))
      (is (= "A customer tax-id was not provided."
             (:body response)))))

  (testing "trying to consult customer with an invalid tax-id returns error"
    (let [response (-> (client/request {:url    (aux/test-url "customer" "?tax-id=178912")
                                        :method :get})
                       deref)]
      (is (= 422
             (:status response)))
      (is (= "The tax-id provided is not valid (it must have 11 numerical digits)."
             (:body response))))))

(deftest add-invalid-customer
  (testing "trying to add customer with missing data returns error"
    (let [customer-missing-data {:name  "Peter Parker"
                                 :email "peter@gmail.com"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (aux/write-json customer-missing-data)})
                       deref)]
      (println response)
      (is (= 400
             (:status response)))
      (is (= "One or more of the required fields (name, email, tax-id) was not provided."
             (:body response)))))

  (testing "trying to add customer with invalid data format returns error"
    (let [invalid-customer {:name   "Peter Parker"
                            :email  "peter@gmail.com"
                            :tax-id "12345678"}
          response (-> (client/request {:url     (aux/test-url "addcustomer")
                                        :method  :post
                                        :headers {"Content-Type" "application/json"}
                                        :body    (aux/write-json invalid-customer)})
                       deref)]
      (println response)
      (is (= 422
             (:status response)))
      (is (= "One or more of the required fields (name, email, tax-id) was provided in an invalid format."
             (:body response))))))