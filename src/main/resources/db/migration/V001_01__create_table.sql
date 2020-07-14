-- 出版社テーブル
create table publishers (id VARCHAR(50) NOT NULL, password VARCHAR(255) NOT NULL, name VARCHAR(100) NOT NULL, PRIMARY KEY (id));
-- 著者テーブル
create table authors (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(50) NOT NULL, profile VARCHAR(255), PRIMARY KEY (id));
-- 書籍テーブル
create table books (id BIGINT NOT NULL AUTO_INCREMENT, title VARCHAR(100), publisher_id VARCHAR(50) NOT NULL, publication_date DATE NOT NULL, summary VARCHAR(255) NOT NULL, PRIMARY KEY (id), FOREIGN KEY(publisher_id) REFERENCES publishers(id));
-- 書籍著者一覧テーブル
create table book_authors (book_id BIGINT NOT NULL, author_id BIGINT NOT NULL, PRIMARY KEY (book_id, author_id), FOREIGN KEY(book_id) REFERENCES books(id), FOREIGN KEY(author_id) REFERENCES authors(id));
