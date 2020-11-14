(ns bank-customers.unit.adapters-test
  (:require [clojure.test :refer :all]
            [bank-customers.adapters :as a]
            [schema.test :as s.test]
            [clojure.data.json :as json])
  (:import (clojure.lang ExceptionInfo)))

(use-fixtures :once s.test/validate-schemas)

(defn ^:private json=
  "Validates if expected matches result content, when result is in a json format"
  [expected result]
  (= expected (json/read-str result)))

(deftest tax-ids-test
  (testing "wire tax-id list with wrong schema as input throws exception"
    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal [[12345678911] ["21345678911"]])))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal 1)))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal "test")))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal nil)))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal {})))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/tax-ids-wire->internal [["12345678911" "51345678911"] ["21345678911"]]))))

  (testing "wire tax-id list from DB is converted to internal tax-id list"
    (is (= {:tax-ids ["12345678911" "21345678911" "51345678911"]}
           (a/tax-ids-wire->internal [["12345678911"] ["21345678911"] ["51345678911"]])))

    (is (= {:tax-ids ["12345678911"]}
           (a/tax-ids-wire->internal [["12345678911"]])))

    (is (= {:tax-ids []}
           (a/tax-ids-wire->internal []))))

  (testing "internal tax-id list from is converted to wire tax-id list to http"
    (is (json= {"tax-ids" ["12345678911" "21345678911" "51345678911"]}
               (a/tax-ids-internal->wire {:tax-ids ["12345678911" "21345678911" "51345678911"]})))

    (is (json= {"tax-ids" ["12345678911"]}
               (a/tax-ids-internal->wire {:tax-ids ["12345678911"]})))

    (is (json= {"tax-ids" []}
               (a/tax-ids-internal->wire {:tax-ids []})))))


(deftest customer-test
  (testing "wire customer with wrong schema throws exception"
    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/customer-wire->internal [["Joseph" "joseph@gmail.com"]])))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/customer-wire->internal [["Joseph" 5 7]])))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/customer-wire->internal nil)))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to ([^\s]+) does not match schema"
          (a/customer-wire->internal {}))))

  (testing "wire customer from DB is converted to internal customer"
    (is (= {:customer/name   "Joseph"
            :customer/email  "joseph@gmail.com"
            :customer/tax-id "12398745611"}
           (a/customer-wire->internal [["Joseph" "joseph@gmail.com" "12398745611"]]))))

  (testing "no customer customer from DB results in empty map"
    (is (= {}
           (a/customer-wire->internal []))))

  (testing "internal customer is converted to wire to http"
    (is (json= {"name"   "Joseph"
                "email"  "joseph@gmail.com"
                "tax-id" "12398745611"}
               (a/customer-internal->wire {:customer/name   "Joseph"
                                           :customer/email  "joseph@gmail.com"
                                           :customer/tax-id "12398745611"})))))

(deftest customer-operation-test
  (testing "internal customer operation is converted to wire successfully"
    (is (json= {"customer" {"name"   "Joseph"
                            "email"  "joseph@gmail.com"
                            "tax-id" "12398745611"}
                "result"   "some-result"}
               (a/customer-operation-internal->wire {:customer {:customer/name   "Joseph"
                                                                :customer/email  "joseph@gmail.com"
                                                                :customer/tax-id "12398745611"}
                                                     :result   :some-result})))))