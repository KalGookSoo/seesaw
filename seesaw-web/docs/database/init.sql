-- 데이터베이스 계정 생성(postgres)
CREATE USER seesaw_admin WITH PASSWORD 'YOUR_PASSWORD';

CREATE DATABASE seesaw;

ALTER DATABASE seesaw OWNER TO seesaw_admin;

GRANT ALL PRIVILEGES ON DATABASE seesaw TO seesaw_admin;