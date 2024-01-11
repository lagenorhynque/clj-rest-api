(ns clj-rest-api.boundary.db.song
  (:require
   [clj-rest-api.boundary.db.artist :as artist]
   [clj-rest-api.boundary.db.core :as db]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honey.sql.helpers :refer [order-by where]])
  (:import
   (java.time
    LocalDate)))

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

(s/fdef create-song!
  :args (s/cat :db ::db/db
               :tx-data (s/keys :req-un [::name
                                         ::artist-id
                                         ::release-date]))
  :ret ::db/row-id)

(s/fdef update-song!
  :args (s/cat :db ::db/db
               :tx-data (s/keys :req-un [::id]
                                :opt-un [::name
                                         ::artist-id
                                         ::release-date]))
  :ret ::db/row-count)

(s/fdef delete-song!
  :args (s/cat :db ::db/db
               :id ::id)
  :ret ::db/row-count)

(defprotocol Song
  (find-songs [db tx-data])
  (find-song-by-id [db id])
  (create-song! [db tx-data])
  (update-song! [db tx-data])
  (delete-song! [db id]))

(def sql-song-with-artist
  {:select [:s.*
            [:a.name :artist-name]
            [:a.type :artist-type]]
   :from [[:song :s]]
   :join [[:artist :a]
          [:= :s.artist-id :a.id]]})

(extend-protocol Song
  duct.database.sql.Boundary
  (find-songs [db {:keys [name artist-id]}]
    (db/select db (cond-> sql-song-with-artist
                    name (where [:like
                                 :s.name
                                 (str \% (db/escape-like-param name) \%)])
                    artist-id (where [:= :s.artist-id artist-id])
                    true (order-by [:s.id :asc]))))
  (find-song-by-id [db id]
    (db/select-first db (where sql-song-with-artist
                               [:= :s.id id])))
  (create-song! [db tx-data]
    (db/insert! db :song (select-keys tx-data [:name
                                               :artist-id
                                               :release-date])))
  (update-song! [db {:keys [id] :as tx-data}]
    (db/execute! db {:update :song
                     :set (select-keys tx-data [:name
                                                :artist-id
                                                :release-date])
                     :where [:= :id id]}))
  (delete-song! [db id]
    (db/execute! db {:delete-from :song
                     :where [:= :id id]})))
