/* Copyrights and Licenses
 *
 * This product includes Hypersonic SQL.
 * Originally developed by Thomas Mueller and the Hypersonic SQL Group. 
 *
 * Copyright (c) 1995-2000 by the Hypersonic SQL Group. All rights reserved. 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 
 *     -  Redistributions of source code must retain the above copyright notice, this list of conditions
 *         and the following disclaimer. 
 *     -  Redistributions in binary form must reproduce the above copyright notice, this list of
 *         conditions and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution. 
 *     -  All advertising materials mentioning features or use of this software must display the
 *        following acknowledgment: "This product includes Hypersonic SQL." 
 *     -  Products derived from this software may not be called "Hypersonic SQL" nor may
 *        "Hypersonic SQL" appear in their names without prior written permission of the
 *         Hypersonic SQL Group. 
 *     -  Redistributions of any form whatsoever must retain the following acknowledgment: "This
 *          product includes Hypersonic SQL." 
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Hypersonic SQL Group or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage. 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Hypersonic SQL Group.
 *
 *
 * For work added by the HSQL Development Group:
 *
 * Copyright (c) 2001-2004, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer, including earlier
 * license statements (above) and comply with all above license conditions.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution, including earlier
 * license statements (above) and comply with all above license conditions.
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


package org.hsqldb;

import org.hsqldb.HsqlNameManager.HsqlName;
import org.hsqldb.lib.HsqlArrayList;
import org.hsqldb.lib.IntValueHashMap;
import org.hsqldb.store.ValuePool;

// fredt@users 20020215 - patch 1.7.0 by fredt - support GROUP BY with more than one column
// fredt@users 20020215 - patch 1.7.0 by fredt - SQL standard quoted identifiers
// fredt@users 20020218 - patch 1.7.0 by fredt - DEFAULT keyword
// fredt@users 20020221 - patch 513005 by sqlbob@users - SELECT INTO types
// fredt@users 20020425 - patch 548182 by skitt@users - DEFAULT enhancement
// thertz@users 20020320 - patch 473613 by thertz - outer join condition bug
// fredt@users 20021229 - patch 1.7.2 by fredt - new solution for above
// fredt@users 20020420 - patch 523880 by leptipre@users - VIEW support
// fredt@users 20020525 - patch 559914 by fredt@users - SELECT INTO logging
// tony_lai@users 20021020 - patch 1.7.2 - improved aggregates and HAVING
// aggregate functions can now be used in expressions - HAVING supported
// kloska@users 20021030 - patch 1.7.2 - ON UPDATE CASCADE
// fredt@users 20021112 - patch 1.7.2 by Nitin Chauhan - use of switch
// rewrite of the majority of multiple if(){}else{} chains with switch(){}
// boucherb@users 20030705 - patch 1.7.2 - prepared statement support
// fredt@users 20030819 - patch 1.7.2 - EXTRACT({YEAR | MONTH | DAY | HOUR | MINUTE | SECOND } FROM datetime)
// fredt@users 20030820 - patch 1.7.2 - CHAR_LENGTH | CHARACTER_LENGTH | OCTET_LENGTH(string)
// fredt@users 20030820 - patch 1.7.2 - POSITION(string IN string)
// fredt@users 20030820 - patch 1.7.2 - SUBSTRING(string FROM pos [FOR length])
// fredt@users 20030820 - patch 1.7.2 - TRIM({LEADING | TRAILING | BOTH} [<character>] FROM <string expression>)
// fredt@users 20030820 - patch 1.7.2 - CASE [expr] WHEN ... THEN ... [ELSE ...] END and its variants
// fredt@users 20030820 - patch 1.7.2 - NULLIF(expr,expr)
// fredt@users 20030820 - patch 1.7.2 - COALESCE(expr,expr,...)
// fredt@users 20031012 - patch 1.7.2 - improved scoping for column names in all areas

/**
 *  This class is responsible for parsing non-DDL statements.
 *
 * @version    1.7.2
 */

/** @todo fredt - implement numeric value functions (SQL92 6.6)
 *
 * EXTRACT({TIMEZONE_HOUR | TIMEZONE_MINUTE} FROM {datetime | interval})
 */
class Parser {

    private Database  database;
    private Tokenizer tokenizer;
    private Session   session;
    private String    sTable;
    private String    sToken;
    private Object    oData;
    private int       iType;
    private int       iToken;

    //
    private int           subQueryLevel;
    private HsqlArrayList subQueryList = new HsqlArrayList();

    /**
     *  Constructor declaration
     *
     * @param  db
     * @param  t
     * @param  session
     */
    Parser(Database db, Tokenizer t, Session session) {

        database     = db;
        tokenizer    = t;
        this.session = session;
    }

    void reset(String sql) {

        sTable = null;
        sToken = null;
        oData  = null;

        tokenizer.reset(sql);
        subQueryList.clear();

        subQueryLevel = 0;

        parameters.clear();
    }

    void checkTableWriteAccess(Table table,
                               int userRight) throws HsqlException {

        // session level user rights
        session.checkReadWrite();

        // object level user rights
        session.check(table.getName(), userRight);

        // object type
        if (table.isView()) {
            throw Trace.error(Trace.NOT_A_TABLE, table.getName().name);
        }

        // object readonly
        table.checkDataReadOnly();
    }

    static HsqlArrayList getColumnNames(Database db, Tokenizer t,
                                        boolean full) throws HsqlException {

        HsqlArrayList columns = new HsqlArrayList();

        while (true) {
            if (full) {
                String   token  = t.getIdentifier();
                boolean  quoted = t.wasQuotedIdentifier();
                HsqlName name   = db.nameManager.newHsqlName(token, quoted);

                columns.add(name);
            } else {
                columns.add(t.getString());
            }

            String token = t.getString();

            if (token.equals(Token.T_COMMA)) {
                continue;
            }

            if (token.equals(Token.T_CLOSEBRACKET)) {
                break;
            }

            throw Trace.error(Trace.UNEXPECTED_TOKEN, token);
        }

        return columns;
    }

