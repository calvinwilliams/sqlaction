package xyz.calvinwilliams.sqlaction;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class SqlActionTable {
	String					tableName ;
	List<SqlActionColumn>	columnList ;
	List<SqlActionIndex>	indexList ;

	public static int GetAllTablesInDatabase( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database ) throws Exception {
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
			/*
			if( ! table.tableName.equals(sqlactionConf.table) )
				continue;
			*/
			tableType = rs.getString(2) ;
			if( ! tableType.equals("BASE TABLE") )
				continue;
			
			database.tableList.add( table );
		}
		rs.close();
		
		for( SqlActionTable t : database.tableList ) {
			nret = SqlActionColumn.GetAllColumnsInTable( dbserverConf, sqlactionConf, conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllColumnsInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
			
			nret = SqlActionIndex.GetAllIndexesInTable( dbserverConf, sqlactionConf, conn, database, t ) ;
			if( nret != 0 ) {
				System.out.println( "GetAllIndexesInTable failed["+nret+"] , database["+database.databaseName+"] table["+t.tableName+"]" );
				return nret;
			}
		}
		
		return 0;
	}
	
	public static int TravelAllTables( DbServerConf dbserverConf, SqlActionConf sqlactionConf, List<SqlActionTable> sqlactionTableList, int depth ) throws Exception {
		StringBuilder		out = new StringBuilder() ;
		
		for( SqlActionTable t : sqlactionTableList ) {
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"]" );
			
			SqlActionColumn.TravelAllColumns( dbserverConf, sqlactionConf, t.columnList, depth+1, out );
			
			SqlActionIndex.TravelAllIndexes( dbserverConf, sqlactionConf, t.indexList, depth+1, out );
			
			/*
			if( SqlActionUtil.wildcardMatch( sqlactionConf.table, t.tableName ) != 0 )
				continue;
			
			sqlactionConf.javaTableName = new String(t.tableName) ;
			
			if( sqlactionConf.javaClassName == null ) {
				String[] sa = sqlactionConf.table.split( "_" ) ;
				StringBuilder sb = new StringBuilder() ;
				for( String s : sa ) {
					sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
				}
				sqlactionConf.javaClassName = sb.toString() + "SAO" ;
			}
			
			if( sqlactionConf.javaFileName == null ) {
				sqlactionConf.javaFileName = sqlactionConf.javaClassName + ".java" ;
			}
			
			for( int n = 0 ; n < depth ; n++ )
				System.out.print( "\t" );
			System.out.println( "tableName["+t.tableName+"]" );
			
			out.append( "package "+sqlactionConf.javaPackage+";\n" );
			out.append( "\n" );
			out.append( "import java.math.*;\n" );
			out.append( "import java.util.*;\n" );
			out.append( "import java.sql.Time;\n" );
			out.append( "import java.sql.Timestamp;\n" );
			out.append( "import java.sql.Connection;\n" );
			out.append( "import java.sql.Statement;\n" );
			out.append( "import java.sql.PreparedStatement;\n" );
			out.append( "import java.sql.ResultSet;\n" );
			out.append( "\n" );
			out.append( "public class "+sqlactionConf.javaClassName+" {\n" );
			
			SqlActionColumn.TravelAllColumnsForGeneratingClassCode( dbserverConf, sqlactionConf, t.columnList, depth+1, out );
			
			SqlActionIndex.TravelAllIndexesForGeneratingClassCode( dbserverConf, sqlactionConf, t.indexList, depth+1, out );
			
			out.append( "}\n" );
			
			Files.write( Paths.get(sqlactionConf.javaFileName) , out.toString().getBytes() );
			*/
		}
		
		return 0;
	}
	
	public static SqlActionTable FindTable( List<SqlActionTable> sqlactionTableList, String tableName ) throws Exception {
		for( SqlActionTable t : sqlactionTableList ) {
			if( t.tableName.equals(tableName) )
				return t;
		}
		
		return null;
	}
}
