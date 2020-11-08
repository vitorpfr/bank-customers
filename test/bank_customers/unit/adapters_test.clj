(ns bank-customers.unit.adapters-test
  (:require [clojure.test :refer :all]
            [bank-customers.adapters :as a]
            [schema.test :as s.test]
            [clojure.data.json :as json])
  (:import (clojure.lang ExceptionInfo)))

(use-fixtures :once s.test/validate-schemas)

(defn- json=
  "Validates if expected matches result content, when result is in a json format"
  [expected result]
  (= expected (json/read-str result)))

(deftest tax-ids-test
  (testing "wire tax-id list with wrong schema as input throws exception"
    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to tax-ids-wire->internal does not match schema"
          (a/tax-ids-wire->internal [[123456789] ["213456789"]])))

    (is (thrown-with-msg?
          ExceptionInfo
          #"Input to tax-ids-wire->internal does not match schema"
          (a/tax-ids-wire->internal [["123456789" "513456789"] ["213456789"]]))))

  (testing "wire tax-id list from DB is converted to internal tax-id list"
    (is (= {:tax-ids ["123456789" "213456789" "513456789"]}
           (a/tax-ids-wire->internal [["123456789"] ["213456789"] ["513456789"]]))))

  (testing "internal tax-id list from is converted to wire tax-id list to http"
    (is (json= {"tax-ids" ["123456789" "213456789" "513456789"]}
               (a/tax-ids-internal->wire {:tax-ids ["123456789" "213456789" "513456789"]})))))


