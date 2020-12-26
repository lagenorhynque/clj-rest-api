(ns clj-rest-api.routes
  (:require
   [clj-rest-api.handler.songs :as songs]
   [clj-rest-api.interceptor :as interceptor]
   [clj-rest-api.util.time]
   [integrant.core :as ig]
   [io.pedestal.http :as http]
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.route :as route]))

;;; validation rules

(def validation-schemas
  songs/validations)

;;; routing

(defmethod ig/init-key ::routes
  [_ {:keys [db]}]
  (let [common-interceptors [(body-params/body-params)
                             http/json-body
                             interceptor/attach-tx-data
                             (interceptor/validate validation-schemas)
                             (interceptor/attach-database db)]]
    #(route/expand-routes
      #{["/api/songs" :get
         (conj common-interceptors `songs/list-songs)]
        ["/api/songs" :post
         (conj common-interceptors `songs/create-song)]
        ["/api/songs/:song-id" :get
         (conj common-interceptors `songs/fetch-song-by-id)]
        ["/api/songs/:song-id" :put
         (conj common-interceptors `songs/update-song)]
        ["/api/songs/:song-id" :delete
         (conj common-interceptors `songs/delete-song)]})))
