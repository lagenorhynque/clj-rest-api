(ns clj-rest-api.handler.songs
  (:require [clj-rest-api.boundary.db.song :as db.song]
            [ring.util.http-response :as response]
            [struct.core :as st]))

(defn list-songs [{:keys [db tx-data]}]
  (response/ok {:data (db.song/find-songs db tx-data)}))

(defn fetch-song-by-id [{:keys [db tx-data]}]
  (if-let [song (db.song/find-song-by-id db (:song-id tx-data))]
    (response/ok {:data song})
    (response/not-found {:errors {:song-id "doesn't exist"}})))
