(ns clj-rest-api.test-helper.core
  (:require
   [cheshire.core :as cheshire]
   [clj-http.client :as client]
   [clj-rest-api.boundary.db.core]
   [clj-rest-api.test-helper.db :refer [insert-db-data! truncate-all-tables!]]
   [clj-rest-api.util.core :as util]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [duct.core :as duct]
   [integrant.core :as ig]
   [orchestra.spec.test :as stest]))

(duct/load-hierarchy)

;;; fixtures

(defn instrument-specs [f]
  (stest/instrument)
  (f))

;;; macros for testing context

(defn test-system []
  (-> (io/resource "clj_rest_api/config.edn")
      duct/read-config
      (duct/prep-config [:duct.profile/dev :duct.profile/test])))

(s/fdef with-system
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 2)
               :body (s/* any?)))

(defmacro with-system [[bound-var binding-expr] & body]
  `(let [~bound-var (ig/init ~binding-expr)]
     (try
       ~@body
       (finally (ig/halt! ~bound-var)))))

(s/fdef with-db-data
  :args (s/cat :binding (s/coll-of any?
                                   :kind vector?
                                   :count 2)
               :body (s/* any?)))

(defmacro with-db-data [[system db-data-map] & body]
  `(let [db# (:duct.database.sql/hikaricp ~system)]
     (try
       (insert-db-data! db# ~db-data-map)
       ~@body
       (finally (truncate-all-tables! db#)))))

;;; HTTP client

(def ^:private url-prefix "http://localhost:")

(defn- server-port [system]
  (get-in system [:duct.server/pedestal :io.pedestal.http/port]))

(defn http-get [system url & {:as options}]
  (client/get (str url-prefix (server-port system) url)
              (merge {:accept :json
                      :throw-exceptions? false} options)))

(defn http-post [system url body & {:as options}]
  (client/post (str url-prefix (server-port system) url)
               (merge {:body body
                       :content-type :json
                       :accept :json
                       :throw-exceptions? false} options)))

(defn http-put [system url body & {:as options}]
  (client/put (str url-prefix (server-port system) url)
              (merge {:body body
                      :content-type :json
                      :accept :json
                      :throw-exceptions? false} options)))

(defn http-delete [system url & {:as options}]
  (client/delete (str url-prefix (server-port system) url)
                 (merge {:accept :json
                         :throw-exceptions? false} options)))

;;; JSON conversion

(defn ->json [obj]
  (-> obj
      util/transform-keys-to-snake
      cheshire/generate-string))

(defn <-json [str]
  (-> str
      (cheshire/parse-string true)
      util/transform-keys-to-kebab))

;;; misc.

(defn json-errors-keyset [json]
  (-> json <-json :errors keys set))
