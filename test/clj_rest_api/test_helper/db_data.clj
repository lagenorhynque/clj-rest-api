(ns clj-rest-api.test-helper.db-data)

(def artist
  [{:id 1
    :type 1
    :name "Aqours"}
   {:id 2
    :type 1
    :name "CYaRon!"}
   {:id 3
    :type 1
    :name "AZALEA"}
   {:id 4
    :type 1
    :name "Guilty Kiss"}
   {:id 5
    :type 1
    :name "Saint Snow"}
   {:id 6
    :type 1
    :name "Saint Aqours Snow"}])

(def artist-member
  [{:artist-id 1
    :member-id 1}
   {:artist-id 1
    :member-id 2}
   {:artist-id 1
    :member-id 3}
   {:artist-id 2
    :member-id 2}
   {:artist-id 3
    :member-id 1}
   {:artist-id 4
    :member-id 3}
   {:artist-id 5
    :member-id 4}
   {:artist-id 6
    :member-id 1}
   {:artist-id 6
    :member-id 2}
   {:artist-id 6
    :member-id 3}
   {:artist-id 6
    :member-id 4}])

(def member
  [{:id 1
    :name "黒澤 ダイヤ"
    :organization-id 1}
   {:id 2
    :name "渡辺 曜"
    :organization-id 1}
   {:id 3
    :name "津島 善子"
    :organization-id 1}
   {:id 4
    :name "鹿角 理亞"
    :organization-id 2}])

(def organization
  [{:id 1
    :name "浦の星女学院"}
   {:id 2
    :name "函館聖泉女子高等学院"}])

(def song
  [{:id 1
    :name "君のこころは輝いてるかい？"
    :artist-id 1
    :release-date #inst "2015-10-07"}
   {:id 2
    :name "元気全開DAY！DAY！DAY!"
    :artist-id 2
    :release-date #inst "2016-05-11"}
   {:id 3
    :name "トリコリコPLEASE!!"
    :artist-id 3
    :release-date #inst "2016-05-25"}
   {:id 4
    :name "Strawberry Trapper"
    :artist-id 4
    :release-date #inst "2016-06-08"}
   {:id 5
    :name "SELF CONTROL!!"
    :artist-id 5
    :release-date #inst "2016-11-30"}
   {:id 6
    :name "Awaken the power"
    :artist-id 6
    :release-date #inst "2017-12-20"}])
