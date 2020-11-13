(ns bank-customers.unit.db.datomic-test
  (:require [clojure.test :refer :all]
            [bank-customers.db.datomic :as ddb]
            [com.stuartsierra.component :as component]
            [bank-customers.components :as components]
            [schema.test :as s.test]
            [datomic.api :as d])
  (:import (clojure.lang ExceptionInfo)))

(def test-server (atom nil))

(defn with-test-server [f]
  (reset! test-server (components/start-system :test))
  (f)
  (component/stop @test-server))

(use-fixtures :each with-test-server)
(use-fixtures :once s.test/validate-schemas)

(deftest get-customers-tax-id
  (testing "function call returns all tax-ids stored in db at that moment"
    (let [db (-> test-server
                 deref
                 :db)]
      (is (= (ddb/get-customers-tax-ids db)
             {:tax-ids ()}))

      (ddb/add-customer {:customer/name   "John"
                         :customer/email  "john@gmail.com"
                         :customer/tax-id "12345678913"}
                        db)

      (is (= (ddb/get-customers-tax-ids db)
             {:tax-ids ["12345678913"]})))))

(deftest get-customer
  (testing "function call returns desired customer"
    (let [db (-> test-server
                 deref
                 :db)]
      (ddb/add-customer {:customer/name   "John"
                         :customer/email  "john@gmail.com"
                         :customer/tax-id "12345678912"}
                        db)
      (is (= (ddb/get-customer "12345678955" db)
             {}))

      (is (= (ddb/get-customer "12345678912" db)
             {:customer/name   "John"
              :customer/email  "john@gmail.com"
              :customer/tax-id "12345678912"})))))

(deftest add-customer
  (testing "throws exception if added customer is incomplete"
    (let [db (-> test-server
                 deref
                 :db)]
      (is (thrown-with-msg?
            ExceptionInfo
            #"Input to ([^\s]+) does not match schema"
            (ddb/add-customer {:customer/name   "John"
                               :customer/email  "john@gmail.com"}
                              db)))))

  (testing "db is modified successfully with new added customer"
    (let [db (-> test-server
                 deref
                 :db)
          db-after-transaction (-> (ddb/add-customer {:customer/name   "John"
                                                      :customer/email  "john@gmail.com"
                                                      :customer/tax-id "12345678914"}
                                                     db)
                                   deref
                                   :db-after)]
      (is (= (d/db (:connection db))
             db-after-transaction)))))


