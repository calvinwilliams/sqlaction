/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

import java.sql.Connection;
import java.util.List;

public class SqlActionFromTableToken {

	public String			tableName = null ;
	public String			tableAliasName = null ;
	public SqlActionTable	table = null ;

	public static SqlActionTable findTable( List<SqlActionFromTableToken> fromTableTokenList, String tableName ) {
		for( SqlActionFromTableToken tt : fromTableTokenList ) {
			if( tt.tableAliasName != null ) {
				if( tt.tableAliasName.equalsIgnoreCase(tableName) ) {
					return tt.table;
				}
			}
		}
		
		for( SqlActionFromTableToken tt : fromTableTokenList ) {
			if( tt.tableName.equalsIgnoreCase(tableName) ) {
				return tt.table;
			}
		}
		
		return null;
	}
}
