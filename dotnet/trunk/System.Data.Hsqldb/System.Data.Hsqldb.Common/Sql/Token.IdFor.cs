namespace System.Data.Hsqldb.Common.Sql
{
    public partial class Token
    {
        /// <summary>
        /// Numeric identifiers for the SQL tokens declared in <see cref="ValueFor"/>.
        /// </summary>
        public static class IdFor
        {
            #region Constants

            #region SQL 200n Reserved Word Token Identifiers
            /// <value>Numeric identifier of <see cref="ValueFor.ADD"/>.</value>
            public const int ADD = 1;
            /// <value>Numeric identifier of <see cref="ValueFor.ALL"/>.</value>
            public const int ALL = 2;
            /// <value>Numeric identifier of <see cref="ValueFor.ALLOCATE"/>.</value>
            public const int ALLOCATE = 3;
            /// <value>Numeric identifier of <see cref="ValueFor.ALTER"/>.</value>
            public const int ALTER = 4;
            /// <value>Numeric identifier of <see cref="ValueFor.AND"/>.</value>
            public const int AND = 5;
            /// <value>Numeric identifier of <see cref="ValueFor.ANY"/>.</value>
            public const int ANY = 6;
            /// <value>Numeric identifier of <see cref="ValueFor.ARE"/>.</value>
            public const int ARE = 7;
            /// <value>Numeric identifier of <see cref="ValueFor.ARRAY"/>.</value>
            public const int ARRAY = 8;
            /// <value>Numeric identifier of <see cref="ValueFor.AS"/>.</value>
            public const int AS = 9;
            /// <value>Numeric identifier of <see cref="ValueFor.ASENSITIVE"/>.</value>
            public const int ASENSITIVE = 10;
            /// <value>Numeric identifier of <see cref="ValueFor.ASYMMETRIC"/>.</value>
            public const int ASYMMETRIC = 11;
            /// <value>Numeric identifier of <see cref="ValueFor.AT"/>.</value>
            public const int AT = 12;
            /// <value>Numeric identifier of <see cref="ValueFor.ATOMIC"/>.</value>
            public const int ATOMIC = 13;
            /// <value>Numeric identifier of <see cref="ValueFor.AUTHORIZATION"/>.</value>
            public const int AUTHORIZATION = 14;
            /// <value>Numeric identifier of <see cref="ValueFor.BEGIN"/>.</value>
            public const int BEGIN = 15;
            /// <value>Numeric identifier of <see cref="ValueFor.BETWEEN"/>.</value>
            public const int BETWEEN = 16;
            /// <value>Numeric identifier of <see cref="ValueFor.BIGINT"/>.</value>
            public const int BIGINT = 17;
            /// <value>Numeric identifier of <see cref="ValueFor.BINARY"/>.</value>
            public const int BINARY = 18;
            /// <value>Numeric identifier of <see cref="ValueFor.BLOB"/>.</value>
            public const int BLOB = 19;
            /// <value>Numeric identifier of <see cref="ValueFor.BOOLEAN"/>.</value>
            public const int BOOLEAN = 20;
            /// <value>Numeric identifier of <see cref="ValueFor.BOTH"/>.</value>
            public const int BOTH = 21;
            /// <value>Numeric identifier of <see cref="ValueFor.BY"/>.</value>
            public const int BY = 22;
            /// <value>Numeric identifier of <see cref="ValueFor.CALL"/>.</value>
            public const int CALL = 23;
            /// <value>Numeric identifier of <see cref="ValueFor.CALLED"/>.</value>
            public const int CALLED = 24;
            /// <value>Numeric identifier of <see cref="ValueFor.CASCADED"/>.</value>
            public const int CASCADED = 25;
            /// <value>Numeric identifier of <see cref="ValueFor.CASE"/>.</value>
            public const int CASE = 26;
            /// <value>Numeric identifier of <see cref="ValueFor.CAST"/>.</value>
            public const int CAST = 27;
            /// <value>Numeric identifier of <see cref="ValueFor.CHAR"/>.</value>
            public const int CHAR = 28;
            /// <value>Numeric identifier of <see cref="ValueFor.CHARACTER"/>.</value>
            public const int CHARACTER = 29;
            /// <value>Numeric identifier of <see cref="ValueFor.CHECK"/>.</value>
            public const int CHECK = 30;
            /// <value>Numeric identifier of <see cref="ValueFor.CLOB"/>.</value>
            public const int CLOB = 31;
            /// <value>Numeric identifier of <see cref="ValueFor.CLOSE"/>.</value>
            public const int CLOSE = 32;
            /// <value>Numeric identifier of <see cref="ValueFor.COLLATE"/>.</value>
            public const int COLLATE = 33;
            /// <value>Numeric identifier of <see cref="ValueFor.COLUMN"/>.</value>
            public const int COLUMN = 34;
            /// <value>Numeric identifier of <see cref="ValueFor.COMMIT"/>.</value>
            public const int COMMIT = 35;
            /// <value>Numeric identifier of <see cref="ValueFor.CONDITION"/>.</value>
            public const int CONDITION = 36;
            /// <value>Numeric identifier of <see cref="ValueFor.CONNECT"/>.</value>
            public const int CONNECT = 37;
            /// <value>Numeric identifier of <see cref="ValueFor.CONSTRAINT"/>.</value>
            public const int CONSTRAINT = 38;
            /// <value>Numeric identifier of <see cref="ValueFor.CONTINUE"/>.</value>
            public const int CONTINUE = 39;
            /// <value>Numeric identifier of <see cref="ValueFor.CORRESPONDING"/>.</value>
            public const int CORRESPONDING = 40;
            /// <value>Numeric identifier of <see cref="ValueFor.CREATE"/>.</value>
            public const int CREATE = 41;
            /// <value>Numeric identifier of <see cref="ValueFor.CROSS"/>.</value>
            public const int CROSS = 42;
            /// <value>Numeric identifier of <see cref="ValueFor.CUBE"/>.</value>
            public const int CUBE = 43;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT"/>.</value>
            public const int CURRENT = 44;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_DATE"/>.</value>
            public const int CURRENT_DATE = 45;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_DEFAULT_TRANSFORM_GROUP"/>.</value>
            public const int CURRENT_DEFAULT_TRANSFORM_GROUP = 46;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_PATH"/>.</value>
            public const int CURRENT_PATH = 47;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_ROLE"/>.</value>
            public const int CURRENT_ROLE = 48;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_TIME"/>.</value>
            public const int CURRENT_TIME = 49;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_TIMESTAMP"/>.</value>
            public const int CURRENT_TIMESTAMP = 50;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_TRANSFORM_GROUP_FOR_TYPE"/>.</value>
            public const int CURRENT_TRANSFORM_GROUP_FOR_TYPE = 51;
            /// <value>Numeric identifier of <see cref="ValueFor.CURRENT_USER"/>.</value>
            public const int CURRENT_USER = 52;
            /// <value>Numeric identifier of <see cref="ValueFor.CURSOR"/>.</value>
            public const int CURSOR = 53;
            /// <value>Numeric identifier of <see cref="ValueFor.CYCLE"/>.</value>
            public const int CYCLE = 54;
            /// <value>Numeric identifier of <see cref="ValueFor.DATE"/>.</value>
            public const int DATE = 55;
            /// <value>Numeric identifier of <see cref="ValueFor.DAY"/>.</value>
            public const int DAY = 56;
            /// <value>Numeric identifier of <see cref="ValueFor.DEALLOCATE"/>.</value>
            public const int DEALLOCATE = 57;
            /// <value>Numeric identifier of <see cref="ValueFor.DEC"/>.</value>
            public const int DEC = 58;
            /// <value>Numeric identifier of <see cref="ValueFor.DECIMAL"/>.</value>
            public const int DECIMAL = 59;
            /// <value>Numeric identifier of <see cref="ValueFor.DECLARE"/>.</value>
            public const int DECLARE = 60;
            /// <value>Numeric identifier of <see cref="ValueFor.DEFAULT"/>.</value>
            public const int DEFAULT = 61;
            /// <value>Numeric identifier of <see cref="ValueFor.DELETE"/>.</value>
            public const int DELETE = 62;
            /// <value>Numeric identifier of <see cref="ValueFor.DEREF"/>.</value>
            public const int DEREF = 63;
            /// <value>Numeric identifier of <see cref="ValueFor.DESCRIBE"/>.</value>
            public const int DESCRIBE = 64;
            /// <value>Numeric identifier of <see cref="ValueFor.DETERMINISTIC"/>.</value>
            public const int DETERMINISTIC = 65;
            /// <value>Numeric identifier of <see cref="ValueFor.DISCONNECT"/>.</value>
            public const int DISCONNECT = 66;
            /// <value>Numeric identifier of <see cref="ValueFor.DISTINCT"/>.</value>
            public const int DISTINCT = 67;
            /// <value>Numeric identifier of <see cref="ValueFor.DO"/>.</value>
            public const int DO = 68;
            /// <value>Numeric identifier of <see cref="ValueFor.DOUBLE"/>.</value>
            public const int DOUBLE = 69;
            /// <value>Numeric identifier of <see cref="ValueFor.DROP"/>.</value>
            public const int DROP = 70;
            /// <value>Numeric identifier of <see cref="ValueFor.DYNAMIC"/>.</value>
            public const int DYNAMIC = 71;
            /// <value>Numeric identifier of <see cref="ValueFor.EACH"/>.</value>
            public const int EACH = 72;
            /// <value>Numeric identifier of <see cref="ValueFor.ELEMENT"/>.</value>
            public const int ELEMENT = 73;
            /// <value>Numeric identifier of <see cref="ValueFor.ELSE"/>.</value>
            public const int ELSE = 74;
            /// <value>Numeric identifier of <see cref="ValueFor.ELSEIF"/>.</value>
            public const int ELSEIF = 75;
            /// <value>Numeric identifier of <see cref="ValueFor.END"/>.</value>
            public const int END = 76;
            /// <value>Numeric identifier of <see cref="ValueFor.ESCAPE"/>.</value>
            public const int ESCAPE = 77;
            /// <value>Numeric identifier of <see cref="ValueFor.EXCEPT"/>.</value>
            public const int EXCEPT = 78;
            /// <value>Numeric identifier of <see cref="ValueFor.EXEC"/>.</value>
            public const int EXEC = 79;
            /// <value>Numeric identifier of <see cref="ValueFor.EXECUTE"/>.</value>
            public const int EXECUTE = 80;
            /// <value>Numeric identifier of <see cref="ValueFor.EXISTS"/>.</value>
            public const int EXISTS = 81;
            /// <value>Numeric identifier of <see cref="ValueFor.EXIT"/>.</value>
            public const int EXIT = 82;
            /// <value>Numeric identifier of <see cref="ValueFor.EXTERNAL"/>.</value>
            public const int EXTERNAL = 83;
            /// <value>Numeric identifier of <see cref="ValueFor.FALSE"/>.</value>
            public const int FALSE = 84;
            /// <value>Numeric identifier of <see cref="ValueFor.FETCH"/>.</value>
            public const int FETCH = 85;
            /// <value>Numeric identifier of <see cref="ValueFor.FILTER"/>.</value>
            public const int FILTER = 86;
            /// <value>Numeric identifier of <see cref="ValueFor.FLOAT"/>.</value>
            public const int FLOAT = 87;
            /// <value>Numeric identifier of <see cref="ValueFor.FOR"/>.</value>
            public const int FOR = 88;
            /// <value>Numeric identifier of <see cref="ValueFor.FOREIGN"/>.</value>
            public const int FOREIGN = 89;
            /// <value>Numeric identifier of <see cref="ValueFor.FREE"/>.</value>
            public const int FREE = 90;
            /// <value>Numeric identifier of <see cref="ValueFor.FROM"/>.</value>
            public const int FROM = 91;
            /// <value>Numeric identifier of <see cref="ValueFor.FULL"/>.</value>
            public const int FULL = 92;
            /// <value>Numeric identifier of <see cref="ValueFor.FUNCTION"/>.</value>
            public const int FUNCTION = 93;
            /// <value>Numeric identifier of <see cref="ValueFor.GET"/>.</value>
            public const int GET = 94;
            /// <value>Numeric identifier of <see cref="ValueFor.GLOBAL"/>.</value>
            public const int GLOBAL = 95;
            /// <value>Numeric identifier of <see cref="ValueFor.GRANT"/>.</value>
            public const int GRANT = 96;
            /// <value>Numeric identifier of <see cref="ValueFor.GROUP"/>.</value>
            public const int GROUP = 97;
            /// <value>Numeric identifier of <see cref="ValueFor.GROUPING"/>.</value>
            public const int GROUPING = 98;
            /// <value>Numeric identifier of <see cref="ValueFor.HANDLER"/>.</value>
            public const int HANDLER = 99;
            /// <value>Numeric identifier of <see cref="ValueFor.HAVING"/>.</value>
            public const int HAVING = 100;
            /// <value>Numeric identifier of <see cref="ValueFor.HOLD"/>.</value>
            public const int HOLD = 101;
            /// <value>Numeric identifier of <see cref="ValueFor.HOUR"/>.</value>
            public const int HOUR = 102;
            /// <value>Numeric identifier of <see cref="ValueFor.IDENTITY"/>.</value>
            public const int IDENTITY = 103;
            /// <value>Numeric identifier of <see cref="ValueFor.IF"/>.</value>
            public const int IF = 104;
            /// <value>Numeric identifier of <see cref="ValueFor.IMMEDIATE"/>.</value>
            public const int IMMEDIATE = 105;
            /// <value>Numeric identifier of <see cref="ValueFor.IN"/>.</value>
            public const int IN = 106;
            /// <value>Numeric identifier of <see cref="ValueFor.INDICATOR"/>.</value>
            public const int INDICATOR = 107;
            /// <value>Numeric identifier of <see cref="ValueFor.INNER"/>.</value>
            public const int INNER = 108;
            /// <value>Numeric identifier of <see cref="ValueFor.INOUT"/>.</value>
            public const int INOUT = 109;
            /// <value>Numeric identifier of <see cref="ValueFor.INPUT"/>.</value>
            public const int INPUT = 110;
            /// <value>Numeric identifier of <see cref="ValueFor.INSENSITIVE"/>.</value>
            public const int INSENSITIVE = 111;
            /// <value>Numeric identifier of <see cref="ValueFor.INSERT"/>.</value>
            public const int INSERT = 112;
            /// <value>Numeric identifier of <see cref="ValueFor.INT"/>.</value>
            public const int INT = 113;
            /// <value>Numeric identifier of <see cref="ValueFor.INTEGER"/>.</value>
            public const int INTEGER = 114;
            /// <value>Numeric identifier of <see cref="ValueFor.INTERSECT"/>.</value>
            public const int INTERSECT = 115;
            /// <value>Numeric identifier of <see cref="ValueFor.INTERVAL"/>.</value>
            public const int INTERVAL = 116;
            /// <value>Numeric identifier of <see cref="ValueFor.INTO"/>.</value>
            public const int INTO = 117;
            /// <value>Numeric identifier of <see cref="ValueFor.IS"/>.</value>
            public const int IS = 118;
            /// <value>Numeric identifier of <see cref="ValueFor.ITERATE"/>.</value>
            public const int ITERATE = 119;
            /// <value>Numeric identifier of <see cref="ValueFor.JOIN"/>.</value>
            public const int JOIN = 120;
            /// <value>Numeric identifier of <see cref="ValueFor.LANGUAGE"/>.</value>
            public const int LANGUAGE = 121;
            /// <value>Numeric identifier of <see cref="ValueFor.LARGE"/>.</value>
            public const int LARGE = 122;
            /// <value>Numeric identifier of <see cref="ValueFor.LATERAL"/>.</value>
            public const int LATERAL = 123;
            /// <value>Numeric identifier of <see cref="ValueFor.LEADING"/>.</value>
            public const int LEADING = 124;
            /// <value>Numeric identifier of <see cref="ValueFor.LEAVE"/>.</value>
            public const int LEAVE = 125;
            /// <value>Numeric identifier of <see cref="ValueFor.LEFT"/>.</value>
            public const int LEFT = 126;
            /// <value>Numeric identifier of <see cref="ValueFor.LIKE"/>.</value>
            public const int LIKE = 127;
            /// <value>Numeric identifier of <see cref="ValueFor.LOCAL"/>.</value>
            public const int LOCAL = 128;
            /// <value>Numeric identifier of <see cref="ValueFor.LOCALTIME"/>.</value>
            public const int LOCALTIME = 129;
            /// <value>Numeric identifier of <see cref="ValueFor.LOCALTIMESTAMP"/>.</value>
            public const int LOCALTIMESTAMP = 130;
            /// <value>Numeric identifier of <see cref="ValueFor.LOOP"/>.</value>
            public const int LOOP = 131;
            /// <value>Numeric identifier of <see cref="ValueFor.MATCH"/>.</value>
            public const int MATCH = 132;
            /// <value>Numeric identifier of <see cref="ValueFor.MEMBER"/>.</value>
            public const int MEMBER = 133;
            /// <value>Numeric identifier of <see cref="ValueFor.MERGE"/>.</value>
            public const int MERGE = 134;
            /// <value>Numeric identifier of <see cref="ValueFor.METHOD"/>.</value>
            public const int METHOD = 135;
            /// <value>Numeric identifier of <see cref="ValueFor.MINUTE"/>.</value>
            public const int MINUTE = 136;
            /// <value>Numeric identifier of <see cref="ValueFor.MODIFIES"/>.</value>
            public const int MODIFIES = 137;
            /// <value>Numeric identifier of <see cref="ValueFor.MODULE"/>.</value>
            public const int MODULE = 138;
            /// <value>Numeric identifier of <see cref="ValueFor.MONTH"/>.</value>
            public const int MONTH = 139;
            /// <value>Numeric identifier of <see cref="ValueFor.MULTISET"/>.</value>
            public const int MULTISET = 140;
            /// <value>Numeric identifier of <see cref="ValueFor.NATIONAL"/>.</value>
            public const int NATIONAL = 141;
            /// <value>Numeric identifier of <see cref="ValueFor.NATURAL"/>.</value>
            public const int NATURAL = 142;
            /// <value>Numeric identifier of <see cref="ValueFor.NCHAR"/>.</value>
            public const int NCHAR = 143;
            /// <value>Numeric identifier of <see cref="ValueFor.NCLOB"/>.</value>
            public const int NCLOB = 144;
            /// <value>Numeric identifier of <see cref="ValueFor.NEW"/>.</value>
            public const int NEW = 145;
            /// <value>Numeric identifier of <see cref="ValueFor.NO"/>.</value>
            public const int NO = 146;
            /// <value>Numeric identifier of <see cref="ValueFor.NONE"/>.</value>
            public const int NONE = 147;
            /// <value>Numeric identifier of <see cref="ValueFor.NOT"/>.</value>
            public const int NOT = 148;
            /// <value>Numeric identifier of <see cref="ValueFor.NULL"/>.</value>
            public const int NULL = 149;
            /// <value>Numeric identifier of <see cref="ValueFor.NUMERIC"/>.</value>
            public const int NUMERIC = 150;
            /// <value>Numeric identifier of <see cref="ValueFor.OF"/>.</value>
            public const int OF = 151;
            /// <value>Numeric identifier of <see cref="ValueFor.OLD"/>.</value>
            public const int OLD = 152;
            /// <value>Numeric identifier of <see cref="ValueFor.ON"/>.</value>
            public const int ON = 153;
            /// <value>Numeric identifier of <see cref="ValueFor.ONLY"/>.</value>
            public const int ONLY = 154;
            /// <value>Numeric identifier of <see cref="ValueFor.OPEN"/>.</value>
            public const int OPEN = 155;
            /// <value>Numeric identifier of <see cref="ValueFor.OR"/>.</value>
            public const int OR = 156;
            /// <value>Numeric identifier of <see cref="ValueFor.ORDER"/>.</value>
            public const int ORDER = 157;
            /// <value>Numeric identifier of <see cref="ValueFor.OUT"/>.</value>
            public const int OUT = 158;
            /// <value>Numeric identifier of <see cref="ValueFor.OUTER"/>.</value>
            public const int OUTER = 159;
            /// <value>Numeric identifier of <see cref="ValueFor.OUTPUT"/>.</value>
            public const int OUTPUT = 160;
            /// <value>Numeric identifier of <see cref="ValueFor.OVER"/>.</value>
            public const int OVER = 161;
            /// <value>Numeric identifier of <see cref="ValueFor.OVERLAPS"/>.</value>
            public const int OVERLAPS = 162;
            /// <value>Numeric identifier of <see cref="ValueFor.PARAMETER"/>.</value>
            public const int PARAMETER = 163;
            /// <value>Numeric identifier of <see cref="ValueFor.PARTITION"/>.</value>
            public const int PARTITION = 164;
            /// <value>Numeric identifier of <see cref="ValueFor.PRECISION"/>.</value>
            public const int PRECISION = 165;
            /// <value>Numeric identifier of <see cref="ValueFor.PREPARE"/>.</value>
            public const int PREPARE = 166;
            /// <value>Numeric identifier of <see cref="ValueFor.PRIMARY"/>.</value>
            public const int PRIMARY = 167;
            /// <value>Numeric identifier of <see cref="ValueFor.PROCEDURE"/>.</value>
            public const int PROCEDURE = 168;
            /// <value>Numeric identifier of <see cref="ValueFor.RANGE"/>.</value>
            public const int RANGE = 169;
            /// <value>Numeric identifier of <see cref="ValueFor.READS"/>.</value>
            public const int READS = 170;
            /// <value>Numeric identifier of <see cref="ValueFor.REAL"/>.</value>
            public const int REAL = 171;
            /// <value>Numeric identifier of <see cref="ValueFor.RECURSIVE"/>.</value>
            public const int RECURSIVE = 172;
            /// <value>Numeric identifier of <see cref="ValueFor.REF"/>.</value>
            public const int REF = 173;
            /// <value>Numeric identifier of <see cref="ValueFor.REFERENCES"/>.</value>
            public const int REFERENCES = 174;
            /// <value>Numeric identifier of <see cref="ValueFor.REFERENCING"/>.</value>
            public const int REFERENCING = 175;
            /// <value>Numeric identifier of <see cref="ValueFor.RELEASE"/>.</value>
            public const int RELEASE = 176;
            /// <value>Numeric identifier of <see cref="ValueFor.REPEAT"/>.</value>
            public const int REPEAT = 177;
            /// <value>Numeric identifier of <see cref="ValueFor.RESIGNAL"/>.</value>
            public const int RESIGNAL = 178;
            /// <value>Numeric identifier of <see cref="ValueFor.RESULT"/>.</value>
            public const int RESULT = 179;
            /// <value>Numeric identifier of <see cref="ValueFor.RETURN"/>.</value>
            public const int RETURN = 180;
            /// <value>Numeric identifier of <see cref="ValueFor.RETURNS"/>.</value>
            public const int RETURNS = 181;
            /// <value>Numeric identifier of <see cref="ValueFor.REVOKE"/>.</value>
            public const int REVOKE = 182;
            /// <value>Numeric identifier of <see cref="ValueFor.RIGHT"/>.</value>
            public const int RIGHT = 183;
            /// <value>Numeric identifier of <see cref="ValueFor.ROLLBACK"/>.</value>
            public const int ROLLBACK = 184;
            /// <value>Numeric identifier of <see cref="ValueFor.ROLLUP"/>.</value>
            public const int ROLLUP = 185;
            /// <value>Numeric identifier of <see cref="ValueFor.ROW"/>.</value>
            public const int ROW = 186;
            /// <value>Numeric identifier of <see cref="ValueFor.ROWS"/>.</value>
            public const int ROWS = 187;
            /// <value>Numeric identifier of <see cref="ValueFor.SAVEPOINT"/>.</value>
            public const int SAVEPOINT = 188;
            /// <value>Numeric identifier of <see cref="ValueFor.SCOPE"/>.</value>
            public const int SCOPE = 189;
            /// <value>Numeric identifier of <see cref="ValueFor.SCROLL"/>.</value>
            public const int SCROLL = 190;
            /// <value>Numeric identifier of <see cref="ValueFor.SEARCH"/>.</value>
            public const int SEARCH = 191;
            /// <value>Numeric identifier of <see cref="ValueFor.SECOND"/>.</value>
            public const int SECOND = 192;
            /// <value>Numeric identifier of <see cref="ValueFor.SELECT"/>.</value>
            public const int SELECT = 193;
            /// <value>Numeric identifier of <see cref="ValueFor.SENSITIVE"/>.</value>
            public const int SENSITIVE = 194;
            /// <value>Numeric identifier of <see cref="ValueFor.SESSION_USER"/>.</value>
            public const int SESSION_USER = 195;
            /// <value>Numeric identifier of <see cref="ValueFor.SET"/>.</value>
            public const int SET = 196;
            /// <value>Numeric identifier of <see cref="ValueFor.SIGNAL"/>.</value>
            public const int SIGNAL = 197;
            /// <value>Numeric identifier of <see cref="ValueFor.SIMILAR"/>.</value>
            public const int SIMILAR = 198;
            /// <value>Numeric identifier of <see cref="ValueFor.SMALLINT"/>.</value>
            public const int SMALLINT = 199;
            /// <value>Numeric identifier of <see cref="ValueFor.SOME"/>.</value>
            public const int SOME = 200;
            /// <value>Numeric identifier of <see cref="ValueFor.SPECIFIC"/>.</value>
            public const int SPECIFIC = 201;
            /// <value>Numeric identifier of <see cref="ValueFor.SPECIFICTYPE"/>.</value>
            public const int SPECIFICTYPE = 202;
            /// <value>Numeric identifier of <see cref="ValueFor.SQL"/>.</value>
            public const int SQL = 203;
            /// <value>Numeric identifier of <see cref="ValueFor.SQLEXCEPTION"/>.</value>
            public const int SQLEXCEPTION = 204;
            /// <value>Numeric identifier of <see cref="ValueFor.SQLSTATE"/>.</value>
            public const int SQLSTATE = 205;
            /// <value>Numeric identifier of <see cref="ValueFor.SQLWARNING"/>.</value>
            public const int SQLWARNING = 206;
            /// <value>Numeric identifier of <see cref="ValueFor.START"/>.</value>
            public const int START = 207;
            /// <value>Numeric identifier of <see cref="ValueFor.STATIC"/>.</value>
            public const int STATIC = 208;
            /// <value>Numeric identifier of <see cref="ValueFor.SUBMULTISET"/>.</value>
            public const int SUBMULTISET = 209;
            /// <value>Numeric identifier of <see cref="ValueFor.SYMMETRIC"/>.</value>
            public const int SYMMETRIC = 210;
            /// <value>Numeric identifier of <see cref="ValueFor.SYSTEM"/>.</value>
            public const int SYSTEM = 211;
            /// <value>Numeric identifier of <see cref="ValueFor.SYSTEM_USER"/>.</value>
            public const int SYSTEM_USER = 212;
            /// <value>Numeric identifier of <see cref="ValueFor.TABLE"/>.</value>
            public const int TABLE = 213;
            /// <value>Numeric identifier of <see cref="ValueFor.TABLESAMPLE"/>.</value>
            public const int TABLESAMPLE = 214;
            /// <value>Numeric identifier of <see cref="ValueFor.THEN"/>.</value>
            public const int THEN = 215;
            /// <value>Numeric identifier of <see cref="ValueFor.TIME"/>.</value>
            public const int TIME = 216;
            /// <value>Numeric identifier of <see cref="ValueFor.TIMESTAMP"/>.</value>
            public const int TIMESTAMP = 217;
            /// <value>Numeric identifier of <see cref="ValueFor.TIMEZONE_HOUR"/>.</value>
            public const int TIMEZONE_HOUR = 218;
            /// <value>Numeric identifier of <see cref="ValueFor.TIMEZONE_MINUTE"/>.</value>
            public const int TIMEZONE_MINUTE = 219;
            /// <value>Numeric identifier of <see cref="ValueFor.TO"/>.</value>
            public const int TO = 220;
            /// <value>Numeric identifier of <see cref="ValueFor.TRAILING"/>.</value>
            public const int TRAILING = 221;
            /// <value>Numeric identifier of <see cref="ValueFor.TRANSLATION"/>.</value>
            public const int TRANSLATION = 222;
            /// <value>Numeric identifier of <see cref="ValueFor.TREAT"/>.</value>
            public const int TREAT = 223;
            /// <value>Numeric identifier of <see cref="ValueFor.TRIGGER"/>.</value>
            public const int TRIGGER = 224;
            /// <value>Numeric identifier of <see cref="ValueFor.TRUE"/>.</value>
            public const int TRUE = 225;
            /// <value>Numeric identifier of <see cref="ValueFor.UNDO"/>.</value>
            public const int UNDO = 226;
            /// <value>Numeric identifier of <see cref="ValueFor.UNION"/>.</value>
            public const int UNION = 227;
            /// <value>Numeric identifier of <see cref="ValueFor.UNIQUE"/>.</value>
            public const int UNIQUE = 228;
            /// <value>Numeric identifier of <see cref="ValueFor.UNKNOWN"/>.</value>
            public const int UNKNOWN = 229;
            /// <value>Numeric identifier of <see cref="ValueFor.UNNEST"/>.</value>
            public const int UNNEST = 220;
            /// <value>Numeric identifier of <see cref="ValueFor.UNTIL"/>.</value>
            public const int UNTIL = 221;
            /// <value>Numeric identifier of <see cref="ValueFor.UPDATE"/>.</value>
            public const int UPDATE = 222;
            /// <value>Numeric identifier of <see cref="ValueFor.USER"/>.</value>
            public const int USER = 223;
            /// <value>Numeric identifier of <see cref="ValueFor.USING"/>.</value>
            public const int USING = 224;
            /// <value>Numeric identifier of <see cref="ValueFor.VALUE"/>.</value>
            public const int VALUE = 225;
            /// <value>Numeric identifier of <see cref="ValueFor.VALUES"/>.</value>
            public const int VALUES = 226;
            /// <value>Numeric identifier of <see cref="ValueFor.VARCHAR"/>.</value>
            public const int VARCHAR = 227;
            /// <value>Numeric identifier of <see cref="ValueFor.VARYING"/>.</value>
            public const int VARYING = 228;
            /// <value>Numeric identifier of <see cref="ValueFor.WHEN"/>.</value>
            public const int WHEN = 229;
            /// <value>Numeric identifier of <see cref="ValueFor.WHENEVER"/>.</value>
            public const int WHENEVER = 230;
            /// <value>Numeric identifier of <see cref="ValueFor.WHERE"/>.</value>
            public const int WHERE = 231;
            /// <value>Numeric identifier of <see cref="ValueFor.WHILE"/>.</value>
            public const int WHILE = 232;
            /// <value>Numeric identifier of <see cref="ValueFor.WINDOW"/>.</value>
            public const int WINDOW = 233;
            /// <value>Numeric identifier of <see cref="ValueFor.WITH"/>.</value>
            public const int WITH = 234;
            /// <value>Numeric identifier of <see cref="ValueFor.WITHIN"/>.</value>
            public const int WITHIN = 235;
            /// <value>Numeric identifier of <see cref="ValueFor.WITHOUT"/>.</value>
            public const int WITHOUT = 236;
            /// <value>Numeric identifier of <see cref="ValueFor.YEAR"/>.</value>
            public const int YEAR = 237;
            #endregion

            #region Token Identifiers Used In Conditional Constructs
            /// <value>Numeric identifier corresponding to no known token.</value>
            public const int UNKNOWNTOKEN = -1;
            /// <value>Numeric identifier of <see cref="ValueFor.ALIAS"/>.</value>
            public const int ALIAS = 300;
            /// <value>Numeric identifier of <see cref="ValueFor.AUTOCOMMIT"/>.</value>
            public const int AUTOCOMMIT = 301;
            /// <value>Numeric identifier of <see cref="ValueFor.CACHED"/>.</value>
            public const int CACHED = 302;
            /// <value>Numeric identifier of <see cref="ValueFor.CHECKPOINT"/>.</value>
            public const int CHECKPOINT = 303;
            /// <value>Numeric identifier of <see cref="ValueFor.EXPLAIN"/>.</value>
            public const int EXPLAIN = 304;
            /// <value>Numeric identifier of <see cref="ValueFor.IGNORECASE"/>.</value>
            public const int IGNORECASE = 305;
            /// <value>Numeric identifier of <see cref="ValueFor.INDEX"/>.</value>
            public const int INDEX = 306;
            /// <value>Numeric identifier of <see cref="ValueFor.LOGSIZE"/>.</value>
            public const int LOGSIZE = 307;
            /// <value>Numeric identifier of <see cref="ValueFor.MATCHED"/>.</value>
            public const int MATCHED = 308;
            /// <value>Numeric identifier of <see cref="ValueFor.MAXROWS"/>.</value>
            public const int MAXROWS = 309;
            /// <value>Numeric identifier of <see cref="ValueFor.MEMORY"/>.</value>
            public const int MEMORY = 310;
            /// <value>Numeric identifier of <see cref="ValueFor.MINUS"/>.</value>
            public const int MINUS = 311;
            /// <value>Numeric identifier of <see cref="ValueFor.NEXT"/>.</value>
            public const int NEXT = 312;
            /// <value>Numeric identifier of <see cref="ValueFor.OPENBRACKET"/>.</value>
            public const int OPENBRACKET = 313;
            /// <value>Numeric identifier of <see cref="ValueFor.PASSWORD"/>.</value>
            public const int PASSWORD = 314;
            /// <value>Numeric identifier of <see cref="ValueFor.PLAN"/>.</value>
            public const int PLAN = 315;
            /// <value>Numeric identifier of <see cref="ValueFor.PROPERTY"/>.</value>
            public const int PROPERTY = 316;
            /// <value>Numeric identifier of <see cref="ValueFor.READONLY"/>.</value>
            public const int READONLY = 317;
            /// <value>Numeric identifier of <see cref="ValueFor.REFERENTIAL_INTEGRITY"/>.</value>
            public const int REFERENTIAL_INTEGRITY = 318;
            /// <value>Numeric identifier of <see cref="ValueFor.RENAME"/>.</value>
            public const int RENAME = 319;
            /// <value>Numeric identifier of <see cref="ValueFor.RESTART"/>.</value>
            public const int RESTART = 320;
            /// <value>Numeric identifier of <see cref="ValueFor.SCRIPT"/>.</value>
            public const int SCRIPT = 321;
            /// <value>Numeric identifier of <see cref="ValueFor.SCRIPTFORMAT"/>.</value>
            public const int SCRIPTFORMAT = 322;
            /// <value>Numeric identifier of <see cref="ValueFor.SEMICOLON"/>.</value>
            public const int SEMICOLON = 323;
            /// <value>Numeric identifier of <see cref="ValueFor.SEQUENCE"/>.</value>
            public const int SEQUENCE = 324;
            /// <value>Numeric identifier of <see cref="ValueFor.SHUTDOWN"/>.</value>
            public const int SHUTDOWN = 325;
            /// <value>Numeric identifier of <see cref="ValueFor.SOURCE"/>.</value>
            public const int SOURCE = 326;
            /// <value>Numeric identifier of <see cref="ValueFor.TEMP"/>.</value>
            public const int TEMP = 327;
            /// <value>Numeric identifier of <see cref="ValueFor.TEXT"/>.</value>
            public const int TEXT = 328;
            /// <value>Numeric identifier of <see cref="ValueFor.VIEW"/>.</value>
            public const int VIEW = 329;
            /// <value>Numeric identifier of <see cref="ValueFor.WRITE_DELAY"/>.</value>
            public const int WRITE_DELAY = 330;
            #endregion

            #region Other Token Identifiers
            /// <value>Numeric identifier of <see cref="ValueFor.VAR_POP"/>.</value>
            public const int VAR_POP = 330;
            /// <value>Numeric identifier of <see cref="ValueFor.VAR_SAMP"/>.</value>
            public const int VAR_SAMP = 331;
            /// <value>Numeric identifier of <see cref="ValueFor.STDDEV_POP"/>.</value>
            public const int STDDEV_POP = 332;
            /// <value>Numeric identifier of <see cref="ValueFor.STDDEV_SAMP"/>.</value>
            public const int STDDEV_SAMP = 333;
            /// <value>Numeric identifier of <see cref="ValueFor.DEFRAG"/>.</value>
            public const int DEFRAG = 334;
            /// <value>Numeric identifier of <see cref="ValueFor.INCREMENT"/>.</value>
            public const int INCREMENT = 335;
            ///// <value>Numeric identifier of <see cref="ValueFor.TOCHAR"/>.</value>
            //public const int TOCHAR = 336;
            /// <value>Numeric identifier of <see cref="ValueFor.DATABASE"/>.</value>
            public const int DATABASE = 337;
            /// <value>Numeric identifier of <see cref="ValueFor.SCHEMA"/>.</value>
            public const int SCHEMA = 338;
            /// <value>Numeric identifier of <see cref="ValueFor.ROLE"/>.</value>
            public const int ROLE = 339;
            /// <value>Numeric identifier of <see cref="ValueFor.DAYOFWEEK"/>.</value>
            public const int DAYOFWEEK = 340;
            /// <value>Numeric identifier of <see cref="ValueFor.INITIAL"/>.</value>
            public const int INITIAL = 341;
            #endregion

            #endregion
        }
    }
}