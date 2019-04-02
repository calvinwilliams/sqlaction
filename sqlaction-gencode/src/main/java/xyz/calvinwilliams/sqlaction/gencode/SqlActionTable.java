/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	List<SqlActionIndex>	indexList ;

	String					javaClassName ;
	String					javaObjectName ;
	String					javaFileName ;
	
	public static int getAllTablesInDatabase( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database ) throws Exception {
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		SqlActionTable		table ;
		String				tableType ;
		int					nret = 0 ;
		
		database.tableList = new LinkedList<SqlActionTable>() ;
		
		if( dbserverConf.dbms.equals(SqlActionDatabase.SQLACTION_DBMS_MYSQL) ) {
			prestmt = conn.prepareStatement("SELECT table_name,table_type FROM information_schema.TABLES WHERE table_schema=?") ;
			prestmt.setString( 1, database.databaseName );
		}
		rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			table = new SqlActionTable() ;
			
			table.tableName = rs.getString(1) ;
			tableType = rs.getString(2) ;
			if( ! tableType.equals("BASE TABLE") )
				continue;
			
			database.tableList.add( table );
		}
		rs.close();
		
		for( SqlActionTable t : database.tableList ) {
			nret = SqlActionColumn.getAllColumnsInTable( dbserverConf, sqlactionConf, conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllColumnsInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
			
			nret = SqlActionIndex.getAllIndexesInTable( dbserverConf, sqlactionConf, conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllIndexesInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
		}
		
		for( SqlActionTable t : database.tableList ) {
			String[] sa = t.tableName.split( "_" ) ;
			StringBuilder sb = new StringBuilder() ;
			for( String s : sa ) {
				sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
			}
			t.javaClassName = sb.toString() + "SAO" ;
			t.javaObjectName = sb.toString().substring(0,1).toLowerCase(Locale.getDefault()) + sb.toString().substring(1) ;
			t.javaFileName = t.javaClassName + ".java" ;
		}
		
		return 0;
	}
	
	public static int travelAllTables( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionTable> sqlactionTableList, int depth ) throws Exception {
		StringBuilder		out = new StringBuilder() ;
		
		for( SqlActionTable t : sqlactionTableList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"]" );
			
			SqlActionColumn.travelAllColumns( dbserverConf, sqlactionConf, t.columnList, depth+1, out );
			
			SqlActionIndex.travelAllIndexes( dbserverConf, sqlactionConf, t.indexList, depth+1, out );
		}
		
		return 0;
	}
	
	public static SqlActionTable findTable( List<SqlActionTable> sqlactionTableList, String tableName ) throws Exception {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.tableName.equals(tableName) )
				return t;
		}
		
		return null;
	}
}
