package xyz.calvinwilliams.sqlaction;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class SqlActionCodegen {

	public static void main(String[] args) {
		Path					currentPath ;
		Path					sqlactionConfJsonFilePath ;
		String					sqlactionConfJsonFileContent ;
		SqlActionConf			sqlactionConf ;
		Path					dbserverConfJsonFilePath ;
		String					dbserverConfJsonFileContent ;
		DbServerConf			dbserverConf ;
		
		Connection				conn = null ;
		SqlActionDatabase		database = null ;
		
		int						nret = 0 ;
		
		try {
			// Load sqlaction.conf.json
			currentPath = Paths.get(System.getProperty("user.dir")) ;
			
			while( true ) {
				try {
					sqlactionConfJsonFilePath = Paths.get(currentPath.toString(),"sqlaction.conf.json") ;
					sqlactionConfJsonFileContent = new String(Files.readAllBytes(sqlactionConfJsonFilePath)) ;
					break;
				} catch (IOException e) {
					currentPath = currentPath.getParent() ;
					if( currentPath == null ) {
						System.out.println( "*** ERROR : sqlaction.conf.json not found" );
						return;
					}
				}
			}
			
			sqlactionConf = OKJSON.stringToObject( sqlactionConfJsonFileContent, SqlActionConf.class, OKJSON.OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
			if( sqlactionConf == null ) {
				System.out.println(sqlactionConfJsonFilePath+" content invalid , errcode["+OKJSON.getErrorCode()+"] errdesc["+OKJSON.getErrorCode()+"]");
				return;
			}
			
			// Load dbserver.conf.json
			while( true ) {
				try {
					dbserverConfJsonFilePath = Paths.get(currentPath.toString(),"dbserver.conf.json") ;
					dbserverConfJsonFileContent = new String(Files.readAllBytes(dbserverConfJsonFilePath)) ;
					break;
				} catch (IOException e) {
					currentPath = currentPath.getParent() ;
					if( currentPath == null ) {
						System.out.println( "*** ERROR : sqlaction.conf.json not found" );
						return;
					}
				}
			}
			
			dbserverConf = OKJSON.stringToObject( dbserverConfJsonFileContent, DbServerConf.class, OKJSON.OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
			if( dbserverConf == null ) {
				System.out.println(dbserverConfJsonFilePath+" content invalid");
				return;
			}
			
			if( dbserverConf.dbms == null ) {
				String[] sa = dbserverConf.url.split( ":" ) ;
				if( sa.length < 3 ) {
					System.out.println( "dbserverConf.url["+dbserverConf.dbms+"] invalid" );
					return;
				}
				
				dbserverConf.dbms = sa[1] ;
			}
			
			if( ! dbserverConf.dbms.equals("mysql") ) {
				System.out.println( "dbserverConf.dbms["+dbserverConf.dbms+"] not support" );
				return;
			}
			
			System.out.println( "--- dbserverConf ---" );
			System.out.println( "  dbms["+dbserverConf.dbms+"]" );
			System.out.println( "driver["+dbserverConf.driver+"]" );
			System.out.println( "   url["+dbserverConf.url+"]" );
			System.out.println( "  user["+dbserverConf.user+"]" );
			System.out.println( "   pwd["+dbserverConf.pwd+"]" );
			
			System.out.println( "--- sqlactionConf ---" );
			System.out.println( " database["+sqlactionConf.database+"]" );
			for( SqlActionTableConf tc : sqlactionConf.tableConfList ) {
				System.out.println( "    table["+tc.table+"]" );
				for( String s : tc.sqlactions ) {
					System.out.println( "sqlaction["+s+"]" );
				}
			}
			
			// Query database metadata
			Class.forName( dbserverConf.driver );
			conn = DriverManager.getConnection( dbserverConf.url, dbserverConf.user, dbserverConf.pwd ) ;
			
			database = new SqlActionDatabase() ;
			database.databaseName = sqlactionConf.database ;
			
			nret = SqlActionTable.GetAllTablesInDatabase( dbserverConf, sqlactionConf, conn, database ) ;
			if( nret != 0 ) {
				System.out.println("*** ERROR : SqlActionTable.GetAllTablesInDatabase failed["+nret+"]");
				conn.close();
				return;
			} else {
				System.out.println("SqlActionTable.GetAllTablesInDatabase ok");
			}
			
			conn.close();
			
			// Show all databases and tables and columns and indexes
			nret = SqlActionTable.TravelAllTables( dbserverConf, sqlactionConf, database.tableList, 1 ) ;
			if( nret != 0 ) {
				System.out.println("*** ERROR : SqlActionTable.TravelAllTables failed["+nret+"]");
				return;
			} else {
				System.out.println("SqlActionTable.TravelAllTables ok");
			}
			
			// Generate class code
			for( SqlActionTableConf sqlactionTableConf : sqlactionConf.tableConfList ) {
				String[] sa = sqlactionTableConf.table.split( "_" ) ;
				StringBuilder sb = new StringBuilder() ;
				for( String s : sa ) {
					sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
				}
				sqlactionTableConf.javaClassName = sb.toString() + "SAO" ;
				sqlactionTableConf.javaFileName = sqlactionTableConf.javaClassName + ".java" ;
				
				StringBuilder out = new StringBuilder() ;
				
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
				out.append( "public class "+sqlactionTableConf.javaClassName+" {\n" );
				
				// Parse sql actions and dump gencode
				for( String sqlactions : sqlactionTableConf.sqlactions ) {
					SqlActionSyntaxParser parser = new SqlActionSyntaxParser() ;
					nret = parser.ParseSyntax(sqlactions) ;
					if( nret != 0 ) {
						System.out.println( "SqlActionSyntaxParser.ParseSyntax failed["+nret+"]" );
						return;
					}
					
					if( parser.selectAllColumn == true ) {
						for( SqlActionFromTableToken tt : parser.fromTableTokenList ) {
							
							
							
							SqlActionSelectColumnToken ct = new SqlActionSelectColumnToken() ;
							ct.tableName = tt.tableName ;
							ct.tableAliasName = tt.tableAliasName ;
							
							
							
						}
					}
				}
				
				out.append( "}\n" );
				
				Files.write( Paths.get(sqlactionTableConf.javaFileName) , out.toString().getBytes() );
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
