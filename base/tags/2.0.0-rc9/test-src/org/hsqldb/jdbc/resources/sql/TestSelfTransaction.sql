-- ON DELETE SET NULL
set property "hsqldb.nio_data_file" false
set write_delay false;
set autocommit false;
drop table testB if exists;
create cached table testB(id integer, parent integer, ref integer,
 data varchar(200), unique (id), foreign key (parent)
 references testB(id) on delete set null);
/*u1*/insert into testB values(100,100,1,'xxxx');
/*u1*/insert into testB values(101,100,1,'xxxx');
/*u1*/insert into testB values(102,100,1,'xxxx');
/*u1*/insert into testB values(200,200,1,'xxxx');
commit;
/*u1*/delete from testB where id=100;
/*r
 101,NULL,1,xxxx
 102,NULL,1,xxxx
 200,200,1,xxxx
*/select * from testB order by id
/*c2*/select * from testB where parent is null
rollback;
/*c4*/select * from testB;
-- ON DELETE SET DEFAULT
drop table testB if exists;
create cached table testB(id integer, parent integer default 20, ref integer,
 data varchar(200), unique (id),foreign key (parent)
 references testB(id) on delete set default);
/*u1*/insert into testB values(20,20,1,'xxxx');
/*u1*/insert into testB values(100,100,1,'xxxx');
/*u1*/insert into testB values(101,100,1,'xxxx');
/*u1*/insert into testB values(200,200,1,'xxxx');
commit;
/*u1*/delete from testB where id=100;
/*r
 20,20,1,xxxx
 101,20,1,xxxx
 200,200,1,xxxx
*/select * from testB order by id
/*c2*/select * from testB where parent=20
rollback;
/*c4*/select * from testB;
-- CHAINED SELF REFERENCING FK
-- ON DELETE CASCADE
drop table testA if exists;
create cached table testA(a int primary key,b int,
    foreign key(b) references testA(a) on update cascade on delete cascade);
insert into testA(a,b) values(1,1);
insert into testA(a,b) values(2,1);
insert into testA(a,b) values(3,1);
insert into testA(a,b) values(4,2);
insert into testA(a,b) values(5,2);
insert into testA(a,b) values(6,2);
insert into testA(a,b) values(7,3);
insert into testA(a,b) values(8,3);
insert into testA(a,b) values(9,3);
commit;
/*u9*/update testA set a = a+1;
/*r3*/select count(*) from testA where b=4;
/*u9*/update testA set a = a-1;
/*r0*/select count(*) from testA where b=4;
/*r3*/select count(*) from testA where b=3;
/*u1*/delete from testA where a=1;
/*r0*/select count(*) from testA;
rollback;
/*r9*/select count(*) from testA;

