/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

import java.util.*;

public class SqlActionConf {
	public String							database ;
	public LinkedList<SqlActionTableConf>	tables ;
	public String							javaPackage ;
}

class SqlActionTableConf {
	public String					table ;
	public LinkedList<String>		sqlactions ;
}
