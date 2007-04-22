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


package org.hsqldb.jdbc.pool;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import java.sql.Connection;
import java.sql.SQLException;

//#ifdef JDBC4
import javax.sql.StatementEventListener;

//#endif JDBC4
// boucherb@users 20051207 - patch 1.8.0.x initial JDBC 4.0 support work

/**
 * @author Jakob Jenkov
 */
public class JDBCPooledConnection implements PooledConnection {

    private LifeTimeConnectionWrapper connectionWrapper = null;

    public JDBCPooledConnection(LifeTimeConnectionWrapper connectionWrapper) {
        this.connectionWrapper = connectionWrapper;
    }

    public void close() throws SQLException {

        connectionWrapper.closePhysically();

        this.connectionWrapper = null;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionWrapper;
    }

    public void addConnectionEventListener(ConnectionEventListener listener) {
        this.connectionWrapper.addConnectionEventListener(listener);
    }

    public void removeConnectionEventListener(
            ConnectionEventListener listener) {
        this.connectionWrapper.removeConnectionEventListener(listener);
    }

//#ifdef JDBC4
    public void addStatementEventListener(StatementEventListener listener) {
        // TODO:
    }

//#endif JDBC4
//#ifdef JDBC4
    public void removeStatementEventListener(StatementEventListener listener) {
        // TODO:
    }

//#endif JDBC4
}
