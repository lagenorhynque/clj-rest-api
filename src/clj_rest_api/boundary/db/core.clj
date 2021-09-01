(ns clj-rest-api.boundary.db.core
  (:require
   [clj-rest-api.util.core :as util]
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honeysql.core :as sql])
  (:import
   (java.time
    LocalDate)))

;;; JDBC date conversion

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _]
    (.toLocalDate v)))

(extend-protocol jdbc/ISQLValue
  LocalDate
  (sql-value [v]
    (java.sql.Date/valueOf v)))

;;; DB access utilities

(s/def ::spec any?)
(s/def ::db (s/keys :req-un [::spec]))
(s/def ::sql-map (s/map-of keyword? any?))
(s/def ::table keyword?)
(s/def ::row-map (s/map-of keyword? any?))
(s/def ::row-count (s/and integer? (complement neg?)))
(s/def ::row-id (s/and integer? pos?))

(s/fdef with-transaction
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 1)
               :body (s/* any?)))

(defmacro with-transaction [[db] & body]
  (if (simple-symbol? db)
    `(jdbc/with-db-transaction [~db (:spec ~db)]
       (let [~db (duct.database.sql/->Boundary ~db)]
         ~@body))
    `(jdbc/with-db-transaction [~'db (:spec ~db)]
       (let [~'db (duct.database.sql/->Boundary ~'db)]
         ~@body))))

(def quoting :mysql)
(def identifier-quote \`)

(s/fdef select
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/coll-of ::row-map))

(defn select [{:keys [spec]} sql-map]
  (jdbc/query spec (sql/format sql-map :quoting quoting)
              {:identifiers util/->kebab-case}))

(s/fdef select-first
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/nilable ::row-map))

(defn select-first [db sql-map]
  (first (select db sql-map)))

(s/fdef insert!
  :args (s/cat :db ::db
               :table ::table
               :row-map ::row-map)
  :ret ::row-id)

(defn insert! [{:keys [spec]} table row-map]
  (-> (jdbc/insert! spec table row-map {:entities (comp (jdbc/quoted identifier-quote)
                                                        util/->snake_case)})
      first
      :insert_id))

(s/fdef execute!
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret ::row-count)

(defn execute! [{:keys [spec]} sql-map]
  (first (jdbc/execute! spec (sql/format sql-map :quoting quoting))))

(s/fdef insert-multi!
  :args (s/cat :db ::db
               :table ::table
               :row-maps (s/coll-of ::row-map :min-count 1))
  :ret ::row-count)

(s/fdef insert-multi!
  :args (s/cat :db ::db
               :table ::table
               :row-maps (s/coll-of ::row-map :min-count 1))
  :ret ::row-count)

(defn insert-multi! [{:keys [spec]} table row-maps]
  (first (jdbc/execute! spec (sql/format (sql/build :insert-into table
                                                    :values row-maps)
                                         :quoting quoting))))
