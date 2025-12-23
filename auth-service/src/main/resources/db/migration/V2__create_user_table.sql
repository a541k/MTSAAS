CREATE TABLE users (
   id BIGINT NOT NULL AUTO_INCREMENT,
   username VARCHAR(255),
   password VARCHAR(255),
   is_active BIT,
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
