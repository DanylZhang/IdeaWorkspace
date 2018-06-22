create table if not exists proxy (
  ip       varchar(45) default ''     not null primary key,
  port     int default '0'            not null,
  is_valid boolean default false      not null,
  type     varchar(15) default 'http' not null,
  comment  varchar(45) default ''
);
