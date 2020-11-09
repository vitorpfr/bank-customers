(ns bank-customers.temp.db-example
  (:require [datomic.api :as d]))

;;;;; MODIFY DB (CREATE/DELETE) ;;;;;;;;;;;;
;; creating and connecting to db
(def db-url "datomic:dev://localhost:4334/bank-customers")
;(d/create-database db-url)

(defn open-connection []
  (d/connect db-url))

(def conn (open-connection))

conn

;; delete db
(defn delete-db []
  (d/delete-database db-url))
;(delete-db)

;; defining db customer schema
(def customer-schema
  [{:db/ident       :customer/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Full name of a customer"}

   {:db/ident       :customer/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "E-mail of a customer"}

   {:db/ident       :customer/tax-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Tax id of a customer"}])

;(d/transact conn customer-schema)


;; adding new customers to db
(defn new-customer [name email tax-id]
  {:customer/name   name
   :customer/email  email
   :customer/tax-id tax-id})

(def first-customers
  [(new-customer "John Smith" "john@gmail.com" "12345678911")
   (new-customer "Mary Roberts" "mary@gmail.com" "12345677511")])

(d/transact conn first-customers)

;;;;;;;;; QUERY DB ;;;;;;;;;;;;;
; THIS LINE SAVES THE DB IN MEMORY (in the db symbol)
(def db (d/db conn))

; get all entities (customers)
(def all-customers-q '[:find ?e
                       :where [?e :customer/name]])
(d/q all-customers-q db)

; get all customer names
(def all-customer-names-q '[:find ?customer-name
                            :where [_ :customer/name ?customer-name]])

(d/q all-customer-names-q db)

; get customers with email equal to john@gmail.com
(def customers-with-john-email '[:find ?name
                                 :where [?e :customer/name ?name]
                                 [?e :customer/email "john@gmail.com"]])

(d/q customers-with-john-email db)

;; get all data in DB
(def all-data
  '[:find ?name ?email ?tax-id
    :where [?e :customer/name ?name]
    [?e :customer/name ?name]
    [?e :customer/email ?email]
    [?e :customer/tax-id ?tax-id]])

(d/q all-data db)


;; create in-memory DB for testing
(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://bank-customers-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)]
      (d/transact conn customer-schema)
      conn)))

(def conn (create-empty-in-memory-db))

conn
