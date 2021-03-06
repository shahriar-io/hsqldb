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
 * Copyright (c) 2001-2002, The HSQL Development Group
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

import org.hsqldb.lib.ArrayUtil;
import org.hsqldb.HsqlNameManager.HsqlName;

// fredt@users 20020225 - patch 1.7.0 by boucherb@users - named constraints
// fredt@users 20020320 - doc 1.7.0 - update
// tony_lai@users 20020820 - patch 595156 - violation of Integrity constraint name

/**
 * Implementation of a table constraint with references to the indexes used
 * by the constraint.<p>
 *
 * Methods for checking constraint violation must be called from within a
 * synchronized context that locks the ConsraintCore object.
 *
 * @version    1.7.2
 */
class Constraint {

/*
SQL CLI codes

Referential Constraint 0 CASCADE
Referential Constraint 1 RESTRICT
Referential Constraint 2 SET NULL
Referential Constraint 3 NO ACTION
Referential Constraint 4 SET DEFAULT
*/
    static final int CASCADE     = 0,
                     SET_NULL    = 2,
                     NO_ACTION   = 3,
                     SET_DEFAULT = 4;
    static final int FOREIGN_KEY = 0,
                     MAIN        = 1,
                     UNIQUE      = 2,
                     CHECK       = 3;
    ConstraintCore   core;
    HsqlName         constName;
    int              constType;

    /**
     *  Constructor declaration
     *
     * @param  name
     * @param  t
     * @param  index
     */
    Constraint(HsqlName name, Table t, Index index) {

        core           = new ConstraintCore();
        constName      = name;
        constType      = UNIQUE;
        core.mainTable = t;
        core.mainIndex = index;
        /* fredt - in unique constraints column list for iColMain is the
           visible columns of iMain
        */
        core.mainColArray = ArrayUtil.arraySlice(index.getColumns(), 0,
                index.getVisibleColumns());
        core.colLen = core.mainColArray.length;
    }

    /**
     *  Constructor for main constraints (foreign key references in PK table)
     *
     * @param  name
     * @param  t
     * @param  index
     */
    Constraint(HsqlName name, Constraint fkconstraint) {

        constName = name;
        constType = MAIN;
        core      = fkconstraint.core;
    }

    /**
     *  Constructor for foreign key constraints
     *
     * @param  pkname
     * @param  fkname
     * @param  main
     * @param  ref
     * @param  colmain
     * @param  colref
     * @param  imain
     * @param  iref
     * @param  deleteAction
     * @param  updateAction
     * @exception  HsqlException  Description of the Exception
     */
    Constraint(HsqlName pkname, HsqlName fkname, Table main, Table ref,
               int colmain[], int colref[], Index imain, Index iref,
               int deleteAction, int updateAction) throws HsqlException {

        core           = new ConstraintCore();
        core.pkName    = pkname;
        core.fkName    = fkname;
        constName      = fkname;
        constType      = FOREIGN_KEY;
        core.mainTable = main;
        core.refTable  = ref;
        /* fredt - in FK constraints column lists for iColMain and iColRef have
           identical sets to visible columns of iMain and iRef respectively
           but the order of columns can be different and must be preserved
        */
        core.mainColArray   = colmain;
        core.colLen         = core.mainColArray.length;
        core.refColArray    = colref;
        core.tempRefColData = new Object[core.refColArray.length];
        core.mainIndex      = imain;
        core.refIndex       = iref;
        core.deleteAction   = deleteAction;
        core.updateAction   = updateAction;

        setTableRows();
    }

    /**
     * temp constraint constructor
     */
    Constraint(HsqlName name, int[] mainCol, Table refTable, int[] refCol,
               int type, int deleteAction, int updateAction) {

        core              = new ConstraintCore();
        constName         = name;
        constType         = type;
        core.mainColArray = mainCol;
        core.refTable     = refTable;
        core.refColArray  = refCol;
        core.deleteAction = deleteAction;
        core.updateAction = updateAction;
    }

    private Constraint() {}

    private void setTableRows() throws HsqlException {

        core.tempMainData = core.mainTable.getNewRow();

        if (core.refTable != null) {
            core.tempRefData = core.refTable.getNewRow();
        }
    }

