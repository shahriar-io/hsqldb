/* Copyright (c) 2001-2007, The HSQL Development Group
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


package org.hsqldb.types;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.hsqldb.HsqlException;
import org.hsqldb.SessionInterface;
import org.hsqldb.lib.ArrayUtil;

/**
 * Implementation of BlobData for memory binary data.<p>
 *
 * @author fredt@users
 * @version 1.9.0
 * @since 1.9.0
 */
public class BlobDataMemory implements BlobData {

    long             id;
    protected byte[] data;

    /**
     * This constructor is used inside the engine when an already serialized
     * byte[] is read from a file (.log, .script, .data or text table source).
     * In this case clone is false.
     *
     * When a byte[] is submitted as a parameter of PreparedStatement then
     * clone is true.
     */
    public BlobDataMemory(byte[] data, boolean clone) {

        if (clone) {
            data = (byte[]) ArrayUtil.duplicateArray(data);
        }

        this.data = data;
    }

    public BlobDataMemory(BlobData b1, BlobData b2) throws HsqlException {

        data = new byte[(int) (b1.length() + b2.length())];

        System.arraycopy(b1.getBytes(), 0, data, 0, (int) b1.length());
        System.arraycopy(b2.getBytes(), 0, data, (int) b1.length(),
                         (int) b2.length());
    }

    public BlobDataMemory(long length,
                          DataInput stream) throws HsqlException {

        data = new byte[(int) length];

        try {
            stream.readFully(data);
        } catch (IOException e) {
            throw new HsqlException(e, null, 0);
        }
    }

    public byte[] getBytes() {
        return data;
    }

    public byte[] getClonedBytes() {
        return (byte[]) data.clone();
    }

    public long length() {
        return data.length;
    }

    public byte[] getBytes(long pos, int length) {

        if (!isInLimits(data.length, pos, length)) {
            throw new IndexOutOfBoundsException();
        }

        byte[] bytes = new byte[length];

        System.arraycopy(data, (int) pos, bytes, 0, length);

        return bytes;
    }

    public InputStream getBinaryStream() throws HsqlException {
        return new BlobInputStream(this, 0L, length());
    }

    public InputStream getBinaryStream(long pos,
                                       long length) throws HsqlException {

        if (!isInLimits(data.length, pos, length)) {
            throw new IndexOutOfBoundsException();
        }

        return new BlobInputStream(this, pos, length());
    }

    public int setBytes(long pos, byte[] bytes, int offset, int length) {

        if (!isInLimits(data.length, pos, 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (!isInLimits(data.length, pos, length)) {
            data = (byte[]) ArrayUtil.resizeArray(data, (int) pos + length);
        }

        System.arraycopy(bytes, offset, data, (int) pos, length);

        return length;
    }

    public int setBytes(long pos, byte[] bytes) {

        setBytes(pos, bytes, 0, bytes.length);

        return bytes.length;
    }

    public OutputStream setBinaryStream(long pos) {
        return null;
    }

    public void truncate(long len) {

        if (data.length > len) {
            data = (byte[]) ArrayUtil.resizeArray(data, (int) len);
        }
    }

    public BlobData duplicate() throws HsqlException {
        return new BlobDataMemory(data, true);
    }

    public long position(byte[] pattern, long start) {
        return 0L;
    }

    public long position(BlobData pattern, long start) {
        return 0L;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStreamBlockSize() {
        return 1024;
    }

    public boolean isClosed() {
        return false;
    }

    public void free() {}

    public void setSession(SessionInterface session) {}
    ;

    //---
    static boolean isInLimits(long fullLength, long pos, long len) {
        return pos >= 0 && len >= 0 && pos + len <= fullLength;
    }
}
