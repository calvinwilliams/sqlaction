/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	SqlActionColumn			primaryKey ;
	List<SqlActionIndex>	indexList ;

	String					javaSaoClassName ;
	String					javaSauClassName ;
	String					javaSaoFileName ;
	String					javaSauFileName ;
	String					javaObjectName ;
	
	public static SqlActionTable fetchTableMetadataInDatabase( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable tableCache, String tableName ) throws Exception {
		SqlActionTable		table ;
		PreparedStatement	prestmt = null ;
		ResultSet			rs ;
		String				tableType ;
		int					nret = 0 ;
		
		if( tableCache != null ) {
			if( tableName.equalsIgnoreCase(tableCache.tableName) )
				return tableCache;
		}
		
		table = findTable( database.tableList, tableName ) ;
		if( table != null )
			return table;
		
		if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
			prestmt = conn.prepareStatement("SELECT table_name,table_type FROM information_schema.TABLES WHERE table_schema=? AND table_name=?") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
			prestmt = conn.prepareStatement("SELECT table_name,table_type FROM information_schema.TABLES WHERE table_catalog=? AND table_name=?") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
			prestmt = conn.prepareStatement("SELECT table_name FROM user_tables WHERE table_name=?") ;
			prestmt.setString( 1, tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
			prestmt = conn.prepareStatement("SELECT name,type FROM sqlite_master WHERE name=?") ;
			prestmt.setString( 1, tableName );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
			prestmt = conn.prepareStatement("SELECT table_name,table_type FROM INFORMATION_SCHEMA.TABLES WHERE table_catalog=? AND table_name=?") ;
			prestmt.setString( 1, database.databaseName );
			prestmt.setString( 2, tableName );
		}
		rs = prestmt.executeQuery() ;
		if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
			if( rs.getRow() < 1 ) {
				System.out.println( "*** ERROR : GetAllColumnsInTable table not found , database["+database.databaseName+"] table["+tableName+"]" );
				return null;
			}
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
		}
		rs.next();
		
		table = new SqlActionTable() ;
		table.tableName = rs.getString(1) ;
		if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
			tableType = rs.getString(2) ;
			if( ! tableType.equalsIgnoreCase("BASE TABLE") )
				return null;
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
			tableType = rs.getString(2) ;
			if( ! tableType.equalsIgnoreCase("BASE TABLE") )
				return null;
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
			;
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
			tableType = rs.getString(2) ;
			if( ! tableType.equalsIgnoreCase("table") )
				return null;
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
			tableType = rs.getString(2) ;
			if( ! tableType.equalsIgnoreCase("BASE TABLE") )
				return null;
		}
		database.tableList.add( table );
		
		rs.close();
		
		nret = SqlActionColumn.fetchAllColumnsMetadataInTable( dbserverConf, sqlactionConf, conn, database, table ) ;
		if( nret != 0 ) {
			System.out.println( "GetAllColumnsInTable failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"]" );
			return null;
		}
		
		nret = SqlActionIndex.fetchAllIndexesMetadataInTable( dbserverConf, sqlactionConf, conn, database, table ) ;
		if( nret != 0 ) {
			System.out.println( "GetAllIndexesInTable failed["+nret+"] , database["+database.databaseName+"] table["+table.tableName+"]" );
			return null;
		}
		
		String[] sa = table.tableName.split( "_" ) ;
		StringBuilder sb = new StringBuilder() ;
		for( String s : sa ) {
			sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
		}
		table.javaSaoClassName = sb.toString() + "SAO" ;
		table.javaSauClassName = sb.toString() + "SAU" ;
		table.javaSaoFileName = table.javaSaoClassName + ".java" ;
		table.javaSauFileName = table.javaSauClassName + ".java" ;
		table.javaObjectName = sb.toString().substring(0,1).toLowerCase(Locale.getDefault()) + sb.toString().substring(1) ;
		
		travelTable( dbserverConf, sqlactionConf, database, tableName, 1 );
		
		return table;
	}
	
	public static int travelTable( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionDatabase database, String tableName, int depth ) {
		for( SqlActionTable t : database.tableList ) {
			if( ! t.tableName.equalsIgnoreCase(tableName) )
				continue;
			
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"] primaryKey["+(t.primaryKey==null?"null":t.primaryKey.columnName)+"] javaObjectName["+t.javaObjectName+"]" );
			
			SqlActionColumn.travelAllColumnsMetadata( dbserverConf, sqlactionConf, t.columnList, depth+1 );
			
			SqlActionIndex.travelAllIndexesMetadata( dbserverConf, sqlactionConf, t.indexList, depth+1 );
		}
		
		return 0;
	}
	
	public static SqlActionTable findTable( List<SqlActionTable> sqlactionTableList, String tableName ) {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.tableName.equalsIgnoreCase(tableName) )
				return t;
		}
		
		return null;
	}
	
	public static SqlActionTable findTableByJavaSaoClassName( List<SqlActionTable> sqlactionTableList, String javaSaoClassName ) {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.javaSaoClassName.equalsIgnoreCase(javaSaoClassName) )
				return t;
		}
		
		return null;
	}
	
	public static SqlActionTable findTableByJavaSauClassName( List<SqlActionTable> sqlactionTableList, String javaSauClassName ) {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.javaSauClassName.equalsIgnoreCase(javaSauClassName) )
				return t;
		}
		
		return null;
	}
}