    HsqlName getName() {
        return constName;
    }

    /**
     * Changes constraint name.
     *
     * @param name
     * @param isquoted
     */
    private void setName(String name, boolean isquoted) throws HsqlException {
        constName.rename(name, isquoted);
    }

    /**
     *  probably a misnomer, but DatabaseMetaData.getCrossReference specifies
     *  it this way (I suppose because most FKs are declared against the PK of
     *  another table)
     *
     *  @return name of the index refereneced by a foreign key
     */
    String getPkName() {
        return core.pkName == null ? null
                                   : core.pkName.name;
    }

    /**
     *  probably a misnomer, but DatabaseMetaData.getCrossReference specifies
     *  it this way (I suppose because most FKs are declared against the PK of
     *  another table)
     *
     *  @return name of the index for the referencing foreign key
     */
    String getFkName() {
        return core.fkName == null ? null
                                   : core.fkName.name;
    }

    /**
     *  Method declaration
     *
     * @return name of the index for the foreign key column (child)
     */
    int getType() {
        return constType;
    }

    /**
     *  Method declaration
     *
     * @return
     */
    Table getMain() {
        return core.mainTable;
    }

    Index getMainIndex() {
        return core.mainIndex;
    }

    /**
     *  Method declaration
     *
     * @return
     */
    Table getRef() {
        return core.refTable;
    }

    Index getRefIndex() {
        return core.refIndex;
    }

    /**
     *  The action of (foreign key) constraint on delete
     *
     * @return
     */
    int getDeleteAction() {
        return core.deleteAction;
    }

    /**
     *  The action of (foreign key) constraint on update
     *
     * @return
     */
    int getUpdateAction() {
        return core.updateAction;
    }

    /**
     *  Method declaration
     *
     * @return
     */
    int[] getMainColumns() {
        return core.mainColArray;
    }

    /**
     *  Method declaration
     *
     * @return
     */
    int[] getRefColumns() {
        return core.refColArray;
    }

    /**
     *  See if an index is part this constraint and the constraint is set for
     *  a foreign key. Used for tests before dropping an index. (fredt@users)
     *
     * @return
     */
    boolean isIndexFK(Index index) {

        if (constType == FOREIGN_KEY || constType == MAIN) {
            if (core.mainIndex == index || core.refIndex == index) {
                return true;
            }
        }

        return false;
    }

    /**
     *  See if an index is part this constraint and the constraint is set for
     *  a unique constraint. Used for tests before dropping an index.
     *  (fredt@users)
     *
     * @return
     */
    boolean isIndexUnique(Index index) {

        if (constType == UNIQUE && core.mainIndex == index) {
            return true;
        }

        return false;
    }

// fredt@users 20020225 - patch 1.7.0 by fredt - duplicate constraints

    /**
     * Compares this with another constraint column set. This implementation
     * only checks UNIQUE constraints.
     */
    boolean isEquivalent(int col[], int type) {

        if (type != constType || constType != UNIQUE
                || core.colLen != col.length) {
            return false;
        }

        return ArrayUtil.haveEqualSets(core.mainColArray, col, core.colLen);
    }

    /**
     * Compares this with another constraint column set. This implementation
     * only checks FOREIGN KEY constraints.
     */
    boolean isEquivalent(Table tablemain, int colmain[], Table tableref,
                         int colref[]) {

        if (constType != Constraint.MAIN
                || constType != Constraint.FOREIGN_KEY) {
            return false;
        }

        if (tablemain != core.mainTable || tableref != core.refTable) {
            return false;
        }

        return ArrayUtil.areEqualSets(core.mainColArray, colmain)
               && ArrayUtil.areEqualSets(core.refColArray, colref);
    }

