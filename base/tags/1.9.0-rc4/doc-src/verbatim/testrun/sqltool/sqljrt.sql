/*
 * $Id$
 *
 * Tests SQL/JRT
 */

create function dehex(VARCHAR, INTEGER)
    returns INTEGER
    no sql
    language java
    external name 'CLASSPATH:java.lang.Integer.valueOf'
.;

CALL dehex('12', 16);
*if (*? != 18)
    \q SQL/JRT function failed
*end if
