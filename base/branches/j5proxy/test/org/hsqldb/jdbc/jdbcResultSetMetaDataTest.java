/* Copyright (c) 2001-2006, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb.jdbc;

import java.sql.ResultSetMetaData;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * @author boucherb@users
 */
public class jdbcResultSetMetaDataTest extends JdbcTestCase {

    public jdbcResultSetMetaDataTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();

        super.executeScript("setup-all_types-table.sql");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(jdbcResultSetMetaDataTest.class);

        return suite;
    }

    private final String select =
        "select id as           id_column, " +                            // 1
               "c_bigint        as bigint_column, " +
               "c_binary        as binary_column, " +
               "c_boolean       as boolean_column, " +
               "c_char          as char_column, " +                       // 5
               "c_date          as date_column, " +
               "c_decimal       as decimal_column, " +
               "c_double        as double_column, " +
               "c_float         as float_column, " +
               "c_integer       as integer_column, " +                    // 10
               "c_longvarbinary as longvarbinary_column, " +
               "c_longvarchar   as longvarchar_column, " +
               "c_object        as object_column, " +
               "c_real          as real_column, " +
               "c_smallint      as smallint_column, " +                   // 15
               "c_time          as time_column, " +
               "c_timestamp     as timestamp_column, " +
               "c_tinyint       as tinyint_column, " +
               "c_varbinary     as varbinary_column, " +
               "c_varchar       as varchar_column, " +                    // 20
               "c_varchar_ignorecase as varchar_ignorecase_column " +
          "from all_types";

    protected jdbcResultSetMetaData newJdbcResultSetMetaData() throws Exception {
        return (jdbcResultSetMetaData)
        newConnection()
            .createStatement()
                .executeQuery(select)
                    .getMetaData();
    }

    /**
     * Test of getColumnCount method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnCount() throws Exception {
        System.out.println("getColumnCount");

        assertEquals(21, newJdbcResultSetMetaData().getColumnCount());
    }

    /**
     * Test of isAutoIncrement method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsAutoIncrement() throws Exception {
        System.out.println("isAutoIncrement");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            // column 1 is identity (auto-increment)
            assertEquals("column: " + i, i == 1, rsmd.isAutoIncrement(i));
        }
    }

    /**
     * Test of isCaseSensitive method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsCaseSensitive() throws Exception {
        System.out.println("isCaseSensitive");

        jdbcResultSetMetaData rsmd = newJdbcResultSetMetaData();

        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            switch(i) {
                case 5  : // char
                case 12 : // longvarchar
                case 13 : // object
                case 20 : // varchar
                {
                    assertEquals("column: " + i, true, rsmd.isCaseSensitive(i));
                    break;
                }
                default : {
                    assertEquals("column: " + i, false, rsmd.isCaseSensitive(i));
                    break;
                }
            }
        }
    }

    /**
     * Test of isSearchable method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsSearchable() throws Exception {
        System.out.println("isSearchable");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            switch(i) {
                case 13 : // object
                {
                    assertEquals("column: " + i, false,  rsmd.isSearchable(i));
                    break;
                }
                default : {
                    assertEquals("column: " + i, true,  rsmd.isSearchable(i));
                    break;
                }
            }
        }
    }

    /**
     * Test of isCurrency method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsCurrency() throws Exception {
        System.out.println("isCurrency");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // don't support currency type
        for (int i = 1; i <= columnCount; i++) {
            assertEquals("column: " + i, false, rsmd.isCurrency(i));
        }
    }

    /**
     * Test of isNullable method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsNullable() throws Exception {
        System.out.println("isNullable");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            // column 1 is identity (auto-increment), hence not nullable
            assertEquals("column: " + i,
                         i == 1 ? ResultSetMetaData.columnNoNulls
                                : ResultSetMetaData.columnNullable,
                         rsmd.isNullable(i));
        }
    }

    /**
     * Test of isSigned method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsSigned() throws Exception {
        System.out.println("isSigned");

        jdbcResultSetMetaData rsmd = newJdbcResultSetMetaData();

        int columnCount = rsmd.getColumnCount();

        boolean expResult;

        for (int i = 1; i <= columnCount; i++) {
            switch(i) {
                case 1 :  // integer identity
                case 2 :  // bigint
                case 7 :  // decimal
                case 8 :  // double
                case 9 :  // float
                case 10 : // integer
                case 14 : // real
                case 15 : // smallint
                case 18 : // tinyint
                {
                    expResult = true;
                    break;
                }
                default : // non-number types
                {
                    expResult = false;
                    break;
                }
            }

            assertEquals("column: " + i, expResult, rsmd.isSigned(i));
        }
    }

    /**
     * Test of getColumnDisplaySize method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnDisplaySize() throws Exception {
        System.out.println("getColumnDisplaySize");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        // TODO: a thorough test that verifies effect of create params, etc?
        for (int i = 1; i <= columnCount; i++) {
           rsmd.getColumnDisplaySize(i);
        }
    }

    /**
     * Test of getColumnLabel method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnLabel() throws Exception {
        System.out.println("getColumnLabel");

        final String[] labels = new String[] {
               "id_column",                        // 1
               "bigint_column",
               "binary_column",
               "boolean_column",
               "char_column",                       // 5
               "date_column",
               "decimal_column",
               "double_column",
               "float_column",
               "integer_column",                    // 10
               "longvarbinary_column",
               "longvarchar_column",
               "object_column",
               "real_column",
               "smallint_column",                   // 15
               "time_column",
               "timestamp_column",
               "tinyint_column",
               "varbinary_column",
               "varchar_column",                    // 20
               "varchar_ignorecase_column",
        };

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, labels[i-1].toUpperCase(), rsmd.getColumnLabel(i));
        }
    }

    /**
     * Test of getColumnName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnName() throws Exception {
        System.out.println("getColumnName");

        final String[] names = new String[] {
               "id",                          // 1
               "c_bigint",
               "c_binary",
               "c_boolean",
               "c_char",                       // 5
               "c_date",
               "c_decimal",
               "c_double",
               "c_float",
               "c_integer",                    // 10
               "c_longvarbinary",
               "c_longvarchar",
               "c_object",
               "c_real",
               "c_smallint",                   // 15
               "c_time",
               "c_timestamp",
               "c_tinyint",
               "c_varbinary",
               "c_varchar",                    // 20
               "c_varchar_ignorecase",
        };

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           String expName = names[i-1].toUpperCase();
           String actName = rsmd.getColumnName(i);

           assertEquals("column: " + i + ": [" + expName + ", " + actName + "]", expName, actName);
        }
    }

    /**
     * Test of getSchemaName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetSchemaName() throws Exception {
        System.out.println("getSchemaName");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, "PUBLIC", rsmd.getSchemaName(i));
        }
    }

    /**
     * Test of getPrecision method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetPrecision() throws Exception {
        System.out.println("getPrecision");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        // TODO: a thorough test that verifies effect of create params, etc?
        for (int i = 1; i <= columnCount; i++) {
           rsmd.getPrecision(i);
        }
    }

    /**
     * Test of getScale method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetScale() throws Exception {
        System.out.println("getScale");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        // TODO: a thorough test that verifies effect of create params, etc?
        for (int i = 1; i <= columnCount; i++) {
           rsmd.getScale(i);
        };
    }

    /**
     * Test of getTableName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetTableName() throws Exception {
        System.out.println("getTableName");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, "ALL_TYPES", rsmd.getTableName(i));
        }
    }

    /**
     * Test of getCatalogName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetCatalogName() throws Exception {
        System.out.println("getCatalogName");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, "", rsmd.getCatalogName(i));
        }
    }

    /**
     * Test of getColumnType method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnType() throws Exception {
        System.out.println("getColumnType");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        for (int i = 1; i <= columnCount; i++) {
           rsmd.getColumnType(i);
        };
    }

    /**
     * Test of getColumnTypeName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnTypeName() throws Exception {
        System.out.println("getColumnTypeName");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        for (int i = 1; i <= columnCount; i++) {
           rsmd.getColumnTypeName(i);
        };
    }

    /**
     * Test of isReadOnly method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsReadOnly() throws Exception {
        System.out.println("isReadOnly");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, false, rsmd.isReadOnly(i));
        }
    }

    /**
     * Test of isWritable method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsWritable() throws Exception {
        System.out.println("isWritable");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, true, rsmd.isWritable(i));
        }
    }

    /**
     * Test of isDefinitelyWritable method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsDefinitelyWritable() throws Exception {
        System.out.println("isDefinitelyWritable");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
           assertEquals("column: " + i, false, rsmd.isDefinitelyWritable(i));
        }
    }

    /**
     * Test of getColumnClassName method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testGetColumnClassName() throws Exception {
        System.out.println("getColumnClassName");

        jdbcResultSetMetaData rsmd        = newJdbcResultSetMetaData();
        int                   columnCount = rsmd.getColumnCount();

        // It is enough to be able to invoke this against each column type.
        // Different DBMS can have different policies, so we'd just be
        // testing our own policy, not the JDBC spec.

        for (int i = 1; i <= columnCount; i++) {
           assertFalse("column: " + i, null == rsmd.getColumnClassName(i));
        };
    }

    /**
     * Test of unwrap method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testUnwrap() throws Exception {
        System.out.println("unwrap");

        Class             iface = jdbcResultSetMetaData.class;
        ResultSetMetaData rsmd  = (ResultSetMetaData) newJdbcResultSetMetaData();

        assertEquals(rsmd, rsmd.unwrap(iface));
    }

    /**
     * Test of isWrapperFor method, of class org.hsqldb.jdbc.jdbcResultSetMetaData.
     */
    public void testIsWrapperFor() throws Exception {
        System.out.println("isWrapperFor");

        Class             iface = jdbcResultSetMetaData.class;
        ResultSetMetaData rsmd  = (ResultSetMetaData) newJdbcResultSetMetaData();

        assertEquals(true, rsmd.isWrapperFor(iface));
    }

    public static void main(java.lang.String[] argList) {

        junit.textui.TestRunner.run(suite());
    }

}
