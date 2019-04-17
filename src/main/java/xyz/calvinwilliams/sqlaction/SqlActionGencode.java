/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.LinkedList;
import xyz.calvinwilliams.okjson.*;

public class SqlActionGencode {

	final private static String				SQLACTION_VERSION = "0.2.1.0" ;
	
	final public static String				SELECT_COUNT___ = "count(" ;
	final private static String				COUNT___ = "_count_" ;
	
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
		SqlActionTable			table = null ;
		
		int						nret = 0 ;
		
		try {
			System.out.println( "//////////////////////////////////////////////////////////////////////////////" );
			System.out.println( "/// sqlaction v"+SQLACTION_VERSION );
			System.out.println( "/// Copyright by calvin<calvinwilliams@163.com,calvinwilliams@gmail.com>" );
			System.out.println( "//////////////////////////////////////////////////////////////////////////////" );
			
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
			
			sqlactionConf = OKJSON.stringToObject( sqlactionConfJsonFileContent, SqlActionConf.class, OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
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
			
			dbserverConf = OKJSON.stringToObject( dbserverConfJsonFileContent, DbServerConf.class, OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
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
			for( SqlActionConfTable tc : sqlactionConf.tables ) {
				System.out.println( "\t" + "table["+tc.table+"]" );
				for( String s : tc.sqlactions ) {
					System.out.println( "\t\t" + "sqlaction["+s+"]" );
				}
			}
			
			// Query database metadata
			Class.forName( dbserverConf.driver );
			conn = DriverManager.getConnection( dbserverConf.url, dbserverConf.user, dbserverConf.pwd ) ;
			
			database = new SqlActionDatabase() ;
			database.databaseName = sqlactionConf.database ;
			database.tableList = new LinkedList<SqlActionTable>() ;
			
			// Generate class code
			for( SqlActionConfTable tc : sqlactionConf.tables ) {
				// Get the table in the database
				System.out.println( "SqlActionTable.getTableInDatabase["+tc.table+"] ..." );
				table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, null, tc.table ) ;
				if( table == null ) {
					System.out.println( "*** ERROR : SqlActionTable.getTableInDatabase["+tc.table+"] failed["+nret+"]" );
					conn.close();
					return;
				} else {
					System.out.println( "SqlActionTable.getTableInDatabase["+tc.table+"] ok" );
				}
				
				// Show all databases and tables and columns and indexes
				nret = SqlActionTable.travelTable( dbserverConf, sqlactionConf, database, tc.table, 1 ) ;
				if( nret != 0 ) {
					System.out.println( "*** ERROR : SqlActionTable.travelTable["+tc.table+"] failed["+nret+"]" );
					return;
				} else {
					System.out.println( "SqlActionTable.travelTable["+tc.table+"] ok" );
				}
				
				System.out.println( "*** NOTICE : Prepare "+Paths.get(table.javaSaoFileName)+" and "+Paths.get(table.javaSauFileName)+" output buffer ..." );
				
				StringBuilder saoFileBuffer = new StringBuilder() ;
				StringBuilder sauFileBuffer = new StringBuilder() ;
				
				saoFileBuffer.append( "// This file generated by sqlaction v"+SQLACTION_VERSION+"\n" );
				saoFileBuffer.append( "// WARN : DON'T MODIFY THIS FILE\n" );
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append( "package "+sqlactionConf.javaPackage+";\n" );
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append( "import java.math.*;\n" );
				saoFileBuffer.append( "import java.util.*;\n" );
				saoFileBuffer.append( "import java.sql.Time;\n" );
				saoFileBuffer.append( "import java.sql.Timestamp;\n" );
				saoFileBuffer.append( "import java.sql.Connection;\n" );
				saoFileBuffer.append( "import java.sql.Statement;\n" );
				saoFileBuffer.append( "import java.sql.PreparedStatement;\n" );
				saoFileBuffer.append( "import java.sql.ResultSet;\n" );
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append( "public class "+table.javaSaoClassName+" {\n" );
				
				sauFileBuffer.append( "// This file generated by sqlaction v"+SQLACTION_VERSION+"\n" );
				sauFileBuffer.append( "\n" );
				sauFileBuffer.append( "package "+sqlactionConf.javaPackage+";\n" );
				sauFileBuffer.append( "\n" );
				sauFileBuffer.append( "import java.math.*;\n" );
				sauFileBuffer.append( "import java.util.*;\n" );
				sauFileBuffer.append( "import java.sql.Time;\n" );
				sauFileBuffer.append( "import java.sql.Timestamp;\n" );
				sauFileBuffer.append( "import java.sql.Connection;\n" );
				sauFileBuffer.append( "import java.sql.Statement;\n" );
				sauFileBuffer.append( "import java.sql.PreparedStatement;\n" );
				sauFileBuffer.append( "import java.sql.ResultSet;\n" );
				sauFileBuffer.append( "\n" );
				sauFileBuffer.append( "public class "+table.javaSauClassName+" {\n" );
				
				saoFileBuffer.append( "\n" );
				for( SqlActionColumn c : table.columnList ) {
					SqlActionColumn.dumpDefineProperty( c, saoFileBuffer );
				}
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append("\t").append("int				").append(COUNT___).append(" ; // defining for 'SELECT COUNT(*)'\n");
				
				// Parse sql actions and dump gencode
				for( String sqlaction : tc.sqlactions ) {
					String					sql ;
					String					statementSql ;
					String					methodName ;
					String					statementInterceptorMethodName = null ;
					int						beginMetaData ;
					int						endMetaData ;
					
					SqlActionSyntaxParser	parser ;
					
					sqlaction = sqlaction.trim() ;
					
					beginMetaData = sqlaction.indexOf( "@@" ) ;
					if( beginMetaData >= 0 ) {
						sql = sqlaction.substring( 0, beginMetaData ) ;
					} else {
						sql = sqlaction ;
					}
					
					statementSql = sql.replace("\r\n"," ").replace("\n"," ").replace("\t"," ").replaceAll("[ ]+"," ").trim() ;
					
					beginMetaData = sqlaction.indexOf( "@@METHOD(" ) ;
					if( beginMetaData >= 0 ) {
						endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
						if( endMetaData == -1 ) {
							System.out.println( "sql["+sql+"] invalid" );
							return;
						}
						methodName = sqlaction.substring( beginMetaData+9, endMetaData ) ;
					} else {
						methodName = SqlActionUtil.sqlConvertToMethodName(statementSql) ;
					}
					
					String interceptorType = null ;
					beginMetaData = sqlaction.indexOf( "@@STATEMENT_INTERCEPTOR(" ) ;
					if( beginMetaData >= 0 ) {
						endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
						if( endMetaData == -1 ) {
							System.out.println( "*** ERROR : sql["+sql+"] invalid" );
							return;
						}
						statementInterceptorMethodName = sqlaction.substring( beginMetaData+24, endMetaData ) ;
						if( statementInterceptorMethodName.equals("") )
							statementInterceptorMethodName = "STATEMENT_INTERCEPTOR_for_"+methodName ;
					}
					
					System.out.println( "Parse sql action ["+sqlaction+"]" );
					System.out.println( "\t" + "sql["+sql+"]" );
					System.out.println( "\t" + "methodName["+methodName+"]" );
					
					// Parse sql FROM statement
					System.out.println( "Parse sql FROM statement ["+sql+"]" );
					
					parser = new SqlActionSyntaxParser() ;
					
					nret = parser.parseStatementSyntax_FROM( dbserverConf, sqlactionConf, conn, database, table, sql ) ;
					if( nret != 0 ) {
						System.out.println( "*** ERROR : SqlActionSyntaxParser.parseStatementSyntax_FROM failed["+nret+"]" );
						return;
					} else {
						System.out.println( "SqlActionSyntaxParser.parseStatementSyntax_FROM ok" );
					}
					
					// Show all databases and tables and columns and indexes
					System.out.println( "Show all databases and tables and columns and indexes ["+sql+"]" );
					
					for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
						nret = SqlActionTable.travelTable( dbserverConf, sqlactionConf, database, ct.tableName, 1 ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : SqlActionTable.travelTable["+ct.tableName+"] failed["+nret+"]" );
							return;
						} else {
							System.out.println( "SqlActionTable.travelTable["+ct.tableName+"] ok" );
						}
					}
					
					// Parse sql statements except FROM
					System.out.println( "Parse sql statements except FROM ["+sql+"]" );
					
					nret = parser.parseSyntaxExceptFROM( dbserverConf, sqlactionConf, conn, database, table, sql ) ;
					if( nret != 0 ) {
						System.out.println( "*** ERROR : SqlActionSyntaxParser.parseSyntaxExceptFROM failed["+nret+"]" );
						return;
					} else {
						System.out.println( "SqlActionSyntaxParser.parseSyntaxExceptFROM ok" );
					}
					
					// Fixed SELECT * by fill all column to parser.selectColumnTokenList
					System.out.println( "Fixed SELECT * by fill all column to parser.selectColumnTokenList ["+sql+"]" );
					
					if( parser.selectAllColumn == true ) {
						for( SqlActionFromTableToken tt : parser.fromTableTokenList ) {
							for( SqlActionColumn c : table.columnList ) {
								SqlActionSelectColumnToken ct = new SqlActionSelectColumnToken() ;
								ct.tableName = tt.tableName ;
								// ct.tableAliasName = tt.tableAliasName ;
								ct.table = table ;
								ct.columnName = c.columnName ;
								ct.column = c ;
								parser.selectColumnTokenList.add( ct );
							}
						}
					}
					
					// Show parser result
					System.out.println( "Show parser result ["+sql+"]" );
					
					System.out.println( "\t" + "selectHint["+parser.selectHint+"]" );
					
					System.out.println( "\t" + "selectAllColumn["+parser.selectAllColumn+"]" );
					
					for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
						System.out.println( "\t" + "selectColumnToken.tableName["+ct.tableName+"] .table["+ct.table+"] .columnName["+ct.columnName+"] .column["+ct.column+"]" );
					}
					
					for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
						System.out.println( "\t" + "fromTableToken.tableName["+ct.tableName+"] .tableAliasName["+ct.tableAliasName+"]" );
					}
					
					System.out.println( "\t" + "insertTableName["+parser.insertTableName+"]" );
					
					System.out.println( "\t" + "updateTableName["+parser.updateTableName+"]" );
					
					for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
						System.out.println( "\t" + "setColumnToken.tableName["+ct.tableName+"] .column["+ct.columnName+"] .columnValue["+ct.columnValue+"]" );
					}
					
