(ns clj-rest-api.util.time
  (:require
   [cheshire.generate :refer [add-encoder]]
   [java-time.api])
  (:import
   (com.fasterxml.jackson.core
    JsonGenerator)
   (java.time
    LocalDate)))

;;; JSON encoding

(add-encoder
 LocalDate
 (fn [^LocalDate d ^JsonGenerator json-generator]
   (.writeString json-generator
                 ^String (java-time.api/format :iso-local-date d))))
