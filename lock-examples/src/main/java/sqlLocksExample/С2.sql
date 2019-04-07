BEGIN isolation level READ UNCOMMITTED; -- Dirty read (PG not allowed)
BEGIN isolation level READ COMMITTED; -- Dirty read Not possible
SELECT * from trtest;
--update console 1
SELECT * from trtest;
ROLLBACK;



BEGIN isolation level READ COMMITTED; -- Non-repeatable Read
BEGIN isolation level REPEATABLE READ ; -- Non-repeatable Read Not possible
SELECT * from trtest;
--All other transaction
SELECT * from trtest;
ROLLBACK;

BEGIN isolation level REPEATABLE READ ; -- Non-repeatable Read Not possible
SELECT * from trtest;
--Update in transaction;
UPDATE trTest SET salary = 300 where id = 1;
--Commit 1 console transaction; --get Exception
ROLLBACK;


BEGIN isolation level READ COMMITTED ; -- Phantom Read
BEGIN isolation level REPEATABLE READ ; -- Phantom Read (PG not allowed)
BEGIN isolation level SERIALIZABLE; -- Phantom Read Not possible
SELECT * from trtest;
SELECT sum(salary) from trtest;
--All other transaction
SELECT sum(salary) from trtest;
ROLLBACK;


BEGIN isolation level REPEATABLE READ ; -- Phantom Read (PG not allowed)
BEGIN isolation level SERIALIZABLE; -- Phantom Read Not possible
SELECT * from trtest;
SELECT sum(salary) from trtest where id > 2;
--All other transaction
ROLLBACK;

--DeadLock
BEGIN;
-- 1 Update from console 1
UPDATE trtest set salary = salary - 10 WHERE id = 2;
UPDATE trtest set salary = salary + 100 WHERE id = 1;
--2 Update from console 1
ROLLBACK;
COMMIT;

-- Remove duplicate rows
BEGIN;
SELECT city, temp_lo,CTID from wether;
DELETE FROM wether where CTID NOT in
                         (SELECT max(ctid) FROM wether GROUP BY wether.*);
ROLLBACK;


select count(distinct temp_lo) from wether;