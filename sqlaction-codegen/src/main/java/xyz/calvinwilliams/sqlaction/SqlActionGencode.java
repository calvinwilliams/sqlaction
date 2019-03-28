package xyz.calvinwilliams.sqlaction;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class SqlActionGencode {

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
			for( SqlActionTableConf tc : sqlactionConf.tables ) {
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
			for( SqlActionTableConf sqlactionTableConf : sqlactionConf.tables ) {
				String[] sa = sqlactionTableConf.table.split( "_" ) ;
				StringBuilder sb = new StringBuilder() ;
				for( String s : sa ) {
					sb.append( s.substring(0,1).toUpperCase(Locale.getDefault()) + s.substring(1) );
				}
				sqlactionTableConf.javaClassName = sb.toString() + "SAO" ;
				sqlactionTableConf.javaFileName = sqlactionTableConf.javaClassName + ".java" ;
				
				StringBuilder out = new StringBuilder() ;
				
				SqlActionTable table = SqlActionTable.FindTable( database.tableList, sqlactionTableConf.table ) ;
				if( table == null ) {
					System.out.println( "table["+sqlactionTableConf.table+"] not found in database["+sqlactionConf.database+"]" );
					return;
				}
				
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
				for( String sqlaction : sqlactionTableConf.sqlactions ) {
					// Parse sql action
					SqlActionSyntaxParser parser = new SqlActionSyntaxParser() ;
					nret = parser.ParseSyntax(sqlaction) ;
					if( nret != 0 ) {
						System.out.println( "SqlActionSyntaxParser.ParseSyntax failed["+nret+"]" );
						return;
					}
					
					if( parser.selectAllColumn == true ) {
						for( SqlActionFromTableToken tt : parser.fromTableTokenList ) {
							for( SqlActionColumn c : table.columnList ) {
								SqlActionSelectColumnToken ct = new SqlActionSelectColumnToken() ;
								ct.tableName = tt.tableName ;
								ct.tableAliasName = tt.tableAliasName ;
								ct.column = c ;
								ct.columnName = c.columnName ;
								parser.selectColumnTokenList.add( ct );
							}
						}
					}
					
					// Show parser result
					System.out.println( "--- parser ---" );
					System.out.println( "selectAllColumn["+parser.selectAllColumn+"]" );
					for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
						System.out.println( "selectColumnToken["+ct.columnName+"]" );
					}
					for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
						System.out.println( "fromTableToken["+ct.tableName+"]" );
					}
					System.out.println( "insertTableName["+parser.insertTableName+"]" );
					System.out.println( "updateTableName["+parser.updateTableName+"]" );
					for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
						System.out.println( "setColumnToken["+ct.columnName+"]" );
					}
					System.out.println( "deleteTableName["+parser.deleteTableName+"]" );
					for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
						System.out.println( "whereColumnToken["+ct.columnName+"]" );
					}
					
					// Dump gencode
					if( parser.selectColumnTokenList != null && parser.selectColumnTokenList.size() > 0 ) {
						
					} else if( parser.insertTableName != null ) {
						nret = InsertSqlDumpGencode( dbserverConf, sqlactionConf, sqlactionTableConf, sqlaction, parser, database, table, out ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : InsertSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "InsertSqlDumpGencode ok" );
						}
					} else if( parser.updateTableName != null ) {
						nret = UpdateSqlDumpGencode( dbserverConf, sqlactionConf, sqlactionTableConf, sqlaction, parser, database, table, out ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : UpdateSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "UpdateSqlDumpGencode ok" );
						}
					} else if( parser.deleteTableName != null ) {
						nret = DeleteSqlDumpGencode( dbserverConf, sqlactionConf, sqlactionTableConf, sqlaction, parser, database, table, out ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : DeleteSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "DeleteSqlDumpGencode ok" );
						}
					} else {
						System.out.println( "No action in ["+sqlaction+"]" );
						return;
					}
				}
				
				out.append( "}\n" );
				
				Files.write( Paths.get(sqlactionTableConf.javaFileName) , out.toString().getBytes() );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int DumpSelectOutputColumn( int columnIndex, SqlActionColumn column, StringBuilder out ) {
		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBoolean( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getByte( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getShort( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getInt( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getLong( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getFloat( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDouble( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBigDecimal( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getString( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getDate( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTimestamp( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getTime( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t\t" + "selectOutput."+column.javaPropertyName+" = rs.getBytes( "+columnIndex+" ) ;\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int DumpSetInputColumn( int columnIndex, SqlActionColumn column, StringBuilder out ) {

		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t\t" + "prestmt.setBoolean( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t" + "prestmt.setByte( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t" + "prestmt.setShort( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t" + "prestmt.setLong( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t" + "prestmt.setFloat( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t" + "prestmt.setTime( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t" + "prestmt.setTimestamp( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", setInput."+column.javaPropertyName+" );\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int DumpWhereInputColumn( int columnIndex, SqlActionColumn column, StringBuilder out ) {

		switch( column.dataType ) {
			case SQLACTION_DATA_TYPE_BIT :
				out.append( "\t\t" + "prestmt.setBoolean( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYINT :
				out.append( "\t\t" + "prestmt.setByte( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_SMALLINT :
				out.append( "\t\t" + "prestmt.setShort( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMINT :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_INTEGER :
				out.append( "\t\t" + "prestmt.setInt( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BIGINT :
				out.append( "\t\t" + "prestmt.setLong( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_REAL :
				out.append( "\t\t" + "prestmt.setFloat( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_FLOAT :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DOUBLE :
				out.append( "\t\t" + "prestmt.setDouble( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DECIMAL :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_NUMBERIC :
				out.append( "\t\t" + "prestmt.setBigDecimal( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_CHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARCHAR :
				out.append( "\t\t" + "prestmt.setString( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATE :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIME :
				out.append( "\t\t" + "prestmt.setTime( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_DATETIME :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TIMESTAMP :
				out.append( "\t\t" + "prestmt.setTimestamp( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_YEAR :
				out.append( "\t\t" + "prestmt.setDate( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_VARBINARY :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_BLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_TINYBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_MEDIUMBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			case SQLACTION_DATA_TYPE_LONGBLOB :
				out.append( "\t\t" + "prestmt.setBytes( "+columnIndex+", whereInput."+column.javaPropertyName+" );\n" );
				break;
			default :
				System.out.println( "dataType["+column.dataType+"] invalid" );
				return -1;
		}
		
		return 0;
	}
	
	public static int SelectSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionTableConf sqlactionTableConf, String sqlaction, SqlActionSyntaxParser parser, SqlActionDatabase database, SqlActionTable table, StringBuilder out ) {
		
		StringBuilder		sql = new StringBuilder() ;
		StringBuilder		methodName = new StringBuilder() ;
		int					nret = 0 ;
		
		sql.append( "SELECT " );
		if( parser.selectAllColumn ) {
			methodName.append( "SqlAction_SELECT_ALL_FROM_" + table.tableName );
		} else {
			methodName.append( "SqlAction_SELECT_" );
			
			for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
				if( ct != parser.selectColumnTokenList.get(1) ) {
					sql.append( "," );
				}
				sql.append( ct.columnName );
			}
			methodName.append( "_" + table.tableName.replace(',','_') );
			methodName.append( "_FROM_" + table.tableName );
		}
		
		sql.append( " WHERE" );
		methodName.append( "_WHERE" );
		
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			if( ct != parser.whereColumnTokenList.get(1) ) {
				sql.append( "," );
			}
			sql.append( ct.columnName + "=?" );
		}
		methodName.append( "_" + table.tableName.replace(',','_') );
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		if( parser.whereColumnTokenList.size() > 0 ) {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, List<" +sqlactionTableConf.javaClassName+ "> selectOutputList, " + sqlactionTableConf.javaClassName + " whereInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
				columnIndex++;
				nret = DumpWhereInputColumn( columnIndex, ct.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "ResultSet rs = prestmt.executeQuery() ;\n" );
		} else {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, List<" +sqlactionTableConf.javaClassName+ "> selectOutputList ) throws Exception {\n" );
			out.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			out.append( "\t\t" + "ResultSet rs = stmt.executeQuery(\""+sql+"\") ;\n" );
		}
		out.append( "\t\t" + "while( rs.next() ) {\n" );
		out.append( "\t\t\t" + sqlactionTableConf.javaClassName + " selectOutput = new "+sqlactionTableConf.javaClassName+"() ;\n" );
		if( parser.selectColumnTokenList.size() > 0 ) {
			int	columnIndex = 0 ;
			for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
				columnIndex++;
				nret = DumpSelectOutputColumn( columnIndex, ct.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
		}
		out.append( "\t\t\t" + "selectOutputList.add(selectOutput);\n" );
		out.append( "\t\t" + "}\n" );
		out.append( "\t\t" + "return selectOutputList.size();\n" );
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int InsertSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionTableConf sqlactionTableConf, String sqlaction, SqlActionSyntaxParser parser, SqlActionDatabase database, SqlActionTable table, StringBuilder out ) {
		
		StringBuilder		sql = new StringBuilder() ;
		StringBuilder		methodName = new StringBuilder() ;
		int					nret = 0 ;
		
		sql.append( "INSERT INTO " + table.tableName + " (" );
		methodName.append( "SqlAction_INSERT_INTO_" + table.tableName );
		
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				if( c != table.columnList.get(1) )
					sql.append( "," );
				sql.append( c.columnName );
			}
		}
		
		sql.append( ") VALUES (" );
		
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				if( c != table.columnList.get(1) )
					sql.append( "," );
				sql.append( "?" );
			}
		}
		
		sql.append( ")" );
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, " + sqlactionTableConf.javaClassName + " whereInput ) throws Exception {\n" );
		out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
		int	columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				columnIndex++;
				nret = DumpWhereInputColumn( columnIndex, c, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+c.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
		}
		out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int UpdateSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionTableConf sqlactionTableConf, String sqlaction, SqlActionSyntaxParser parser, SqlActionDatabase database, SqlActionTable table, StringBuilder out ) {
		
		StringBuilder			sql = new StringBuilder() ;
		StringBuilder			methodName = new StringBuilder() ;
		int						nret = 0 ;
		
		sql.append( "UPDATE " + table.tableName + " SET " );
		methodName.append( "SqlAction_UPDATE_" + table.tableName + "_SET" );
		
		for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
			if( ct != parser.setColumnTokenList.get(1) ) {
				sql.append( "," );
			}
			sql.append( ct.columnName + "=?" );
		}
		methodName.append( "_" + table.tableName.replace(',','_') );
		
		sql.append( " WHERE" );
		methodName.append( "_WHERE" );
		
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			if( ct != parser.whereColumnTokenList.get(1) ) {
				sql.append( "," );
			}
			sql.append( ct.columnName + "=?" );
		}
		methodName.append( "_" + table.tableName.replace(',','_') );
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		if( parser.whereColumnTokenList.size() > 0 ) {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, " + sqlactionTableConf.javaClassName + " setInput, " + sqlactionTableConf.javaClassName + " whereInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionSetColumnToken c : parser.setColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpSetInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			for( SqlActionWhereColumnToken c : parser.whereColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpWhereInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, " + sqlactionTableConf.javaClassName + " setInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionSetColumnToken c : parser.setColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpSetInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		}
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int DeleteSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionTableConf sqlactionTableConf, String sqlaction, SqlActionSyntaxParser parser, SqlActionDatabase database, SqlActionTable table, StringBuilder out ) {
		
		StringBuilder			sql = new StringBuilder() ;
		StringBuilder			methodName = new StringBuilder() ;
		int						nret = 0 ;
		
		sql.append( "DELETE FROM " + table.tableName + " WHERE" );
		methodName.append( "SqlAction_DELETE_FROM_" + table.tableName + "_WHERE" );
		
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			if( ct != parser.whereColumnTokenList.get(1) ) {
				sql.append( "," );
			}
			sql.append( ct.columnName + "=?" );
		}
		methodName.append( "_" + table.tableName.replace(',','_') );
		
		out.append( "\n" );
		out.append( "\t" + "// "+sqlaction+"\n" );
		if( parser.whereColumnTokenList.size() > 0 ) {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, " + sqlactionTableConf.javaClassName + " setInput, " + sqlactionTableConf.javaClassName + " whereInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionSetColumnToken c : parser.setColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpSetInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			for( SqlActionWhereColumnToken c : parser.whereColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpWhereInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			out.append( "\t" + "public static int " + methodName.toString() + "( Connection conn, " + sqlactionTableConf.javaClassName + " setInput ) throws Exception {\n" );
			out.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement(\""+sql+"\") ;\n" );
			int	columnIndex = 0 ;
			for( SqlActionSetColumnToken c : parser.setColumnTokenList ) {
				columnIndex++;
				if( c.column == null ) {
					c.column = SqlActionColumn.FindColumn( table.columnList, c.columnName ) ;
					if( c.column == null ) {
						System.out.println( "column["+c.columnName+"] not found in table["+table.tableName+"]" );
						return -1;
					}
				}
				nret = DumpSetInputColumn( columnIndex, c.column, out ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+c.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
			out.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		}
		out.append( "\t" + "}\n" );
		
		return 0;
	}
	
}