create table if not exists proxy (
  ip          varchar(30) default ''                         not null,
  port        int default 0                                  not null,
  speed       int default 60000                              not null,
  type        varchar(15) default 'http',
  is_valid    boolean default false                          not null,
  comment     varchar(45) default '',
  create_time timestamp default current_timestamp()          not null,
  primary key (ip, port)
);