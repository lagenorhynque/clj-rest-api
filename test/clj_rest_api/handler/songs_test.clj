(ns clj-rest-api.handler.songs-test
  (:require [clj-rest-api.handler.songs :as sut]
            [clj-rest-api.test-helper.core :as helper :refer [with-db-data with-system]]
            [clj-rest-api.test-helper.db-data :as db-data]
            [clojure.test :as t]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-song-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :song db-data/song}]
      (t/testing "指定したIDの楽曲が取得できる"
        (let [{:keys [status body]}
              (helper/http-get sys "/api/songs/1")]
          (t/is (= 200 status))
          (t/is (= {:data {:id 1
                           :name "君のこころは輝いてるかい？"
                           :artist-id 1
                           :release-date "2015-10-07"
                           :artist-name "Aqours"
                           :artist-type 1}}
                   (helper/<-json body)))))
      (t/testing "指定したIDが存在しなければエラー"
        (let [{:keys [status body]}
              (helper/http-get sys "/api/songs/100")]
          (t/is (= 404 status))
          (t/is (= #{:song-id}
                   (helper/json-errors-keyset body))))))))