    /**
     *  Used to update constrains to reflect structural changes in a table.
     *
     * @param  oldt reference to the old version of the table
     * @param  newt referenct to the new version of the table
     * @param  colindex index at which table column is added or removed
     * @param  adjust -1, 0, +1 to indicate if column is added or removed
     * @throws  HsqlException
     */
    void replaceTable(Table oldt, Table newt, int colindex,
                      int adjust) throws HsqlException {

        if (oldt == core.mainTable) {
            core.mainTable = newt;

            setTableRows();

            core.mainIndex =
                core.mainTable.getIndex(core.mainIndex.getName().name);
            core.mainColArray =
                ArrayUtil.toAdjustedColumnArray(core.mainColArray, colindex,
                                                adjust);
        }

        if (oldt == core.refTable) {
            core.refTable = newt;

            setTableRows();

            if (core.refIndex != null) {
                core.refIndex =
                    core.refTable.getIndex(core.refIndex.getName().name);

                if (core.refIndex != core.mainIndex) {
                    core.refColArray =
                        ArrayUtil.toAdjustedColumnArray(core.refColArray,
                                                        colindex, adjust);
                }
            }
        }
    }

    /**
     *  Adding or dropping indexes may require changes to the indexes used by
     *  a constraint to an equivalent index.
     *
     * @param  oldi reference to the old index
     * @param  newt referenct to the new index
     * @throws  HsqlException
     */
    void replaceIndex(Index oldi, Index newi) {

        if (oldi == core.refIndex) {
            core.refIndex = newi;
        }

        if (oldi == core.mainIndex) {
            core.mainIndex = newi;
        }
    }

    /**
     *  Checks for foreign key violation when inserting a row in the child
     *  table.
     *
     * @param  row
     * @throws  HsqlException
     */
    void checkInsert(Object row[]) throws HsqlException {

        if (constType == Constraint.MAIN || constType == Constraint.UNIQUE) {

            // inserts in the main table are never a problem
            // unique constraints are checked by the unique index
            return;
        }

        if (constType == Constraint.CHECK) {
            core.checkFilter.currentData = row;

            if (!core.check.test()) {
                core.checkFilter.currentRow = null;

                throw Trace.error(Trace.CHECK_CONSTRAINT_VIOLATION,
                                  Trace.Constraint_violation, new Object[] {
                    constName.name, core.mainTable.tableName.name
                });
            }

            core.checkFilter.currentData = null;

            return;
        }

        // must be called synchronized because of oMain
        for (int i = 0; i < core.colLen; i++) {
            Object o = row[core.refColArray[i]];

            if (o == null) {

                // if one column is null then integrity is not checked
                return;
            }

            core.tempMainData[core.mainColArray[i]] = o;
        }

        // a record must exist in the main table
        if (core.mainIndex.find(core.tempMainData) == null) {
            if (core.mainTable == core.refTable) {
                boolean match = true;

                for (int i = 0; i < core.colLen; i++) {
                    if (!row[core.refColArray[i]].equals(
                            row[core.mainColArray[i]])) {
                        match = false;

                        break;
                    }
                }

                if (match) {
                    return;
                }
            }

            throw Trace.error(Trace.INTEGRITY_CONSTRAINT_VIOLATION,
                              Trace.Constraint_violation, new Object[] {
                core.fkName.name, core.mainTable.getName().name
            });
        }
    }

    /**
     *  Check if a row in the referenced (parent) table can be deleted. Used
     *  only for UPDATE table statements. Checks for DELETE FROM table
     *  statements are now handled by findFkRef() to support ON DELETE
     *  CASCADE.
     *
     * @param  row
     * @throws  HsqlException
     */
    private void checkDelete(Object row[]) throws HsqlException {

        // must be called synchronized because of oRef
        for (int i = 0; i < core.colLen; i++) {
            Object o = row[core.mainColArray[i]];

            if (o == null) {

                // if one column is null then integrity is not checked
                return;
            }

            core.tempRefData[core.refColArray[i]] = o;
        }

        // there must be no record in the 'slave' table
        Node node = core.refIndex.find(core.tempRefData);

        // tony_lai@users 20020820 - patch 595156
        if (node != null) {
            throw Trace.error(Trace.INTEGRITY_CONSTRAINT_VIOLATION,
                              Trace.Constraint_violation, new Object[] {
                core.fkName.name, core.refTable.getName().name
            });
        }
    }

// fredt@users 20020225 - patch 1.7.0 - cascading deletes

