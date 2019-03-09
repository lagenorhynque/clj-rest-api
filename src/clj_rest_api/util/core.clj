(ns clj-rest-api.util.core
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :refer [transform-keys]]))

(defn ->kebab-case [v]
  (csk/->kebab-case v :separator \_))

(defn ->snake_case [v]
  (csk/->snake_case v :separator \-))

(defn transform-keys-to-kebab [coll]
  (transform-keys ->kebab-case coll))

(defn transform-keys-to-snake [coll]
  (transform-keys ->snake_case coll))
