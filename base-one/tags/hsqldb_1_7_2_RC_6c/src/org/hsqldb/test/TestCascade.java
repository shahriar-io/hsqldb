/* Copyright (c) 2001-2004, The HSQL Development Group
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


package org.hsqldb.test;

/*
 * CascadeDeleteBug.java
 *
 * Created on June 24, 2002, 8:48 AM
 */
import java.io.*;
import java.sql.*;

/**
 * Test case to demonstrate catastrophic bug in cascade delete code.
 *
 * @version 1.0
 * @author  David Kopp
 */
public class TestCascade {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Class.forName("org.hsqldb.jdbcDriver");
            createDatabase();

            Connection con = DriverManager.getConnection("jdbc:hsqldb:testdb",
                "sa", "");

            insertData(con);
            System.out.println("should print 12");
            printNumberOfCARecords(con);
            deleteXBRecord(con);
            System.out.println("should print 9");
            printNumberOfCARecords(con);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    // main

    private static void createDatabase() throws SQLException {

        new File("testdb.backup").delete();
        new File("testdb.data").delete();
        new File("testdb.properties").delete();
        new File("testdb.script").delete();

        Connection con = DriverManager.getConnection("jdbc:hsqldb:testdb",
            "sa", "");
        String[] saDDL = {
            "CREATE CACHED TABLE XB (EIACODXA VARCHAR(10) NOT NULL, LSACONXB VARCHAR(18) NOT NULL, ALTLCNXB VARCHAR(2) NOT NULL, LCNTYPXB VARCHAR(1) NOT NULL, LCNINDXB VARCHAR(1), LCNAMEXB VARCHAR(19), UPDT_BY VARCHAR(32), LST_UPDT TIMESTAMP, CONSTRAINT XPKXB PRIMARY KEY (EIACODXA, LSACONXB, ALTLCNXB, LCNTYPXB));",
            "CREATE INDEX XIF2XB ON XB (EIACODXA);",
            "CREATE CACHED TABLE CA ( EIACODXA VARCHAR(10) NOT NULL, LSACONXB VARCHAR(18) NOT NULL, ALTLCNXB VARCHAR(2) NOT NULL, LCNTYPXB VARCHAR(1) NOT NULL, TASKCDCA VARCHAR(7) NOT NULL, TSKFRQCA NUMERIC(7,4), UPDT_BY VARCHAR(32), LST_UPDT TIMESTAMP, CONSTRAINT XPKCA PRIMARY KEY (EIACODXA, LSACONXB, ALTLCNXB, LCNTYPXB, TASKCDCA),        CONSTRAINT R_XB_CA FOREIGN KEY (EIACODXA, LSACONXB, ALTLCNXB, LCNTYPXB) REFERENCES XB ON DELETE CASCADE);",
            "CREATE INDEX XIF26CA ON CA ( EIACODXA, LSACONXB, ALTLCNXB, LCNTYPXB);"
        };
        Statement stmt = con.createStatement();

        for (int index = 0; index < saDDL.length; index++) {
            stmt.executeUpdate(saDDL[index]);
        }

        con.close();
    }    // createDatabase

    /**
     * This method demonstrates the bug in cascading deletes. Before this method,
     * the CA table has 12 records. After, it should have 9, but instead it has
     * 0.
     */
    private static void deleteXBRecord(Connection con) throws SQLException {

        Statement stmt = con.createStatement();

        stmt.executeUpdate(
            "DELETE FROM XB WHERE LSACONXB = 'LEAA' AND EIACODXA = 'T850' AND LCNTYPXB = 'P' AND ALTLCNXB = '00'");
        stmt.close();
    }    // deleteXBRecord

    private static void insertData(Connection con) throws SQLException {

        String[] saData = {
            "INSERT INTO XB VALUES('T850','LEAA','00','P',NULL,'LCN NAME','sa','NOW')",
            "INSERT INTO XB VALUES('T850','LEAA01','00','P',NULL,'LCN NAME','sa','NOW')",
            "INSERT INTO XB VALUES('T850','LEAA02','00','P',NULL,'LCN NAME','sa','NOW')",
            "INSERT INTO XB VALUES('T850','LEAA03','00','P',NULL,'LCN NAME','sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA','00','P','ABCDEFG',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA','00','P','QRSTUJV',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA','00','P','ZZZZZZZ',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA01','00','P','ABCDEFG',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA01','00','P','QRSTUJV',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA01','00','P','ZZZZZZZ',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA02','00','P','ABCDEFG',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA02','00','P','QRSTUJV',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA02','00','P','ZZZZZZZ',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA03','00','P','ABCDEFG',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA03','00','P','QRSTUJV',3.14,'sa','NOW')",
            "INSERT INTO CA VALUES('T850','LEAA03','00','P','ZZZZZZZ',3.14,'sa','NOW')"
        };
        Statement stmt = con.createStatement();

        for (int index = 0; index < saData.length; index++) {
            stmt.executeUpdate(saData[index]);
        }
    }    // insertData

    private static void printNumberOfCARecords(Connection con)
    throws SQLException {

        Statement stmt = con.createStatement();
        ResultSet rs   = stmt.executeQuery("SELECT COUNT(EIACODXA) FROM CA");

        rs.next();
        System.out.println(rs.getInt(1));
        System.out.flush();
        stmt.close();
    }    // printNumberOfCARecords
}    // CascadeDeleteBug

