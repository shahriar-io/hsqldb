/* Copyright (c) 2001-2009, The HSQL Development Group
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.hsqldb.jdbc.testbase.BaseTestCase;

/**
 * Test of interface java.sql.Blob.
 *
 * @author boucherb@users
 */
public class JDBCBlobTest extends BaseTestCase {

    public JDBCBlobTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(JDBCBlobTest.class);

        return suite;
    }

    protected Blob newBlob(byte[] bytes) throws Exception {
        return new JDBCBlob(bytes);
    }

    protected Blob createBlob() throws Exception {
        return newConnection().createBlob();
    }

    /**
     * Test of length method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testLength() throws Exception {
        println("length");

        Blob blob   = newBlob(new byte[100]);
        long result = blob.length();

        assertEquals(100L, result);
    }

    /**
     * Test of getBytes method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testGetBytes() throws Exception {
        println("getBytes");

        byte[] bytes = "getBytes".getBytes();
        Blob   blob  = newBlob(bytes);

        byte[] result = blob.getBytes(1, bytes.length);

        assertEquals(bytes.length, result.length);
        assertJavaArrayEquals(bytes, result);
    }

    /**
     * Test of getBinaryStream method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testGetBinaryStream() throws Exception {
        println("getBinaryStream");

        byte[] bytes = "getBinaryStream".getBytes();
        Blob   blob  = newBlob(bytes);

        InputStream expResult = new ByteArrayInputStream(bytes);
        InputStream result    = blob.getBinaryStream();

        assertStreamEquals(expResult, result);
    }

    /**
     * Test of position method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testPosition() throws Exception {
        println("position");

        byte[] pattern = new byte[]{(byte) 1, (byte)3};
        byte[] bytes   = new byte[]{(byte) 10, (byte) 1, (byte) 3, (byte) 2};
        Blob   blob    = newBlob(bytes);
        long   result  = blob.position(pattern, 1L);

        assertEquals(2L, result);
    }

    /**
     * Test of setBytes method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testSetBytes() throws Exception {
        println("setBytes");

        byte[] bytes  = new byte[1];
        Blob   blob   = createBlob();
        int    result = 0;

        try {
            result = blob.setBytes(1, bytes);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(1, result);
    }

    /**
     * Test of setBinaryStream method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testSetBinaryStream() throws Exception {
        println("setBinaryStream");

        Blob blob = createBlob();

        try {
            OutputStream result = blob.setBinaryStream(1);

            result.write(new byte[10]);

            assertEquals(0L, blob.length());

            result.close();

            assertEquals(10L, blob.length());

            assertJavaArrayEquals(new byte[10], blob.getBytes(1, 10));
        } catch (SQLException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test of truncate method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testTruncate() throws Exception {
        println("truncate");

        Blob blob = newBlob(new byte[10]);

        try {
            blob.truncate(5);
        } catch (Exception e) {
            fail(e.toString());
        }

        assertEquals(5L, blob.length());
    }

    /**
     * Test of free method, of class org.hsqldb.jdbc.jdbcBlob.
     * @throws java.lang.Exception
     */
    public void testFree() throws Exception {
        println("free");

        Blob blob = newBlob(new byte[10]);

        try {
            blob.free();
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    // after free contract tests....

    public void testGetBinaryStreamAfterFree() throws Exception {
        println("getBinaryStreamAfterFree");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.getBinaryStream();
            assertTrue("getBinaryStream operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testGetBytesAfterFree() throws Exception {
        println("getBytesAfterFree");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.getBytes(1,1);
            assertTrue("getBytes operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testLengthAfterFree() throws Exception {
        println("lengthAfterFree");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.length();
            assertTrue("length operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testPositionAfterFree() throws Exception {
        println("positionAfterFree");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.position(new byte[1],1);
            assertTrue("position(byte[],long) operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testSetBinaryStreamAfterFree() throws Exception {
        println("binaryStreamAfterFree");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.setBinaryStream(1);
            assertTrue("setBinaryStream operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testSetBytesAfterFree() throws Exception {
        println("free");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.setBytes(1, new byte[1]);
            assertTrue("setBytes(long,byte[]) operation allowed after free", false);
        } catch (Exception e){ }
    }

    public void testTruncateAfterFree() throws Exception {
        println("free");

        Blob blob = newBlob(new byte[10]);

        blob.free();

        try {
            blob.truncate(1);
            assertTrue("truncate operation allowed after free", false);
        } catch (Exception e){ }
    }

    public static void main(java.lang.String[] argList) {

        junit.textui.TestRunner.run(suite());
    }

}
