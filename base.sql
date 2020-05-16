create table if not exists user (
  nom varchar(30) not null,
  prenom varchar(30) not null,
  login varchar(30) not null,
  password blob not null,
  id int primary key not null auto_increment);

create table if not exists session(
  key_session varchar(32) not null primary key,
  id_user int not null,
  time timestamp not null,
  constraint fk_session foreign key (id_user) references user(id) on delete cascade);

create table if not exists friend(
  id_1 int,
  id_2 int,
  constraint pk_friend primary key (id_1, id_2),
  constraint fk_friend1 foreign key (id_1) references user(id) on delete cascade,
  constraint fk_friend2 foreign key (id_2) references user(id) on delete cascade);

create table if not exists block(
  idBlock int,
  idCurrentUser int,
  idBlockedUser int,
  constraint pk_block primary key (idCurrentUser, idBlockedUser),
  constraint fk_currentUser foreign key (idCurrentUser) references user(id) on delete cascade,
  constraint fk_blockedUser foreign key (idBlockedUser) references user(id) on delete cascade);

insert into user(nom, prenom, login, password) values
  ("CAO","Gabriel","Arinha201","azertyuiop"),
  ("MONSOURIS","Medor","Pilan12","251efzfe"),
  ("SHIBATA","Touna","Kina511","1026rzsfgt"),
  ("OULIN","Tywin","Boulash","1045695"),
  ("RESTES","Celia","Celia121589","1234546");
