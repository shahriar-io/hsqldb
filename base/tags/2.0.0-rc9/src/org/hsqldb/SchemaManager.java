/* Copyright (c) 2001-2010, The HSQL Development Group
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


package org.hsqldb;

import org.hsqldb.HsqlNameManager.HsqlName;
import org.hsqldb.error.Error;
import org.hsqldb.error.ErrorCode;
import org.hsqldb.lib.HashMappedList;
import org.hsqldb.lib.HsqlArrayList;
import org.hsqldb.lib.Iterator;
import org.hsqldb.lib.MultiValueHashMap;
import org.hsqldb.lib.OrderedHashSet;
import org.hsqldb.lib.WrapperIterator;
import org.hsqldb.navigator.RowIterator;
import org.hsqldb.rights.Grantee;
import org.hsqldb.types.LobData;
import org.hsqldb.types.Type;

/**
 * Manages all SCHEMA related database objects
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 * @version  1.9.0
 * @since 1.8.0
 */
public class SchemaManager {

    Database          database;
    HsqlName          defaultSchemaHsqlName;
    HashMappedList    schemaMap        = new HashMappedList();
    MultiValueHashMap referenceMap     = new MultiValueHashMap();
    int               defaultTableType = TableBase.MEMORY_TABLE;
    long              schemaChangeTimestamp;

    SchemaManager(Database database) {

        this.database         = database;
        defaultSchemaHsqlName = SqlInvariants.INFORMATION_SCHEMA_HSQLNAME;

        Schema schema =
            new Schema(SqlInvariants.INFORMATION_SCHEMA_HSQLNAME,
                       SqlInvariants.INFORMATION_SCHEMA_HSQLNAME.owner);

        schemaMap.put(schema.name.name, schema);

        try {
            schema.typeLookup.add(SqlInvariants.CARDINAL_NUMBER);
            schema.typeLookup.add(SqlInvariants.YES_OR_NO);
            schema.typeLookup.add(SqlInvariants.CHARACTER_DATA);
            schema.typeLookup.add(SqlInvariants.SQL_IDENTIFIER);
            schema.typeLookup.add(SqlInvariants.TIME_STAMP);
            schema.charsetLookup.add(SqlInvariants.SQL_TEXT);
            schema.charsetLookup.add(SqlInvariants.SQL_IDENTIFIER_CHARSET);
            schema.charsetLookup.add(SqlInvariants.SQL_CHARACTER);
        } catch (HsqlException e) {}
    }

    public void setSchemaChangeTimestamp() {
        schemaChangeTimestamp = database.txManager.getGlobalChangeTimestamp();
    }

    public long getSchemaChangeTimestamp() {
        return schemaChangeTimestamp;
    }

    // pre-defined
    public HsqlName getSQLJSchemaHsqlName() {
        return SqlInvariants.SQLJ_SCHEMA_HSQLNAME;
    }

    // SCHEMA management
    void createPublicSchema() {

        HsqlName name = database.nameManager.newHsqlName(null,
            SqlInvariants.PUBLIC_SCHEMA, SchemaObject.SCHEMA);
        Schema schema = new Schema(name,
                                   database.getGranteeManager().getDBARole());

        defaultSchemaHsqlName = schema.name;

        schemaMap.put(schema.name.name, schema);
    }

    /**
     * Creates a schema belonging to the given grantee.
     */
    void createSchema(HsqlName name, Grantee owner) {

        SqlInvariants.checkSchemaNameNotSystem(name.name);

        Schema schema = new Schema(name, owner);

        schemaMap.add(name.name, schema);
    }

    void dropSchema(Session session, String name, boolean cascade) {

        Schema schema = (Schema) schemaMap.get(name);

        if (schema == null) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        if (!cascade && !schema.isEmpty()) {
            throw Error.error(ErrorCode.X_2B000);
        }

        OrderedHashSet externalReferences = new OrderedHashSet();

        getCascadingSchemaReferences(schema.getName(), externalReferences);
        removeSchemaObjects(externalReferences);

        Iterator tableIterator =
            schema.schemaObjectIterator(SchemaObject.TABLE);

        while (tableIterator.hasNext()) {
            Table        table = ((Table) tableIterator.next());
            Constraint[] list  = table.fkConstraints;

            for (int i = 0; i < list.length; i++) {
                Constraint constraint = list[i];

                if (constraint.getMain().getSchemaName() != schema.getName()) {
                    constraint.getMain().removeConstraint(
                        constraint.getMainName().name);
                }
            }

            removeTable(session, table);
        }

        Iterator sequenceIterator =
            schema.schemaObjectIterator(SchemaObject.SEQUENCE);

        while (sequenceIterator.hasNext()) {
            NumberSequence sequence =
                ((NumberSequence) sequenceIterator.next());

            database.getGranteeManager().removeDbObject(sequence.getName());
        }

        schema.clearStructures();
        schemaMap.remove(name);

        if (defaultSchemaHsqlName.name.equals(name)) {
            HsqlName hsqlName = database.nameManager.newHsqlName(name, false,
                SchemaObject.SCHEMA);

            schema = new Schema(hsqlName,
                                database.getGranteeManager().getDBARole());
            defaultSchemaHsqlName = schema.name;

            schemaMap.put(schema.name.name, schema);
        }

        // these are called last and in this particular order
        database.getUserManager().removeSchemaReference(name);
        database.getSessionManager().removeSchemaReference(schema);
    }

    void renameSchema(HsqlName name, HsqlName newName) {

        Schema schema = (Schema) schemaMap.get(name.name);
        Schema exists = (Schema) schemaMap.get(newName.name);

        if (schema == null) {
            throw Error.error(ErrorCode.X_42501, name.name);
        }

        if (exists != null) {
            throw Error.error(ErrorCode.X_42504, newName.name);
        }

        SqlInvariants.checkSchemaNameNotSystem(name.name);
        SqlInvariants.checkSchemaNameNotSystem(newName.name);

        int index = schemaMap.getIndex(name.name);

        schema.name.rename(newName);
        schemaMap.set(index, newName.name, schema);
    }

    void clearStructures() {

        Iterator it = schemaMap.values().iterator();

        while (it.hasNext()) {
            Schema schema = (Schema) it.next();

            schema.clearStructures();
        }
    }

    public Iterator allSchemaNameIterator() {
        return schemaMap.keySet().iterator();
    }

