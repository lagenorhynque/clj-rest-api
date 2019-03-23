(ns clj-rest-api.boundary.db.artist
  (:require [clj-rest-api.util.const :as const]
            [clj-rest-api.boundary.db.core :as db]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-where]]))

(s/def ::id nat-int?)
(s/def ::type const/artist-types)
(s/def ::name string?)

(s/def ::artist
  (s/keys :req-un [::id
                   ::type
                   ::name]))

(s/fdef find-artist-by-id
  :args (s/cat :db ::db/db
               :id ::id)
  :ret (s/nilable ::artist))

(defprotocol Artist
  (find-artist-by-id [db id]))

(def sql-artist
  (sql/build
   :select [:a.*]
   :from [[:artist :a]]))

(extend-protocol Artist
  duct.database.sql.Boundary
  (find-artist-by-id [db id]
    (db/select-first db (merge-where sql-artist
                                     [:= :a.id id]))))
