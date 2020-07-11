-- 書籍データ挿入
insert into books (title, publisher_id, publication_date, summary) values ('リーダブルコード', 'OReilly', '2012-06-23', '「美しいコードを見ると感動する。優れたコードは見た瞬間に何をしているかが伝わってくる。そういうコードは使うのが楽しいし、自分のコードもそうあるべきだと思わせてくれる。本書の目的は、君のコードを良くすることだ');
insert into book_authors (book_id, author_id) values (1, 1);
insert into book_authors (book_id, author_id) values (1, 2);

insert into books (title, publisher_id, publication_date, summary) values ('コーディングを支える技術', 'SHOEISHA', '2018-11-14', '本書は，プログラミング言語が持つ各種概念が「なぜ」存在するのかを解説する書籍です。');
insert into book_authors (book_id, author_id) values (2, 3);

insert into books (title, publisher_id, publication_date, summary) values ('良いコードを書く技術', 'SHOEISHA', '2018-11-14', '読みやすく保守しやすい「良いコード」の書き方を解説した入門書です。『WEB DB PRESS』で断トツ人気だった連載を加筆・修正して書籍化しました。');
insert into book_authors (book_id, author_id) values (3, 4);
