(ns clj-rest-api.boundary.db.organization
  (:require [clojure.spec.alpha :as s]))

(s/def ::id nat-int?)
(s/def ::name string?)

(s/def ::organization
  (s/keys :req-un [::id
                   ::name]))
