(ns bank-customers.unit.controllers-test
  (:require [clojure.test :refer :all]
            [bank-customers.controllers :as c]
            [bank-customers.components :as components]
            [com.stuartsierra.component :as component]
            [schema.test :as s.test])
  (:import (clojure.lang ExceptionInfo)))

(def test-server (atom nil))
(defn get-db [test-server] (:db @test-server))

(defn with-test-server [f]
  (reset! test-server (components/start-system :test))
  (f)
  (component/stop @test-server))

(use-fixtures :each with-test-server)
(use-fixtures :once s.test/validate-schemas)

(deftest get-customers-tax-ids-test
  (testing "throws exception if not provided a valid db component"
    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (c/get-customers-tax-ids nil)))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (c/get-customers-tax-ids {:customers []}))))

  (testing "returns tax-id in db when it is provided correctly"
    (is (= {:tax-ids []}
           (c/get-customers-tax-ids (get-db test-server))))))

(deftest add-customer-test
  (testing "throws exception if not provided a valid db or customer"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}
          invalid-customer {:customer/name   "John"
                            :customer/email  "john@gmail.com"
                            :customer/tax-id 123}]

      (is (thrown-with-msg?
            ExceptionInfo
            #"Input to ([^\s]+) does not match schema"
            (c/add-customer! valid-customer nil)))

      (is (thrown-with-msg?
            ExceptionInfo
            #"Input to ([^\s]+) does not match schema"
            (c/add-customer! invalid-customer (get-db test-server))))))

  (testing "returns confirmation of provided customer added to db"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}]
      (is (= {:customer valid-customer
              :result   :customer-added-to-db}
             (c/add-customer! valid-customer (get-db test-server))))))

  (testing "returns customer-already-exists results if trying to add again the same customer"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}]
      (is (= {:customer valid-customer
              :result   :customer-already-exists-in-db}
             (c/add-customer! valid-customer (get-db test-server)))))))

(deftest get-customer-test
  (testing "throws exception if not provided a valid db or tax-id"
    (let [valid-tax-id "12345678911"
          invalid-tax-id 123]

      (is (thrown-with-msg?
            ExceptionInfo
            #"Input to ([^\s]+) does not match schema"
            (c/get-customer valid-tax-id nil)))

      (is (thrown-with-msg?
            ExceptionInfo
            #"Input to ([^\s]+) does not match schema"
            (c/get-customer invalid-tax-id (get-db test-server))))))


  (testing "returns customer if existing customer in db provided"
    (let [valid-customer {:customer/name   "John"
                          :customer/email  "john@gmail.com"
                          :customer/tax-id "12345678912"}]
      (c/add-customer! valid-customer (get-db test-server))
      (is (= {:customer valid-customer
              :result   :is-customer}
             (c/get-customer (:customer/tax-id valid-customer) (get-db test-server))))))

  (testing "returns customer-not-found message if trying to find tax-id not in db"
    (is (= {:customer {}
            :result   :customer-not-found}
           (c/get-customer "55555555555" (get-db test-server))))))