    HsqlName getUserSchemaHsqlName(String name) {

        Schema schema = (Schema) schemaMap.get(name);

        if (schema == null) {
            throw Error.error(ErrorCode.X_3F000, name);
        }

        if (schema.getName() == SqlInvariants.INFORMATION_SCHEMA_HSQLNAME) {
            throw Error.error(ErrorCode.X_3F000, name);
        }

        return schema.name;
    }

    public Grantee toSchemaOwner(String name) {

        // Note that INFORMATION_SCHEMA and DEFINITION_SCHEMA aren't in the
        // backing map.
        // This may not be the most elegant solution, but it is the safest
        // (without doing a code review for implications of adding
        // them to the map).
        if (SqlInvariants.INFORMATION_SCHEMA_HSQLNAME.name.equals(name)) {
            return SqlInvariants.INFORMATION_SCHEMA_HSQLNAME.owner;
        }

        Schema schema = (Schema) schemaMap.get(name);

        return schema == null ? null
                              : schema.owner;
    }

    public HsqlName getDefaultSchemaHsqlName() {
        return defaultSchemaHsqlName;
    }

    public void setDefaultSchemaHsqlName(HsqlName name) {
        defaultSchemaHsqlName = name;
    }

    boolean schemaExists(String name) {
        return SqlInvariants.INFORMATION_SCHEMA.equals(name)
               || schemaMap.containsKey(name);
    }

    public HsqlName findSchemaHsqlName(String name) {

        Schema schema = ((Schema) schemaMap.get(name));

        if (schema == null) {
            return null;
        }

        return schema.name;
    }

    /**
     * If schemaName is null, return the default schema name, else return
     * the HsqlName object for the schema. If schemaName does not exist,
     * throw.
     */
    public HsqlName getSchemaHsqlName(String name) {

        if (name == null) {
            return defaultSchemaHsqlName;
        }

        if (SqlInvariants.INFORMATION_SCHEMA.equals(name)) {
            return SqlInvariants.INFORMATION_SCHEMA_HSQLNAME;
        }

        Schema schema = ((Schema) schemaMap.get(name));

        if (schema == null) {
            throw Error.error(ErrorCode.X_3F000, name);
        }

        return schema.name;
    }

    /**
     * Same as above, but return string
     */
    public String getSchemaName(String name) {
        return getSchemaHsqlName(name).name;
    }

    public Schema findSchema(String name) {
        return ((Schema) schemaMap.get(name));
    }

    /**
     * Iterator includes DEFINITION_SCHEMA
     */
    public Iterator fullSchemaNamesIterator() {
        return schemaMap.keySet().iterator();
    }

    public boolean isSystemSchema(String schema) {

        return SqlInvariants.INFORMATION_SCHEMA.equals(schema)
               || SqlInvariants.DEFINITION_SCHEMA.equals(schema)
               || SqlInvariants.SYSTEM_SCHEMA.equals(schema);
    }

    public boolean isLobsSchema(String schema) {
        return SqlInvariants.LOBS_SCHEMA.equals(schema);
    }

    /**
     * is a grantee the authorization of any schema
     */
    boolean isSchemaAuthorisation(Grantee grantee) {

        Iterator schemas = allSchemaNameIterator();

        while (schemas.hasNext()) {
            String schemaName = (String) schemas.next();

            if (grantee.equals(toSchemaOwner(schemaName))) {
                return true;
            }
        }

        return false;
    }

    /**
     * drop all schemas with the given authorisation
     */
    void dropSchemas(Session session, Grantee grantee, boolean cascade) {

        HsqlArrayList list = getSchemas(grantee);
        Iterator      it   = list.iterator();

        while (it.hasNext()) {
            Schema schema = (Schema) it.next();

            dropSchema(session, schema.name.name, cascade);
        }
    }

    HsqlArrayList getSchemas(Grantee grantee) {

        HsqlArrayList list = new HsqlArrayList();
        Iterator      it   = schemaMap.values().iterator();

        while (it.hasNext()) {
            Schema schema = (Schema) it.next();

            if (grantee.equals(schema.owner)) {
                list.add(schema);
            }
        }

        return list;
    }

    boolean hasSchemas(Grantee grantee) {

        Iterator it = schemaMap.values().iterator();

        while (it.hasNext()) {
            Schema schema = (Schema) it.next();

            if (grantee.equals(schema.owner)) {
                return true;
            }
        }

        return false;
    }

    /**
     *  Returns an HsqlArrayList containing references to all non-system
     *  tables and views. This includes all tables and views registered with
     *  this Database.
     */
    public HsqlArrayList getAllTables() {

        Iterator      schemas   = allSchemaNameIterator();
        HsqlArrayList alltables = new HsqlArrayList();

        while (schemas.hasNext()) {
            String name = (String) schemas.next();

            if (isSystemSchema(name) || isLobsSchema(name)) {
                continue;
            }

            HashMappedList current = getTables(name);

            alltables.addAll(current.values());
        }

        return alltables;
    }

    public HashMappedList getTables(String schema) {

        Schema temp = (Schema) schemaMap.get(schema);

        return temp.tableList;
    }

    public HsqlName[] getCatalogAndBaseTableNames() {

        OrderedHashSet names  = new OrderedHashSet();
        HsqlArrayList  tables = getAllTables();

        for (int i = 0; i < tables.size(); i++) {
            Table table = (Table) tables.get(i);

            if (!table.isTemp()) {
                names.add(table.getName());
            }
        }

        names.add(database.getCatalogName());

        HsqlName[] array = new HsqlName[names.size()];

        names.toArray(array);

        return array;
    }

    SchemaObjectSet getSchemaObjectSet(Schema schema, int type) {

        SchemaObjectSet set = null;

        switch (type) {

            case SchemaObject.SEQUENCE :
                set = schema.sequenceLookup;
                break;

            case SchemaObject.TABLE :
            case SchemaObject.VIEW :
                set = schema.tableLookup;
                break;

            case SchemaObject.CHARSET :
                set = schema.charsetLookup;
                break;

            case SchemaObject.COLLATION :
                set = schema.collationLookup;
                break;

            case SchemaObject.PROCEDURE :
                set = schema.procedureLookup;
                break;

            case SchemaObject.FUNCTION :
                set = schema.functionLookup;
                break;

            case SchemaObject.DOMAIN :
            case SchemaObject.TYPE :
                set = schema.typeLookup;
                break;

            case SchemaObject.INDEX :
                set = schema.indexLookup;
                break;

            case SchemaObject.CONSTRAINT :
                set = schema.constraintLookup;
                break;

            case SchemaObject.TRIGGER :
                set = schema.triggerLookup;
                break;

            case SchemaObject.SPECIFIC_ROUTINE :
                set = schema.specificRoutineLookup;
        }

        return set;
    }

