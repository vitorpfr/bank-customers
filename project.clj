(defproject bank-customers "0.1.0-SNAPSHOT"
  :description "A service that stores customer data on a database"
  :url "TODO"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "0.2.6"]
                 [com.datomic/datomic-pro "1.0.6202"]
                 [com.stuartsierra/component "1.0.0"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [prismatic/schema "1.1.12"]]
  :source-paths ["src"]
  :test-paths ["test"]
  :repl-options {:init-ns bank-customers.core}
  :main bank-customers.core)
