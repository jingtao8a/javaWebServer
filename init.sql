create schema java_webserver;
use java_webserver;
create TABLE users(
    name  VARCHAR(10) NOT NULL PRIMARY KEY,
    password VARCHAR(20) NOT NULL
);

insert into users values
("yuxintao", "123"), ("root", "123");

select * from java_webserver.users;