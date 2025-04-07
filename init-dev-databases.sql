CREATE DATABASE IF NOT EXISTS portafoglio CHARACTER SET = 'utf8' COLLATE = 'utf8_swedish_ci';

CREATE USER 'portafoglio'@'%' IDENTIFIED BY 'portapass';
GRANT ALL PRIVILEGES ON portafoglio.* TO 'portafoglio'@'%';
FLUSH PRIVILEGES;