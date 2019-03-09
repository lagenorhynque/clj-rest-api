(ns clj-rest-api.boundary.db.member
  (:require [clj-rest-api.boundary.db.core :as db]
            [duct.database.sql]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [merge-join merge-order-by merge-where]]))

(defprotocol Member
  (find-members [db tx-data]))

(def sql-member-with-organization
  (sql/build
   :select [:m.*
            [:o.name :organization-name]]
   :from [[:member :m]]
   :join [[:organization :o]
          [:= :m.organization-id :o.id]]))

(defn where-=-artist-id [sql artist-id]
  (-> sql
      (merge-join [:artist-member :am]
                  [:= :m.id :am.member-id])
      (merge-where [:= :am.artist-id artist-id])))

(extend-protocol Member
  duct.database.sql.Boundary
  (find-members [db {:keys [artist-id name]}]
    (db/select db (cond-> sql-member-with-organization
                    name (merge-where [:like :m.name (str \% name \%)])
                    artist-id (where-=-artist-id artist-id)
                    true (merge-order-by [:m.id :asc])))))