					System.out.println( "\t" + "deleteTableName["+parser.deleteTableName+"]" );
					
					for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
						System.out.println( "\t" + "whereColumnToken.tableName["+ct.tableName+"] .columnName["+ct.columnName+"] .operator["+ct.operator+"]" );
					}
					
					System.out.println( "\t" + "parser.otherTokens["+parser.otherTokens+"]" );
					
					// Dump gencode
					System.out.println( "Dump gencode ["+sql+"]" );
					
					if( parser.selectColumnTokenList != null && parser.selectColumnTokenList.size() > 0 ) {
						nret = selectSqlDumpGencode( dbserverConf, sqlactionConf, tc, sql, statementSql, methodName, statementInterceptorMethodName, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
						if( nret != 0 ) {
							System.out.println( "\t" + "*** ERROR : SelectSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "\t" + "SelectSqlDumpGencode ok" );
						}
					} else if( parser.insertTableName != null ) {
						nret = insertSqlDumpGencode( dbserverConf, sqlactionConf, tc, sql, statementSql, methodName, statementInterceptorMethodName, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
						if( nret != 0 ) {
							System.out.println( "\t" + "*** ERROR : InsertSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "\t" + "InsertSqlDumpGencode ok" );
						}
					} else if( parser.updateTableName != null ) {
						nret = updateSqlDumpGencode( dbserverConf, sqlactionConf, tc, sql, statementSql, methodName, statementInterceptorMethodName, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
						if( nret != 0 ) {
							System.out.println( "\t" + "*** ERROR : UpdateSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "\t" + "UpdateSqlDumpGencode ok" );
						}
					} else if( parser.deleteTableName != null ) {
						nret = deleteSqlDumpGencode( dbserverConf, sqlactionConf, tc, sql, statementSql, methodName, statementInterceptorMethodName, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
						if( nret != 0 ) {
							System.out.println( "\t" + "*** ERROR : DeleteSqlDumpGencode failed["+nret+"]" );
							return;
						} else {
							System.out.println( "\t" + "DeleteSqlDumpGencode ok" );
						}
					} else {
						System.out.println( "\t" + "Action["+sqlaction+"] invalid" );
						return;
					}
				}
				
				
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append( "}\n" );
				
				sauFileBuffer.append( "\n" );
				sauFileBuffer.append( "}\n" );
				
				Files.write( Paths.get(table.javaSaoFileName) , saoFileBuffer.toString().getBytes() );
				
				File file = new File( table.javaSauFileName ) ;
				if( ! file.exists() ) {
					Files.write( Paths.get(table.javaSauFileName) , sauFileBuffer.toString().getBytes() );
				}
				
				System.out.println( "*** NOTICE : Write "+Paths.get(table.javaSaoFileName)+" and "+Paths.get(table.javaSauFileName)+" completed!!!" );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static int selectSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
											String sql, String statementSql, String methodName,
											String statementInterceptorMethodName,
											SqlActionDatabase database, SqlActionTable table,
											SqlActionSyntaxParser parser,
											StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {
		
		StringBuilder		methodParameters = new StringBuilder() ;
		int					columnIndex ;
		int					nret = 0 ;
		
		methodParameters.append( "Connection conn" );
		for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
			methodParameters.append( ", List<"+ct.table.javaSaoClassName+"> "+ct.table.javaObjectName+"ListForSelectOutput" );
		}
		
		columnIndex = 0 ;
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			columnIndex++;
			methodParameters.append( ", "+ct.column.javaPropertyType+" _"+columnIndex+"_"+ct.column.javaPropertyName );
		}
		
		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, sql );
		if( statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );
			
			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, sql );
			sauFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.whereColumnTokenList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, ct.column, "_"+columnIndex+"_"+ct.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			saoFileBuffer.append( "\t\t" + "ResultSet rs = prestmt.executeQuery() ;\n" );
		} else {
			saoFileBuffer.append( "\t" + "public static int " + methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			saoFileBuffer.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( \""+statementSql+"\" ) ;\n" );
			}
		}
		saoFileBuffer.append( "\t\t" + "while( rs.next() ) {\n" );
		for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
			saoFileBuffer.append( "\t\t\t" + ct.table.javaSaoClassName + " "+ct.table.javaObjectName+" = new "+ct.table.javaSaoClassName+"() ;\n" );
		}
		if( parser.selectColumnTokenList.size() > 0 ) {
			columnIndex = 0 ;
			for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
				columnIndex++;
				if( ct.columnName.equalsIgnoreCase(SqlActionGencode.SELECT_COUNT___) ) {
					saoFileBuffer.append("\t\t\t").append(ct.table.javaObjectName+"."+COUNT___).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				} else {
					nret = SqlActionColumn.dumpSelectOutputColumn( columnIndex, ct.column, ct.table.javaObjectName+"."+ct.column.javaPropertyName, saoFileBuffer ) ;
					if( nret != 0 ) {
						System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
						return nret;
					}
				}
			}
		}
		for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
			saoFileBuffer.append( "\t\t\t" + ct.table.javaObjectName+"ListForSelectOutput.add("+ct.table.javaObjectName+") ;\n" );
		}
		saoFileBuffer.append( "\t\t" + "}\n" );
		saoFileBuffer.append( "\t\t" + "return "+parser.fromTableTokenList.get(0).table.javaObjectName+"ListForSelectOutput.size();\n" );
		saoFileBuffer.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int insertSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
											String sql, String statementSql, String methodName,
											String statementInterceptorMethodName,
											SqlActionDatabase database, SqlActionTable table,
											SqlActionSyntaxParser parser,
											StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {
		
		StringBuilder		statementSqlBuilder = new StringBuilder() ;
		StringBuilder		methodParameters = new StringBuilder() ;
		int					columnIndex ;
		int					nret = 0 ;
		
		statementSqlBuilder.append( statementSql + " (" );
		
		columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				columnIndex++;
				if( columnIndex > 1 )
					statementSqlBuilder.append( "," );
				statementSqlBuilder.append( c.columnName );
			}
		}
		
		statementSqlBuilder.append( ") VALUES (" );
		
		columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				columnIndex++;
				if( columnIndex > 1 )
					statementSqlBuilder.append( "," );
				statementSqlBuilder.append( "?" );
			}
		}
		
		statementSqlBuilder.append( ")" );
		
		sql = statementSqlBuilder.toString() ;
		
		methodParameters.append( "Connection conn, " + table.javaSaoClassName + " " + table.javaObjectName );
		
		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, sql );
		if( statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );
			
			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, sql );
			sauFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		saoFileBuffer.append( "\t" + "public static int " + methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
		if( statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSqlBuilder+"\" ) ;\n" );
		}
		columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == false ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, c, table.javaObjectName+"."+c.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+c.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
		}
		saoFileBuffer.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		saoFileBuffer.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int updateSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
											String sql, String statementSql, String methodName,
											String statementInterceptorMethodName,
											SqlActionDatabase database, SqlActionTable table,
											SqlActionSyntaxParser parser,
											StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {
		
		StringBuilder		methodParameters = new StringBuilder() ;
		int					setColumnIndex ;
		int					columnTokenIndex ;
		int					nret = 0 ;
		
		if( parser.whereColumnTokenList.size() > 0 ) {
			methodParameters.append( "Connection conn" );
		} else {
			methodParameters.append( "Connection conn" );
		}
		
		columnTokenIndex = 0 ;
		for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
			columnTokenIndex++;
			methodParameters.append( ", "+ct.column.javaPropertyType+" _"+columnTokenIndex+"_"+ct.column.javaPropertyName+"_ForSetInput" );
		}
		
		columnTokenIndex = 0 ;
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			columnTokenIndex++;
			methodParameters.append( ", "+ct.column.javaPropertyType+" _"+columnTokenIndex+"_"+ct.column.javaPropertyName+"_ForWhereInput" );
		}
		
		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, sql );
		if( statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );
			
			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, sql );
			sauFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.setColumnTokenList.size() > 0 || parser.whereColumnTokenList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSql+"\" ) ;\n" );
			}
			setColumnIndex = 0 ;
			columnTokenIndex = 0 ;
			for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
				setColumnIndex++;
				columnTokenIndex++;
				if( ct.columnValue.equals("?") ) {
					nret = SqlActionColumn.dumpSetInputColumn( setColumnIndex, ct.column, "_"+columnTokenIndex+"_"+ct.column.javaPropertyName+"_ForSetInput", saoFileBuffer ) ;
					if( nret != 0 ) {
						System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+ct.columnName+\"] failed["+nret+"]" );
						return nret;
					}
				}
			}
			columnTokenIndex = 0 ;
			for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
				setColumnIndex++;
				columnTokenIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( setColumnIndex, ct.column, "_"+columnTokenIndex+"_"+ct.column.javaPropertyName+"_ForWhereInput", saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			saoFileBuffer.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			saoFileBuffer.append( "\t" + "public static int " + methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSql+"\" ) ;\n" );
			}
			setColumnIndex = 0 ;
			columnTokenIndex = 0 ;
			for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
				setColumnIndex++;
				columnTokenIndex++;
				if( ct.columnValue.equals("?") ) {
					nret = SqlActionColumn.dumpSetInputColumn( setColumnIndex, ct.column, "_"+columnTokenIndex+"_"+ct.column.javaPropertyName+"_ForSetInput", saoFileBuffer ) ;
					if( nret != 0 ) {
						System.out.println( "DumpSetInputColumn[\"+table.tableName+\"][\"+ct.columnName+\"] failed["+nret+"]" );
						return nret;
					}
				}
			}
			saoFileBuffer.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		}
		saoFileBuffer.append( "\t" + "}\n" );
		
		return 0;
	}
	
	public static int deleteSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
											String sql, String statementSql, String methodName,
											String statementInterceptorMethodName,
											SqlActionDatabase database, SqlActionTable table,
											SqlActionSyntaxParser parser,
											StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {
		
		StringBuilder		methodParameters = new StringBuilder() ;
		int					columnIndex ;
		int					nret = 0 ;
		
		methodParameters.append( "Connection conn" );
		
		columnIndex = 0 ;
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			columnIndex++;
			methodParameters.append( ", "+ct.column.javaPropertyType+" _"+columnIndex+"_"+ct.column.javaPropertyName );
		}
		
		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, sql );
		if( statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );
			
			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, sql );
			sauFileBuffer.append( "\t" + "public static String "+statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.whereColumnTokenList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, ct.column, "_"+columnIndex+"_"+ct.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			saoFileBuffer.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		} else {
			saoFileBuffer.append( "\t" + "public static int " + methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+statementInterceptorMethodName+"(\""+statementSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+statementSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionSetColumnToken ct : parser.setColumnTokenList ) {
				columnIndex++;
				if( ct.columnValue.equals("?") ) {
					nret = SqlActionColumn.dumpSetInputColumn( columnIndex, ct.column, table.javaObjectName+"ForSetInput."+ct.column.javaPropertyName, saoFileBuffer ) ;
					if( nret != 0 ) {
						System.out.println( "DumpWhereInputColumn[\"+table.tableName+\"][\"+ct.columnName+\"] failed["+nret+"]" );
						return nret;
					}
				}
			}
			saoFileBuffer.append( "\t\t" + "return prestmt.executeUpdate() ;\n" );
		}
		saoFileBuffer.append( "\t" + "}\n" );
		
		return 0;
	}
	
	private static void OutAppendSql( StringBuilder out, String sql ) {
		String[] sa = sql.split( "\n" ) ;
		for( String s : sa ) {
			if( s.trim().replaceAll("\t","").isEmpty() )
				continue;
			out.append( "\t" + "// "+s+"\n" );
		}
	}
}
