INSERT INTO organization (id, name) VALUES
  (1, '浦の星女学院'),
  (2, '函館聖泉女子高等学院');

INSERT INTO member (id, name, organization_id) VALUES
  (1, '高海 千歌', 1),
  (2, '桜内 梨子', 1),
  (3, '松浦 果南', 1),
  (4, '黒澤 ダイヤ', 1),
  (5, '渡辺 曜', 1),
  (6, '津島 善子', 1),
  (7, '国木田 花丸', 1),
  (8, '小原 鞠莉', 1),
  (9, '黒澤 ルビィ', 1),
  (10, '鹿角 聖良', 2),
  (11, '鹿角 理亞', 2);

INSERT INTO artist (id, type, name) VALUES
  (1, 1, 'Aqours'),
  (2, 1, 'CYaRon!'),
  (3, 1, 'AZALEA'),
  (4, 1, 'Guilty Kiss'),
  (5, 1, 'Saint Snow'),
  (6, 1, 'Saint Aqours Snow');

INSERT INTO artist_member (artist_id, member_id) VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (1, 5),
  (1, 6),
  (1, 7),
  (1, 8),
  (1, 9),
  (2, 1),
  (2, 5),
  (2, 9),
  (3, 3),
  (3, 4),
  (3, 7),
  (4, 2),
  (4, 6),
  (4, 8),
  (5, 10),
  (5, 11),
  (6, 1),
  (6, 2),
  (6, 3),
  (6, 4),
  (6, 5),
  (6, 6),
  (6, 7),
  (6, 8),
  (6, 9),
  (6, 10),
  (6, 11);

INSERT INTO song (id, name, artist_id, release_date) VALUES
  (1, '君のこころは輝いてるかい？', 1, '2015-10-07'),
  (2, '恋になりたいAQUARIUM', 1, '2016-04-27'),
  (3, 'HAPPY PARTY TRAIN', 1, '2017-04-05'),
  (4, '元気全開DAY！DAY！DAY！', 2, '2016-05-11'),
  (5, '近未来ハッピーエンド', 2, '2017-05-10'),
  (6, 'トリコリコPLEASE!!', 3, '2016-05-25'),
  (7, 'GALAXY HidE and SeeK', 3, '2017-05-31'),
  (8, 'Strawberry Trapper', 4, '2016-06-08'),
  (9, 'コワレヤスキ', 4, '2017-06-21'),
  (10, 'SELF CONTROL!!', 5, '2016-11-30'),
  (11, 'CRASH MIND', 5, '2017-12-20'),
  (12, 'DROPOUT!?', 5, '2017-12-20'),
  (13, 'Awaken the power', 6, '2017-12-20');
