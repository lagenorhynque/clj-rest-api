(ns clj-rest-api.handler.songs
  (:require [clj-rest-api.boundary.db.artist :as db.artist]
            [clj-rest-api.boundary.db.core :refer [with-transaction]]
            [clj-rest-api.boundary.db.member :as db.member]
            [clj-rest-api.boundary.db.song :as db.song]
            [clj-rest-api.util.validator :as validator]
            [clojure.set :as set]
            [io.pedestal.http.route :as route]
            [ring.util.http-response :as response]
            [struct.core :as st]))

(def validations
  {::list-songs
   [[:name st/string]
    [:artist-id st/integer-str st/positive]]

   ::create-song
   [[:name st/required st/string]
    [:artist-id st/required st/integer-str st/positive]
    [:release-date st/required validator/date-str]]

   ::fetch-song-by-id
   [[:song-id st/required st/integer-str st/positive]]

   ::update-song
   [[:song-id st/required st/integer-str st/positive]
    [:name st/string]
    [:artist-id st/integer-str st/positive]
    [:release-date validator/date-str]]

   ::delete-song
   [[:song-id st/required st/integer-str st/positive]]})

(defn list-songs [{:keys [db tx-data]}]
  (response/ok {:data (db.song/find-songs db tx-data)}))

(defn create-song [{:keys [db tx-data]}]
  (with-transaction [db]
    (if (db.artist/find-artist-by-id db (:artist-id tx-data))
      (let [id (db.song/create-song! db tx-data)]
        (response/created (route/url-for ::fetch-song-by-id
                                         :params {:song-id id})))
      (response/not-found {:errors {:artist-id "doesn't exist"}}))))

(defn fetch-song-by-id [{:keys [db tx-data]}]
  (if-let [song (db.song/find-song-by-id db (:song-id tx-data))]
    (let [members (db.member/find-members db (select-keys song [:artist-id]))]
      (response/ok {:data (assoc song :members members)}))
    (response/not-found {:errors {:song-id "doesn't exist"}})))

(defn update-song [{:keys [db tx-data]}]
  (with-transaction [db]
    (let [{:keys [song-id artist-id]} tx-data]
      (if-let [song (db.song/find-song-by-id db song-id)]
        (if (or (not artist-id) (db.artist/find-artist-by-id db artist-id))
          (do (db.song/update-song! db (set/rename-keys tx-data {:song-id :id}))
              (response/no-content))
          (response/not-found {:errors {:artist-id "doesn't exist"}}))
        (response/not-found {:errors {:song-id "doesn't exist"}})))))

(defn delete-song [{:keys [db tx-data]}]
  (with-transaction [db]
    (let [{:keys [song-id]} tx-data]
      (if-let [song (db.song/find-song-by-id db song-id)]
        (do (db.song/delete-song! db song-id)
            (response/no-content))
        (response/not-found {:errors {:song-id "doesn't exist"}})))))
