(ns clj-rest-api.boundary.db.core
  (:require
   [clj-rest-api.util.core :as util]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honey.sql :as sql]
   [next.jdbc]
   [next.jdbc.prepare]
   [next.jdbc.quoted]
   [next.jdbc.result-set]
   [next.jdbc.sql])
  (:import
   (java.time
    LocalDate)))

;;; JDBC date conversion

(extend-protocol next.jdbc.result-set/ReadableColumn
  java.sql.Date
  (read-column-by-index [^java.sql.Date v _ _]
    (.toLocalDate v))
  (read-column-by-label [^java.sql.Date v _ _]
    (.toLocalDate v)))

(extend-protocol next.jdbc.prepare/SettableParameter
  LocalDate
  (set-parameter [^LocalDate v ^java.sql.PreparedStatement stmt ^long ix]
    (.setDate stmt ix (java.sql.Date/valueOf v))))

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
    `(next.jdbc/with-transaction [datasource# (:spec ~db)]
       (let [~db (duct.database.sql/->Boundary {:datasource datasource#})]
         ~@body))
    `(next.jdbc/with-transaction [datasource# (:spec ~db)]
       (let [~'db (duct.database.sql/->Boundary {:datasource datasource#})]
         ~@body))))

(def sql-format-opts
  {:dialect :mysql
   :allow-dashed-names? true
   :quoted-snake true})

(s/fdef select
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret (s/coll-of ::row-map))

(defn select [{{:keys [datasource]} :spec} sql-map]
  (next.jdbc.sql/query datasource (sql/format sql-map sql-format-opts)
                       {:builder-fn next.jdbc.result-set/as-unqualified-kebab-maps}))

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

(defn insert! [{{:keys [datasource]} :spec} table row-map]
  (-> datasource
      (next.jdbc.sql/insert! table
                             row-map
                             {:table-fn (next.jdbc.quoted/schema next.jdbc.quoted/mysql)
                              :column-fn (comp util/->snake_case
                                               next.jdbc.quoted/mysql)
                              :builder-fn next.jdbc.result-set/as-unqualified-kebab-maps})
      :insert-id))

(s/fdef execute!
  :args (s/cat :db ::db
               :sql-map ::sql-map)
  :ret ::row-count)

(defn execute! [{{:keys [datasource]} :spec} sql-map]
  (-> datasource
      (next.jdbc/execute-one! (sql/format sql-map sql-format-opts))
      :next.jdbc/update-count))

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

(defn insert-multi! [{{:keys [datasource]} :spec} table row-maps]
  (-> datasource
      (next.jdbc/execute-one!  (sql/format {:insert-into table
                                            :values row-maps}
                                           sql-format-opts))
      :next.jdbc/update-count))
