--  Testing some advanced trigger functionality

CREATE TABLE log_table (
    stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    message VARCHAR(80) NOT NULL,
 )
CREATE PROCEDURE record(msg VARCHAR(60))
    MODIFIES SQL DATA
    BEGIN ATOMIC
        INSERT INTO log_table(message) VALUES (msg);
    END
CREATE TABLE base_table (
    id BIGINT generated ALWAYS AS IDENTITY PRIMARY KEY,
    i INTEGER NOT NULL,
    vc VARCHAR(600) NOT NULL,
 )

/*r0*/ SELECT COUNT(*) FROM log_table

CALL record('test record')

/*r1*/ SELECT COUNT(*) FROM log_table

CREATE TRIGGER manual_log_trigger AFTER INSERT ON base_table
    INSERT INTO log_table(message) VALUES ('totally manual trigger event')

INSERT INTO base_table(i, vc) VALUES(1, 'one')

/*r2*/ SELECT COUNT(*) FROM log_table

CREATE TRIGGER call_log_trigger AFTER INSERT ON base_table
    CALL record('trigger routine CALL event')

INSERT INTO base_table(i, vc) VALUES(2, 'two')

/*r4*/ SELECT COUNT(*) FROM log_table
/*r2*/ SELECT COUNT(*) FROM base_table

-- this trigger is called recursively -- WHEN clause avoids infinite recursion
CREATE TRIGGER extra_ins_trigger_ AFTER INSERT ON base_table
    REFERENCING NEW as new_row
    FOR EACH ROW
    WHEN (MOD(new_row.i,2) = 1)
        INSERT INTO base_table(i, vc)
        VALUES(1 + new_row.i, 'TRIG_ADDITION' || new_row.vc)

-- Sanity check to verify that the Trigger creaion didn't change anything
/*r4*/ SELECT COUNT(*) FROM log_table
/*r2*/ SELECT COUNT(*) FROM base_table

INSERT INTO base_table(i, vc) VALUES(3, 'x')
-- Should write 2 base_table entries, hence 4 log records
/*r8*/ SELECT COUNT(*) FROM log_table
/*r4*/ SELECT COUNT(*) FROM base_table
