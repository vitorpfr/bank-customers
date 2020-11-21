(ns bank-customers.unit.db.datomic-test
  (:require [clojure.test :refer :all]
            [bank-customers.db.datomic :as ddb]
            [com.stuartsierra.component :as component]
            [bank-customers.components :as components]
            [schema.test :as s.test]
            [datomic.api :as d])
  (:import (clojure.lang ExceptionInfo)))

(def test-server (atom nil))
(defn get-db [test-server] (:db @test-server))

(defn with-test-server [f]
  (reset! test-server (components/start-system :test))
  (f)
  (component/stop @test-server))

(use-fixtures :each with-test-server)
(use-fixtures :once s.test/validate-schemas)

(deftest get-customers-tax-id
  (testing "function call returns all tax-ids stored in db at that moment"
    (is (= (ddb/get-customers-tax-ids (get-db test-server))
           {:tax-ids ()}))

    (ddb/add-customer! {:customer/name  "John"
                       :customer/email  "john@gmail.com"
                       :customer/tax-id "12345678913"}
                       (get-db test-server))

    (is (= (ddb/get-customers-tax-ids (get-db test-server))
           {:tax-ids ["12345678913"]}))))

(deftest get-customer
  (testing "function call returns desired customer"
    (ddb/add-customer! {:customer/name  "John"
                       :customer/email  "john@gmail.com"
                       :customer/tax-id "12345678912"}
                       (get-db test-server))

    (is (= (ddb/get-customer "12345678955" (get-db test-server))
           {}))

    (is (= (ddb/get-customer "12345678912" (get-db test-server))
           {:customer/name   "John"
            :customer/email  "john@gmail.com"
            :customer/tax-id "12345678912"}))))

(deftest add-customer
  (testing "throws exception if added customer is invalid"
    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (ddb/add-customer! {:customer/name "John"
                             :customer/email "john@gmail.com"}
                             (get-db test-server)))))

  (testing "db is modified successfully with new added customer"
    (let [db-on-transaction-response (:db-after @(ddb/add-customer! {:customer/name  "John"
                                                                    :customer/email  "john@gmail.com"
                                                                    :customer/tax-id "12345678914"}
                                                                    (get-db test-server)))
          db-consulted-after-transaction (d/db (get-in @test-server [:db :connection]))]
      (is (= db-on-transaction-response
             db-consulted-after-transaction))))

  (testing "db is not modified if customer already exists in there"
    (let [db-consulted-before-transaction (d/db (get-in @test-server [:db :connection]))
          transaction-response (ddb/add-customer! {:customer/name  "John"
                                                  :customer/email  "john@gmail.com"
                                                  :customer/tax-id "12345678914"}
                                                  (get-db test-server))
          db-consulted-after-transaction (d/db (get-in @test-server [:db :connection]))
          ]
      (is (= db-consulted-before-transaction
             db-consulted-after-transaction))

      (is (nil? transaction-response)))))
