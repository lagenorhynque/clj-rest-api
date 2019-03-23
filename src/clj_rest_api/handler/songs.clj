(ns clj-rest-api.handler.songs
  (:require [clj-rest-api.boundary.db.member :as db.member]
            [clj-rest-api.boundary.db.song :as db.song]
            [ring.util.http-response :as response]
            [struct.core :as st]))

(def validations
  {::list-songs
   [[:name st/string]
    [:artist-id st/integer-str st/positive]]

   ::fetch-song-by-id
   [[:song-id st/required st/integer-str st/positive]]})

(defn list-songs [{:keys [db tx-data]}]
  (response/ok {:data (db.song/find-songs db tx-data)}))

(defn fetch-song-by-id [{:keys [db tx-data]}]
  (if-let [song (db.song/find-song-by-id db (:song-id tx-data))]
    (let [members (db.member/find-members db (select-keys song [:artist-id]))]
      (response/ok {:data (assoc song :members members)}))
    (response/not-found {:errors {:song-id "doesn't exist"}})))