    /**
     * The SubQuery objects are added to the end of subquery list
     */
    SubQuery parseSubquery(View v, boolean resolveAll,
                           int predicateType) throws HsqlException {

        SubQuery sq;

        if (v == null) {
            sq = new SubQuery();

            subQueryLevel++;

            Select s = parseSelect(false);

            sq.level = subQueryLevel;

            subQueryLevel--;

            boolean isResolved = s.resolveAll(resolveAll);

            sq.select     = s;
            sq.isResolved = isResolved;

            if (!isResolved) {
                return sq;
            }

            // it's not a problem that this table has not a unique name
            Table table = new Table(
                database,
                database.nameManager.newHsqlName("SYSTEM_SUBQUERY", false),
                Table.SYSTEM_SUBQUERY, 0);

            for (int i = 0; i < s.iResultLen; i++) {
                String colname = s.exprColumns[i].getAlias();

                if (colname == null || colname.length() == 0) {

                    // fredt - this does not guarantee the uniqueness of column
                    // names but addColumns() will throw if names are not unique.
                    colname = "COL_" + String.valueOf(i + 1);

                    s.exprColumns[i].setAlias(colname, false);
                }
            }

            table.addColumns(s);

            int[] pcol = predicateType == Expression.IN ? new int[]{ 0 }
                                                        : null;

            table.createPrimaryKey(pcol);

            sq.table = table;

            subQueryList.add(sq);
        } else {
            sq = v.viewSubQuery;

            for (int i = 0; i < v.viewSubqueries.length; i++) {
                subQueryList.add(v.viewSubqueries[i]);
            }
        }

        return sq;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    Select parseSelect(boolean isUnion) throws HsqlException {

        Select select = new Select();

        if (!isUnion) {
            parseLimit(select);
        }

        String token = tokenizer.getString();

        if (token.equals(Token.T_DISTINCT)) {
            select.isDistinctSelect = true;
        } else {
            tokenizer.back();
        }

        // parse column list
        HsqlArrayList vcolumn = new HsqlArrayList();

        do {
            Expression e = parseExpression();

            token = tokenizer.getString();

            if (token.equals(Token.T_AS)) {
                e.setAlias(tokenizer.getName(),
                           tokenizer.wasQuotedIdentifier());

                token = tokenizer.getString();
            } else if (tokenizer.wasName()) {
                e.setAlias(token, tokenizer.wasQuotedIdentifier());

                token = tokenizer.getString();
            }

            vcolumn.add(e);
        } while (token.equals(Token.T_COMMA));

        if (token.equals(Token.T_INTO)) {
            boolean getname = true;

            token = tokenizer.getString();

            switch (Token.get(token)) {

                case Token.CACHED :
                    select.intoType = Table.CACHED_TABLE;
                    break;

                case Token.TEMP :
                    select.intoType = Table.TEMP_TABLE;
                    break;

                case Token.TEXT :
                    select.intoType = Table.TEXT_TABLE;
                    break;

                case Token.MEMORY :
                    select.intoType = Table.MEMORY_TABLE;
                    break;

                default :
                    select.intoType = Table.MEMORY_TABLE;
                    getname         = false;
                    break;
            }

            if (getname) {
                token = tokenizer.getString();
            }

            select.sIntoTable = database.nameManager.newHsqlName(token,
                    tokenizer.wasQuotedIdentifier());
            token = tokenizer.getString();

            if ("?".equals(sToken)) {
                throw Trace.error(Trace.UNEXPECTED_TOKEN,
                                  Trace.PARAMETRIC_TABLE_NAME);
            }
        }

        if (!token.equals(Token.T_FROM)) {
            throw Trace.error(Trace.UNEXPECTED_TOKEN, token);
        }

        Expression condition = null;

        // parse table list
        HsqlArrayList vfilter = new HsqlArrayList();

        vfilter.add(parseTableFilter(false));

        while (true) {
            token = tokenizer.getString();

            if (token.equals(Token.T_INNER)) {
                token = tokenizer.getThis(Token.T_JOIN);
            }

            if (token.equals(Token.T_LEFT)) {
                token = tokenizer.getString();

                if (token.equals(Token.T_OUTER)) {
                    token = tokenizer.getString();
                }

                Trace.check(token.equals(Token.T_JOIN),
                            Trace.UNEXPECTED_TOKEN, token);

                TableFilter tf = parseTableFilter(true);

                vfilter.add(tf);
                tokenizer.getThis(Token.T_ON);

                Expression newcondition = parseExpression();

                newcondition.checkTables(vfilter);

                condition = addJoinCondition(condition, newcondition, tf,
                                             true);
            } else if (token.equals(Token.T_JOIN)) {
                vfilter.add(parseTableFilter(false));
                tokenizer.getThis(Token.T_ON);

                Expression newcondition = parseExpression();

                newcondition.checkTables(vfilter);

                condition = addJoinCondition(condition, newcondition, null,
                                             false);
            } else if (token.equals(Token.T_COMMA)) {
                vfilter.add(parseTableFilter(false));
            } else {
                tokenizer.back();

                break;
            }
        }

        resolveSelectTableFilter(select, vcolumn, vfilter);

        // where
        token = tokenizer.getString();

        if (token.equals(Token.T_WHERE)) {
            Expression newcondition = parseExpression();

            newcondition = resolveWhereColumnAliases(newcondition, vcolumn);
            condition    = addCondition(condition, newcondition);
            token        = tokenizer.getString();
        }

        select.queryCondition = condition;

        if (token.equals(Token.T_GROUP)) {
            tokenizer.getThis(Token.T_BY);

            int len = 0;

            do {
                Expression e = parseExpression();

                // tony_lai@users having support:
                // "group by" does not allow refering to other columns alias.
                //e = doOrderGroup(e, vcolumn);
                vcolumn.add(e);

                token = tokenizer.getString();

                len++;
            } while (token.equals(Token.T_COMMA));

            select.iGroupLen = len;
        }

        // tony_lai@users - having support
        // fredt - this one does not go through resolve, etc.
        if (token.equals(Token.T_HAVING)) {
            select.iHavingIndex    = vcolumn.size();
            select.havingCondition = parseExpression();
            token                  = tokenizer.getString();

            vcolumn.add(select.havingCondition);
        }

        switch (Token.get(token)) {

            case Token.UNION :
                token = tokenizer.getString();

                if (token.equals(Token.T_ALL)) {
                    select.iUnionType = Select.UNIONALL;
                } else if (token.equals(Token.T_DISTINCT)) {
                    select.iUnionType = Select.UNION;
                } else {
                    select.iUnionType = Select.UNION;

                    tokenizer.back();
                }

                tokenizer.getThis(Token.T_SELECT);

                select.sUnion = parseSelect(true);
                break;

            case Token.INTERSECT :
                token = tokenizer.getString();

                if (token.equals(Token.T_DISTINCT)) {}
                else {
                    tokenizer.back();
                }

                tokenizer.getThis(Token.T_SELECT);

                select.iUnionType = Select.INTERSECT;
                select.sUnion     = parseSelect(true);
                break;

            case Token.EXCEPT :
            case Token.MINUS :
                token = tokenizer.getString();

                if (token.equals(Token.T_DISTINCT)) {}
                else {
                    tokenizer.back();
                }

                tokenizer.getThis(Token.T_SELECT);

                select.iUnionType = Select.EXCEPT;
                select.sUnion     = parseSelect(true);
                break;

            default :
                tokenizer.back();
                break;
        }

        if (!isUnion) {
            token = tokenizer.getString();

            if (token.equals(Token.T_ORDER)) {
                tokenizer.getThis(Token.T_BY);
                parseOrderBy(select, vcolumn);
            } else {
                tokenizer.back();
            }
        }

        int len = vcolumn.size();

        select.exprColumns = new Expression[len];

        vcolumn.toArray(select.exprColumns);

        return select;
    }

// fredt@users 20011010 - patch 471710 by fredt - LIMIT rewritten
// SELECT LIMIT n m DISTINCT ... queries and error message
// "SELECT LIMIT n m ..." creates the result set for the SELECT statement then
// discards the first n rows and returns m rows of the remaining result set
// "SELECT LIMIT 0 m" is equivalent to "SELECT TOP m" or "SELECT FIRST m"
// in other RDBMS's
// "SELECT LIMIT n 0" discards the first n rows and returns the remaining rows
// fredt@users 20020225 - patch 456679 by hiep256 - TOP keyword
    private void parseLimit(Select select) throws HsqlException {

        String     token = tokenizer.getString();
        Expression e1;
        Expression e2;
        boolean    islimit = false;

        if (token.equals(Token.T_LIMIT)) {
            read();

            e1      = readTerm();
            e2      = readTerm();
            islimit = true;

            tokenizer.back();
        } else if (token.equals(Token.T_TOP)) {
            read();

            e1 = new Expression(Types.INTEGER, ValuePool.getInt(0));
            e2 = readTerm();

            tokenizer.back();
        } else {
            tokenizer.back();

            return;
        }

        if ((e1.getType() == Expression.VALUE && e1.getDataType() == Types
                .INTEGER && ((Integer) e1.getValue()).intValue() >= 0) || e1
                    .isParam()) {
            if ((e2.getType() == Expression.VALUE && e2.getDataType() == Types
                    .INTEGER && ((Integer) e1.getValue())
                    .intValue() >= 0) || e2.isParam()) {

                // necessary for params
                e1.setDataType(Types.INTEGER);
                e2.setDataType(Types.INTEGER);

                select.limitCondition = new Expression(Expression.LIMIT, e1,
                                                       e2);

                return;
            }
        }

        int messageid = islimit ? Trace.Parser_parseLimit1
                                : Trace.Parser_parseLimit2;

        throw Trace.error(Trace.WRONG_DATA_TYPE, messageid);
    }

    private void parseOrderBy(Select select,
                              HsqlArrayList vcolumn) throws HsqlException {

        String token;
        int    len = 0;

        do {
            Expression e = parseExpression();

            e = resolveOrderByColumnAlias(e, vcolumn, select.iResultLen,
                                          select.sUnion != null);
            token = tokenizer.getString();

            if (token.equals(Token.T_DESC)) {
                e.setDescending();

                token = tokenizer.getString();
            } else if (token.equals(Token.T_ASC)) {
                token = tokenizer.getString();
            }

            vcolumn.add(e);

            len++;
        } while (token.equals(Token.T_COMMA));

        tokenizer.back();

        select.iOrderLen = len;
    }

    private static void resolveSelectTableFilter(Select select,
            HsqlArrayList vcolumn,
            HsqlArrayList vfilter) throws HsqlException {

        int         len       = vfilter.size();
        TableFilter filters[] = new TableFilter[len];

        vfilter.toArray(filters);

        select.tFilter = filters;

        // expand [table.]* columns
        len = vcolumn.size();

        for (int i = 0; i < len; i++) {
            Expression e = (Expression) (vcolumn.get(i));

            if (e.getType() == Expression.ASTERIX) {
                int    current = i;
                Table  table   = null;
                String n       = e.getTableName();

                for (int t = 0; t < filters.length; t++) {
                    TableFilter f = filters[t];

//                    e.resolve(f);
                    e.resolveTables(f);

                    if (n != null &&!n.equals(f.getName())) {
                        continue;
                    }

                    table = f.getTable();

                    int col = table.getColumnCount();

                    for (int c = 0; c < col; c++) {
                        Expression ins = new Expression(f.getName(),
                                                        table.getColumn(c));

                        vcolumn.add(current++, ins);

                        // now there is one element more to parse
                        len++;
                    }
                }

                Trace.check(table != null, Trace.TABLE_NOT_FOUND, n);

                // minus the asterix element
                len--;

                vcolumn.remove(current);
            } else if (e.getType() == Expression.COLUMN) {
                if (e.getFilter() == null) {
                    for (int f = 0; f < filters.length; f++) {
                        e.resolveTables(filters[f]);
                    }
                }
            }
        }

        for (int i = 0; i < len; i++) {
            Expression e = (Expression) (vcolumn.get(i));

            e.resolveTypes();
        }

        select.iResultLen = len;
    }

    /**
     * Checks an Expression in the WHERE clause and if it contains an alias or
     * column index returns the column expression it refers to.
     *
     * todo
     *
     * @param  e                          Description of the Parameter
     * @param  vcolumn                    Description of the Parameter
     * @return                            Description of the Return Value
     * @exception  java.sql.HsqlException  Description of the Exception
     */
    private static Expression resolveWhereColumnAliases(Expression e,
            HsqlArrayList vcolumn) throws HsqlException {
        return e;
    }

    /**
     * Checks an ORDER BY Expression and if it is an alias or column index
     * returns the column expression it refers to.<b>
     *
     * Ambiguous reference to an alias and non-integer column index throw an
     * exception.
     *
     * If select is a SET QUERY then only column indexes are allowed and the
     * rest throw an exception.
     *
     * @param  e                          search column expression
     * @param  vcolumn                    list of columns
     * @param  union                      is select a union
     * @return                            new or the same expression
     * @exception  java.sql.HsqlException  when invalid search specification
     */
    private static Expression resolveOrderByColumnAlias(Expression e,
            HsqlArrayList vcolumn, int visiblecols,
            boolean union) throws HsqlException {

        if (e.getType() == Expression.VALUE) {

            // order by 1,2,3
            if (e.getDataType() == Types.INTEGER) {
                int i = ((Integer) e.getValue()).intValue();

                if (i <= vcolumn.size()) {
                    Expression colexpr = (Expression) vcolumn.get(i - 1);

                    colexpr.orderColumnIndex = i - 1;

                    return colexpr;
                }
            }

            throw Trace.error(Trace.INVALID_ORDER_BY);
        } else if (e.getType() == Expression.COLUMN
                   && e.getTableName() == null) {

            // this could be an alias column
            String     s     = e.getColumnName();
            Expression found = e;

            for (int i = 0, size = vcolumn.size(); i < size; i++) {
                Expression colexpr  = (Expression) vcolumn.get(i);
                String     colalias = colexpr.getDefinedAlias();

                if (s.equals(colalias)
                        || (colalias == null
                            && s.equals(colexpr.getColumnName()))) {

                    // check for ambiguity if two displayed cols have the same name
                    // do not check beyond as a column may be repeated for grouping purposes
                    if (found != e && i < visiblecols) {
                        throw Trace.error(Trace.AMBIGUOUS_COLUMN_REFERENCE,
                                          s);
                    }

                    found = colexpr;

                    // set this for use in sorting
                    found.orderColumnIndex = i;
                }
            }

            e = found;
        } else if (union) {
            throw Trace.error(Trace.INVALID_ORDER_BY);
        }

        return e;
    }

    /**
     *  Method declaration
     *
     * @param  outerjoin
     * @return
     * @throws  HsqlException
     */
    private TableFilter parseTableFilter(boolean outerjoin)
    throws HsqlException {

        String      token = tokenizer.getString();
        Table       t     = null;
        SubQuery    sq;
        TableFilter tf;
        String      sAlias = null;

        if (token.equals(Token.T_OPENBRACKET)) {
            tokenizer.getThis(Token.T_SELECT);

            // fredt - not correlated - a joined subquery table must resolve fully
            sq = parseSubquery(null, true, Expression.QUERY);

            tokenizer.getThis(Token.T_CLOSEBRACKET);

            t = sq.table;
        } else {
            t = database.getTable(token, session);

            session.check(t.getName(), UserManager.SELECT);

            if (t.isView()) {
                sq        = parseSubquery((View) t, true, Expression.QUERY);
                sq.select = ((View) t).viewSelect;
                t         = sq.table;
                sAlias    = token;
            }
        }

        token = tokenizer.getString();

        // fredt - we removed LEFT from the list of reserved words in Tokenizer
        // to allow LEFT() to work. Thus wasName() will return true for LEFT
        // and we check separately for this token
        if (token.equals(Token.T_LEFT)) {
            tokenizer.back();
        } else if (token.equals(Token.T_AS)) {
            sAlias = tokenizer.getName();
        } else if (tokenizer.wasName()) {
            sAlias = token;
        } else {
            tokenizer.back();
        }

        tf = new TableFilter(t, sAlias, outerjoin);

        return tf;
    }

    /**
     *  Add a condition from the WHERE clause.
     *
     * @param  e1
     * @param  e2
     * @return
     */
    private static Expression addCondition(Expression e1, Expression e2) {

        if (e1 == null) {
            return e2;
        } else if (e2 == null) {
            return e1;
        } else {
            return new Expression(Expression.AND, e1, e2);
        }
    }

    /**
     *  Add a condition from the JOIN table ON clause.
     *
     * @param  e1
     * @param  e2
     * @param tf
     * @param outer
     * @return
     */
    private static Expression addJoinCondition(Expression e1, Expression e2,
            TableFilter tf, boolean outer) throws HsqlException {

        if (!e2.setForJoin(tf, outer)) {
            throw Trace.error(Trace.OUTER_JOIN_CONDITION);
        }

        return addCondition(e1, e2);
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    Expression parseExpression() throws HsqlException {

        read();

        Expression r = readOr();

        tokenizer.back();

        return r;
    }

    private Expression readAggregate() throws HsqlException {

        boolean distinct = false;
        int     type     = iToken;

        read();

        if (tokenizer.getString().equals(Token.T_DISTINCT)) {
            distinct = true;
        } else {
            tokenizer.back();
        }

        readThis(Expression.OPEN);

        Expression s = readOr();

        readThis(Expression.CLOSE);

        Expression aggregateExp = new Expression(type, s, null);

        aggregateExp.setDistinctAggregate(distinct);

        return aggregateExp;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readOr() throws HsqlException {

        Expression r = readAnd();

        while (iToken == Expression.OR) {
            int        type = iToken;
            Expression a    = r;

            read();

            r = new Expression(type, a, readAnd());
        }

        return r;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readAnd() throws HsqlException {

        Expression r = readCondition();

        while (iToken == Expression.AND) {
            int        type = iToken;
            Expression a    = r;

            read();

            r = new Expression(type, a, readCondition());
        }

        return r;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readCondition() throws HsqlException {

        switch (iToken) {

            case Expression.NOT : {
                int type = iToken;

                read();

                return new Expression(type, readCondition(), null);
            }
            case Expression.EXISTS : {
                int type = iToken;

                read();
                readThis(Expression.OPEN);
                Trace.check(iToken == Expression.SELECT,
                            Trace.UNEXPECTED_TOKEN);

                SubQuery sq = parseSubquery(null, false, Expression.EXISTS);
                Select   select = sq.select;
                Expression s = new Expression(select, sq.table,
                                              !sq.isResolved);

                read();
                readThis(Expression.CLOSE);

                return new Expression(type, s, null);
            }
            default : {
                Expression a   = readConcat();
                boolean    not = false;

                if (iToken == Expression.NOT) {
                    not = true;

                    read();
                }

                switch (iToken) {

                    case Expression.LIKE : {
                        a = parseLikePredicate(a);

                        break;
                    }
                    case Expression.BETWEEN : {
                        a = parseBetweenPredicate(a);

                        break;
                    }
                    case Expression.IN : {
                        a = this.parseInPredicate(a);

                        break;
                    }
                    default : {
                        Trace.check(!not, Trace.UNEXPECTED_TOKEN);

                        if (Expression.isCompare(iToken)) {
                            int type = iToken;

                            read();

                            return new Expression(type, a, readConcat());
                        }

                        return a;
                    }
                }

                if (not) {
                    a = new Expression(Expression.NOT, a, null);
                }

                return a;
            }
        }
    }

    private Expression parseLikePredicate(Expression a) throws HsqlException {

        read();

        Expression b = readTerm();

        // boucherb@users 2003-09-25 - patch 1.7.2 Alpha P
        // correct default like escape characters (i.e. the one
        // we report from jdbcDatabaseMetaData)
        // fredt@users - both 0 and '\\' are wrong - there shouldn't be any escape character if none is specified in the SQL
        Character escape = null;

        if (sToken.equals(Token.T_ESCAPE)) {
            read();

            Expression c = readTerm();

            Trace.check(c.getType() == Expression.VALUE,
                        Trace.INVALID_ESCAPE);

            String s = (String) c.getValue(Types.VARCHAR);

            // boucherb@users 2003-09-25
            // CHECKME:
            // Assert s.length() == 1 for xxxchar comparisons?
            // TODO:
            // SQL200n says binary escape can be 1 or more octets.
            // Maybe we need to retain s and check this in
            // Expression.resolve()?
            if (s == null || s.length() < 1) {
                throw Trace.error(Trace.INVALID_ESCAPE, s);
            }

            escape = new Character(s.charAt(0));
        }

        a = new Expression(a, b, escape);

        return a;
    }

    private Expression parseBetweenPredicate(Expression a)
    throws HsqlException {

        read();

        Expression l = new Expression(Expression.BIGGER_EQUAL, a,
                                      readConcat());

        readThis(Expression.AND);

        Expression h = new Expression(Expression.SMALLER_EQUAL, a,
                                      readConcat());

        if (l.getArg().isParam() && l.getArg2().isParam()) {
            throw Trace.error(Trace.Parser_ambiguous_between1);
        }

        if (h.getArg().isParam() && h.getArg2().isParam()) {
            throw Trace.error(Trace.Parser_ambiguous_between2);
        }

        return new Expression(Expression.AND, l, h);
    }

    private Expression parseInPredicate(Expression a) throws HsqlException {

        int type = iToken;

        read();
        readThis(Expression.OPEN);

        Expression b = null;

        if (iToken == Expression.SELECT) {
            SubQuery sq     = parseSubquery(null, false, Expression.IN);
            Select   select = sq.select;

            // until we support rows in IN predicates
            Trace.check(select.iResultLen == 1, Trace.SINGLE_COLUMN_EXPECTED);

            b = new Expression(select, sq.table, !sq.isResolved);

            read();
        } else {
            tokenizer.back();

            HsqlArrayList v = new HsqlArrayList();

            while (true) {
                Expression value = parseExpression();

                if (value.exprType == Expression.VALUE
                        && value.valueData == null &&!value.isParam()) {
                    throw Trace.error(Trace.NULL_IN_VALUE_LIST);
                }

                v.add(value);
                read();

                if (iToken != Expression.COMMA) {
                    break;
                }
            }

            Expression[] valueList;

            valueList = (Expression[]) v.toArray(new Expression[v.size()]);
            b         = new Expression(valueList);
        }

        readThis(Expression.CLOSE);

        return new Expression(type, a, b);
    }

    /**
     *  Method declaration
     *
     * @param  type
     * @throws  HsqlException
     */
    private void readThis(int type) throws HsqlException {
        Trace.check(iToken == type, Trace.UNEXPECTED_TOKEN);
        read();
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readConcat() throws HsqlException {

        Expression r = readSum();

        while (iToken == Expression.STRINGCONCAT) {
            int        type = Expression.CONCAT;
            Expression a    = r;

            read();

            r = new Expression(type, a, readSum());
        }

        return r;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readSum() throws HsqlException {

        Expression r = readFactor();

        while (true) {
            int type;

            if (iToken == Expression.PLUS) {
                type = Expression.ADD;
            } else if (iToken == Expression.NEGATE) {
                type = Expression.SUBTRACT;
            } else {
                break;
            }

            Expression a = r;

            read();

            r = new Expression(type, a, readFactor());
        }

        return r;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readFactor() throws HsqlException {

        Expression r = readTerm();

        while (iToken == Expression.MULTIPLY || iToken == Expression.DIVIDE) {
            int        type = iToken;
            Expression a    = r;

            read();

            r = new Expression(type, a, readTerm());
        }

        return r;
    }

    /**
     *  Method declaration
     *
     * @return
     * @throws  HsqlException
     */
    private Expression readTerm() throws HsqlException {

        Expression r = null;

        switch (iToken) {

            case Expression.COLUMN : {
                String name = sToken;

                r = new Expression(sTable, sToken);

                read();

                if (iToken == Expression.OPEN) {
                    String   javaName = database.getAlias(name);
                    Function f        = new Function(name, javaName, session);

                    session.check(javaName, UserManager.ALL);

                    int len = f.getArgCount();
                    int i   = 0;

                    read();

                    if (iToken != Expression.CLOSE) {
                        while (true) {
                            f.setArgument(i++, readOr());

                            if (iToken != Expression.COMMA) {
                                break;
                            }

                            read();
                        }
                    }

                    readThis(Expression.CLOSE);

                    // TODO: Maybe allow AS <alias> here
                    r = new Expression(f);
                }

                break;
            }
            case Expression.NEGATE : {
                int type = iToken;

                read();

                r = new Expression(type, readTerm(), null);

                Trace.check(!r.getArg().isParam(),
                            Trace.Expression_resolveTypes1);

                break;
            }
            case Expression.PLUS : {
                read();

                r = readTerm();

                Trace.check(!r.isParam(), Trace.Expression_resolveTypes1);

                break;
            }
            case Expression.OPEN : {
                read();

                r = readOr();

                if (iToken != Expression.CLOSE) {
                    throw Trace.error(Trace.UNEXPECTED_TOKEN, sToken);
                }

                read();

                break;
            }
            case Expression.VALUE : {
                r = new Expression(iType, oData);

                read();

                break;
            }
            case Expression.PARAM : {
                r = new Expression(Types.NULL, null, true);

                parameters.add(r);
                read();

                break;
            }
            case Expression.SELECT : {
                Select select = parseSelect(false);

                select.resolve();

                r = new Expression(select, null, true);

                read();

                break;
            }
            case Expression.MULTIPLY : {
                r = new Expression(sTable, (String) null);

                read();

                break;
            }
            case Expression.CONCAT : {
                int type = iToken;

                read();
                readThis(Expression.OPEN);

                r = readOr();

                readThis(Expression.COMMA);

                r = new Expression(type, r, readOr());

                readThis(Expression.CLOSE);

                break;
            }
            case Expression.CASEWHEN : {
                int type = iToken;

                read();
                readThis(Expression.OPEN);

                r = readOr();

                readThis(Expression.COMMA);

                Expression thenelse = readOr();

                readThis(Expression.COMMA);

                // thenelse part is never evaluated; only init
                thenelse = new Expression(Expression.ALTERNATIVE, thenelse,
                                          readOr());
                r = new Expression(type, r, thenelse);

                readThis(Expression.CLOSE);

                break;
            }
            case Expression.CASE : {
                int        type      = Expression.CASEWHEN;
                Expression predicand = null;

                read();

                if (iToken != Expression.WHEN) {
                    predicand = readOr();
                }

                Expression leaf = null;

                while (true) {
                    Expression casewhen = parseCaseWhen(predicand);

                    if (r == null) {
                        r = casewhen;
                    } else {
                        leaf.setRightExpression(casewhen);
                    }

                    leaf = casewhen.getRightExpression();

                    if (iToken != Expression.WHEN) {
                        break;
                    }
                }

                if (iToken == Expression.ELSE) {
                    readThis(Expression.ELSE);

                    Expression elsexpr = readOr();

                    leaf.setRightExpression(elsexpr);
                }

                readThis(Expression.ENDWHEN);

                break;
            }
            case Expression.NULLIF : {

                // turn into a CASEWHEN
                read();
                readThis(Expression.OPEN);

                r = readOr();

                readThis(Expression.COMMA);

                Expression thenelse =
                    new Expression(Expression.ALTERNATIVE,
                                   new Expression(Types.NULL, null), r);

                r = new Expression(Expression.EQUAL, r, readOr());
                r = new Expression(Expression.CASEWHEN, r, thenelse);

                readThis(Expression.CLOSE);

                break;
            }
            case Expression.COALESCE :
            case Expression.IFNULL : {

                // turn into a CASEWHEN
                read();
                readThis(Expression.OPEN);

                Expression leaf = null;

                while (true) {
                    Expression current = readOr();
                    Expression condition =
                        new Expression(Expression.EQUAL, current,
                                       new Expression(Types.NULL, null));
                    Expression alternatives =
                        new Expression(Expression.ALTERNATIVE,
                                       new Expression(Types.NULL, null),
                                       current);
                    Expression casewhen = new Expression(Expression.CASEWHEN,
                                                         condition,
                                                         alternatives);

                    if (r == null) {
                        r = casewhen;
                    } else {
                        leaf.setLeftExpression(casewhen);
                    }

                    leaf = alternatives;

                    if (iToken == Expression.CLOSE) {
                        readThis(Expression.CLOSE);

                        break;
                    }

                    readThis(Expression.COMMA);
                }

                break;
            }
            case Expression.SEQUENCE : {
                tokenizer.getThis(Token.T_VALUE);
                tokenizer.getThis(Token.T_FOR);

                String name = tokenizer.getIdentifier();

                tokenizer.getString();

                NumberSequence sequence =
                    (NumberSequence) database.sequenceManager.getSequence(
                        name);

                Trace.check(sequence != null, Trace.SEQUENCE_NOT_FOUND);

                r = new Expression(sequence);

                break;
            }
            case Expression.CONVERT : {
                int type = iToken;

                read();
                readThis(Expression.OPEN);

                r = readOr();

                readThis(Expression.COMMA);

                int t = Types.getTypeNr(sToken);

                // For now, parse but ignore precision and scale
                // TODO: definitely validate values (e.g. check non-neg) and
                //       maybe even enforce in Expression.getValue(), incl.
                //       trim, pad, throw on overflow, etc.
                int p = 0;
                int s = 0;

                if (Types.acceptsPrecisionCreateParam(t)
                        && tokenizer.isGetThis(Token.T_OPENBRACKET)) {
                    p = tokenizer.getInt();

                    if (Types.acceptsScaleCreateParam(t)
                            && tokenizer.isGetThis(Token.T_COMMA)) {
                        s = tokenizer.getInt();
                    }

                    tokenizer.getThis(Token.T_CLOSEBRACKET);
                }

                if (r.isParam()) {
                    r.setDataType(t);
                }

                r = new Expression(type, r, null);

                r.setDataType(t);
                read();
                readThis(Expression.CLOSE);

                break;
            }
            case Expression.CAST : {
                read();
                readThis(Expression.OPEN);

                r = readOr();

                readThis(Expression.AS);

                int t = Types.getTypeNr(sToken);

                // For now, parse but ignore precision and scale
                // TODO: definitely validate values (e.g. check non-neg) and
                //       maybe even enforce in Expression.getValue(), incl.
                //       trim, pad, throw on overflow, etc.
                int p = 0;
                int s = 0;

                if (Types.acceptsPrecisionCreateParam(t)
                        && tokenizer.isGetThis(Token.T_OPENBRACKET)) {
                    p = tokenizer.getInt();

                    if (Types.acceptsScaleCreateParam(t)
                            && tokenizer.isGetThis(Token.T_COMMA)) {
                        s = tokenizer.getInt();
                    }

                    tokenizer.getThis(Token.T_CLOSEBRACKET);
                }

                if (r.isParam()) {
                    r.setDataType(t);
                }

                r = new Expression(Expression.CONVERT, r, null);

                r.setDataType(t);
                read();
                readThis(Expression.CLOSE);

                break;
            }
            case Expression.EXTRACT : {
                read();
                readThis(Expression.OPEN);

                String name = sToken;

                // must be an accepted identifier
                if (!Expression.SQL_EXTRACT_FIELD_NAMES.contains(name)) {
                    throw Trace.error(Trace.UNEXPECTED_TOKEN, sToken);
                }

                readToken();
                readThis(Expression.FROM);

                // the name argument is DAY, MONTH etc.  - OK for now for CHECK constraints
                Function f = new Function(name, database.getAlias(name),
                                          session);

                f.setArgument(0, readOr());
                readThis(Expression.CLOSE);

                r = new Expression(f);

                break;
            }
            case Expression.TRIM : {
                read();
                readThis(Expression.OPEN);

                String type = sToken;

                if (Expression.SQL_TRIM_SPECIFICATION.contains(type)) {
                    read();
                } else {
                    type = Token.T_BOTH;
                }

                String trimstr;

                if (sToken.length() == 1) {
                    trimstr = sToken;

                    read();
                } else {
                    trimstr = " ";
                }

                readThis(Expression.FROM);

                Expression trim = new Expression(Types.CHAR, trimstr);
                Expression leading;
                Expression trailing;

                if (type.equals(Token.T_LEADING)) {
                    leading  = new Expression(true);
                    trailing = new Expression(false);
                } else if (type.equals(Token.T_TRAILING)) {
                    leading  = new Expression(false);
                    trailing = new Expression(true);
                } else {
                    leading = trailing = new Expression(true);
                }

                // name argument is OK for now for CHECK constraints
                Function f = new Function(Token.T_TRIM,
                                          "org.hsqldb.Library.trim", session);

                f.setArgument(0, readOr());
                f.setArgument(1, trim);
                f.setArgument(2, leading);
                f.setArgument(3, trailing);
                readThis(Expression.CLOSE);

                r = new Expression(f);

                break;
            }
            case Expression.POSITION : {
                read();
                readThis(Expression.OPEN);

                Function f = new Function(Token.T_POSITION,
                                          "org.hsqldb.Library.position",
                                          session);

                f.setArgument(0, readTerm());
                readThis(Expression.IN);
                f.setArgument(1, readOr());
                readThis(Expression.CLOSE);

                r = new Expression(f);

                break;
            }
            case Expression.SUBSTRING : {
                boolean commas = false;

                read();
                readThis(Expression.OPEN);

                // OK for now for CHECK search conditions
                Function f = new Function(Token.T_SUBSTRING,
                                          "org.hsqldb.Library.substring",
                                          session);

                f.setArgument(0, readTerm());

                if (iToken == Expression.FROM) {
                    readThis(Expression.FROM);
                } else {
                    readThis(Expression.COMMA);

                    commas = true;
                }

                f.setArgument(1, readOr());

                Expression count = null;

                if (!commas && iToken == Expression.FOR) {
                    readThis(Expression.FOR);

                    count = readTerm();
                } else if (commas && iToken == Expression.COMMA) {
                    readThis(Expression.COMMA);

                    count = readTerm();
                }

                f.setArgument(2, count);
                readThis(Expression.CLOSE);

                r = new Expression(f);

                break;
            }
            default : {
                if (Expression.isAggregate(iToken)) {
                    r = readAggregate();
                } else {
                    throw Trace.error(Trace.UNEXPECTED_TOKEN, sToken);
                }

                break;
            }
        }

        return r;
    }

    Expression parseCaseWhen(Expression r) throws HsqlException {

        readThis(Expression.WHEN);

        Expression condition;

        if (r == null) {
            condition = readOr();
        } else {
            condition = new Expression(Expression.EQUAL, r, readOr());
        }

        readThis(Expression.THEN);

        Expression current = readOr();
        Expression alternatives = new Expression(Expression.ALTERNATIVE,
            current, new Expression(Types.NULL, null));
        Expression casewhen = new Expression(Expression.CASEWHEN, condition,
                                             alternatives);

        return casewhen;
    }

    /**
     *  Method declaration
     *
     * @throws  HsqlException
     */

// fredt@users 20020130 - patch 497872 by Nitin Chauhan
// reordering for speed
    private void read() throws HsqlException {

        sToken = tokenizer.getString();

        if (tokenizer.wasValue()) {
            iToken = Expression.VALUE;
            oData  = tokenizer.getAsValue();
            iType  = tokenizer.getType();
        } else if (tokenizer.wasName()) {
            iToken = Expression.COLUMN;
            sTable = null;
        } else if (tokenizer.wasLongName()) {
            sTable = tokenizer.getLongNameFirst();

            if (sToken.equals(Token.T_ASTERISK)) {
                iToken = Expression.MULTIPLY;
            } else {
                iToken = Expression.COLUMN;
            }
        } else if (sToken.length() == 0) {
            iToken = Expression.END;
        } else {
            iToken = tokenSet.get(sToken, -1);

            if (iToken == -1) {
                iToken = Expression.END;
            }

            switch (iToken) {

                case Expression.COMMA :
                case Expression.EQUAL :
                case Expression.NOT_EQUAL :
                case Expression.SMALLER :
                case Expression.BIGGER :
                case Expression.SMALLER_EQUAL :
                case Expression.BIGGER_EQUAL :
                case Expression.AND :
                case Expression.OR :
                case Expression.NOT :
                case Expression.IN :
                case Expression.EXISTS :
                case Expression.BETWEEN :
                case Expression.PLUS :
                case Expression.NEGATE :
                case Expression.DIVIDE :
                case Expression.STRINGCONCAT :
                case Expression.OPEN :
                case Expression.CLOSE :
                case Expression.SELECT :
                case Expression.LIKE :
                case Expression.COUNT :
                case Expression.SUM :
                case Expression.MIN :
                case Expression.MAX :
                case Expression.AVG :
                case Expression.CONVERT :
                case Expression.CAST :
                case Expression.SEQUENCE :
                case Expression.IFNULL :
                case Expression.COALESCE :
                case Expression.NULLIF :
                case Expression.CASE :
                case Expression.WHEN :
                case Expression.THEN :
                case Expression.ELSE :
                case Expression.ENDWHEN :
                case Expression.CASEWHEN :
                case Expression.CONCAT :
                case Expression.EXTRACT :
                case Expression.POSITION :
                case Expression.SUBSTRING :
                case Expression.FROM :
                case Expression.FOR :
                case Expression.END :
                case Expression.PARAM :
                case Expression.TRIM :
                case Expression.LEADING :
                case Expression.TRAILING :
                case Expression.BOTH :
                case Expression.AS :
                    break;            // nothing else required, iToken initialized properly

                case Expression.MULTIPLY :
                    sTable = null;    // in case of ASTERIX
                    break;

                case Expression.IS :
                    sToken = tokenizer.getString();

                    if (sToken.equals(Token.T_NOT)) {
                        iToken = Expression.NOT_EQUAL;
                    } else {
                        iToken = Expression.EQUAL;

                        tokenizer.back();
                    }
                    break;

                default :
                    iToken = Expression.END;
            }
        }
    }

    /**
     * a workaround to read MONTH, DAY, YEAR etc. with EXTRACT while not making them SQL KEYWORDS in Tokenizer
     */
    private void readToken() throws HsqlException {
        sToken = tokenizer.getString();
        iToken = tokenSet.get(sToken, -1);
    }

    private static IntValueHashMap tokenSet = new IntValueHashMap(37);

    static {
        tokenSet.put(Token.T_COMMA, Expression.COMMA);
        tokenSet.put(Token.T_EQUALS, Expression.EQUAL);
        tokenSet.put("!=", Expression.NOT_EQUAL);
        tokenSet.put("<>", Expression.NOT_EQUAL);
        tokenSet.put("<", Expression.SMALLER);
        tokenSet.put(">", Expression.BIGGER);
        tokenSet.put("<=", Expression.SMALLER_EQUAL);
        tokenSet.put(">=", Expression.BIGGER_EQUAL);
        tokenSet.put(Token.T_AND, Expression.AND);
        tokenSet.put(Token.T_NOT, Expression.NOT);
        tokenSet.put(Token.T_OR, Expression.OR);
        tokenSet.put(Token.T_IN, Expression.IN);
        tokenSet.put(Token.T_EXISTS, Expression.EXISTS);
        tokenSet.put(Token.T_BETWEEN, Expression.BETWEEN);
        tokenSet.put("+", Expression.PLUS);
        tokenSet.put("-", Expression.NEGATE);
        tokenSet.put("*", Expression.MULTIPLY);
        tokenSet.put("/", Expression.DIVIDE);
        tokenSet.put("||", Expression.STRINGCONCAT);
        tokenSet.put(Token.T_OPENBRACKET, Expression.OPEN);
        tokenSet.put(Token.T_CLOSEBRACKET, Expression.CLOSE);
        tokenSet.put(Token.T_SELECT, Expression.SELECT);
        tokenSet.put(Token.T_LIKE, Expression.LIKE);
        tokenSet.put(Token.T_COUNT, Expression.COUNT);
        tokenSet.put(Token.T_SUM, Expression.SUM);
        tokenSet.put(Token.T_MIN, Expression.MIN);
        tokenSet.put(Token.T_MAX, Expression.MAX);
        tokenSet.put(Token.T_AVG, Expression.AVG);
        tokenSet.put(Token.T_IFNULL, Expression.IFNULL);
        tokenSet.put(Token.T_NULLIF, Expression.NULLIF);
        tokenSet.put(Token.T_CONVERT, Expression.CONVERT);
        tokenSet.put(Token.T_CAST, Expression.CAST);
        tokenSet.put(Token.T_NEXT, Expression.SEQUENCE);
        tokenSet.put(Token.T_CASE, Expression.CASE);
        tokenSet.put(Token.T_WHEN, Expression.WHEN);
        tokenSet.put(Token.T_THEN, Expression.THEN);
        tokenSet.put(Token.T_ELSE, Expression.ELSE);
        tokenSet.put(Token.T_END, Expression.ENDWHEN);
        tokenSet.put(Token.T_CASEWHEN, Expression.CASEWHEN);
        tokenSet.put(Token.T_CONCAT, Expression.CONCAT);
        tokenSet.put(Token.T_COALESCE, Expression.COALESCE);
        tokenSet.put(Token.T_EXTRACT, Expression.EXTRACT);
        tokenSet.put(Token.T_POSITION, Expression.POSITION);
        tokenSet.put(Token.T_FROM, Expression.FROM);
        tokenSet.put(Token.T_TRIM, Expression.TRIM);
        tokenSet.put(Token.T_SUBSTRING, Expression.SUBSTRING);
        tokenSet.put(Token.T_FOR, Expression.FOR);
        tokenSet.put(Token.T_AS, Expression.AS);
        tokenSet.put(Token.T_IS, Expression.IS);
        tokenSet.put("?", Expression.PARAM);
    }

// boucherb@users 20030411 - patch 1.7.2 - for prepared statements
// ---------------------------------------------------------------
    HsqlArrayList                     parameters   = new HsqlArrayList();
    private static final Expression[] noParameters = new Expression[0];
    private static final SubQuery[]   noSubqueries = new SubQuery[0];

    /**
     *  Destructive get method
     */
    Expression[] getParameters() {

        Expression[] result = parameters.size() == 0 ? noParameters
                                                     : (Expression[]) parameters.toArray(
                                                         new Expression[parameters.size()]);

        parameters.clear();

        return result;
    }

    void clearParameters() {
        parameters.clear();
    }

    // fredt - new implementation of subquery list

    /**
     * Sets the subqueries as belonging to the View being constructed
     */
    void setAsView(View view) {

        for (int i = 0; i < subQueryList.size(); i++) {
            SubQuery sq = (SubQuery) subQueryList.get(i);

            if (sq.view == null) {
                sq.view = view;
            }
        }
    }

    /**
     * Return the list of subqueries as an array sorted according to the order
     * of materialization, then clear the internal subquery list
     */
    SubQuery[] getSortedSubqueries() {

        if (subQueryList.size() == 0) {
            return noSubqueries;
        }

        subQueryList.sort((SubQuery) subQueryList.get(0));

        SubQuery[] subqueries = new SubQuery[subQueryList.size()];

        subQueryList.toArray(subqueries);
        subQueryList.clear();

        return subqueries;
    }

    /**
     * Retrieves a CALL-type CompiledStatement from this parse context.
     */
    CompiledStatement compileCallStatement() throws HsqlException {

        clearParameters();

        Expression expression = parseExpression();
        CompiledStatement cs = new CompiledStatement(expression,
            getParameters());

        cs.subqueries = getSortedSubqueries();

        return cs;
    }

    /**
     * Retrieves a DELETE-type CompiledStatement from this parse context.
     */
    CompiledStatement compileDeleteStatement() throws HsqlException {

        String     token;
        Table      table;
        Expression condition;

        clearParameters();
        tokenizer.getThis(Token.T_FROM);

        token = tokenizer.getString();
        table = database.getTable(token, session);

        checkTableWriteAccess(table, UserManager.DELETE);

        token     = tokenizer.getString();
        condition = null;

        if (token.equals(Token.T_WHERE)) {
            condition = parseExpression();
        } else {
            tokenizer.back();
        }

        CompiledStatement cs = new CompiledStatement(table, condition,
            getParameters());

        cs.subqueries = getSortedSubqueries();

        return cs;
    }

    private void getInsertColumnValueExpressions(Table t, Expression[] acve,
            int len) throws HsqlException {

        boolean    enclosed;
        String     token;
        Expression cve;
        int        i;

        enclosed = false;
        i        = 0;

        tokenizer.getThis(Token.T_OPENBRACKET);

        for (; i < len; i++) {
            cve = parseExpression();

//            cve.resolve(null);
            cve.resolveTables(null);
            cve.resolveTypes();

            acve[i] = cve;
            token   = tokenizer.getString();

            if (token.equals(Token.T_COMMA)) {
                continue;
            }

            if (token.equals(Token.T_CLOSEBRACKET)) {
                enclosed = true;

                break;
            }

            throw Trace.error(Trace.UNEXPECTED_TOKEN, token);
        }

        if (!enclosed || i != len - 1) {
            throw Trace.error(Trace.COLUMN_COUNT_DOES_NOT_MATCH);
        }
    }

    /**
     * Retrieves an INSERT_XXX-type CompiledStatement from this parse context.
     */
    CompiledStatement compileInsertStatement() throws HsqlException {

        clearParameters();
        tokenizer.getThis(Token.T_INTO);

        HsqlArrayList columnNames;
        boolean[]     columnCheckList;
        int[]         columnMap;
        int           len;
        String        token = tokenizer.getString();
        Table         table = database.getTable(token, session);

        checkTableWriteAccess(table, UserManager.INSERT);

        token           = tokenizer.getString();
        columnNames     = null;
        columnCheckList = null;
        columnMap       = table.getColumnMap();
        len             = table.getColumnCount();

        if (token.equals(Token.T_OPENBRACKET)) {
            columnNames = getColumnNames(database, tokenizer, false);

            if (columnNames.size() > len) {
                throw Trace.error(Trace.COLUMN_COUNT_DOES_NOT_MATCH);
            }

            len             = columnNames.size();
            columnCheckList = table.getNewColumnCheckList();
            columnMap       = new int[len];

            for (int i = 0; i < len; i++) {
                int ci = table.getColumnNr((String) columnNames.get(i));

                columnMap[i]        = ci;
                columnCheckList[ci] = true;
            }

            token = tokenizer.getString();
        }

        if (token.equals(Token.T_VALUES)) {
            Expression[] acve = new Expression[len];

            getInsertColumnValueExpressions(table, acve, len);

            CompiledStatement cs = new CompiledStatement(table, columnMap,
                acve, columnCheckList, getParameters());

            cs.subqueries = getSortedSubqueries();

            return cs;
        } else if (token.equals(Token.T_SELECT)) {
            Select select = parseSelect(false);

            if (len != select.iResultLen) {
                throw Trace.error(Trace.COLUMN_COUNT_DOES_NOT_MATCH);
            }

            CompiledStatement cs = new CompiledStatement(table, columnMap,
                columnCheckList, select, getParameters());

            cs.subqueries = getSortedSubqueries();

            return cs;
        } else {
            throw Trace.error(Trace.UNEXPECTED_TOKEN, token);
        }
    }

    /**
     * Retrieves a SELECT-type CompiledStatement from this parse context.
     */
    CompiledStatement compileSelectStatement(boolean isview)
    throws HsqlException {

        Select select;

        clearParameters();

        select = parseSelect(isview);

        CompiledStatement cs = new CompiledStatement(select, getParameters());

        cs.subqueries = getSortedSubqueries();

        return cs;
    }

    /**
     * Retrieves an UPDATE-type CompiledStatement from this parse context.
     */
    CompiledStatement compileUpdateStatement() throws HsqlException {

        String token;
        Table  table;
        String alias = null;

// todo: this would be more efficient as either a primitive list or
// an IntKeyIntValueHashMap
        HsqlArrayList ciList;
        HsqlArrayList cveList;
        int           len;
        Expression    cve;
        Expression    condition;
        int[]         cm;
        Expression[]  acve;

        clearParameters();

        token = tokenizer.getString();
        table = database.getTable(token, session);

        checkTableWriteAccess(table, UserManager.UPDATE);

        if (!tokenizer.isGetThis(Token.T_SET)) {
            alias = tokenizer.getIdentifier();

            tokenizer.getThis(Token.T_SET);
        }

        ciList  = new HsqlArrayList();
        cveList = new HsqlArrayList();
        len     = 0;
        token   = null;

        do {
            len++;

            int ci = table.getColumnNr(tokenizer.getString());

            ciList.add(ValuePool.getInt(ci));
            tokenizer.getThis(Token.T_EQUALS);

            cve = parseExpression();

            cveList.add(cve);

            token = tokenizer.getString();
        } while (token.equals(Token.T_COMMA));

        condition = null;

        if (token.equals(Token.T_WHERE)) {
            condition = parseExpression();
        } else {
            tokenizer.back();
        }

        cm   = new int[len];
        acve = new Expression[len];

        for (int i = 0; i < len; i++) {
            cm[i]   = ((Integer) ciList.get(i)).intValue();
            acve[i] = (Expression) cveList.get(i);
        }

        CompiledStatement cs = new CompiledStatement(table, alias, cm, acve,
            condition, getParameters());

        cs.subqueries = getSortedSubqueries();

        return cs;
    }
}
