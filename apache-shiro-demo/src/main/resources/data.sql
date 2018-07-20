-- 初始化数据 --
merge into permission values (1, 'add', '');
merge into permission values (2, 'delete', '');
merge into permission values (3, 'edit', '');
merge into permission values (4, 'query', '');

merge into user values (1, 'admin', '123');
merge into user values (2, 'demo', '123');

merge into role values (1, 'admin');
merge into role values (2, 'customer');

insert into permission_role values (1, 1);
insert into permission_role values (1, 2);
insert into permission_role values (1, 3);
insert into permission_role values (1, 4);
insert into permission_role values (2, 1);
insert into permission_role values (2, 4);

insert into user_role values (1, 1);
insert into user_role values (2, 2);
