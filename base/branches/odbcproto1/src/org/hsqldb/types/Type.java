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


package org.hsqldb.types;

import org.hsqldb.Error;
import org.hsqldb.ErrorCode;
import org.hsqldb.HsqlException;
import org.hsqldb.HsqlNameManager;
import org.hsqldb.HsqlNameManager.HsqlName;
import org.hsqldb.SchemaObject;
import org.hsqldb.Session;
import org.hsqldb.SessionInterface;
import org.hsqldb.Types;
import org.hsqldb.lib.IntValueHashMap;
import org.hsqldb.lib.OrderedHashSet;
import org.hsqldb.rights.Grantee;
import org.hsqldb.store.ValuePool;

/**
 * Base class for type objects.<p>
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 * @version 1.9.0
 * @since 1.9.0
 */
public abstract class Type implements SchemaObject, Cloneable {

    public final static Type[] emptyArray = new Type[]{};

    //
    public final int        typeComparisonGroup;
    public final int        typeCode;
    public final long       precision;
    public final int        scale;
    public UserTypeModifier userTypeModifier;

    //
    Type(int typeGroup, int type, long precision, int scale) {

        this.typeComparisonGroup = typeGroup;
        this.typeCode            = type;
        this.precision           = precision;
        this.scale               = scale;
    }

