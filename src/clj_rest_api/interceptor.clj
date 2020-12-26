(ns clj-rest-api.interceptor
  (:require
   [clj-rest-api.util.core :as util]
   [ring.util.http-response :as response]
   [struct.core :as st]))

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

(defn validate [schemas]
  {:name ::validate
   :enter
   (fn [context]
     (let [tx-data (get-in context [:request :tx-data])
           route-name (get-in context [:route :route-name])
           schema (get schemas route-name [])
           [errors validated-data] (st/validate tx-data schema {:strip true})]
       (if (empty? errors)
         (assoc-in context [:request :tx-data] validated-data)
         (assoc context :response (response/bad-request {:errors errors})))))})

(defn attach-database [db]
  {:name ::attach-database
   :enter
   (fn [context]
     (assoc-in context [:request :db] db))})
