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


package org.hsqldb.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.hsqldb.HsqlException;

public class DataOutputStream {

    OutputStream stream;
    byte[]       buf = new byte[8];

    public DataOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public final void writeByte(int v) throws IOException {
        stream.write(v);
    }

    public final void writeInt(int v) throws IOException {

        int count = 0;

        buf[count++] = (byte) (v >>> 24);
        buf[count++] = (byte) (v >>> 16);
        buf[count++] = (byte) (v >>> 8);
        buf[count++] = (byte) v;

        stream.write(buf, 0, count);
    }

    public final void writeLong(long v) throws IOException {
        writeInt((int) (v >>> 32));
        writeInt((int) v);
    }

    public void writeChar(int v) throws IOException {

        int count = 0;

        buf[count++] = (byte) (v >>> 8);
        buf[count++] = (byte) v;

        stream.write(buf, 0, count);
    }

    public void writeChars(String s) throws IOException {

        int len = s.length();

        for (int i = 0; i < len; i++) {
            int v     = s.charAt(i);
            int count = 0;

            buf[count++] = (byte) (v >>> 8);
            buf[count++] = (byte) v;

            stream.write(buf, 0, count);
        }
    }

    public void writeChars(char[] c) throws IOException {
        writeChars(c, c.length);
    }

    public void writeChars(char[] c, int length) throws IOException {

        for (int i = 0; i < length; i++) {
            int v     = c[i];
            int count = 0;

            buf[count++] = (byte) (v >>> 8);
            buf[count++] = (byte) v;

            stream.write(buf, 0, count);
        }
    }

    public void write(byte[] b) throws IOException {
        stream.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }

    public void write(Reader reader) throws IOException {

        char[] data = new char[128];

        while (true) {
            int count = reader.read(data);

            if (count < 1) {
                break;
            }

            writeChars(data, count);
        }
    }

    public void write(InputStream inputStream) throws IOException {

        byte[] data = new byte[128];

        while (true) {
            int count = inputStream.read(data);

            if (count < 1) {
                break;
            }

            write(data, 0, count);
        }
    }

    public void flush() throws IOException {
        stream.flush();
    }

    public void close() throws IOException {
        stream.close();
    }
}