    void checkSchemaObjectNotExists(HsqlName name) {

        Schema          schema = (Schema) schemaMap.get(name.schema.name);
        SchemaObjectSet set    = getSchemaObjectSet(schema, name.type);

        set.checkAdd(name);
    }

    /**
     *  Returns the specified user-defined table or view visible within the
     *  context of the specified Session, or any system table of the given
     *  name. It excludes any temp tables created in other Sessions.
     *  Throws if the table does not exist in the context.
     */
    public Table getTable(Session session, String name, String schema) {

        Table t = null;

        if (schema == null) {
            t = findSessionTable(session, name, null);
        }

        if (t == null) {
            schema = session.getSchemaName(schema);
            t      = findUserTable(session, name, schema);
        }

        if (t == null) {
            if (SqlInvariants.INFORMATION_SCHEMA.equals(schema)
                    && database.dbInfo != null) {
                t = database.dbInfo.getSystemTable(session, name);
            }
        }

        if (t == null) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return t;
    }

    public Table getUserTable(Session session, HsqlName name) {
        return getUserTable(session, name.name, name.schema.name);
    }

    /**
     *  Returns the specified user-defined table or view visible within the
     *  context of the specified Session. It excludes system tables and
     *  any temp tables created in different Sessions.
     *  Throws if the table does not exist in the context.
     */
    public Table getUserTable(Session session, String name, String schema) {

        Table t = findUserTable(session, name, schema);

        if (t == null) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return t;
    }

