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

import java.sql.*;
import java.io.*;
import java.util.Vector;

/**
 * Utility class providing methodes for submitting test statements or
 * scripts to the database, comparing the results returned with
 * the expected results. The test script format is compatible with existing
 * scripts.
 *
 * @author ewanslater@users
 * @author fredt@users
 */
public class TestUtil {

    /**
     * Runs a preformatted script.<p>
     *
     * Where a result set is required, each line in the script will
     * be interpreted as a seperate expected row in the ResultSet
     * returned by the query.  Within each row, fields should be delimited
     * using either comma (the default), or a user defined delimiter
     * which should be specified in the System property TestUtilFieldDelimiter
     * @param aConnection Connection object for the database
     * @param aPath Path of the script file to be tested
     */
    static void testScript(Connection aConnection, String aPath) {

        try {
            Statement statement = aConnection.createStatement();
            File      testfile  = new File(aPath);
            LineNumberReader reader =
                new LineNumberReader(new FileReader(testfile));
            Vector section = null;

            print("Opened test script file: " + testfile.getAbsolutePath());

            /**
             * we read the lines from the start of one section of the script "/*"
             *  until the start of the next section, collecting the lines
             *  in the Vector lines.
             *  When a new section starts, we will pass the vector of lines
             *  to the test method to be processed.
             */
            int startLine = 1;

            while (true) {
                boolean startSection = false;
                String  line         = reader.readLine();

                if (line == null) {
                    break;
                }

                line = line.substring(
                    0, org.hsqldb.lib.StringUtil.rTrimSize(line));

                //if the line is blank or a comment, then ignore it
                if ((line.length() == 0) || line.startsWith("--")) {
                    continue;
                }

                //...check if we're starting a new section...
                if (line.startsWith("/*")) {
                    startSection = true;
                }

                if (line.charAt(0) != ' ' && line.charAt(0) != '*') {
                    startSection = true;
                }

                if (startSection) {

                    //...if we are, test the previous section (if it exists)...
                    if (section != null) {
                        testSection(statement, section, startLine);
                    }

                    //...and then start a new section...
                    section   = new Vector();
                    startLine = reader.getLineNumber();
                }

                section.add(line);
            }

            //send the last section for testing
            if (section != null) {
                testSection(statement, section, startLine);
            }

            statement.close();
            print("Processed lines: " + reader.getLineNumber());
        } catch (Exception e) {
            e.printStackTrace();
            print("test script file error: " + e.getMessage());
        }
    }

    /**
     * Performs a preformatted statement or group of statements and throws
     *  if the result does not match the expected one.
     * @param line start line in the script file for this test
     * @param stat Statement object used to access the database
     * @param s Contains the type, expected result and SQL for the test
     */
    static void test(Statement stat, String s, int line) {

        //maintain the interface for this method
        Vector section = new Vector();

        section.add(s);
        testSection(stat, section, line);
    }

    /**
     * Method to save typing ;-)
     * @param s String to be printed
     */
    static void print(String s) {
        System.out.println(s);
    }

    /**
     * Takes a discrete section of the test script, contained in the
     * section vector, splits this into the expected result(s) and
     * submits the statement to the database, comparing the results
     * returned with the expected results.
     * If the actual result differs from that expected, or an
     * exception is thrown, then the appropriate message is printed.
     * @param stat Statement object used to access the database
     * @param section Vector of script lines containing a discrete
     * section of script (i.e. test type, expected results,
     * SQL for the statement).
     * @param line line of the script file where this section started
     */
    private static void testSection(Statement stat, Vector section,
                                    int line) {

        //create an appropriate instance of ParsedSection
        ParsedSection pSection = parsedSectionFactory(section);

        if (!pSection.test(stat)) {
            print("section starting at line " + line);
            print("returned an unexpected result:");
            print(pSection.toString());
        }
    }

    /**
     * Factory method to create appropriate parsed section class for the section
     * @param aSection Vector containing the section of script
     */
    private static ParsedSection parsedSectionFactory(Vector aSection) {

        //type of the section
        char type = ' ';

        //section represented as an array of Strings, one for each significant
        //line in the section
        String[] rows = null;

        //read the first line of the Vector...
        String topLine = (String) aSection.get(0);

        //...and check it for the type...
        if (topLine.startsWith("/*")) {
            type = topLine.charAt(2);

            //...strip out the type declaration...
            topLine = topLine.substring(3);
        }

        //if, after stripping out the declaration from topLine, the length of topLine
        //is greater than 0, then keep the rest of the line, as the first row.
        //Otherwise it will be discarded, and the offset (between the array and the vector)
        //set to 1.
        int offset = 0;

        if (topLine.trim().length() > 0) {
            rows    = new String[aSection.size()];
            rows[0] = topLine;
        } else {
            rows   = new String[aSection.size() - 1];
            offset = 1;
        }

        //pull the rest of aSection into the rows array.
        for (int i = (1 - offset); i < rows.length; i++) {
            rows[i] = (String) aSection.get(i + offset);
        }

        //then pass this to the constructor for the ParsedSection class that
        //corresponds to the value of type
        switch (type) {

            case 'u' :
                return new UpdateParsedSection(rows);

            case 'r' :
                return new ResultSetParsedSection(rows);

            case 'c' :
                return new CountParsedSection(rows);

            case 'e' :
                return new ExceptionParsedSection(rows);

            case ' ' :
                return new BlankParsedSection(rows);

            default :
                return null;
        }
    }
}

