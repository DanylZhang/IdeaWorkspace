-- 初始化数据 --
insert or ignore into permission values (1, 'add', '');
insert or ignore into permission values (2, 'delete', '');
insert or ignore into permission values (3, 'edit', '');
insert or ignore into permission values (4, 'query', '');

insert or ignore into user values (1, 'admin', '123');
insert or ignore into user values (2, 'demo', '123');

insert or ignore into role values (1, 'admin');
insert or ignore into role values (2, 'customer');

insert or ignore into permission_role values (1, 1);
insert or ignore into permission_role values (1, 2);
insert or ignore into permission_role values (1, 3);
insert or ignore into permission_role values (1, 4);
insert or ignore into permission_role values (2, 1);
insert or ignore into permission_role values (2, 4);

insert or ignore into user_role values (1, 1);
insert or ignore into user_role values (2, 2);
