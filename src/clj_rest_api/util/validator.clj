(ns clj-rest-api.util.validator
  (:require [java-time.format :as time.format]
            [java-time.local :as time.local]
            [struct.core :as st]))

(def date-str
  (let [fmt (time.format/formatter :iso-local-date)]
    {:message "must be an ISO date formatted string"
     :optional true
     :validate #(try
                  (time.local/local-date fmt %)
                  true
                  (catch Exception _
                    false))
     :coerce #(time.local/local-date fmt %)}))