    // interface specific methods
    public final int getType() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getType();
    }

    public final HsqlName getName() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getName();
    }

    public final HsqlName getCatalogName() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getSchemaName().schema;
    }

    public final HsqlName getSchemaName() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getSchemaName();
    }

    public final Grantee getOwner() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getOwner();
    }

    public final OrderedHashSet getReferences() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getReferences();
    }

    public final OrderedHashSet getComponents() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getComponents();
    }

    public final void compile(Session session) throws HsqlException {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        userTypeModifier.compile(session);
    }

    /**
     *  Retrieves the SQL character sequence required to (re)create the
     *  trigger, as a StringBuffer
     *
     * @return the SQL character sequence required to (re)create the
     *  trigger
     */
    public String getSQL() {

        if (userTypeModifier == null) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }

        return userTypeModifier.getSQL();
    }

    public Type duplicate() {

        try {
            return (Type) clone();
        } catch (CloneNotSupportedException e) {
            throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }
    }

    public abstract int displaySize();

    /**
     * Returns the JDBC type number of type, if it exists,
     * otherwise the HSQLDB / SQL type.
     */
    public abstract int getJDBCTypeCode();

    /**
     * Returns the JDBC class name of type, if it exists,
     * otherwise the HSQLDB class name.
     */
    public abstract String getJDBCClassName();

    public Integer getJDBCScale() {
        return acceptsScale() ? ValuePool.getInt(scale)
                              : null;
    }

    public Integer getJDBCPrecision() {

        return precision > Integer.MAX_VALUE
               ? ValuePool.getInt(Integer.MAX_VALUE)
               : ValuePool.INTEGER_0;
    }

    /**
     * Returns the generic SQL CLI type number of type, if it exists,
     * otherwise the HSQLDB type. The generic type is returned for DATETIME
     * and INTERVAL types.
     */
    public int getSQLGenericTypeCode() {
        return typeCode;
    }

    /**
     * Returns the name of the type
     */
    public abstract String getNameString();

    /**
     * Returns the name of the type
     */
    public String getFullNameString() {
        return getNameString();
    }

    /**
     * Returns the full definition of the type, including parameters
     */
    abstract String getDefinition();

    public final String getTypeDefinition() {

        if (userTypeModifier == null) {
            return getDefinition();
        }

        return getSchemaName().statementName + '.' + getName().statementName;
    }

    public abstract int compare(Object a, Object b);

    public abstract Object convertToTypeLimits(Object a) throws HsqlException;

    /**
     * Explicit casts are handled by this method.
     * SQL standard 6.12 rules for enforcement of size, precision and scale
     * are implemented. For CHARACTER values, it performs truncation in all
     * cases of long strings.
     */
    public Object castToType(SessionInterface session, Object a,
                             Type type) throws HsqlException {
        return convertToType(session, a, type);
    }

    /**
     * Same as castToType except for CHARACTER values. Perform string
     * truncation of trailing spaces only. For other long strings, it raises
     * an exception.
     */
    public abstract Object convertToType(SessionInterface session, Object a,
                                         Type type) throws HsqlException;

    /**
     * Convert type for JDBC. Same as convertToType, but supports non-standard
     * SQL conversions supported by JDBC
     */
    public Object convertToTypeJDBC(SessionInterface session, Object a,
                                    Type type) throws HsqlException {
        return convertToType(session, a, type);
    }

    public Object convertJavaToSQL(SessionInterface session,
                                   Object a) throws HsqlException {
        return a;
    }

    public Object convertSQLToJava(SessionInterface session,
                                   Object a) throws HsqlException {
        return a;
    }

    /**
     * Converts the object to the given type. Used for JDBC conversions.
     */
    public abstract Object convertToDefaultType(
        SessionInterface sessionInterface, Object o) throws HsqlException;

    public abstract String convertToString(Object a);

    public abstract String convertToSQLString(Object a);

    public abstract boolean canConvertFrom(Type otherType);

    public boolean isDistinctType() {

        return userTypeModifier == null ? false
                                        : userTypeModifier.schemaObjectType
                                          == SchemaObject.TYPE;
    }

    public boolean isStructuredType() {
        return false;
    }

    public boolean isDomainType() {

        return userTypeModifier == null ? false
                                        : userTypeModifier.schemaObjectType
                                          == SchemaObject.DOMAIN;
    }

    public boolean isCharacterType() {
        return false;
    }

    public boolean isNumberType() {
        return false;
    }

    public boolean isIntegralType() {
        return false;
    }

    public boolean isExactNumberType() {
        return false;
    }

    public boolean isDateTimeType() {
        return false;
    }

    public boolean isDateTimeTypeWithZone() {
        return false;
    }

    public boolean isIntervalType() {
        return false;
    }

    public boolean isBinaryType() {
        return false;
    }

    public boolean isBooleanType() {
        return false;
    }

    public boolean isLobType() {
        return false;
    }

    public boolean isBitType() {
        return false;
    }

    public boolean isObjectType() {
        return false;
    }

    public boolean acceptsPrecision() {
        return false;
    }

    public boolean requiresPrecision() {
        return false;
    }

    public boolean acceptsFractionalPrecision() {
        return false;
    }

    public boolean acceptsScale() {
        return false;
    }

    public int precedenceDegree(Type other) {

        if (other.typeCode == typeCode) {
            return 0;
        }

        return Integer.MIN_VALUE;
    }

    /**
     * Common type used in comparison opertions. other must be comparable
     * with this.
     */
    public abstract Type getAggregateType(Type other) throws HsqlException;

    /**
     * Result type of combining values of two types in different opertions.
     * other type is not allways comparable with this, but a operation should
     * be valid without any explicit CAST
     */
    public abstract Type getCombinedType(Type other,
                                         int operation) throws HsqlException;

    public int compareToTypeRange(Object o) {
        return 0;
    }

    /**
     * All arithmetic ops are called on the pre-determined Type object of the result
     */
    public Object absolute(Object a) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object negate(Object a) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object add(Object a, Object b,
                      Type otherType) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object subtract(Object a, Object b,
                           Type otherType) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object multiply(Object a, Object b) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object divide(Object a, Object b) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public Object concat(Session session, Object a,
                         Object b) throws HsqlException {
        throw Error.runtimeError(ErrorCode.U_S0500, "Type");
    }

    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (other instanceof Type) {
            return ((Type) other).typeCode == typeCode
                   && ((Type) other).precision == precision
                   && ((Type) other).scale == scale
                   && ((Type) other).userTypeModifier == userTypeModifier;
        }

        return false;
    }

    public int hashCode() {
        return typeCode + (int) precision << 8 + scale << 16;
    }

    // null type
    public static final Type SQL_ALL_TYPES = NullType.getNullType();

    // character types
    public static final Type SQL_CHAR = new CharacterType(Types.SQL_CHAR, 0);
    public static final Type SQL_VARCHAR = new CharacterType(Types.SQL_VARCHAR,
        0);
    public static final Type SQL_VARCHAR_MAX_LENGTH =
        new CharacterType(Types.SQL_VARCHAR, 0);    // todo - needs max implementation defined length. used for parameters
    public static final ClobType SQL_CLOB = new ClobType();
    public static final Type VARCHAR_IGNORECASE =
        new CharacterType(Types.VARCHAR_IGNORECASE, 0);

    // binary types
    public static final BitType SQL_BIT = new BitType(Types.SQL_BIT, 0);
    public static final BitType SQL_BIT_VARYING =
        new BitType(Types.SQL_BIT_VARYING, 0);
    public static final BitType SQL_BIT_VARYING_MAX_LENGTH =
        new BitType(Types.SQL_BIT_VARYING, 0);      // todo - needs max implementation defined length. used for parameters

    // binary types
    public static final BinaryType SQL_BINARY =
        new BinaryType(Types.SQL_BINARY, 0);
    public static final BinaryType SQL_VARBINARY =
        new BinaryType(Types.SQL_VARBINARY, 0);
    public static final BlobType SQL_BLOB = new BlobType();

    // other type
    public static final OtherType OTHER = OtherType.getOtherType();

    // boolean type
    public static final BooleanType SQL_BOOLEAN = BooleanType.getBooleanType();

    // number types
    public static final NumberType SQL_NUMERIC =
        new NumberType(Types.SQL_NUMERIC, NumberType.defaultNumericPrecision,
                       0);
    public static final NumberType SQL_DECIMAL =
        new NumberType(Types.SQL_DECIMAL, NumberType.defaultNumericPrecision,
                       0);
    public static final NumberType SQL_DECIMAL_BIGINT_SQR =
        new NumberType(Types.SQL_DECIMAL,
                       NumberType.bigintSquareNumericPrecision, 0);
    public static final NumberType SQL_DOUBLE =
        new NumberType(Types.SQL_DOUBLE, 0, 0);

    //
    public static final NumberType TINYINT = new NumberType(Types.TINYINT,
        NumberType.tinyintPrecision, 0);
    public static final NumberType SQL_SMALLINT =
        new NumberType(Types.SQL_SMALLINT, NumberType.smallintPrecision, 0);
    public static final NumberType SQL_INTEGER =
        new NumberType(Types.SQL_INTEGER, NumberType.integerPrecision, 0);
    public static final NumberType SQL_BIGINT =
        new NumberType(Types.SQL_BIGINT, NumberType.bigintPrecision, 0);

    // date time
    public static final DateTimeType SQL_DATE =
        new DateTimeType(Types.SQL_TIMESTAMP, Types.SQL_DATE, 0);
    public static final DateTimeType SQL_TIME =
        new DateTimeType(Types.SQL_TIME, Types.SQL_TIME,
                         DTIType.defaultTimeFractionPrecision);
    public static final DateTimeType SQL_TIME_WITH_TIME_ZONE =
        new DateTimeType(Types.SQL_TIME, Types.SQL_TIME_WITH_TIME_ZONE,
                         DTIType.defaultTimeFractionPrecision);
    public static final DateTimeType SQL_TIMESTAMP =
        new DateTimeType(Types.SQL_TIMESTAMP, Types.SQL_TIMESTAMP,
                         DTIType.defaultTimestampFractionPrecision);
    public static final DateTimeType SQL_TIMESTAMP_WITH_TIME_ZONE =
        new DateTimeType(Types.SQL_TIMESTAMP,
                         Types.SQL_TIMESTAMP_WITH_TIME_ZONE,
                         DTIType.defaultTimestampFractionPrecision);
    public static final DateTimeType SQL_TIMESTAMP_NO_FRACTION =
        new DateTimeType(Types.SQL_TIMESTAMP, Types.SQL_TIMESTAMP, 0);

    // interval
    public static final IntervalType SQL_INTERVAL_YEAR =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_YEAR,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_MONTH =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_MONTH,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_DAY =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_DAY,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_HOUR =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_HOUR,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_MINUTE =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_MINUTE,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_SECOND =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_SECOND,
                                     DTIType.defaultIntervalPrecision,
                                     DTIType.defaultIntervalFractionPrecision);
    public static final IntervalType SQL_INTERVAL_SECOND_MAX_FRACTION =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_SECOND,
                                     DTIType.defaultIntervalPrecision,
                                     DTIType.maxFractionPrecision);
    public static final IntervalType SQL_INTERVAL_YEAR_TO_MONTH =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_YEAR_TO_MONTH,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_DAY_TO_HOUR =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_DAY_TO_HOUR,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_DAY_TO_MINUTE =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_DAY_TO_MINUTE,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_DAY_TO_SECOND =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_DAY_TO_SECOND,
                                     DTIType.defaultIntervalPrecision,
                                     DTIType.defaultIntervalFractionPrecision);
    public static final IntervalType SQL_INTERVAL_HOUR_TO_MINUTE =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_HOUR_TO_MINUTE,
                                     DTIType.defaultIntervalPrecision, 0);
    public static final IntervalType SQL_INTERVAL_HOUR_TO_SECOND =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_HOUR_TO_SECOND,
                                     DTIType.defaultIntervalPrecision,
                                     DTIType.defaultIntervalFractionPrecision);
    public static final IntervalType SQL_INTERVAL_MINUTE_TO_SECOND =
        IntervalType.newIntervalType(Types.SQL_INTERVAL_MINUTE_TO_SECOND,
                                     DTIType.defaultIntervalPrecision,
                                     DTIType.defaultIntervalFractionPrecision);

    public static Type getDefaultType(int type) {

        try {
            return getType(type, 0, 0, 0);
        } catch (Exception e) {
            return null;
        }
    }

    public static Type getDefaultTypeWithSize(int type) {

        try {
            return getType(type, 0, 0, 0);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getHSQLDBTypeCode(int jdbcTypeNumber) {

        switch (jdbcTypeNumber) {

            case Types.BIGINT :
                return Types.SQL_BIGINT;

            case Types.LONGVARCHAR :
                return Types.SQL_VARCHAR;

            case Types.CLOB :
                return Types.SQL_CLOB;

            case Types.BINARY :
                return Types.SQL_BINARY;

            case Types.BIT :
                return Types.SQL_BIT_VARYING;

            case Types.VARBINARY :
            case Types.LONGVARBINARY :
                return Types.SQL_VARBINARY;

            case Types.BLOB :
                return Types.SQL_BLOB;

            default :
                return jdbcTypeNumber;
        }
    }

    /**
     * translate an internal type number to JDBC type number if a type is not
     * supported internally, it is returned without translation
     *
     * @param type int
     * @return int
     */
    public static int getJDBCTypeCode(int type) {

        switch (type) {

            case Types.SQL_BLOB :
                return Types.BLOB;

            case Types.SQL_CLOB :
                return Types.CLOB;

            case Types.SQL_BIGINT :
                return Types.BIGINT;

            case Types.SQL_BINARY :
                return Types.BINARY;

            case Types.SQL_VARBINARY :
                return Types.VARBINARY;

            case Types.SQL_BIT :
            case Types.SQL_BIT_VARYING :
                return Types.BIT;

            default :
                return type;
        }
    }

    /**
     * Enforces precision and scale limits on type
     */
    public static Type getType(int type, int collation, long precision,
                               int scale) throws HsqlException {

        switch (type) {

            case Types.SQL_ALL_TYPES :
                return SQL_ALL_TYPES;

//                return SQL_ALL_TYPES; // needs changes to Expression type resolution
            case Types.SQL_CHAR :
            case Types.SQL_VARCHAR :
            case Types.VARCHAR_IGNORECASE :
            case Types.SQL_CLOB :
                return CharacterType.getCharacterType(type, precision);

            case Types.SQL_INTEGER :
                return SQL_INTEGER;

            case Types.SQL_SMALLINT :
                return SQL_SMALLINT;

            case Types.SQL_BIGINT :
                return SQL_BIGINT;

            case Types.TINYINT :
                return TINYINT;

            case Types.SQL_FLOAT :
                if (precision > 53) {
                    throw Error.error(ErrorCode.X_42592, "" + precision);
                }

            // fall through
            case Types.SQL_REAL :
            case Types.SQL_DOUBLE :
                return SQL_DOUBLE;

            case Types.SQL_NUMERIC :
            case Types.SQL_DECIMAL :
                if (precision == 0) {
                    precision = NumberType.defaultNumericPrecision;
                }

                return NumberType.getNumberType(type, precision, scale);

            case Types.SQL_BOOLEAN :
                return SQL_BOOLEAN;

            case Types.SQL_BINARY :
            case Types.SQL_VARBINARY :
            case Types.SQL_BLOB :
                return BinaryType.getBinaryType(type, precision);

            case Types.SQL_BIT :
            case Types.SQL_BIT_VARYING :
                return BitType.getBitType(type, precision);

            case Types.SQL_DATE :
            case Types.SQL_TIME :
            case Types.SQL_TIME_WITH_TIME_ZONE :
            case Types.SQL_TIMESTAMP :
            case Types.SQL_TIMESTAMP_WITH_TIME_ZONE :
                return DateTimeType.getDateTimeType(type, scale);

            case Types.SQL_INTERVAL_YEAR :
            case Types.SQL_INTERVAL_YEAR_TO_MONTH :
            case Types.SQL_INTERVAL_MONTH :
            case Types.SQL_INTERVAL_DAY :
            case Types.SQL_INTERVAL_DAY_TO_HOUR :
            case Types.SQL_INTERVAL_DAY_TO_MINUTE :
            case Types.SQL_INTERVAL_DAY_TO_SECOND :
            case Types.SQL_INTERVAL_HOUR :
            case Types.SQL_INTERVAL_HOUR_TO_MINUTE :
            case Types.SQL_INTERVAL_HOUR_TO_SECOND :
            case Types.SQL_INTERVAL_MINUTE :
            case Types.SQL_INTERVAL_MINUTE_TO_SECOND :
            case Types.SQL_INTERVAL_SECOND :
                return IntervalType.getIntervalType(type, precision, scale);

            case Types.OTHER :
                return OTHER;

            default :
                throw Error.runtimeError(ErrorCode.U_S0500, "Type");
        }
    }

    public static Type getAggregateType(Type add,
                                        Type existing) throws HsqlException {

        if (existing == null || existing.typeCode == Types.SQL_ALL_TYPES) {
            return add;
        }

        if (add == null || add.typeCode == Types.SQL_ALL_TYPES) {
            return existing;
        }

        return existing.getAggregateType(add);
    }

    public static final IntValueHashMap typeAliases;
    public static final IntValueHashMap typeNames;

    static {
        typeNames = new IntValueHashMap(37);

        typeNames.put("CHARACTER", Types.SQL_CHAR);
        typeNames.put("VARCHAR", Types.SQL_VARCHAR);
        typeNames.put("VARCHAR_IGNORECASE", Types.VARCHAR_IGNORECASE);
        typeNames.put("DATE", Types.SQL_DATE);
        typeNames.put("TIME", Types.SQL_TIME);
        typeNames.put("TIMESTAMP", Types.SQL_TIMESTAMP);
        typeNames.put("INTERVAL", Types.SQL_INTERVAL);
        typeNames.put("TINYINT", Types.TINYINT);
        typeNames.put("SMALLINT", Types.SQL_SMALLINT);
        typeNames.put("INTEGER", Types.SQL_INTEGER);
        typeNames.put("BIGINT", Types.SQL_BIGINT);
        typeNames.put("REAL", Types.SQL_REAL);
        typeNames.put("FLOAT", Types.SQL_FLOAT);
        typeNames.put("DOUBLE", Types.SQL_DOUBLE);
        typeNames.put("NUMERIC", Types.SQL_NUMERIC);
        typeNames.put("DECIMAL", Types.SQL_DECIMAL);
        typeNames.put("BOOLEAN", Types.SQL_BOOLEAN);
        typeNames.put("BINARY", Types.SQL_BINARY);
        typeNames.put("VARBINARY", Types.SQL_VARBINARY);
        typeNames.put("CLOB", Types.SQL_CLOB);
        typeNames.put("BLOB", Types.SQL_BLOB);
        typeNames.put("BIT", Types.SQL_BIT);
        typeNames.put("OTHER", Types.OTHER);

        //
        typeAliases = new IntValueHashMap(67, 1);

        typeAliases.put("CHAR", Types.SQL_CHAR);
/*
        typeAliases.put("CHAR VARYING", Types.SQL_VARCHAR);
        typeAliases.put("CHARACTER VARYING", Types.SQL_VARCHAR);
        typeAliases.put("CHARACTER LARGE OBJECT", Types.SQL_CLOB);
*/
        typeAliases.put("INT", Types.SQL_INTEGER);
        typeAliases.put("DEC", Types.SQL_DECIMAL);
        typeAliases.put("LONGVARCHAR", Types.SQL_VARCHAR);
        typeAliases.put("DATETIME", Types.SQL_TIMESTAMP);
        typeAliases.put("LONGVARBINARY", Types.SQL_VARBINARY);
        typeAliases.put("OBJECT", Types.OTHER);
    }

    public static int getTypeNr(String name) {

        int i = typeNames.get(name, Integer.MIN_VALUE);

        if (i == Integer.MIN_VALUE) {
            i = typeAliases.get(name, Integer.MIN_VALUE);
        }

        return i;
    }

    public static boolean isSupportedSQLType(int typeNumber) {

        if (getDefaultType(typeNumber) == null) {
            return false;
        }

        return true;
    }

    public static boolean matches(Type[] one, Type[] other) {

        for (int i = 0; i < one.length; i++) {
            if (one[i].typeCode != other[i].typeCode) {
                return false;
            }
        }

        return true;
    }
}
