(ns clj-rest-api.handler.songs-test
  (:require [clj-rest-api.handler.songs :as sut]
            [clj-rest-api.test-helper.core :as helper :refer [with-db-data with-system]]
            [clj-rest-api.test-helper.db-data :as db-data]
            [clojure.test :as t]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-list-songs
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :song db-data/song}]
      (t/testing "バリデーションエラー"
        (t/testing "データ型チェック"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?artist_id=foo")]
            (t/is (= 400 status))
            (t/is (= #{:artist-id}
                     (helper/json-errors-keyset body)))))
        (t/testing "数値範囲チェック"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?artist_id=0")]
            (t/is (= 400 status))
            (t/is (= #{:artist-id}
                     (helper/json-errors-keyset body))))))
      (t/testing "楽曲の一覧が取得できる"
        (let [{:keys [status body]}
              (helper/http-get sys "/api/songs")]
          (t/is (= 200 status))
          (t/is (= {:data [{:id 1
                            :name "君のこころは輝いてるかい？"
                            :artist-id 1
                            :release-date "2015-10-07"
                            :artist-name "Aqours"
                            :artist-type 1}
                           {:id 2
                            :name "元気全開DAY！DAY！DAY!"
                            :artist-id 2
                            :release-date "2016-05-11"
                            :artist-name "CYaRon!"
                            :artist-type 1}
                           {:id 3
                            :name "トリコリコPLEASE!!"
                            :artist-id 3
                            :release-date "2016-05-25"
                            :artist-name "AZALEA"
                            :artist-type 1}
                           {:id 4
                            :name "Strawberry Trapper"
                            :artist-id 4
                            :release-date "2016-06-08"
                            :artist-name "Guilty Kiss"
                            :artist-type 1}
                           {:id 5
                            :name "SELF CONTROL!!"
                            :artist-id 5
                            :release-date "2016-11-30"
                            :artist-name "Saint Snow"
                            :artist-type 1}
                           {:id 6
                            :name "Awaken the power"
                            :artist-id 6
                            :release-date "2017-12-20"
                            :artist-name "Saint Aqours Snow"
                            :artist-type 1}]}
                   (helper/<-json body))))
        (t/testing "楽曲名指定あり"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?name=君")]
            (t/is (= 200 status))
            (t/is (= {:data [{:id 1
                              :name "君のこころは輝いてるかい？"
                              :artist-id 1
                              :release-date "2015-10-07"
                              :artist-name "Aqours"
                              :artist-type 1}]}
                     (helper/<-json body))))
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?name=DAY!")]
            (t/is (= 200 status))
            (t/is (= {:data [{:id 2
                              :name "元気全開DAY！DAY！DAY!"
                              :artist-id 2
                              :release-date "2016-05-11"
                              :artist-name "CYaRon!"
                              :artist-type 1}]}
                     (helper/<-json body))))
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?name=tr")]
            (t/is (= 200 status))
            (t/is (= {:data [{:id 4
                              :name "Strawberry Trapper"
                              :artist-id 4
                              :release-date "2016-06-08"
                              :artist-name "Guilty Kiss"
                              :artist-type 1}
                             {:id 5
                              :name "SELF CONTROL!!"
                              :artist-id 5
                              :release-date "2016-11-30"
                              :artist-name "Saint Snow"
                              :artist-type 1}]}
                     (helper/<-json body))))
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?name=AQUARIUM")]
            (t/is (= 200 status))
            (t/is (= {:data []}
                     (helper/<-json body)))))
        (t/testing "アーティストID指定あり"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs?artist_id=1")]
            (t/is (= 200 status))
            (t/is (= {:data [{:id 1
                              :name "君のこころは輝いてるかい？"
                              :artist-id 1
                              :release-date "2015-10-07"
                              :artist-name "Aqours"
                              :artist-type 1}]}
                     (helper/<-json body)))))))))

(t/deftest test-fetch-song-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist-member db-data/artist-member
                        :member db-data/member
                        :organization db-data/organization
                        :song db-data/song}]
      (t/testing "バリデーションエラー"
        (t/testing "データ型チェック"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs/foo")]
            (t/is (= 400 status))
            (t/is (= #{:song-id}
                     (helper/json-errors-keyset body)))))
        (t/testing "数値範囲チェック"
          (let [{:keys [status body]}
                (helper/http-get sys "/api/songs/0")]
            (t/is (= 400 status))
            (t/is (= #{:song-id}
                     (helper/json-errors-keyset body))))))
      (t/testing "楽曲が存在しなければエラー"
        (let [{:keys [status body]}
              (helper/http-get sys "/api/songs/100")]
          (t/is (= 404 status))
          (t/is (= #{:song-id}
                   (helper/json-errors-keyset body)))))
      (t/testing "指定したIDの楽曲が取得できる"
        (let [{:keys [status body]}
              (helper/http-get sys "/api/songs/2")]
          (t/is (= 200 status))
          (t/is (= {:data {:id 2
                           :name "元気全開DAY！DAY！DAY!"
                           :artist-id 2
                           :release-date "2016-05-11"
                           :artist-name "CYaRon!"
                           :artist-type 1
                           :members [{:id 2
                                      :name "渡辺 曜"
                                      :organization-id 1
                                      :organization-name "浦の星女学院"}]}}
                   (helper/<-json body))))))))
