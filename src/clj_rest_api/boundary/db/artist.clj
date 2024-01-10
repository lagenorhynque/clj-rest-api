(ns clj-rest-api.boundary.db.artist
  (:require
   [clj-rest-api.boundary.db.core :as db]
   [clj-rest-api.util.const :as const]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honey.sql.helpers :refer [where]]))

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
  {:select [:a.*]
   :from [[:artist :a]]})

(extend-protocol Artist
  duct.database.sql.Boundary
  (find-artist-by-id [db id]
    (db/select-first db (where sql-artist
                               [:= :a.id id]))))