/**
 * Abstract inner class representing a parsed section of script.
 * The specific ParsedSections for each type of test should inherit from this.
 */
abstract class ParsedSection {

    /**
     * Type of this test.  Allowed values are:
     * 'u' (update)
     * 'c' (count)
     * 'e' (exception)
     * 'r' (results)
     * ' ' (not a test)
     */
    protected char type = ' ';

    /** error message for this section */
    String message = null;

    /** contents of the section as an array of Strings, one for each line in the section. */
    protected String[] lines = null;

    /** number of the last row containing results in sectionLines */
    protected int resEndRow = 0;

    /** SQL query to be submitted to the database. */
    protected String sqlString = null;

    /**
     * Common constructor functions for this family.
     * @param aLines Array of the script lines containing the section of script.
     * @param aStatement Statement for this section to use to execute against the
     * database
     */
    protected ParsedSection(String[] aLines) {

        lines = aLines;

        //read the lines array backwards to get out the SQL String
        //using a StringBuffer for efficency until we've got the whole String
        StringBuffer sqlBuff  = new StringBuffer();
        int          endIndex = 0;
        int          k        = lines.length - 1;

        do {

            //check to see if the row contains the end of the result set
            if ((endIndex = lines[k].indexOf("*/")) != -1) {

                //then this is the end of the result set
                sqlBuff.insert(0, lines[k].substring(endIndex + 2));

                lines[k] = lines[k].substring(0, endIndex);

                if (lines[k].length() == 0) {
                    resEndRow = k - 1;
                } else {
                    resEndRow = k;
                }

                break;
            } else {
                sqlBuff.insert(0, lines[k]);
            }

            k--;
        } while (k >= 0);

        //set sqlString value
        sqlString = sqlBuff.toString();
    }

    /**
     * String representation of this ParsedSection
     * @return String representation of this ParsedSection
     */
    public String toString() {

        StringBuffer b = new StringBuffer();

        b.append("\n******\n");
        b.append("contents of lines array:\n");

        for (int i = 0; i < lines.length; i++) {
            b.append("line ").append(i).append(": ").append(lines[i]).append(
                "\n");
        }

        b.append("Type: ");
        b.append(getType()).append("\n");
        b.append("SQL: ").append(getSql()).append("\n");
        b.append("results:\n");
        b.append(getResultString());

        //check to see if the message field has been populated
        if (getMessage() != null) {
            b.append("\nmessage:\n");
            b.append(getMessage());
        }

        b.append("\n******\n");

        return b.toString();
    }

    /**
     * returns a String representation of the expected result for the test
     * @return The expected result(s) for the test
     */
    abstract protected String getResultString();

    /** returns the error message for the section */
    protected String getMessage() {
        return message;
    }

    /**
     * returns the type of this section
     * @return type of this section
     */
    protected char getType() {
        return type;
    }

    /**
     * returns the SQL statement for this section
     * @return SQL statement for this section
     */
    protected String getSql() {
        return sqlString;
    }

    /**
     * performs the test contained in the section against the database.
     * @return true if the result(s) are as expected, otherwise false
     */
    protected boolean test(Statement aStatement) {

        try {
            aStatement.execute(getSql());
        } catch (Exception x) {
            message = x.getMessage();

            return false;
        }

        return true;
    }
}

/** Represents a ParsedSection for a ResultSet test */
class ResultSetParsedSection extends ParsedSection {

    private String delim = System.getProperty("TestUtilFieldDelimiter", ",");
    private String[] expectedRows = null;

    /**
     * constructs a new instance of ResultSetParsedSection, interpreting
     * the supplied results as one or more lines of delimited field values
     */
    protected ResultSetParsedSection(String[] lines) {

        super(lines);

        type = 'r';

        //now we'll populate the expectedResults array
        expectedRows = new String[(resEndRow + 1)];

        for (int i = 0; i <= resEndRow; i++) {
            expectedRows[i] = lines[i];
        }
    }

    protected String getResultString() {

        StringBuffer printVal = new StringBuffer();

        for (int i = 0; i < getExpectedRows().length; i++) {
            printVal.append(getExpectedRows()[i]).append("\n");
        }

        return printVal.toString();
    }

