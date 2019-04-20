/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.util.*;

public class DbServerConf {
	public String		dbms ;
	public String		driver ;
	public String		url ;
	public String		user ;
	public String		pwd ;
	public LinkedList<DbServerConfUserDefineDataTypes>	userDefineDataTypes ;
}
