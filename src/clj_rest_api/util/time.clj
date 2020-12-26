(ns clj-rest-api.util.time
  (:require
   [cheshire.generate :refer [add-encoder]]
   [java-time.format :as time.format])
  (:import
   (java.time
    LocalDate)))

;;; JSON encoding

(add-encoder
 LocalDate
 (fn [^LocalDate d json-generator]
   (.writeString json-generator (time.format/format :iso-local-date d))))