    protected boolean test(Statement aStatement) {

        try {

            //execute the SQL
            aStatement.execute(getSql());

            //check that update count != -1
            if (aStatement.getUpdateCount() != -1) {
                throw new Exception(
                    "Expected a ResultSet, but got an update count of "
                    + aStatement.getUpdateCount());
            }

            //iterate over the ResultSet
            ResultSet results = aStatement.getResultSet();
            int       count   = 0;

            while (results.next()) {
                if (count < getExpectedRows().length) {
                    String[] expectedFields =
                        getExpectedRows()[count].split(delim);

                    //check that we have the number of columns expected...
                    if (results.getMetaData().getColumnCount()
                            == expectedFields.length) {

                        //...and if so, check that the column values are as expected...
                        int j = 0;

                        for (int i = 0; i < expectedFields.length; i++) {
                            j = i + 1;

                            //...including null values...
                            if (results.getString(j) == null) {    //..then we have a null

                                //...check to see if we were expecting it...
                                if (!expectedFields[i].trim()
                                        .equalsIgnoreCase("NULL")) {
                                    throw new Exception(
                                        "Expected row " + count
                                        + " of the ResultSet to contain:\n"
                                        + getExpectedRows()[count]
                                        + "\nbut field " + j
                                        + " contained NULL");
                                }
                            } else if (!results.getString(j).equals(
                                    expectedFields[i].trim())) {

                                //then the results are different
                                throw new Exception(
                                    "Expected row " + count
                                    + " of the ResultSet to contain:\n"
                                    + getExpectedRows()[count]
                                    + "\nbut field " + j + " contained "
                                    + results.getString(j));
                            }
                        }
                    } else {

                        //we have the wrong number of columns
                        throw new Exception(
                            "Expected the ResultSet to contain "
                            + expectedFields.length
                            + " fields, but it contained "
                            + results.getMetaData().getColumnCount()
                            + " fields.");
                    }
                }

                count++;
            }

            //check that we got as many rows as expected
            if (count != getExpectedRows().length) {

                //we don't have the expected number of rows
                throw new Exception("Expected the ResultSet to contain "
                                    + getExpectedRows().length
                                    + " rows, but it contained " + count
                                    + " rows.");
            }
        } catch (Exception x) {
            message = x.getMessage();

            x.printStackTrace();

            return false;
        }

        return true;
    }

    private String[] getExpectedRows() {
        return expectedRows;
    }
}

/** Represents a ParsedSection for an update test */
class UpdateParsedSection extends ParsedSection {

    //expected update count
    int countWeWant;

    protected UpdateParsedSection(String[] lines) {

        super(lines);

        type        = 'u';
        countWeWant = Integer.parseInt(lines[0]);
    }

    protected String getResultString() {
        return Integer.toString(getCountWeWant());
    }

    private int getCountWeWant() {
        return countWeWant;
    }
}

/** Represents a ParsedSection for a count test */
class CountParsedSection extends ParsedSection {

    //expected row count
    private int countWeWant;

    protected CountParsedSection(String[] lines) {

        super(lines);

        type        = 'c';
        countWeWant = Integer.parseInt(lines[0]);
    }

    protected String getResultString() {
        return Integer.toString(getCountWeWant());
    }

    private int getCountWeWant() {
        return countWeWant;
    }

    protected boolean test(java.sql.Statement aStatement) {

        try {

            //execute the SQL
            aStatement.execute(getSql());

            //check that update count != -1
            if (aStatement.getUpdateCount() != -1) {
                throw new Exception(
                    "Expected a ResultSet, but got an update count of "
                    + aStatement.getUpdateCount());
            }

            //iterate over the ResultSet
            ResultSet results = aStatement.getResultSet();
            int       count   = 0;

            while (results.next()) {
                count++;
            }

            //check that we got as many rows as expected
            if (count != getCountWeWant()) {

                //we don't have the expected number of rows
                throw new Exception("Expected the ResultSet to contain "
                                    + getCountWeWant()
                                    + " rows, but it contained " + count
                                    + " rows.");
            }
        } catch (Exception x) {
            message = x.getMessage();

            return false;
        }

        return true;
    }
}

/** Represents a ParsedSection for an Exception test */
class ExceptionParsedSection extends ParsedSection {

    protected ExceptionParsedSection(String[] lines) {

        super(lines);

        type = 'e';
    }

    protected String getResultString() {
        return "SQLException";
    }

    protected boolean test(java.sql.Statement aStatement) {

        try {
            aStatement.execute(getSql());
        } catch (SQLException sqlX) {
            return true;
        } catch (Exception x) {
            message = x.getMessage();

            return false;
        }

        return false;
    }
}

/** Represents a ParsedSection for a section with blank type */
class BlankParsedSection extends ParsedSection {

    protected BlankParsedSection(String[] lines) {

        super(lines);

        type = ' ';
    }

    protected String getResultString() {
        return "No result specified for this section";
    }
}
