SET MODE POSTGRESQL;
CREATE TABLE IF NOT EXISTS student (
  id                            NUMERIC(18) PRIMARY KEY,
  last_name                     VARCHAR(256) NOT NULL,
  first_name                    VARCHAR(256) NOT NULL,
  patronymic                    VARCHAR(256) NOT NULL,
  date_of_birth                 TIMESTAMP    NOT NULL,
  military_rank                 VARCHAR(256),
  military_rank_award_date      TIMESTAMP,
  military_rank_order_name      VARCHAR(256),
  nationality                   VARCHAR(256),
  fleet                         VARCHAR(256),
  achievement_list              TEXT,
  position                      VARCHAR(256),
  university_id                 NUMERIC(18) REFERENCES university (id),
  graduation_year               NUMERIC(10),
  average_points                NUMERIC(10),
  foreign_lang                  VARCHAR(256),
  identification_series_number  VARCHAR(256),
  personal_number               VARCHAR(256),
  admission_form                VARCHAR(256),
  admission_date                TIMESTAMP,
  passport_number               VARCHAR(256),
  passport_issue_date           TIMESTAMP,
  passport_issuer               VARCHAR(256),
  international_passport_number VARCHAR(256),
  family_info                   TEXT,
  wife_nationality              VARCHAR(256),
  address                       VARCHAR(256),
  state_rewards                 VARCHAR(256),
  diploma_topic                 VARCHAR(256),
  preliminary_allocation        VARCHAR(256),
  final_allocation              VARCHAR(256),
  additional_info               TEXT,
  education_form                VARCHAR(256),
  graduation_type               VARCHAR(256),
  photo_base64                         TEXT
);

CREATE SEQUENCE seq_student START WITH 1;

CREATE TABLE IF NOT EXISTS university (
  id    NUMERIC(18) PRIMARY KEY,
  title VARCHAR(256) NOT NULL
);

CREATE SEQUENCE seq_university START WITH 1;