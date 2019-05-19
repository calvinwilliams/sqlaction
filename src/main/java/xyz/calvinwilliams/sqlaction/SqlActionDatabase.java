/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.util.*;

public class SqlActionDatabase {
	
	final public static String	DBMS_MYSQL = "MySql" ;
	final public static String	DBMS_POSTGRESQL = "PostgreSQL" ;
	final public static String	DBMS_ORACLE = "Oracle" ;
	final public static String	DBMS_SQLITE = "Sqlite" ;
	final public static String	DBMS_SQLSERVER = "SqlServer" ;
	
	String						databaseName ;
	List<SqlActionTable>		tableList ;
	
}