    /**
     * New method to find any referencing node (containing the row) for a
     * foreign key (finds row in child table). If ON DELETE CASCADE is
     * supported by this constraint, then the method finds the first row
     * among the rows of the table ordered by the index and doesn't throw.
     * Without ON DELETE CASCADE, the method attempts to finds any row that
     * exists, in which case it throws an exception. If no row is found,
     * null is returned.
     * (fredt@users)
     *
     * @param  array of objects for a database row
     * @param  forDelete should we allow 'ON DELETE CASCADE' or 'ON UPDATE CASCADE'
     * @return Node object or null
     * @throws  HsqlException
     */
    Node findFkRef(Object row[], boolean forDelete) throws HsqlException {

        // must be called synchronized because of oRef
        if (row == null) {
            return null;
        }

        for (int i = 0; i < core.colLen; i++) {
            Object o = row[core.mainColArray[i]];

            if (o == null) {

                // if one column is null then integrity is not checked
                return null;
            }

            core.tempRefColData[i] = o;
        }

        // there must be no record in the 'slave' table
        // sebastian@scienion -- dependent on forDelete | forUpdate
        boolean findfirst = forDelete ? core.deleteAction != NO_ACTION
                                      : core.updateAction != NO_ACTION;
        Node node = core.refIndex.findSimple(core.tempRefColData, findfirst);

        // tony_lai@users 20020820 - patch 595156
        // sebastian@scienion -- check wether we should allow 'ON DELETE CASCADE' or 'ON UPDATE CASCADE'
        if (!(node == null || findfirst)) {
            throw Trace.error(Trace.INTEGRITY_CONSTRAINT_VIOLATION,
                              Trace.Constraint_violation, new Object[] {
                core.fkName.name, core.refTable.getName().name
            });
        }

        return node;
    }

    /**
     * Method to find any referring node in the main table. This is used
     * to check referential integrity when updating a node. We have to make
     * sure that the main table still holds a valid main record. If a valid
     * row is found the corresponding <code>Node</code> is returned.
     * Otherwise a 'INTEGRITY VIOLATION' Exception gets thrown.
     *
     * @param row Obaject[]; the row containing the key columns which have to be
     * checked.
     *
     * @see Table#checkUpdateCascade(Table,Object[],Object[],Session,boolean)
     *
     * @throws HsqlException
     */
    Node findMainRef(Object row[]) throws HsqlException {

        for (int i = 0; i < core.colLen; i++) {
            Object o = row[core.refColArray[i]];

            if (o == null) {

                // if one column is null then integrity is not checked
                return null;
            }

            core.tempRefColData[i] = o;
        }

        Node node = core.mainIndex.findSimple(core.tempRefColData, true);

        // -- there has to be a valid node in the main table
        // --
        if (node == null) {
            throw Trace.error(Trace.INTEGRITY_CONSTRAINT_VIOLATION,
                              Trace.Constraint_violation, new Object[] {
                core.fkName.name, core.refTable.getName().name
            });
        }

        return node;
    }

    /**
     *  Checks if updating a set of columns in a table row breaks the
     *  check and referential integrity constraint.
     *
     * @param  col array of column indexes for columns to check
     * @param  deleted  rows to delete
     * @param  inserted rows to insert
     * @throws  HsqlException
     */
    void checkUpdate(int col[], Result deleted,
                     Result inserted) throws HsqlException {

        if (constType == UNIQUE) {

            // unique constraints are checked by the unique index
            return;
        }

        if (constType == Constraint.CHECK) {

            // check inserted records
            Record r = inserted.rRoot;

            while (r != null) {
                checkInsert(r.data);

                r = r.next;
            }

            return;
        }

        if (constType == MAIN) {
            if (!ArrayUtil.haveCommonElement(col, core.mainColArray,
                                             core.colLen)) {
                return;
            }

            // check deleted records
            Record r = deleted.rRoot;

            while (r != null) {

                // if an identical record exists we don't have to test
                if (core.mainIndex.find(r.data) == null) {
                    checkDelete(r.data);
                }

                r = r.next;
            }
        } else if (constType == FOREIGN_KEY) {
            if (!ArrayUtil.haveCommonElement(col, core.mainColArray,
                                             core.colLen)) {
                return;
            }

            // check inserted records
            Record r = inserted.rRoot;

            while (r != null) {
                checkInsert(r.data);

                r = r.next;
            }
        }
    }
}
