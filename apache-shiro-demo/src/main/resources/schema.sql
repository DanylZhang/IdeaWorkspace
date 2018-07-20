-- 权限表 --
create table if not exists permission (
  pid  integer primary key auto_increment not null,
  name varchar(255)                       not null default '',
  url  varchar(512)                                default ''
);

-- 用户表 --
create table if not exists user (
  uid      integer primary key auto_increment not null,
  username varchar(255)                       not null default '',
  password varchar(512)                       not null default ''
);

-- 角色表 --
create table if not exists role (
  rid   integer primary key auto_increment not null,
  rname varchar(255)                       not null default ''
);

-- 权限角色关系表 --
create table if not exists permission_role (
  rid integer not null,
  pid integer not null
);
create index if not exists idx_rid
  on permission_role (rid);
create index if not exists idx_pid
  on permission_role (pid);

-- 用户角色关系表 --
create table if not exists user_role (
  uid integer not null,
  rid integer not null
);
create index if not exists idx_uid
  on user_role (uid);
create index if not exists idx_ur_rid
  on user_role (rid);