    /**
     *  Returns the specified user-defined table or view visible within the
     *  context of the specified schema. It excludes system tables.
     *  Returns null if the table does not exist in the context.
     */
    public Table findUserTable(Session session, String name,
                               String schemaName) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema == null) {
            return null;
        }

        int i = schema.tableList.getIndex(name);

        if (i == -1) {
            return null;
        }

        return (Table) schema.tableList.get(i);
    }

    /**
     *  Returns the specified session context table.
     *  Returns null if the table does not exist in the context.
     */
    public Table findSessionTable(Session session, String name,
                                  String schemaName) {
        return session.findSessionTable(name);
    }

    /**
     * Drops the specified user-defined view or table from this Database object.
     *
     * <p> The process of dropping a table or view includes:
     * <OL>
     * <LI> checking that the specified Session's currently connected User has
     * the right to perform this operation and refusing to proceed if not by
     * throwing.
     * <LI> checking for referential constraints that conflict with this
     * operation and refusing to proceed if they exist by throwing.</LI>
     * <LI> removing the specified Table from this Database object.
     * <LI> removing any exported foreign keys Constraint objects held by any
     * tables referenced by the table to be dropped. This is especially
     * important so that the dropped Table ceases to be referenced, eventually
     * allowing its full garbage collection.
     * <LI>
     * </OL>
     *
     * <p>
     *
     * @param session the connected context in which to perform this operation
     * @param table if true and if the Table to drop does not exist, fail
     *   silently, else throw
     * @param cascade true if the name argument refers to a View
     */
    void dropTableOrView(Session session, Table table, boolean cascade) {

// ft - concurrent
        session.commit(false);

        if (table.isView()) {
            removeSchemaObject(table.getName(), cascade);
        } else {
            dropTable(session, table, cascade);
        }
    }

    void dropTable(Session session, Table table, boolean cascade) {

        Schema schema    = (Schema) schemaMap.get(table.getSchemaName().name);
        int    dropIndex = schema.tableList.getIndex(table.getName().name);
        OrderedHashSet externalConstraints =
            table.getDependentExternalConstraints();
        OrderedHashSet externalReferences = new OrderedHashSet();

        getCascadingReferencingObjectNames(table.getName(),
                                           externalReferences);

        if (!cascade) {
            for (int i = 0; i < externalConstraints.size(); i++) {
                Constraint c         = (Constraint) externalConstraints.get(i);
                HsqlName   tablename = c.getRef().getName();
                HsqlName   refname   = c.getRefName();

                if (c.getConstraintType()
                        == SchemaObject.ConstraintTypes.MAIN) {
                    throw Error.error(ErrorCode.X_42533,
                                      refname.schema.name + '.'
                                      + tablename.name + '.' + refname.name);
                }
            }

            if (!externalReferences.isEmpty()) {
                int i = 0;

                for (; i < externalReferences.size(); i++) {
                    HsqlName name = (HsqlName) externalReferences.get(i);

                    if (name.parent == table.getName()) {
                        continue;
                    }

                    throw Error.error(ErrorCode.X_42502,
                                      name.getSchemaQualifiedStatementName());
                }
            }
        }

        OrderedHashSet tableSet          = new OrderedHashSet();
        OrderedHashSet constraintNameSet = new OrderedHashSet();
        OrderedHashSet indexNameSet      = new OrderedHashSet();

        // only columns with refs
        OrderedHashSet childReferences = table.getReferences();
        TriggerDef[]   triggers        = table.getTriggers();

        for (int i = 0; i < triggers.length; i++) {
            childReferences.add(triggers[i].getName());
        }

        for (int i = 0; i < externalConstraints.size(); i++) {
            Constraint c = (Constraint) externalConstraints.get(i);
            Table      t = c.getMain();

            if (t != table) {
                tableSet.add(t);
            }

            t = c.getRef();

            if (t != table) {
                tableSet.add(t);
            }

            constraintNameSet.add(c.getMainName());
            constraintNameSet.add(c.getRefName());
            indexNameSet.add(c.getRefIndex().getName());
        }

        TableWorks tw = new TableWorks(session, table);

        tableSet = tw.makeNewTables(tableSet, constraintNameSet, indexNameSet);

        tw.setNewTablesInSchema(tableSet);
        tw.updateConstraints(tableSet, constraintNameSet);
        removeSchemaObjects(externalReferences);
        removeSchemaObjects(childReferences);
        removeReferencedObject(table.getName());
        removeReferencingObject(table);
        schema.tableList.remove(dropIndex);
        schema.indexLookup.removeParent(table.tableName);
        schema.constraintLookup.removeParent(table.tableName);
        removeTable(session, table);
        recompileDependentObjects(tableSet);
    }

    void removeTable(Session session, Table table) {

        database.getGranteeManager().removeDbObject(table.getName());
        table.releaseTriggers();

        if (table.hasLobColumn) {
            RowIterator it = table.rowIterator(session);

            while (it.hasNext()) {
                Row      row  = it.getNextRow();
                Object[] data = row.getData();

                for (int i = 0; i < data.length; i++) {
                    if (data[i] instanceof LobData) {
                        database.lobManager.adjustUsageCount(
                            ((LobData) data[i]).getId(), -1);
                    }
                }
            }
        }

        database.persistentStoreCollection.releaseStore(table);
    }

    void setTable(int index, Table table) {

        Schema schema = (Schema) schemaMap.get(table.getSchemaName().name);

        schema.tableList.set(index, table.getName().name, table);
    }

    /**
     *  Returns index of a table or view in the HashMappedList that
     *  contains the table objects for this Database.
     *
     * @param  table the Table object
     * @return  the index of the specified table or view, or -1 if not found
     */
    int getTableIndex(Table table) {

        Schema schema = (Schema) schemaMap.get(table.getSchemaName().name);

        if (schema == null) {
            return -1;
        }

        HsqlName name = table.getName();

        return schema.tableList.getIndex(name.name);
    }

    void recompileDependentObjects(OrderedHashSet tableSet) {

        OrderedHashSet set = new OrderedHashSet();

        for (int i = 0; i < tableSet.size(); i++) {
            Table table = (Table) tableSet.get(i);

            set.addAll(getReferencingObjectNames(table.getName()));
        }

        Session session = database.sessionManager.getSysSession();

        for (int i = 0; i < set.size(); i++) {
            HsqlName name = (HsqlName) set.get(i);

            switch (name.type) {

                case SchemaObject.VIEW :
                case SchemaObject.CONSTRAINT :
                case SchemaObject.ASSERTION :
                case SchemaObject.ROUTINE :
                case SchemaObject.PROCEDURE :
                case SchemaObject.FUNCTION :
                case SchemaObject.SPECIFIC_ROUTINE :
                    SchemaObject object = getSchemaObject(name);

                    object.compile(session, null);
                    break;
            }
        }
    }

    /**
     * After addition or removal of columns and indexes all views that
     * reference the table should be recompiled.
     */
    void recompileDependentObjects(Table table) {

        OrderedHashSet set = new OrderedHashSet();

        getCascadingReferencingObjectNames(table.getName(), set);

        Session session = database.sessionManager.getSysSession();

        for (int i = 0; i < set.size(); i++) {
            HsqlName name = (HsqlName) set.get(i);

            switch (name.type) {

                case SchemaObject.VIEW :
                case SchemaObject.CONSTRAINT :
                case SchemaObject.ASSERTION :
                case SchemaObject.ROUTINE :
                case SchemaObject.PROCEDURE :
                case SchemaObject.FUNCTION :
                case SchemaObject.SPECIFIC_ROUTINE :
                    SchemaObject object = getSchemaObject(name);

                    object.compile(session, null);
                    break;
            }
        }

        HsqlArrayList list = getAllTables();

        for (int i = 0; i < list.size(); i++) {
            Table t = (Table) list.get(i);

            t.verifyConstraintsIntegrity();
        }
    }

    NumberSequence getSequence(String name, String schemaName, boolean raise) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema != null) {
            NumberSequence object =
                (NumberSequence) schema.sequenceList.get(name);

            if (object != null) {
                return object;
            }
        }

        if (raise) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return null;
    }

    public Type getUserDefinedType(String name, String schemaName,
                                   boolean raise) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema != null) {
            SchemaObject object = schema.typeLookup.getObject(name);

            if (object != null) {
                return (Type) object;
            }
        }

        if (raise) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return null;
    }

    public Type getDomain(String name, String schemaName, boolean raise) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema != null) {
            SchemaObject object = schema.typeLookup.getObject(name);

            if (object != null && ((Type) object).isDomainType()) {
                return (Type) object;
            }
        }

        if (raise) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return null;
    }

    public Type getDistinctType(String name, String schemaName,
                                boolean raise) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema != null) {
            SchemaObject object = schema.typeLookup.getObject(name);

            if (object != null && ((Type) object).isDomainType()) {
                return (Type) object;
            }
        }

        if (raise) {
            throw Error.error(ErrorCode.X_42501, name);
        }

        return null;
    }

    public SchemaObject getSchemaObject(String name, String schemaName,
                                        int type) {

        SchemaObject object = findSchemaObject(name, schemaName, type);

        if (object == null) {
            throw Error.error(SchemaObjectSet.getGetErrorCode(type), name);
        }

        return object;
    }

    public SchemaObject findSchemaObject(String name, String schemaName,
                                         int type) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        if (schema == null) {
            return null;
        }

        SchemaObjectSet set = null;
        HsqlName        objectName;
        Table           table;

        switch (type) {

            case SchemaObject.SEQUENCE :
                return schema.sequenceLookup.getObject(name);

            case SchemaObject.TABLE :
            case SchemaObject.VIEW :
                return schema.sequenceLookup.getObject(name);

            case SchemaObject.CHARSET :
                if (name.equals("SQL_IDENTIFIER")) {
                    return SqlInvariants.SQL_IDENTIFIER_CHARSET;
                }

                if (name.equals("SQL_TEXT")) {
                    return SqlInvariants.SQL_TEXT;
                }

                if (name.equals("LATIN1")) {
                    return SqlInvariants.LATIN1;
                }

                if (name.equals("ASCII_GRAPHIC")) {
                    return SqlInvariants.ASCII_GRAPHIC;
                }

                return schema.charsetLookup.getObject(name);

            case SchemaObject.COLLATION :
                return schema.collationLookup.getObject(name);

            case SchemaObject.PROCEDURE :
                return schema.procedureLookup.getObject(name);

            case SchemaObject.FUNCTION :
                return schema.functionLookup.getObject(name);

            case SchemaObject.ROUTINE : {
                SchemaObject object = schema.procedureLookup.getObject(name);

                if (object == null) {
                    object = schema.functionLookup.getObject(name);
                }

                return object;
            }
            case SchemaObject.SPECIFIC_ROUTINE :
                return schema.specificRoutineLookup.getObject(name);

            case SchemaObject.DOMAIN :
            case SchemaObject.TYPE :
                return schema.typeLookup.getObject(name);

            case SchemaObject.INDEX :
                set        = schema.indexLookup;
                objectName = set.getName(name);

                if (objectName == null) {
                    return null;
                }

                table = (Table) schema.tableList.get(objectName.parent.name);

                return table.getIndex(name);

            case SchemaObject.CONSTRAINT :
                set        = schema.constraintLookup;
                objectName = set.getName(name);

                if (objectName == null) {
                    return null;
                }

                table = (Table) schema.tableList.get(objectName.parent.name);

                if (table == null) {
                    return null;
                }

                return table.getConstraint(name);

            case SchemaObject.TRIGGER :
                set        = schema.indexLookup;
                objectName = set.getName(name);

                if (objectName == null) {
                    return null;
                }

                table = (Table) schema.tableList.get(objectName.parent.name);

                return table.getTrigger(name);

            default :
                throw Error.runtimeError(ErrorCode.U_S0500, "SchemaManager");
        }
    }

    // INDEX management

    /**
     * Returns the table that has an index with the given name and schema.
     */
    Table findUserTableForIndex(Session session, String name,
                                String schemaName) {

        Schema   schema    = (Schema) schemaMap.get(schemaName);
        HsqlName indexName = schema.indexLookup.getName(name);

        if (indexName == null) {
            return null;
        }

        return findUserTable(session, indexName.parent.name, schemaName);
    }

    /**
     * Drops the index with the specified name.
     */
    void dropIndex(Session session, HsqlName name) {

        Table t = getTable(session, name.parent.name, name.parent.schema.name);
        TableWorks tw = new TableWorks(session, t);

        tw.dropIndex(name.name);
    }

    /**
     * Drops the index with the specified name.
     */
    void dropConstraint(Session session, HsqlName name, boolean cascade) {

        Table t = getTable(session, name.parent.name, name.parent.schema.name);
        TableWorks tw = new TableWorks(session, t);

        tw.dropConstraint(name.name, cascade);
    }

    void removeDependentObjects(HsqlName name) {

        Schema schema = (Schema) schemaMap.get(name.schema.name);

        schema.indexLookup.removeParent(name);
        schema.constraintLookup.removeParent(name);
        schema.triggerLookup.removeParent(name);
    }

    /**
     *  Removes any foreign key Constraint objects (exported keys) held by any
     *  tables referenced by the specified table. <p>
     *
     *  This method is called as the last step of a successful call to
     *  dropTable() in order to ensure that the dropped Table ceases to be
     *  referenced when enforcing referential integrity.
     *
     * @param  toDrop The table to which other tables may be holding keys.
     *      This is a table that is in the process of being dropped.
     */
    void removeExportedKeys(Table toDrop) {

        // toDrop.schema may be null because it is not registerd
        Schema schema = (Schema) schemaMap.get(toDrop.getSchemaName().name);

        for (int i = 0; i < schema.tableList.size(); i++) {
            Table table = (Table) schema.tableList.get(i);

            for (int j = table.constraintList.length - 1; j >= 0; j--) {
                Table refTable = table.constraintList[j].getRef();

                if (toDrop == refTable) {
                    table.removeConstraint(j);
                }
            }
        }
    }

    public Iterator databaseObjectIterator(String schemaName, int type) {

        Schema schema = (Schema) schemaMap.get(schemaName);

        return schema.schemaObjectIterator(type);
    }

    public Iterator databaseObjectIterator(int type) {

        Iterator it      = schemaMap.values().iterator();
        Iterator objects = new WrapperIterator();

        while (it.hasNext()) {
            Schema temp = (Schema) it.next();

            objects = new WrapperIterator(objects,
                                          temp.schemaObjectIterator(type));
        }

        return objects;
    }

    // references
    private void addReferences(SchemaObject object) {

        OrderedHashSet set = object.getReferences();

        if (set == null) {
            return;
        }

        for (int i = 0; i < set.size(); i++) {
            HsqlName referenced = (HsqlName) set.get(i);
            HsqlName name = object.getName();

            if (object instanceof Routine) {
                name = ((Routine)object).getSpecificName();
            }

            referenceMap.put(referenced, name);
        }
    }

    private void removeReferencedObject(HsqlName referenced) {
        referenceMap.remove(referenced);
    }

    private void removeReferencingObject(SchemaObject object) {

        OrderedHashSet set = object.getReferences();

        if (set == null) {
            return;
        }

        for (int i = 0; i < set.size(); i++) {
            HsqlName referenced = (HsqlName) set.get(i);

            referenceMap.remove(referenced, object.getName());

            if (object.getName().parent != null) {
                referenceMap.remove(referenced, object.getName().parent);
            }
        }
    }

    OrderedHashSet getReferencingObjectNames(HsqlName object) {

        OrderedHashSet set = new OrderedHashSet();
        Iterator       it  = referenceMap.get(object);

        while (it.hasNext()) {
            HsqlName name = (HsqlName) it.next();

            set.add(name);
        }

        return set;
    }

    OrderedHashSet getReferencingObjectNames(HsqlName table, HsqlName column) {

        OrderedHashSet set = new OrderedHashSet();
        Iterator       it  = referenceMap.get(table);

        while (it.hasNext()) {
            HsqlName       name       = (HsqlName) it.next();
            SchemaObject   object     = getSchemaObject(name);
            OrderedHashSet references = object.getReferences();

            if (references.contains(column)) {
                set.add(name);
            }
        }

        return set;
    }

    private boolean isReferenced(HsqlName object) {
        return referenceMap.containsKey(object);
    }

    //
    void getCascadingReferencingObjectNames(HsqlName object,
            OrderedHashSet set) {

        OrderedHashSet newSet = new OrderedHashSet();
        Iterator       it     = referenceMap.get(object);

        while (it.hasNext()) {
            HsqlName name  = (HsqlName) it.next();
            boolean  added = set.add(name);

            if (added) {
                newSet.add(name);
            }
        }

        for (int i = 0; i < newSet.size(); i++) {
            HsqlName name = (HsqlName) newSet.get(i);

            getCascadingReferencingObjectNames(name, set);
        }
    }

    //
    void getCascadingSchemaReferences(HsqlName schema, OrderedHashSet set) {

        Iterator mainIterator = referenceMap.keySet().iterator();

        while (mainIterator.hasNext()) {
            HsqlName name = (HsqlName) mainIterator.next();

            if (name.schema != schema) {
                continue;
            }

            getCascadingReferencingObjectNames(name, set);
        }

        for (int i = 0; i < set.size(); i++) {
            HsqlName name = (HsqlName) set.get(i);

            if (name.schema == schema) {
                set.remove(i);

                i--;
            }
        }
    }

    //
    HsqlName getSchemaObjectName(HsqlName schemaName, String name, int type,
                                 boolean raise) {

        Schema          schema = (Schema) schemaMap.get(schemaName.name);
        SchemaObjectSet set    = null;

        if (schema == null) {
            if (raise) {
                throw Error.error(SchemaObjectSet.getGetErrorCode(type));
            } else {
                return null;
            }
        }

        if (type == RoutineSchema.ROUTINE) {
            set = schema.functionLookup;

            SchemaObject object = schema.functionLookup.getObject(name);

            if (object == null) {
                set    = schema.procedureLookup;
                object = schema.procedureLookup.getObject(name);
            }
        } else {
            set = getSchemaObjectSet(schema, type);
        }

        if (raise) {
            set.checkExists(name);
        }

        return set.getName(name);
    }

    SchemaObject getSchemaObject(HsqlName name) {

        Schema schema = (Schema) schemaMap.get(name.schema.name);

        if (schema == null) {
            return null;
        }

        switch (name.type) {

            case SchemaObject.SEQUENCE :
                return (SchemaObject) schema.sequenceList.get(name.name);

            case SchemaObject.TABLE :
            case SchemaObject.VIEW :
                return (SchemaObject) schema.tableList.get(name.name);

            case SchemaObject.CHARSET :
                return schema.charsetLookup.getObject(name.name);

            case SchemaObject.COLLATION :
                return schema.collationLookup.getObject(name.name);

            case SchemaObject.PROCEDURE :
                return schema.procedureLookup.getObject(name.name);

            case SchemaObject.FUNCTION :
                return schema.functionLookup.getObject(name.name);

            case RoutineSchema.SPECIFIC_ROUTINE :
                return schema.specificRoutineLookup.getObject(name.name);

            case RoutineSchema.ROUTINE :
                SchemaObject object =
                    schema.functionLookup.getObject(name.name);

                if (object == null) {
                    object = schema.procedureLookup.getObject(name.name);
                }

                return object;

            case SchemaObject.DOMAIN :
            case SchemaObject.TYPE :
                return schema.typeLookup.getObject(name.name);

            case SchemaObject.TRIGGER : {
                name = schema.triggerLookup.getName(name.name);

                if (name == null) {
                    return null;
                }

                HsqlName tableName = name.parent;
                Table    table = (Table) schema.tableList.get(tableName.name);

                return table.getTrigger(name.name);
            }
            case SchemaObject.CONSTRAINT : {
                name = schema.constraintLookup.getName(name.name);

                if (name == null) {
                    return null;
                }

                HsqlName tableName = name.parent;
                Table    table = (Table) schema.tableList.get(tableName.name);

                return table.getConstraint(name.name);
            }
            case SchemaObject.ASSERTION :
                return null;

            case SchemaObject.INDEX :
                name = schema.indexLookup.getName(name.name);

                if (name == null) {
                    return null;
                }

                HsqlName tableName = name.parent;
                Table    table = (Table) schema.tableList.get(tableName.name);

                return table.getIndex(name.name);
        }

        return null;
    }

    void checkColumnIsReferenced(HsqlName tableName, HsqlName name) {

        OrderedHashSet set = getReferencingObjectNames(tableName, name);

        if (!set.isEmpty()) {
            HsqlName objectName = (HsqlName) set.get(0);

            throw Error.error(ErrorCode.X_42502,
                              objectName.getSchemaQualifiedStatementName());
        }
    }

    void checkObjectIsReferenced(HsqlName name) {

        OrderedHashSet set     = getReferencingObjectNames(name);
        HsqlName       refName = null;

        for (int i = 0; i < set.size(); i++) {
            refName = (HsqlName) set.get(i);

            // except columns of same table
            if (refName.parent != name) {
                break;
            }

            refName = null;
        }

        if (refName == null) {
            return;
        }

        throw Error.error(ErrorCode.X_42502,
                          refName.getSchemaQualifiedStatementName());
    }

    void checkSchemaNameCanChange(HsqlName name) {

        Iterator it      = referenceMap.values().iterator();
        HsqlName refName = null;

        mainLoop:
        while (it.hasNext()) {
            refName = (HsqlName) it.next();

            switch (refName.type) {

                case SchemaObject.VIEW :
                case SchemaObject.ROUTINE :
                case SchemaObject.FUNCTION :
                case SchemaObject.PROCEDURE :
                case SchemaObject.TRIGGER :
                case SchemaObject.SPECIFIC_ROUTINE :
                    if (refName.schema == name) {
                        break mainLoop;
                    }
                    break;

                default :
                    break;
            }

            refName = null;
        }

        if (refName == null) {
            return;
        }

        throw Error.error(ErrorCode.X_42502,
                          refName.getSchemaQualifiedStatementName());
    }

    void addSchemaObject(SchemaObject object) {

        HsqlName        name   = object.getName();
        Schema          schema = (Schema) schemaMap.get(name.schema.name);
        SchemaObjectSet set    = getSchemaObjectSet(schema, name.type);

        switch (name.type) {

            case SchemaObject.PROCEDURE :
            case SchemaObject.FUNCTION : {
                RoutineSchema routine =
                    (RoutineSchema) set.getObject(name.name);

                if (routine == null) {
                    routine = new RoutineSchema(name.type, name);

                    routine.addSpecificRoutine(database, (Routine) object);
                    set.checkAdd(name);

                    SchemaObjectSet specificSet = getSchemaObjectSet(schema,
                        SchemaObject.SPECIFIC_ROUTINE);

                    specificSet.checkAdd(((Routine) object).getSpecificName());
                    set.add(routine);
                    specificSet.add(object);
                } else {
                    SchemaObjectSet specificSet = getSchemaObjectSet(schema,
                        SchemaObject.SPECIFIC_ROUTINE);
                    HsqlName specificName =
                        ((Routine) object).getSpecificName();

                    if (specificName != null) {
                        specificSet.checkAdd(specificName);
                    }

                    routine.addSpecificRoutine(database, (Routine) object);
                    specificSet.add(object);
                }

                addReferences(object);

                return;
            }
            case SchemaObject.TABLE : {
                OrderedHashSet refs = object.getReferences();

                for (int i = 0; i < refs.size(); i++) {
                    HsqlName ref = (HsqlName) refs.get(i);

                    if (ref.type == SchemaObject.COLUMN) {
                        int index = ((Table) object).findColumn(ref.name);
                        ColumnSchema column =
                            ((Table) object).getColumn(index);

                        addSchemaObject(column);
                    }
                }

                break;
            }
            case SchemaObject.COLUMN : {
                if (object.getReferences().isEmpty()) {
                    return;
                }

                break;
            }
        }

        if (set != null) {
            set.add(object);
        }

        addReferences(object);
    }

    void removeSchemaObject(HsqlName name, boolean cascade) {

        OrderedHashSet objectSet = new OrderedHashSet();

        switch (name.type) {

            case SchemaObject.ROUTINE :
            case SchemaObject.PROCEDURE :
            case SchemaObject.FUNCTION : {
                RoutineSchema routine = (RoutineSchema) getSchemaObject(name);

                if (routine != null) {
                    Routine[] specifics = routine.getSpecificRoutines();

                    for (int i = 0; i < specifics.length; i++) {
                        getCascadingReferencingObjectNames(
                            specifics[i].getSpecificName(), objectSet);
                    }
                }
            }
            break;

            case SchemaObject.SEQUENCE :
            case SchemaObject.TABLE :
            case SchemaObject.VIEW :
            case SchemaObject.TYPE :
            case SchemaObject.CHARSET :
            case SchemaObject.COLLATION :
            case SchemaObject.SPECIFIC_ROUTINE :
                getCascadingReferencingObjectNames(name, objectSet);
                break;

            case SchemaObject.DOMAIN :
                OrderedHashSet set = getReferencingObjectNames(name);
                Iterator       it  = set.iterator();

                while (it.hasNext()) {
                    HsqlName ref = (HsqlName) it.next();

                    if (ref.type == SchemaObject.COLUMN) {
                        it.remove();
                    }
                }

                if (!set.isEmpty()) {
                    HsqlName objectName = (HsqlName) set.get(0);

                    throw Error.error(
                        ErrorCode.X_42502,
                        objectName.getSchemaQualifiedStatementName());
                }
                break;
        }

        if (objectSet.isEmpty()) {
            removeSchemaObject(name);

            return;
        }

        if (!cascade) {
            HsqlName objectName = (HsqlName) objectSet.get(0);

            throw Error.error(ErrorCode.X_42502,
                              objectName.getSchemaQualifiedStatementName());
        }

        objectSet.add(name);
        removeSchemaObjects(objectSet);
    }

    void removeSchemaObjects(OrderedHashSet set) {

        for (int i = 0; i < set.size(); i++) {
            HsqlName name = (HsqlName) set.get(i);

            removeSchemaObject(name);
        }
    }

    void removeSchemaObject(HsqlName name) {

        Schema          schema = (Schema) schemaMap.get(name.schema.name);
        SchemaObject    object = null;
        SchemaObjectSet set    = null;

        switch (name.type) {

            case SchemaObject.SEQUENCE :
                set    = schema.sequenceLookup;
                object = set.getObject(name.name);
                break;

            case SchemaObject.TABLE :
            case SchemaObject.VIEW : {
                set    = schema.tableLookup;
                object = set.getObject(name.name);

                break;
            }
            case SchemaObject.COLUMN : {
                Table table = (Table) getSchemaObject(name.parent);

                if (table != null) {
                    object = table.getColumn(table.getColumnIndex(name.name));
                }

                break;
            }
            case SchemaObject.CHARSET :
                set    = schema.charsetLookup;
                object = set.getObject(name.name);
                break;

            case SchemaObject.COLLATION :
                set    = schema.collationLookup;
                object = set.getObject(name.name);
                break;

            case SchemaObject.PROCEDURE : {
                set = schema.procedureLookup;

                RoutineSchema routine =
                    (RoutineSchema) set.getObject(name.name);

                object = routine;

                Routine[] specifics = routine.getSpecificRoutines();

                for (int i = 0; i < specifics.length; i++) {
                    removeSchemaObject(specifics[i].getSpecificName());
                }

                break;
            }
            case SchemaObject.FUNCTION : {
                set = schema.functionLookup;

                RoutineSchema routine =
                    (RoutineSchema) set.getObject(name.name);

                object = routine;

                Routine[] specifics = routine.getSpecificRoutines();

                for (int i = 0; i < specifics.length; i++) {
                    removeSchemaObject(specifics[i].getSpecificName());
                }

                break;
            }
            case SchemaObject.SPECIFIC_ROUTINE : {
                set = schema.specificRoutineLookup;

                Routine routine = (Routine) set.getObject(name.name);

                object = routine;

                routine.routineSchema.removeSpecificRoutine(routine);

                if (routine.routineSchema.getSpecificRoutines().length == 0) {
                    removeSchemaObject(routine.getName());
                }

                break;
            }
            case SchemaObject.DOMAIN :
            case SchemaObject.TYPE :
                set    = schema.typeLookup;
                object = set.getObject(name.name);
                break;

            case SchemaObject.INDEX :
                set = schema.indexLookup;
                break;

            case SchemaObject.CONSTRAINT : {
                set = schema.constraintLookup;

                if (name.parent.type == SchemaObject.TABLE) {
                    Table table =
                        (Table) schema.tableList.get(name.parent.name);

                    object = table.getConstraint(name.name);

                    table.removeConstraint(name.name);
                } else if (name.parent.type == SchemaObject.DOMAIN) {
                    Type type =
                        (Type) schema.typeLookup.getObject(name.parent.name);

                    object = type.userTypeModifier.getConstraint(name.name);

                    type.userTypeModifier.removeConstraint(name.name);
                }

                break;
            }
            case SchemaObject.TRIGGER : {
                set = schema.triggerLookup;

                Table table = (Table) schema.tableList.get(name.parent.name);

                object = table.getTrigger(name.name);

                if (object != null) {
                    table.removeTrigger((TriggerDef) object);
                }

                break;
            }
            default :
                throw Error.runtimeError(ErrorCode.U_S0500, "SchemaManager");
        }

        if (object != null) {
            database.getGranteeManager().removeDbObject(name);
            removeReferencingObject(object);
        }

        if (set != null) {
            set.remove(name.name);
        }

        removeReferencedObject(name);
    }

    void renameSchemaObject(HsqlName name, HsqlName newName) {

        if (name.schema != newName.schema) {
            throw Error.error(ErrorCode.X_42505, newName.schema.name);
        }

        checkObjectIsReferenced(name);

        Schema          schema = (Schema) schemaMap.get(name.schema.name);
        SchemaObjectSet set    = getSchemaObjectSet(schema, name.type);

        set.rename(name, newName);
    }

    public String[] getSQLArray() {

        OrderedHashSet resolved   = new OrderedHashSet();
        OrderedHashSet unresolved = new OrderedHashSet();
        HsqlArrayList  list       = new HsqlArrayList();
        Iterator       schemas    = schemaMap.values().iterator();

        schemas = schemaMap.values().iterator();

        while (schemas.hasNext()) {
            Schema schema = (Schema) schemas.next();

            if (database.schemaManager.isSystemSchema(schema.name.name)) {
                continue;
            }

            if (database.schemaManager.isLobsSchema(schema.name.name)) {
                continue;
            }

            list.add(schema.getDefinitionSQL());
            schema.addSimpleObjects(unresolved);
        }

        while (true) {
            Iterator it = unresolved.iterator();

            if (!it.hasNext()) {
                break;
            }

            OrderedHashSet newResolved = new OrderedHashSet();

            SchemaObjectSet.addAllSQL(resolved, unresolved, list, it,
                                      newResolved);
            unresolved.removeAll(newResolved);

            if (newResolved.size() == 0) {
                break;
            }
        }

        schemas = schemaMap.values().iterator();

        while (schemas.hasNext()) {
            Schema schema = (Schema) schemas.next();

            if (isSystemSchema(schema.name.name)) {
                continue;
            }

            if (isLobsSchema(schema.name.name)) {
                continue;
            }

            list.addAll(schema.getSQLArray(resolved, unresolved));
        }

        while (true) {
            Iterator it = unresolved.iterator();

            if (!it.hasNext()) {
                break;
            }

            OrderedHashSet newResolved = new OrderedHashSet();

            SchemaObjectSet.addAllSQL(resolved, unresolved, list, it,
                                      newResolved);
            unresolved.removeAll(newResolved);

            if (newResolved.size() == 0) {
                break;
            }
        }

        schemas = schemaMap.values().iterator();

        while (schemas.hasNext()) {
            Schema schema = (Schema) schemas.next();

            if (database.schemaManager.isSystemSchema(schema.name.name)) {
                continue;
            }

            if (database.schemaManager.isLobsSchema(schema.name.name)) {
                continue;
            }

            String[] t = schema.getTriggerSQL();

            if (t.length > 0) {
                list.add(Schema.getSetSchemaSQL(schema.name));
                list.addAll(t);
            }

            list.addAll(schema.getSequenceRestartSQL());
        }

        schemas = schemaMap.values().iterator();

        while (schemas.hasNext()) {
            Schema schema = (Schema) schemas.next();

            list.addAll(schema.getSequenceRestartSQL());
        }

        if (defaultSchemaHsqlName != null) {
            StringBuffer sb = new StringBuffer();

            sb.append(Tokens.T_SET).append(' ').append(Tokens.T_DATABASE);
            sb.append(' ').append(Tokens.T_DEFAULT).append(' ');
            sb.append(Tokens.T_INITIAL).append(' ').append(Tokens.T_SCHEMA);
            sb.append(' ').append(defaultSchemaHsqlName.statementName);
            list.add(sb.toString());
        }

        String[] array = new String[list.size()];

        list.toArray(array);

        return array;
    }

    public String[] getTablePropsSQL(boolean withHeader) {

        HsqlArrayList tableList = getAllTables();
        HsqlArrayList list      = new HsqlArrayList();

        for (int i = 0; i < tableList.size(); i++) {
            Table t = (Table) tableList.get(i);

            if (t.isText()) {
                String[] ddl = t.getSQLForTextSource(withHeader);

                list.addAll(ddl);
            } else {
                String ddl = t.getSQLForReadOnly();

                if (ddl != null) {
                    list.add(ddl);
                }
            }
        }

        String[] array = new String[list.size()];

        list.toArray(array);

        return array;
    }

    public String[] getIndexRootsSQL() {

        Session       sysSession = database.sessionManager.getSysSession();
        int[][]       rootsArray = getIndexRoots(sysSession);
        HsqlArrayList tableList  = getAllTables();
        HsqlArrayList list       = new HsqlArrayList();

        for (int i = 0; i < rootsArray.length; i++) {
            Table t = (Table) tableList.get(i);

            if (rootsArray[i] != null && rootsArray[i].length > 0
                    && rootsArray[i][0] != -1) {
                String ddl =
                    ((Table) tableList.get(i)).getIndexRootsSQL(rootsArray[i]);

                list.add(ddl);
            }
        }

        String[] array = new String[list.size()];

        list.toArray(array);

        return array;
    }

    int[][] tempIndexRoots;

    public void setTempIndexRoots(int[][] roots) {
        tempIndexRoots = roots;
    }

    public int[][] getIndexRoots(Session session) {

        if (tempIndexRoots != null) {
            int[][] roots = tempIndexRoots;

            tempIndexRoots = null;

            return roots;
        }

        HsqlArrayList allTables = getAllTables();
        HsqlArrayList list      = new HsqlArrayList();

        for (int i = 0, size = allTables.size(); i < size; i++) {
            Table t = (Table) allTables.get(i);

            if (t.getTableType() == TableBase.CACHED_TABLE) {
                int[] roots = t.getIndexRootsArray();

                list.add(roots);
            } else {
                list.add(null);
            }
        }

        int[][] array = new int[list.size()][];

        list.toArray(array);

        return array;
    }

    /**
     * called after the completion of defrag
     */
    public void setIndexRoots(int[][] roots) {

        HsqlArrayList allTables = database.schemaManager.getAllTables();

        for (int i = 0, size = allTables.size(); i < size; i++) {
            Table t = (Table) allTables.get(i);

            if (t.getTableType() == TableBase.CACHED_TABLE) {
                int[] rootsArray = roots[i];

                if (roots != null) {
                    t.setIndexRoots(rootsArray);
                }
            }
        }
    }

    public void setDefaultTableType(int type) {
        defaultTableType = type;
    }

    public int getDefaultTableType() {
        return defaultTableType;
    }
}
