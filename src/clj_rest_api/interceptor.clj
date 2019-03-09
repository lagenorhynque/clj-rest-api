(ns clj-rest-api.interceptor
  (:require [clj-rest-api.util.core :as util]))

(def attach-tx-data
  {:name ::attach-tx-data
   :enter
   (fn [context]
     (let [params (merge (get-in context [:request :json-params])
                         (get-in context [:request :query-params])
                         (get-in context [:request :path-params]))]
       (assoc-in context [:request :tx-data] (util/transform-keys-to-kebab params))))
   :leave
   (fn [context]
     (update context :response util/transform-keys-to-snake))})

(defn attach-database [db]
  {:name ::attach-database
   :enter
   (fn [context]
     (assoc-in context [:request :db] db))})
