/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

import java.util.*;

public class SqlActionDatabase {
	
	final public static String	SQLACTION_DBMS_MYSQL = "mysql" ;
	
	String						databaseName ;
	List<SqlActionTable>		tableList ;
	
}
