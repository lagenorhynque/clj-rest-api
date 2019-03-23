(ns clj-rest-api.boundary.db.song
  (:require [clj-rest-api.boundary.db.artist :as artist]
            [clj-rest-api.boundary.db.core :as db]
            [clojure.spec.alpha :as s]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-order-by merge-where]])
  (:import (java.time LocalDate)))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::artist-id ::artist/id)
(s/def ::release-date #(instance? LocalDate %))

(s/def ::artist-name ::artist/name)
(s/def ::artist-type ::artist/type)

(s/def ::song
  (s/keys :req-un [::id
                   ::name
                   ::artist-id
                   ::release-date]
          :opt-un [::artist-name
                   ::artist-type]))

(s/fdef find-songs
  :args (s/cat :db ::db/db
               :condition (s/keys :opt-un [::name
                                           ::artist-id]))
  :ret (s/coll-of ::song))

(s/fdef find-song-by-id
  :args (s/cat :db ::db/db
               :id ::artist-id)
  :ret (s/nilable ::song))

(defprotocol Song
  (find-songs [db tx-data])
  (find-song-by-id [db id]))

(def sql-song-with-artist
  (sql/build
   :select [:s.*
            [:a.name :artist-name]
            [:a.type :artist-type]]
   :from [[:song :s]]
   :join [[:artist :a]
          [:= :s.artist-id :a.id]]))

(extend-protocol Song
  duct.database.sql.Boundary
  (find-songs [db {:keys [name artist-id]}]
    (db/select db (cond-> sql-song-with-artist
                    name (merge-where [:like :s.name (str \% name \%)])
                    artist-id (merge-where [:= :s.artist-id artist-id])
                    true (merge-order-by [:s.id :asc]))))
  (find-song-by-id [db id]
    (db/select-first db (merge-where sql-song-with-artist
                                     [:= :s.id id]))))
