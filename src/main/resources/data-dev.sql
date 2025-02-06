-- User 테이블 데이터
INSERT INTO `user` (
    email,
    name,
    nickname,
    password,
    role,
    is_premium,
    user_status,
    provider
) VALUES
      ('user1@test.com', '유저1', '유저닉1', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'USER', false, 'ACTIVE', 'LOCAL'),
      ('user2@test.com', '유저2', '유저닉2', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'USER', true, 'ACTIVE', 'LOCAL'),
      ('user3@test.com', '유저3', '유저닉3', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'USER', false, 'ACTIVE', 'LOCAL'),
      ('user4@test.com', '유저4', '유저닉4', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'USER', true, 'ACTIVE', 'LOCAL'),
      ('admin1@test.com', '관리자1', '관리자닉1', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'ADMIN', true, 'ACTIVE', 'LOCAL'),
      ('admin2@test.com', '관리자2', '관리자닉2', '$2a$10$HuoYrGAAXUn2LJ3EA8ZTJubGdkLyvlYxbs4cQjG0WYQ0BNDa5Xi2S', 'ADMIN', false, 'ACTIVE', 'LOCAL');

-- MatchUserInfo 테이블 데이터
INSERT INTO match_user_info (
    user_id,
    gender,
    game_rank,
    skill_level,
    mic_usage,
    message
) VALUES
    (1, 'MALE', 'DIAMOND', 5, true, '같이 트롤하실분? 정글러 구함'),
    (2, 'FEMALE', 'BRONZE', 1, true, '그냥 즐겜할래요.'),
    (3, 'FEMALE', 'GOLD', 3, true, '랭겜 즐기실분 구합니다.'),
    (4, 'MALE', 'SILVER', 2, true, '초보 뉴비 환영합니다!'),
    (5, 'FEMALE', 'CHALLENGER', 6, false, '빡겜할 듀오 찾습니다.'),
    (6, 'MALE', 'PLATINUM', 4, true, '실버 이상만요!');

-- user_lanes 테이블 데이터
INSERT INTO user_lanes (match_user_info_id, lanes)
VALUES
    (1, 'TOP'), (1, 'JUNGLE'),
    (2, 'MID'),
    (3, 'BOTTOM_AD'), (3, 'BOTTOM_SUPPORTER'),
    (4, 'TOP'), (4, 'BOTTOM_SUPPORTER'),
    (5, 'JUNGLE'), (5, 'MID'),
    (6, 'TOP'), (6, 'JUNGLE');

-- user_purposes 테이블 데이터
INSERT INTO user_purposes (match_user_info_id, purposes)
VALUES
    (1, 'NORMAL_GAME'), (1, 'JUST_FOR_FUN'), (1, 'TEAMWORK'),
    (2, 'RANK_GAME'), (2, 'DUO_PLAY'),
    (3, 'MENTORING'), (3, 'BEGINNER_FRIENDLY'),
    (4, 'JUST_FOR_FUN'), (4, 'TEAMWORK'),
    (5, 'TRY_HARD'), (5, 'RANK_GAME'),
    (6, 'NORMAL_GAME');

-- user_play_times 테이블 데이터
INSERT INTO user_play_times (match_user_info_id, play_time_ranges)
VALUES
    (1, 'ZERO_TO_SIX'), (1, 'EIGHTEEN_TO_TWENTY_FOUR'),
    (2, 'SIX_TO_TWELVE'),
    (3, 'TWELVE_TO_EIGHTEEN'),
    (4, 'EIGHTEEN_TO_TWENTY_FOUR'),
    (5, 'ZERO_TO_SIX'),
    (6, 'SIX_TO_TWELVE'), (6, 'EIGHTEEN_TO_TWENTY_FOUR');

-- game 테이블 데이터
INSERT INTO game (title, genre, platform, description)
VALUES
    ('라스트 오브 어스', 'Action', 'PlayStation', '종말 이후의 세계를 배경으로 한 스토리 중심의 서바이벌 게임.'),
    ('마인크래프트', 'Sandbox', 'PC', '무한히 생성되는 세계에서 블록을 쌓고 모험을 떠나는 게임.'),
    ('오버워치', 'Shooter', 'PC', '독특한 능력을 가진 다양한 영웅들이 등장하는 팀 기반 1인칭 슈팅 게임.'),
    ('스타듀 밸리', 'Simulation', 'PC', '할아버지의 오래된 농장을 물려받아 경영하는 농장 시뮬레이션 RPG.'),
    ('엘든 링', 'RPG', 'PC', '미야자키 히데타카와 조지 R.R. 마틴이 만든 다크 판타지 오픈 월드 액션 RPG.'),
    ('피파 23', 'Sports', 'PlayStation', '인기 축구 시뮬레이션 시리즈의 최신작으로, 업데이트된 팀과 향상된 게임플레이 제공.'),
    ('콜 오브 듀티: 워존', 'Shooter', 'PC', '콜 오브 듀티 세계관을 배경으로 한 무료 배틀로얄 게임.'),
    ('모여봐요 동물의 숲', 'Simulation', 'Nintendo Switch', '무인도에서 자신만의 낙원을 만들어가는 소셜 시뮬레이션 게임.'),
    ('포르자 호라이즌 5', 'Racing', 'Xbox', '멕시코의 생동감 넘치고 끊임없이 변화하는 풍경을 배경으로 한 오픈 월드 레이싱 게임.'),
    ('할로우 나이트', 'Adventure', 'PC', '광대하고 서로 연결된 세계를 배경으로 한 도전적인 2D 액션 어드벤처 게임.');