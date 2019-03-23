(ns clj-rest-api.boundary.db.artist
  (:require [clj-rest-api.util.const :as const]
            [clojure.spec.alpha :as s]))

(s/def ::id nat-int?)
(s/def ::type const/artist-types)
(s/def ::name string?)

(s/def ::artist
  (s/keys :req-un [::id
                   ::type
                   ::name]))
