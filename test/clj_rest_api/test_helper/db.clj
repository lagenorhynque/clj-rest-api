(ns clj-rest-api.test-helper.db
  (:require
   [clj-rest-api.boundary.db.core :as db]
   [clojure.spec.alpha :as s]
   [next.jdbc]))

(s/def ::name string?)
(s/def ::table (s/keys :req-un [::name]))

(s/def ::db-data-map
  (s/map-of keyword?
            (s/coll-of ::db/row-map :min-count 1)))

(s/fdef select-tables
  :args (s/cat :db ::db/db)
  :ret (s/coll-of ::table))

(defn select-tables [db]
  (db/select db {:select [[[:concat :table-schema "." :table-name] :name]]
                 :from :information-schema.tables
                 :where [:= :table-type "BASE TABLE"]}))

(s/fdef truncate-table!
  :args (s/cat :db ::db/db
               :table ::table)
  :ret any?)

(defn truncate-table! [{{:keys [datasource]} :spec} table]
  (next.jdbc/execute! datasource [(str "truncate table " (:name table))]))

(s/fdef set-foreign-key-checks!
  :args (s/cat :db ::db/db
               :enabled? boolean?)
  :ret any?)

(defn set-foreign-key-checks! [{{:keys [datasource]} :spec} enabled?]
  (next.jdbc/execute! datasource [(str "set @@session.foreign_key_checks = "
                                       (if enabled? 1 0))]))

(s/fdef insert-db-data!
  :args (s/cat :db ::db/db
               :db-data-map ::db-data-map)
  :ret any?)

(defn insert-db-data! [db db-data-map]
  (set-foreign-key-checks! db false)
  (doseq [[table records] db-data-map]
    (db/insert-multi! db table records))
  (set-foreign-key-checks! db true))

(s/fdef truncate-all-tables!
  :args (s/cat :db ::db/db)
  :ret any?)

(defn truncate-all-tables! [db]
  (set-foreign-key-checks! db false)
  (doseq [table (select-tables db)]
    (truncate-table! db table))
  (set-foreign-key-checks! db true))
