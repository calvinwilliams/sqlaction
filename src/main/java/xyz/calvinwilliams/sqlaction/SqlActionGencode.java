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

	final private static String				SQLACTION_VERSION = "0.2.9.0" ;

	final public static String				SQL_COUNT_ = "count(" ;
	final public static String				SAO_PROPERTY_COUNT_ = "_count_" ;

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

			// Load dbserver.conf.json
			currentPath = Paths.get(System.getProperty("user.dir")) ;
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

			if( dbserverConf.url == null ) {
				System.out.println( "dbserverConf.url["+dbserverConf.dbms+"] invalid" );
				return;
			}

			if( dbserverConf.dbms == null ) {
				if( dbserverConf.url.indexOf("mysql") >= 0 )
					dbserverConf.dbms = SqlActionDatabase.DBMS_MYSQL ;
				else if( dbserverConf.url.indexOf("postgresql") >= 0 )
					dbserverConf.dbms = SqlActionDatabase.DBMS_POSTGRESQL ;
				else if( dbserverConf.url.indexOf("oracle") >= 0 )
					dbserverConf.dbms = SqlActionDatabase.DBMS_ORACLE ;
				else if( dbserverConf.url.indexOf("sqlite") >= 0 )
					dbserverConf.dbms = SqlActionDatabase.DBMS_SQLITE ;
				else if( dbserverConf.url.indexOf("sqlserver") >= 0 )
					dbserverConf.dbms = SqlActionDatabase.DBMS_SQLSERVER ;
			}

			if( dbserverConf.dbms == null ) {
				System.out.println( "dbserverConf.dbms null" );
			} else if( dbserverConf.dbms != SqlActionDatabase.DBMS_MYSQL
					&& dbserverConf.dbms != SqlActionDatabase.DBMS_POSTGRESQL
					&& dbserverConf.dbms != SqlActionDatabase.DBMS_ORACLE
					&& dbserverConf.dbms != SqlActionDatabase.DBMS_SQLITE
					&& dbserverConf.dbms != SqlActionDatabase.DBMS_SQLSERVER ) {
				System.out.println( "dbserverConf.dbms["+dbserverConf.dbms+"] not support" );
				return;
			}

			System.out.println( "--- dbserverConf ---" );
			System.out.println( "  dbms["+dbserverConf.dbms+"]" );
			System.out.println( "driver["+dbserverConf.driver+"]" );
			System.out.println( "   url["+dbserverConf.url+"]" );
			System.out.println( "  user["+dbserverConf.user+"]" );
			System.out.println( "   pwd["+dbserverConf.pwd+"]" );

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
			if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
				conn = DriverManager.getConnection( dbserverConf.url ) ;
			} else {
				conn = DriverManager.getConnection( dbserverConf.url, dbserverConf.user, dbserverConf.pwd ) ;
			}

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
				/*
				nret = SqlActionTable.travelTable( dbserverConf, sqlactionConf, database, tc.table, 1 ) ;
				if( nret != 0 ) {
					System.out.println( "*** ERROR : SqlActionTable.travelTable["+tc.table+"] failed["+nret+"]" );
					return;
				} else {
					System.out.println( "SqlActionTable.travelTable["+tc.table+"] ok" );
				}
				*/

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
				sauFileBuffer.append( "public class "+table.javaSauClassName+" extends "+table.javaSaoClassName+" {\n" );

				saoFileBuffer.append( "\n" );
				for( SqlActionColumn c : table.columnList ) {
					SqlActionColumn.dumpDefineProperty( c, saoFileBuffer );
				}
				saoFileBuffer.append( "\n" );
				saoFileBuffer.append("\t").append("int				").append(SAO_PROPERTY_COUNT_).append(" ; // defining for 'SELECT COUNT(*)'\n");

				// Parse sql actions and dump gencode
				for( String sqlaction : tc.sqlactions ) {
					int						beginMetaData ;
					int						endMetaData ;

					SqlActionSyntaxParser	parser = new SqlActionSyntaxParser() ;

					parser.fromTableTokenList = new LinkedList<SqlActionFromTableToken>() ;
					parser.selectColumnTokenList = new LinkedList<SqlActionSelectColumnToken>() ;
					parser.setColumnTokenList = new LinkedList<SqlActionSetColumnToken>() ;
					parser.whereColumnTokenList = new LinkedList<SqlActionWhereColumnToken>() ;

					parser.fromTableTokenForAdvancedModeList = new LinkedList<SqlActionFromTableTokenForAdvancedMode>() ;
					parser.selectColumnTokenForAdvancedModeList = new LinkedList<SqlActionSelectColumnTokenForAdvancedMode>() ;
					parser.otherClassTokenForAdvancedModeList = new LinkedList<SqlActionOtherClassTokenForAdvancedMode>() ;
					parser.whereColumnTokenForAdvancedModeList = new LinkedList<SqlActionWhereColumnTokenForAdvancedMode>() ;

					parser.sqlaction = sqlaction.trim() ;

					beginMetaData = sqlaction.indexOf( "@@" ) ;
					if( beginMetaData >= 0 ) {
						parser.sql = sqlaction.substring( 0, beginMetaData ) ;
					} else {
						parser.sql = sqlaction ;
					}

					parser.prepareSql = parser.sql.replace("\r\n"," ").replace("\n"," ").replace("\t"," ").replaceAll("\\#\\{[^}]*\\}","").replaceAll("[ ]+"," ").trim() ;

					beginMetaData = sqlaction.indexOf( "@@METHOD(" ) ;
					if( beginMetaData >= 0 ) {
						endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
						if( endMetaData == -1 ) {
							System.out.println( "sql["+parser.sql+"] invalid" );
							return;
						}
						parser.methodName = sqlaction.substring( beginMetaData+9, endMetaData ) ;
					} else {
						parser.methodName = SqlActionUtil.sqlConvertToMethodName(parser.prepareSql) ;
					}

					beginMetaData = sqlaction.indexOf( "@@ADVANCEDMODE" ) ;
					if( beginMetaData >= 0 ) {
						parser.advancedMode = true ;

						// Parse sql FROM statement
						System.out.println( "Parse sql FROM statement ["+parser.sql+"] for advanced mode" );

						nret = parser.parseStatementSyntaxForAdvancedMode_FROM( dbserverConf, sqlactionConf, conn, database, table ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : SqlActionSyntaxParser.parseStatementSyntaxForAdvancedMode_FROM failed["+nret+"]" );
							return;
						} else {
							System.out.println( "SqlActionSyntaxParser.parseStatementSyntaxForAdvancedMode_FROM ok" );
						}

						// Parse sql statements except FROM
						System.out.println( "Parse sql statements except FROM ["+parser.sql+"] for advanced mode" );

						nret = parser.parseStatementSyntaxForAdvancedMode_ExceptFROM( dbserverConf, sqlactionConf, conn, database, table ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : SqlActionSyntaxParser.parseStatementSyntaxForAdvancedMode_ExceptFROM failed["+nret+"]" );
							return;
						} else {
							System.out.println( "SqlActionSyntaxParser.parseStatementSyntaxForAdvancedMode_ExceptFROM ok" );
						}
					} else {
						parser.advancedMode = false ;

						// Parse sql FROM statement
						System.out.println( "Parse sql FROM statement ["+parser.sql+"]" );

						nret = parser.parseStatementSyntax_FROM( dbserverConf, sqlactionConf, conn, database, table ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : SqlActionSyntaxParser.parseStatementSyntax_FROM failed["+nret+"]" );
							return;
						} else {
							System.out.println( "SqlActionSyntaxParser.parseStatementSyntax_FROM ok" );
						}

						// Show all databases and tables and columns and indexes
						/*
						System.out.println( "Show all databases and tables and columns and indexes ["+parser.sql+"]" );

						for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
							nret = SqlActionTable.travelTable( dbserverConf, sqlactionConf, database, ct.tableName, 1 ) ;
							if( nret != 0 ) {
								System.out.println( "*** ERROR : SqlActionTable.travelTable["+ct.tableName+"] failed["+nret+"]" );
								return;
							} else {
								System.out.println( "SqlActionTable.travelTable["+ct.tableName+"] ok" );
							}
						}
						*/

						// Parse sql statements except FROM
						System.out.println( "Parse sql statements except FROM ["+parser.sql+"]" );

						nret = parser.parseStatementSyntax_ExceptFROM( dbserverConf, sqlactionConf, conn, database, table ) ;
						if( nret != 0 ) {
							System.out.println( "*** ERROR : SqlActionSyntaxParser.parseSyntaxExceptFROM failed["+nret+"]" );
							return;
						} else {
							System.out.println( "SqlActionSyntaxParser.parseSyntaxExceptFROM ok" );
						}

						beginMetaData = sqlaction.indexOf( "@@SELECTSEQ(" ) ;
						if( beginMetaData >= 0 ) {
							endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
							if( endMetaData == -1 ) {
								System.out.println( "*** ERROR : sql["+parser.sql+"] invalid" );
								return;
							}
							parser.selectSeq = sqlaction.substring( beginMetaData+12, endMetaData ) ;
						}

						beginMetaData = sqlaction.indexOf( "@@SELECTKEY(" ) ;
						if( beginMetaData >= 0 ) {
							endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
							if( endMetaData == -1 ) {
								System.out.println( "*** ERROR : sql["+parser.sql+"] invalid" );
								return;
							}
							parser.selectKey = sqlaction.substring( beginMetaData+12, endMetaData ) ;
							parser.selectKeyColumn = SqlActionColumn.findColumn( table.columnList , parser.selectKey ) ;
							if( parser.selectKeyColumn == null ) {
								System.out.println( "\t" + "*** ERROR : @@PAGEKEY["+parser.selectKey+"] not found in table["+table.tableName+"]" );
								return;
							}
						}

						beginMetaData = sqlaction.indexOf( "@@PAGEKEY(" ) ;
						if( beginMetaData >= 0 ) {
							endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
							if( endMetaData == -1 ) {
								System.out.println( "*** ERROR : sql["+parser.sql+"] invalid" );
								return;
							}
							parser.pageKey = sqlaction.substring( beginMetaData+10, endMetaData ) ;
							parser.pageKeyColumn = SqlActionColumn.findColumn( table.columnList , parser.pageKey ) ;
							if( parser.pageKeyColumn == null ) {
								System.out.println( "\t" + "*** ERROR : @@PAGEKEY["+parser.pageKey+"] not found in table["+table.tableName+"]" );
								return;
							}
						}

						beginMetaData = sqlaction.indexOf( "@@PAGESORT(" ) ;
						if( beginMetaData >= 0 ) {
							endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
							if( endMetaData == -1 ) {
								System.out.println( "*** ERROR : sql["+parser.sql+"] invalid" );
								return;
							}
							parser.pageSort = sqlaction.substring( beginMetaData+11, endMetaData ) ;
							if( ! parser.pageSort.equalsIgnoreCase("ASC") && ! parser.pageSort.equalsIgnoreCase("DESC") ) {
								System.out.println( "\t" + "*** ERROR : @@PAGESORT["+parser.pageSort+"] invalid" );
								return;
							}
						}

						if( parser.pageKeyColumn != null ) {
							parser.methodName = parser.methodName + "_PAGEKEY_" + parser.pageKeyColumn.columnName ;
						}
					}

					beginMetaData = sqlaction.indexOf( "@@STATEMENT_INTERCEPTOR(" ) ;
					if( beginMetaData >= 0 ) {
						endMetaData = sqlaction.indexOf( ")", beginMetaData ) ;
						if( endMetaData == -1 ) {
							System.out.println( "*** ERROR : sql["+parser.sql+"] invalid" );
							return;
						}
						parser.statementInterceptorMethodName = sqlaction.substring( beginMetaData+24, endMetaData ) ;
						if( parser.statementInterceptorMethodName.equals("") )
							parser.statementInterceptorMethodName = "STATEMENT_INTERCEPTOR_for_"+parser.methodName ;
					}

					System.out.println( "Parse sql action ["+sqlaction+"]" );
					System.out.println( "\t" + "                           sql["+parser.sql+"]" );
					System.out.println( "\t" + "                  advancedMode["+parser.advancedMode+"]" );
					System.out.println( "\t" + "                     selectKey["+parser.selectKey+"]" );
					System.out.println( "\t" + "                    methodName["+parser.methodName+"]" );
					System.out.println( "\t" + "statementInterceptorMethodName["+parser.statementInterceptorMethodName+"]" );

					// Fixed SELECT * by fill all column to parser.selectColumnTokenList
					System.out.println( "Fixed SELECT * by fill all column to parser.selectColumnTokenList ["+parser.sql+"]" );

					// Show parser result
					System.out.println( "Show parser result ["+parser.sql+"]" );

					System.out.println( "\t" + "sqlPredicate["+parser.sqlPredicate+"]" );

					System.out.println( "\t" + "selectHint["+parser.selectHint+"]" );

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

					for( SqlActionSelectColumnTokenForAdvancedMode cta : parser.selectColumnTokenForAdvancedModeList ) {
						if( cta.table != null ) {
							if( cta.column != null ) {
								System.out.println( "\t" + "selectColumnTokenForAdvancedMode.tableName["+cta.table.tableName+"] .table["+cta.table.tableName+"] .columnName["+cta.columnName+"] .column["+cta.column.columnName+"]" );
							} else {
								System.out.println( "\t" + "selectColumnTokenForAdvancedMode.tableName["+cta.table.tableName+"] .table["+cta.table.tableName+"] .columnName["+cta.columnName+"]" );
							}
						} else {
							System.out.println( "\t" + "selectColumnTokenForAdvancedMode.javaClassName["+cta.javaClassName+"] .javaObjectName["+cta.javaObjectName+"] .javaPropertyName["+cta.javaPropertyName+"] .javaPropertyType["+cta.javaPropertyType+"]" );
						}
					}

					for( SqlActionFromTableTokenForAdvancedMode cta : parser.fromTableTokenForAdvancedModeList ) {
						System.out.println( "\t" + "fromTableTokenForAdvancedMode.tableName["+cta.table.tableName+"]" );
					}

					for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
						System.out.println( "\t" + "whereColumnTokenForAdvancedMode.tableName["+cta.table.tableName+"] .columnName["+cta.column.columnName+"]" );
					}

					// Dump gencode
					System.out.println( "Dump gencode ["+parser.sql+"]" );

					if( parser.advancedMode == true ) {
						if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_SELECT ) {
							nret = selectSqlDumpGencodeForAdvancedMode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
							if( nret != 0 ) {
								System.out.println( "\t" + "*** ERROR : selectSqlDumpGencodeForAdvancedMode failed["+nret+"]" );
								return;
							} else {
								System.out.println( "\t" + "selectSqlDumpGencodeForAdvancedMode ok" );
							}
						} else if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_INSERT || parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_UPDATE || parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_DELETE ) {
							nret = insertOrUpdateOrDeleteSqlDumpGencodeForAdvancedMode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
							if( nret != 0 ) {
								System.out.println( "\t" + "*** ERROR : insertOrUpdateOrDeleteSqlDumpGencodeForAdvancedMode failed["+nret+"]" );
								return;
							} else {
								System.out.println( "\t" + "insertOrUpdateOrDeleteSqlDumpGencodeForAdvancedMode ok" );
							}
						} else {
							System.out.println( "\t" + "Action["+sqlaction+"] invalid" );
							return;
						}
					} else {
						if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_SELECT ) {
							nret = selectSqlDumpGencode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
							if( nret != 0 ) {
								System.out.println( "\t" + "*** ERROR : SelectSqlDumpGencode failed["+nret+"]" );
								return;
							} else {
								System.out.println( "\t" + "SelectSqlDumpGencode ok" );
							}
						} else if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_INSERT ) {
							nret = insertSqlDumpGencode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
							if( nret != 0 ) {
								System.out.println( "\t" + "*** ERROR : InsertSqlDumpGencode failed["+nret+"]" );
								return;
							} else {
								System.out.println( "\t" + "InsertSqlDumpGencode ok" );
							}
						} else if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_UPDATE ) {
							nret = updateSqlDumpGencode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
							if( nret != 0 ) {
								System.out.println( "\t" + "*** ERROR : UpdateSqlDumpGencode failed["+nret+"]" );
								return;
							} else {
								System.out.println( "\t" + "UpdateSqlDumpGencode ok" );
							}
						} else if( parser.sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_DELETE ) {
							nret = deleteSqlDumpGencode( dbserverConf, sqlactionConf, tc, database, table, parser, saoFileBuffer, sauFileBuffer ) ;
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
			SqlActionDatabase database, SqlActionTable table,
			SqlActionSyntaxParser parser,
			StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {

		StringBuilder	methodParameters = new StringBuilder() ;
		StringBuilder	interceptorMethodInvokeParameters = new StringBuilder() ;
		StringBuilder	interceptorMethodInputParameters = new StringBuilder() ;
		int				fromPos = -1 ;
		int				wherePos = -1 ;
		int				orderPos = -1 ;
		int				columnIndex ;
		int				nret = 0 ;

		methodParameters.append( "Connection conn" );
		
		for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
			methodParameters.append( ", List<"+ct.table.javaSauClassName+"> "+ct.table.javaObjectName+"ListForSelectOutput" );
		}

		columnIndex = 0 ;
		for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
			columnIndex++;
			methodParameters.append( ", "+ct.column.javaPropertyType+" _"+columnIndex+"_"+ct.table.javaSauClassName+"_"+ct.column.javaPropertyName );
			interceptorMethodInvokeParameters.append( ", _"+columnIndex+"_"+ct.table.javaSauClassName+"_"+ct.column.javaPropertyName );
			interceptorMethodInputParameters.append( ", "+ct.column.javaPropertyType+" _"+columnIndex+"_"+ct.table.javaSauClassName+"_"+ct.column.javaPropertyName );
		}
		if( parser.pageKeyColumn != null ) {
			columnIndex++;
			methodParameters.append( ", int _"+columnIndex+"_pageSize, int _"+(columnIndex+1)+"_pageNum" );

			if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
				if( parser.hasWhereStatement ) {
					wherePos = SqlActionUtil.indexOfWord( parser.prepareSql.toUpperCase() , "WHERE" ) ;
					// [SQL1                    ][SQL2]
					// SELECT * FROM table WHERE ...
					// [SQL1                    ][CONST                                                            ][SQL2][CONST]
					// SELECT * FROM table WHERE id<>null AND id>=(SELECT id FROM table ORDER BY key LIMIT ?,1) AND ...   LIMIT ?
					parser.prepareSql = parser.prepareSql.substring(0,wherePos+5) + " "+parser.pageKeyColumn.columnName+((parser.pageSort==null||parser.pageSort.equalsIgnoreCase("ASC"))?">=":"<=")+"(SELECT "+parser.pageKeyColumn.columnName+" FROM "+table.tableName+" ORDER BY "+parser.pageKeyColumn.columnName+(parser.pageSort!=null?" "+parser.pageSort:"")+" LIMIT ?,1) AND"+parser.prepareSql.substring(wherePos+5)+" ORDER BY "+parser.pageKeyColumn.columnName+(parser.pageSort!=null?" "+parser.pageSort:"")+" LIMIT ?" ;
				} else {
					// [SQL1             ]
					// SELECT * FROM table
					// [SQL1              ][CONST                                                               ]
					// SELECT * FROM table WHERE id<>null AND id>=(SELECT id FROM table ORDER BY key LIMIT ?,1) LIMIT ?
					parser.prepareSql = parser.prepareSql + " WHERE "+parser.pageKeyColumn.columnName+((parser.pageSort==null||parser.pageSort.equalsIgnoreCase("ASC"))?">=":"<=")+"(SELECT "+parser.pageKeyColumn.columnName+" FROM "+table.tableName+" ORDER BY "+parser.pageKeyColumn.columnName+(parser.pageSort!=null?" "+parser.pageSort:"")+" LIMIT ?,1) ORDER BY "+parser.pageKeyColumn.columnName+(parser.pageSort!=null?" "+parser.pageSort:"")+" LIMIT ?" ;
				}
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
				parser.prepareSql += " OFFSET ? LIMIT ?" ;
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
				fromPos = parser.prepareSql.toUpperCase().indexOf("FROM") ;
				wherePos = SqlActionUtil.indexOfWord( parser.prepareSql.toUpperCase() , "WHERE" ) ;
				orderPos = SqlActionUtil.indexOfWord( parser.prepareSql.toUpperCase() , "ORDER" ) ;
				if( wherePos == -1 && orderPos == -1 ) {
					// [SQL1   ][SQL2         ]
					// SELECT * FROM user_order
					// [SQL1   ][CONST                           ][SQL2         ][CONST                              ]
					// SELECT * FROM ( SELECT t.*,ROWNUM AS rowno FROM user_order t WHERE ROWNUM < ? ) WHERE rowno >= ?
					parser.prepareSql = parser.prepareSql.substring(0,fromPos) + "FROM ( SELECT t.*,ROWNUM AS rowno " + parser.prepareSql.substring(fromPos) + " t WHERE ROWNUM < ? ) WHERE rowno >= ?" ;
				} else if( wherePos >= 0 && orderPos == -1 ) {
					// [SQL1   ][SQL2                   ]
					// SELECT * FROM user_order WHERE ...
					// [SQL1   ][CONST                                           ][SQL2                   ][CONST                             ]
					// SELECT * FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT * FROM user_order WHERE ... AND ROWNUM < ? ) WHERE rowno >= ? )
					parser.prepareSql = parser.prepareSql.substring(0,fromPos) + "FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT *" + parser.prepareSql.substring(fromPos) + " AND ROWNUM < ? ) WHERE rowno >= ? )" ;
				} else if( wherePos == -1 && orderPos >= 0 ) {
					// [SQL1   ][SQL2                      ]
					// SELECT * FROM user_order ORDER BY ...
					// [SQL1   ][CONST                                           ][SQL2                                  ][CONST                                ]
					// SELECT * FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT * FROM user_order ORDER BY total_price ASC ) t WHERE ROWNUM < ? ) WHERE rowno >= ?
					parser.prepareSql = parser.prepareSql.substring(0,fromPos) + "FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT * " + parser.prepareSql.substring(fromPos) + " ) t WHERE ROWNUM < ? ) WHERE rowno >= ?" ;
				} else {
					// [SQL1   ][SQL2                                ]
					// SELECT * FROM user_order WHERE ... ORDER BY ...
					// [SQL1   ][CONST                                           ][SQL2                                            ][CONST                                ]
					// SELECT * FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT * FROM user_order WHERE ... ORDER BY total_price ASC ) t WHERE ROWNUM < ? ) WHERE rowno >= ?
					parser.prepareSql = parser.prepareSql.substring(0,fromPos) + "FROM ( SELECT t.*,ROWNUM AS rowno FROM ( SELECT * " + parser.prepareSql.substring(fromPos) + " ) t WHERE ROWNUM < ? ) WHERE rowno >= ?" ;
				}
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
				parser.prepareSql += " LIMIT ? OFFSET ?" ;
			} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
				parser.prepareSql += " ORDER BY "+parser.pageKeyColumn.columnName+(parser.pageSort!=null?" "+parser.pageSort:"")+" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY" ;
			}
		}

		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql"+interceptorMethodInputParameters+" ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql"+interceptorMethodInputParameters+" ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		saoFileBuffer.append( "\t" + "public static int " + parser.methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
		if( parser.whereColumnTokenList.size() > 0 || parser.pageKeyColumn != null ) {
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\""+interceptorMethodInvokeParameters+") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnToken ct : parser.whereColumnTokenList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, ct.column, "_"+columnIndex+"_"+ct.table.javaSauClassName+"_"+ct.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+ct.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			if( parser.pageKeyColumn != null ) {
				columnIndex++;
				int pageColumnIndex = columnIndex ;
				if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*(_"+(pageColumnIndex+1)+"_pageNum-1)").append(" );\n");
					columnIndex++;
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+(pageColumnIndex)+"_pageSize").append(" );\n");
				} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*(_"+(pageColumnIndex+1)+"_pageNum-1)").append(" );\n");
					columnIndex++;
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+(pageColumnIndex)+"_pageSize").append(" );\n");
				} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*_"+(pageColumnIndex+1)+"_pageNum").append(" );\n");
					columnIndex++;
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*(_"+(pageColumnIndex+1)+"_pageNum-1)").append(" );\n");
				} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+(pageColumnIndex)+"_pageSize").append(" );\n");
					columnIndex++;
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*(_"+(pageColumnIndex+1)+"_pageNum-1)").append(" );\n");
				} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+pageColumnIndex+"_pageSize*(_"+(pageColumnIndex+1)+"_pageNum-1)").append(" );\n");
					columnIndex++;
					saoFileBuffer.append("\t\t").append("prestmt.setInt( ").append(columnIndex).append(", ").append("_"+(pageColumnIndex)+"_pageSize").append(" );\n");
				}
			}
			saoFileBuffer.append( "\t\t" + "ResultSet rs = prestmt.executeQuery() ;\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( \""+parser.prepareSql+"\" ) ;\n" );
			}
		}
		saoFileBuffer.append( "\t\t" + "while( rs.next() ) {\n" );
		for( SqlActionFromTableToken ct : parser.fromTableTokenList ) {
			saoFileBuffer.append( "\t\t\t" + ct.table.javaSauClassName + " "+ct.table.javaObjectName+" = new "+ct.table.javaSauClassName+"() ;\n" );
		}
		if( parser.selectColumnTokenList.size() > 0 ) {
			columnIndex = 0 ;
			for( SqlActionSelectColumnToken ct : parser.selectColumnTokenList ) {
				columnIndex++;
				if( ct.columnName.equalsIgnoreCase(SqlActionGencode.SQL_COUNT_) ) {
					saoFileBuffer.append("\t\t\t").append(ct.table.javaObjectName+"."+SAO_PROPERTY_COUNT_).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
				} else {
					nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t\t", columnIndex, ct.column, ct.table.javaObjectName+"."+ct.column.javaPropertyName, saoFileBuffer ) ;
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
		saoFileBuffer.append( "\t\t" + "rs.close();\n" );
		if( parser.whereColumnTokenList.size() > 0 || parser.pageKeyColumn != null ) {
			saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
		}
		saoFileBuffer.append( "\t\t" + "return "+parser.fromTableTokenList.get(0).table.javaObjectName+"ListForSelectOutput.size();\n" );
		saoFileBuffer.append( "\t" + "}\n" );

		return 0;
	}

	public static int insertSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
			SqlActionDatabase database, SqlActionTable table,
			SqlActionSyntaxParser parser,
			StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {

		StringBuilder		statementSqlBuilder = new StringBuilder() ;
		StringBuilder		methodParameters = new StringBuilder() ;
		int					columnIndex ;
		int					nret = 0 ;

		statementSqlBuilder.append( parser.prepareSql + " (" );

		columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == true )
				continue;

			columnIndex++;
			if( columnIndex > 1 )
				statementSqlBuilder.append( "," );
			statementSqlBuilder.append( c.columnName );
		}

		statementSqlBuilder.append( ") VALUES (" );

		columnIndex = 0 ;
		for( SqlActionColumn c : table.columnList ) {
			if( c.isAutoIncrement == true )
				continue;

			columnIndex++;
			if( columnIndex > 1 )
				statementSqlBuilder.append( "," );
			statementSqlBuilder.append( "?" );
		}

		statementSqlBuilder.append( ")" );

		parser.sql = statementSqlBuilder.toString() ;

		methodParameters.append( "Connection conn, " + table.javaSauClassName + " " + table.javaObjectName );

		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		saoFileBuffer.append( "\t" + "public static int " + parser.methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
		saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt ;\n" );
		saoFileBuffer.append( "\t\t" + "Statement stmt ;\n" );
		saoFileBuffer.append( "\t\t" + "ResultSet rs ;\n" );
		if( ( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL || dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) && parser.selectSeq != null && parser.selectKey != null ) {
			saoFileBuffer.append( "\t\t" + "stmt = conn.createStatement() ;\n" );
			saoFileBuffer.append( "\t\t" + "rs = stmt.executeQuery( \"SELECT NEXTVAL('"+parser.selectSeq+"')\" ) ;\n" );
			saoFileBuffer.append( "\t\t" + "rs.next();\n" );
			nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t", 1, parser.selectKeyColumn, table.javaObjectName+"."+parser.selectKeyColumn.javaPropertyName, saoFileBuffer ) ;
			if( nret != 0 ) {
				System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+parser.selectKeyColumn.columnName+"] failed["+nret+"]" );
				return nret;
			}
			saoFileBuffer.append( "\t\t" + "rs.close();\n" );
			saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER && parser.selectSeq != null && parser.selectKey != null ) {
			saoFileBuffer.append( "\t\t" + "stmt = conn.createStatement() ;\n" );
			saoFileBuffer.append( "\t\t" + "rs = stmt.executeQuery( \"SELECT NEXT VALUE FOR "+parser.selectSeq+"\" ) ;\n" );
			saoFileBuffer.append( "\t\t" + "rs.next();\n" );
			nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t", 1, parser.selectKeyColumn, table.javaObjectName+"."+parser.selectKeyColumn.javaPropertyName, saoFileBuffer ) ;
			if( nret != 0 ) {
				System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+parser.selectKeyColumn.columnName+"] failed["+nret+"]" );
				return nret;
			}
			saoFileBuffer.append( "\t\t" + "rs.close();\n" );
			saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
		}
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t\t" + "prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "prestmt = conn.prepareStatement( \""+statementSqlBuilder+"\" ) ;\n" );
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
		if( dbserverConf.dbms == SqlActionDatabase.DBMS_MYSQL ) {
			if( parser.selectKeyColumn == null ) {
				saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
				saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "return count;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
				saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "if( count != 1 )\n" );
				saoFileBuffer.append( "\t\t" + "	return count;\n" );
				saoFileBuffer.append( "\t\t" + "\n" );
				saoFileBuffer.append( "\t\t" + "stmt = conn.createStatement() ;\n" );
				saoFileBuffer.append( "\t\t" + "rs = stmt.executeQuery( \"SELECT LAST_INSERT_ID()\" ) ;\n" );
				saoFileBuffer.append( "\t\t" + "rs.next();\n" );
				nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t", 1, parser.selectKeyColumn, table.javaObjectName+"."+parser.selectKeyColumn.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+parser.selectKeyColumn.columnName+"] failed["+nret+"]" );
					return nret;
				}
				saoFileBuffer.append( "\t\t" + "rs.close();\n" );
				saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "\n" );
				saoFileBuffer.append( "\t\t" + "return count;\n" );
			}
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_POSTGRESQL ) {
			saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
			saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
			saoFileBuffer.append( "\t\t" + "return count;\n" );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_ORACLE ) {
			saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
			saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
			saoFileBuffer.append( "\t\t" + "return count;\n" );
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLITE ) {
			if( parser.selectKeyColumn == null ) {
				saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
				saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "return count;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
				saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "if( count != 1 )\n" );
				saoFileBuffer.append( "\t\t" + "	return count;\n" );
				saoFileBuffer.append( "\t\t" + "\n" );
				saoFileBuffer.append( "\t\t" + "stmt = conn.createStatement() ;\n" );
				saoFileBuffer.append( "\t\t" + "rs = stmt.executeQuery( \"SELECT last_insert_rowid()\" ) ;\n" );
				saoFileBuffer.append( "\t\t" + "rs.next();\n" );
				nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t", 1, parser.selectKeyColumn, table.javaObjectName+"."+parser.selectKeyColumn.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+parser.selectKeyColumn.columnName+"] failed["+nret+"]" );
					return nret;
				}
				saoFileBuffer.append( "\t\t" + "rs.close();\n" );
				saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
				saoFileBuffer.append( "\t\t" + "\n" );
				saoFileBuffer.append( "\t\t" + "return count;\n" );
			}
		} else if( dbserverConf.dbms == SqlActionDatabase.DBMS_SQLSERVER ) {
			saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
			saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
			saoFileBuffer.append( "\t\t" + "return count;\n" );
		}
		saoFileBuffer.append( "\t" + "}\n" );

		return 0;
	}

	public static int updateSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
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
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.setColumnTokenList.size() > 0 || parser.whereColumnTokenList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
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
		} else {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
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
		}
		saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
		saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
		saoFileBuffer.append( "\t\t" + "return count;\n" );
		saoFileBuffer.append( "\t" + "}\n" );

		return 0;
	}

	public static int deleteSqlDumpGencode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
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
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.whereColumnTokenList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
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
		} else {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
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
		}
		saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
		saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
		saoFileBuffer.append( "\t\t" + "return count;\n" );
		saoFileBuffer.append( "\t" + "}\n" );

		return 0;
	}

	public static int selectSqlDumpGencodeForAdvancedMode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
			SqlActionDatabase database, SqlActionTable table,
			SqlActionSyntaxParser parser,
			StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {

		StringBuilder	methodParameters = new StringBuilder() ;
		int				fromPos = -1 ;
		int				wherePos = -1 ;
		int				orderPos = -1 ;
		int				columnIndex ;
		int				nret = 0 ;

		methodParameters.append( "Connection conn" );
		
		for( SqlActionFromTableTokenForAdvancedMode cta : parser.fromTableTokenForAdvancedModeList ) {
			methodParameters.append( ", List<"+cta.table.javaSauClassName+"> "+cta.table.javaObjectName+"ListForSelectOutput" );
		}
		for( SqlActionOtherClassTokenForAdvancedMode cta : parser.otherClassTokenForAdvancedModeList ) {
			methodParameters.append( ", List<"+cta.javaClassName+"> "+cta.javaObjectName+"ListForSelectOutput" );
		}
		
		columnIndex = 0 ;
		for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
			columnIndex++;
			methodParameters.append( ", "+cta.column.javaPropertyType+" _"+columnIndex+"_"+cta.table.javaSauClassName+"_"+cta.column.javaPropertyName );
		}

		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		saoFileBuffer.append( "\t" + "public static int " + parser.methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
		if( parser.whereColumnTokenForAdvancedModeList.size() > 0 ) {
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, cta.column, "_"+columnIndex+"_"+cta.table.javaSauClassName+"_"+cta.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+cta.column.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
			saoFileBuffer.append( "\t\t" + "ResultSet rs = prestmt.executeQuery() ;\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "Statement stmt = conn.createStatement() ;\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "ResultSet rs = stmt.executeQuery( \""+parser.prepareSql+"\" ) ;\n" );
			}
		}
		saoFileBuffer.append( "\t\t" + "while( rs.next() ) {\n" );
		for( SqlActionFromTableTokenForAdvancedMode cta : parser.fromTableTokenForAdvancedModeList ) {
			saoFileBuffer.append( "\t\t\t" + cta.table.javaSauClassName + " "+cta.table.javaObjectName+" = new "+cta.table.javaSauClassName+"() ;\n" );
		}
		for( SqlActionOtherClassTokenForAdvancedMode oct : parser.otherClassTokenForAdvancedModeList ) {
			saoFileBuffer.append( "\t\t\t" + oct.javaClassName + " "+oct.javaObjectName+" = new "+oct.javaClassName+"() ;\n" );
		}
		if( parser.selectColumnTokenForAdvancedModeList.size() > 0 ) {
			columnIndex = 0 ;
			for( SqlActionSelectColumnTokenForAdvancedMode cta : parser.selectColumnTokenForAdvancedModeList ) {
				columnIndex++;
				if( cta.table != null ) {
					if( cta.column != null ) {
						nret = SqlActionColumn.dumpSelectOutputColumn( "\t\t\t", columnIndex, cta.column, cta.table.javaObjectName+"."+cta.column.javaPropertyName, saoFileBuffer ) ;
						if( nret != 0 ) {
							System.out.println( "DumpSelectOutputColumn["+table.tableName+"]["+cta.column.columnName+"] failed["+nret+"]" );
							return nret;
						}
					} else {
						saoFileBuffer.append("\t\t\t").append(cta.table.javaObjectName+"."+cta.columnName).append(" = rs.getInt( "+columnIndex+" ) ;\n" );
					}
				} else {
					saoFileBuffer.append("\t\t\t").append(cta.javaObjectName+"."+cta.javaPropertyName).append(" = rs.get"+cta.javaPropertyType+"( "+columnIndex+" ) ;\n" );
				}
			}
		}
		for( SqlActionFromTableTokenForAdvancedMode cta : parser.fromTableTokenForAdvancedModeList ) {
			saoFileBuffer.append( "\t\t\t" + cta.table.javaObjectName+"ListForSelectOutput.add("+cta.table.javaObjectName+") ;\n" );
		}
		for( SqlActionOtherClassTokenForAdvancedMode oct : parser.otherClassTokenForAdvancedModeList ) {
			saoFileBuffer.append( "\t\t\t" + oct.javaObjectName+"ListForSelectOutput.add("+oct.javaObjectName+") ;\n" );
		}
		saoFileBuffer.append( "\t\t" + "}\n" );
		saoFileBuffer.append( "\t\t" + "rs.close();\n" );
		if( parser.whereColumnTokenForAdvancedModeList.size() > 0 ) {
			saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
		} else {
			saoFileBuffer.append( "\t\t" + "stmt.close();\n" );
		}
		saoFileBuffer.append( "\t\t" + "return "+parser.fromTableTokenForAdvancedModeList.get(0).table.javaObjectName+"ListForSelectOutput.size();\n" );
		saoFileBuffer.append( "\t" + "}\n" );

		return 0;
	}

	public static int insertOrUpdateOrDeleteSqlDumpGencodeForAdvancedMode( DbServerConf dbserverConf, SqlActionConf sqlactionConf, SqlActionConfTable sqlactionConfTable,
			SqlActionDatabase database, SqlActionTable table,
			SqlActionSyntaxParser parser,
			StringBuilder saoFileBuffer, StringBuilder sauFileBuffer ) {

		StringBuilder		methodParameters = new StringBuilder() ;
		int					columnIndex ;
		int					nret = 0 ;

		methodParameters.append( "Connection conn" );

		columnIndex = 0 ;
		for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
			columnIndex++;
			methodParameters.append( ", "+cta.column.javaPropertyType+" _"+columnIndex+"_"+cta.column.javaPropertyName );
		}

		saoFileBuffer.append( "\n" );
		OutAppendSql( saoFileBuffer, parser.sqlaction );
		if( parser.statementInterceptorMethodName != null ) {
			saoFileBuffer.append( "\t" + "/*\n" );
			saoFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			saoFileBuffer.append( "\t\t" + "\n" );
			saoFileBuffer.append( "\t\t" + "return statementSql;\n" );
			saoFileBuffer.append( "\t" + "}\n" );
			saoFileBuffer.append( "\t" + "*/\n" );

			sauFileBuffer.append( "\t" + "\n" );
			OutAppendSql( sauFileBuffer, parser.sqlaction );
			sauFileBuffer.append( "\t" + "public static String "+parser.statementInterceptorMethodName+"( String statementSql ) {\n" );
			sauFileBuffer.append( "\t\t" + "\n" );
			sauFileBuffer.append( "\t\t" + "return statementSql;\n" );
			sauFileBuffer.append( "\t" + "}\n" );
		}
		if( parser.whereColumnTokenForAdvancedModeList.size() > 0 ) {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpWhereInputColumn( columnIndex, cta.column, "_"+columnIndex+"_"+cta.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn["+table.tableName+"]["+cta.column.columnName+"] failed["+nret+"]" );
					return nret;
				}
			}
		} else {
			saoFileBuffer.append( "\t" + "public static int " + parser.methodName.toString() + "( "+methodParameters.toString()+" ) throws Exception {\n" );
			if( parser.statementInterceptorMethodName != null ) {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( "+table.javaSauClassName+"."+parser.statementInterceptorMethodName+"(\""+parser.prepareSql+"\") ) ;\n" );
			} else {
				saoFileBuffer.append( "\t\t" + "PreparedStatement prestmt = conn.prepareStatement( \""+parser.prepareSql+"\" ) ;\n" );
			}
			columnIndex = 0 ;
			for( SqlActionWhereColumnTokenForAdvancedMode cta : parser.whereColumnTokenForAdvancedModeList ) {
				columnIndex++;
				nret = SqlActionColumn.dumpSetInputColumn( columnIndex, cta.column, table.javaObjectName+"ForSetInput."+cta.column.javaPropertyName, saoFileBuffer ) ;
				if( nret != 0 ) {
					System.out.println( "DumpWhereInputColumn[\"+table.tableName+\"][\"+cta.columnName+\"] failed["+nret+"]" );
					return nret;
				}
			}
		}
		saoFileBuffer.append( "\t\t" + "int count = prestmt.executeUpdate() ;\n" );
		saoFileBuffer.append( "\t\t" + "prestmt.close();\n" );
		saoFileBuffer.append( "\t\t" + "return count;\n" );
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
