(ns clj-rest-api.boundary.db.member
  (:require
   [clj-rest-api.boundary.db.artist :as artist]
   [clj-rest-api.boundary.db.core :as db]
   [clj-rest-api.boundary.db.organization :as organization]
   [clojure.spec.alpha :as s]
   [duct.database.sql]
   [honey.sql.helpers :refer [join order-by where]]))

(s/def ::id nat-int?)
(s/def ::name string?)
(s/def ::organization-id ::organization/id)

(s/def ::organization-name ::organization/name)
(s/def ::artist-id ::artist/id)

(s/def ::member
  (s/keys :req-un [::id
                   ::name
                   ::organization-id]
          :opt-un [::organization-name]))

(s/fdef find-members
  :args (s/cat :db ::db/db
               :condition (s/keys :opt-un [::name
                                           ::artist-id]))
  :ret (s/coll-of ::member))

(defprotocol Member
  (find-members [db tx-data]))

(def sql-member-with-organization
  {:select [:m.*
            [:o.name :organization-name]]
   :from [[:member :m]]
   :join [[:organization :o]
          [:= :m.organization-id :o.id]]})

(defn where-=-artist-id [sql artist-id]
  (-> sql
      (join [:artist-member :am]
            [:= :m.id :am.member-id])
      (where [:= :am.artist-id artist-id])))

(extend-protocol Member
  duct.database.sql.Boundary
  (find-members [db {:keys [name artist-id]}]
    (db/select db (cond-> sql-member-with-organization
                    name (where [:like
                                 :m.name
                                 (str \% (db/escape-like-param name) \%)])
                    artist-id (where-=-artist-id artist-id)
                    true (order-by [:m.id :asc])))))
