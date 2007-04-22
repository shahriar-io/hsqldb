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

import org.hsqldb.HsqlException;
import org.hsqldb.Trace;
import org.hsqldb.Types;

public class IntervalMonthData {

    final int    units;

    public static IntervalMonthData newIntervalYear(int years,
            IntervalType type) throws HsqlException {
        return new IntervalMonthData(years * 12, type);
    }

    public static IntervalMonthData newIntervalMonth(int months,
            IntervalType type) throws HsqlException {
        return new IntervalMonthData(months, type);
    }

    public IntervalMonthData(int months,
                             IntervalType type) throws HsqlException {

        if (months >= type.getIntervalValueLimit()) {

            // todo - message precision exceeded
            // data exception interval field overflow.
            throw Trace.error(Trace.NUMERIC_VALUE_OUT_OF_RANGE);
        }

        if (type.type == Types.SQL_INTERVAL_YEAR ) {
            months -= (months % 12);
        }

        this.units = months;
    }

    public boolean equals(Object other) {

        if (other instanceof IntervalMonthData) {
            return units == ((IntervalMonthData) other).units;
        }

        return false;
    }

    public int hashCode() {
        return units;
    }

    public int compareTo(IntervalMonthData b) {

        int diff = units - b.units;

        if (diff == 0) {
            return 0;
        } else {
            return diff > 0 ? 1
                            : -1;
        }
    }

    public String toString() {

        throw Trace.runtimeError(Trace.UNSUPPORTED_INTERNAL_OPERATION,
                                 "IntervalMonthData");
    }
}
