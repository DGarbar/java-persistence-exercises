Create TABLE trTest
(
    id       serial
        CONSTRAINT trTest_PK PRIMARY KEY NOT NULL,
    salary   BIGINT                               DEFAULT 100,
    name     VARCHAR(80),
    date     TIMESTAMP                            DEFAULT now(),
    language VARCHAR(80)                 NOT NULL DEFAULT 'any'
);

select * from trTest;

insert into trTest(name, language,salary)
values ('t1', 'java', 100),
       ('t2', 'cpp', 50),
       ('t21', 'cpp', 50),
       ('t3', 'csharp',50);

INSERT into trTest values (1,'t1',now(),'java');


BEGIN; -- Dirty read (PG not allowed)
UPDATE trTest SET name = 't1 Updated' where id = 1;
COMMIT;
ROLLBACK;

BEGIN; -- Non-repeatable Read
UPDATE trTest SET name = 't1 Updated' where id = 1;
COMMIT;
ROLLBACK;

UPDATE trTest SET name = 't1' where id = 1;


BEGIN; -- Phantom Read
UPDATE trTest SET salary = 200 where id = 1;
COMMIT;
ROLLBACK;

UPDATE trTest SET salary = 100 where id = 1;

--DeadLock
BEGIN;
UPDATE trtest set salary = salary + 100 WHERE id = 1;
UPDATE trtest set salary = salary - 10 WHERE id = 2;
ROLLBACK;
COMMIT;
UPDATE trTest SET salary = 100 where id = 1;