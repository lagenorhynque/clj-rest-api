(ns clj-rest-api.boundary.db.song
  (:require [clj-rest-api.boundary.db.core :as db]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-order-by merge-where]]))

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
                    true (merge-order-by [:s.id :asc]) )))
  (find-song-by-id [db id]
    (db/select-first db (merge-where sql-song-with-artist
                                     [:= :s.id id]))))
