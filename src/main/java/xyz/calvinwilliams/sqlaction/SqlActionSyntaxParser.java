/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction;

import java.sql.Connection;
import java.util.*;

public class SqlActionSyntaxParser {

	public SqlActionPredicateEnum			sqlPredicate ;
	public SqlActionStatementEnum			sqlStatement ;
	
	public String							selectHint ;
	public boolean							selectAllColumn ;
	public List<SqlActionSelectColumnToken>	selectColumnTokenList ;
	public List<SqlActionFromTableToken>	fromTableTokenList ;
	
	public String							insertTableName ;
	
	public String							updateTableName ;
	public List<SqlActionSetColumnToken>	setColumnTokenList ;
	
	public String							deleteTableName ;
	
	public boolean							hasWhereStatement ;
	public List<SqlActionWhereColumnToken>	whereColumnTokenList ;
	
	public String							otherTokens ;
	
	String									pageKey ;
	public SqlActionColumn					pageKeyColumn ;
	String									pageSort ;
	
	public List<SqlActionSelectColumnTokenForAdvancedMode>	selectColumnTokenForAdvancedModeList ;
	public List<SqlActionFromTableTokenForAdvancedMode>		fromTableTokenForAdvancedModeList ;
	public List<SqlActionWhereColumnTokenForAdvancedMode>	whereColumnTokenForAdvancedModeList ;
	
	String									sqlaction ;
	String									sql ;
	boolean									advancedMode ;
	String									prepareSql ;
	String									selectSeq ;
	String									selectKey ;
	public SqlActionColumn					selectKeyColumn ;
	String									methodName ;
	String									statementInterceptorMethodName ;
	
	public int parseStatementSyntax_FROM( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception {
		SqlActionLexicalParser	lexicalParser ;
		String					token ;
		SqlActionFromTableToken fromTableToken ;
		
		lexicalParser = new SqlActionLexicalParser() ;
		lexicalParser.setSqlString(sql);
		
		fromTableTokenList = new LinkedList<SqlActionFromTableToken>() ;
		
		while(true) {
			token = lexicalParser.getSqlToken() ;
			if( token == null )
				return 0;
			else if( token.equalsIgnoreCase("FROM") )
				break;
		}
		
		// Support for
		// FROM table (WHERE|GROUP|ORDER|HAVING)
		// FROM table t (WHERE|GROUP|ORDER|HAVING)
		// FROM table,table2 (WHERE|GROUP|ORDER|HAVING)
		// FROM table t,table2 t2 (WHERE|GROUP|ORDER|HAVING)
		while(true) {
			String token1 = lexicalParser.getSqlToken() ;
			if( token1 == null ) {
				//      1
				// FROM \0
				return -11;
			}
			String token2 = lexicalParser.getSqlToken() ;
			if( token2 == null ) {
				//      1
				// FROM table\0
				fromTableToken = new SqlActionFromTableToken() ;
				fromTableToken.tableName = token1 ;
				fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
				if( fromTableToken.table == null ) {
					System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
					return -12;
				}
				fromTableTokenList.add(fromTableToken);
				break;
			} else if( token2.equalsIgnoreCase("WHERE") || token2.equalsIgnoreCase("GROUP") || token2.equalsIgnoreCase("ORDER") || token2.equalsIgnoreCase("HAVING") ) {
				//      1     2
				// FROM table (WHERE|GROUP|ORDER|HAVING)
				fromTableToken = new SqlActionFromTableToken() ;
				fromTableToken.tableName = token1 ;
				fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
				if( fromTableToken.table == null ) {
					System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
					return -13;
				}
				fromTableTokenList.add(fromTableToken);
				break;
			} else if( token2.equals(",") ) {
				//      1    2
				// FROM table,
				fromTableToken = new SqlActionFromTableToken() ;
				fromTableToken.tableName = token1 ;
				fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
				if( fromTableToken.table == null ) {
					System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
					return -14;
				}
				fromTableTokenList.add(fromTableToken);
				continue;
			} else {
				//      1      2
				// FROM table1 t1
				String token3 = lexicalParser.getSqlToken() ;
				if( token3 == null ) {
					//      1     2 3
					// FROM table t\0
					fromTableToken = new SqlActionFromTableToken() ;
					fromTableToken.tableName = token1 ;
					fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
					if( fromTableToken.table == null ) {
						System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
						return -151;
					}
					fromTableToken.tableAliasName = token2 ;
					fromTableTokenList.add(fromTableToken);

					break;
				} else if( token3.equals(",") ) {
					//      1     2 3
					// FROM table t,
					fromTableToken = new SqlActionFromTableToken() ;
					fromTableToken.tableName = token1 ;
					fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
					if( fromTableToken.table == null ) {
						System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
						return -152;
					}
					fromTableToken.tableAliasName = token2 ;
					fromTableTokenList.add(fromTableToken);
					continue;
				} else if( token3.equalsIgnoreCase("WHERE") || token3.equalsIgnoreCase("GROUP") || token3.equalsIgnoreCase("ORDER") || token3.equalsIgnoreCase("HAVING") ) {
					//      1     2  3
					// FROM table t (WHERE|GROUP|ORDER|HAVING)
					fromTableToken = new SqlActionFromTableToken() ;
					fromTableToken.tableName = token1 ;
					fromTableToken.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, fromTableToken.tableName ) ;
					if( fromTableToken.table == null ) {
						System.out.println( "\t" + "*** ERROR : tableName["+fromTableToken.tableName+"] not found in database["+database.databaseName+"]" );
						return -153;
					}
					fromTableToken.tableAliasName = token2 ;
					fromTableTokenList.add(fromTableToken);
					break;
				} else {
					return -154;
				}
			}
		}
		
		return 0;
	}
	
	public int parseStatementSyntax_ExceptFROM( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) {
		SqlActionSelectColumnToken	selectColumnToken = null ;
		SqlActionSetColumnToken		setColumnToken = null ;
		SqlActionWhereColumnToken	whereColumnToken = null ;
		String						token9 = null ;
		
		SqlActionLexicalParser lexicalParser = new SqlActionLexicalParser() ;
		lexicalParser.setSqlString(sql);
		
		selectColumnTokenList = new LinkedList<SqlActionSelectColumnToken>() ;
		setColumnTokenList = new LinkedList<SqlActionSetColumnToken>() ;
		whereColumnTokenList = new LinkedList<SqlActionWhereColumnToken>() ;
		
		String	token = lexicalParser.getSqlToken() ;
		if( token == null )
			return 0;
		while( token != null ) {
			if( token.equalsIgnoreCase("SELECT") ) {
				if( sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_NONE )
					sqlPredicate = SqlActionPredicateEnum.SQLACTION_PREDICATE_SELECT ;
				
				// Support for
				// SELECT * (FROM)
				// SELECT [table.]column,... (FROM)
				// SELECT FUNC([table.]column,...),... (FROM)
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -21;
					}
					if( token1.length() > 2 && ( token1.charAt(0) == '/' || token1.charAt(1) == '*' ) ) {
						selectHint = token1 ;
						continue;
					} else if( token1.equals("*") ) {
						// SELECT *
						selectAllColumn = true ;
						
						String token2 = lexicalParser.getSqlToken() ;
						if( token2 == null ) {
							// SELECT *\0
							token = token2 ;
							break;
						} else if( token2.equalsIgnoreCase("FROM") ) {
							// SELECT * FROM
							token = token2 ;
							break;
						} else {
							System.out.println( "sql["+sql+"] invalid" );
							return -22;
						}
					} else if( token1.equalsIgnoreCase("COUNT") ) {
						String token2 = lexicalParser.getSqlToken() ;
						if( token2 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -221;
						} else if( ! token2.equals("(") ) {
							System.out.println( "\t" + "*** ERROR : expect '(' after '"+token1+"' on parsing sql["+sql+"]" );
							return -222;
						}
						
						String token3 = lexicalParser.getSqlToken() ;
						if( token3 == null ) {
							System.out.println( "\t" + "*** ERROR : expect something after '"+token1+token2+"' on parsing sql["+sql+"]" );
							return -223;
						}
						
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -224;
						} else if( ! token4.equals(")") ) {
							System.out.println( "\t" + "*** ERROR : expect ')' after '"+token1+token2+token3+"' on parsing sql["+sql+"]" );
							return -225;
						}
						
						// 1    234
						// COUNT(*)
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableName = table.tableName ;
						selectColumnToken.table = table ;
						selectColumnToken.columnName = SqlActionGencode.SELECT_COUNT___ ;
						selectColumnTokenList.add(selectColumnToken);
						
						String token5 = lexicalParser.getSqlToken() ;
						if( token5 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -226;
						}
						if( token5.equals(",") ) {
							continue;
						} else if( token5.equalsIgnoreCase("FROM") ) {
							token = token5 ;
							break;
						} else {
							System.out.println( "\t" + "*** ERROR : expect something after '"+token1+token2+token3+token4+"' on parsing sql["+sql+"]" );
							return -227;
						}
					}
					
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						//        1     2
						// SELECT column\0
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableName = table.tableName ;
						selectColumnToken.table = table ;
						selectColumnToken.columnName = token1 ;
						selectColumnToken.column = SqlActionColumn.findColumn( selectColumnToken.table.columnList, selectColumnToken.columnName ) ;
						if( selectColumnToken.column == null ) {
							System.out.println( "\t" + "*** ERROR : column["+selectColumnToken.columnName+"] not found in table["+table.tableName+"] on parsing sql["+sql+"]" );
							return -23;
						}
						selectColumnTokenList.add(selectColumnToken);
						
						token = null ;
						break;
					}
					if( token2.equals(",") ) {
						//        1     2
						// SELECT column,
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableName = table.tableName ;
						selectColumnToken.table = table ;
						selectColumnToken.columnName = token1 ;
						selectColumnToken.column = SqlActionColumn.findColumn( selectColumnToken.table.columnList, selectColumnToken.columnName ) ;
						if( selectColumnToken.column == null ) {
							System.out.println( "\t" + "*** ERROR : column["+selectColumnToken.columnName+"] not found in table["+table.tableName+"] on parsing sql["+sql+"]" );
							return -24;
						}
						selectColumnTokenList.add(selectColumnToken);
						
						continue;
					} else if( token2.equalsIgnoreCase("FROM") ) {
						//        1      2
						// SELECT column FROM
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableName = table.tableName ;
						selectColumnToken.table = table ;
						selectColumnToken.columnName = token1 ;
						selectColumnToken.column = SqlActionColumn.findColumn( selectColumnToken.table.columnList, selectColumnToken.columnName ) ;
						if( selectColumnToken.column == null ) {
							System.out.println( "\t" + "*** ERROR : column["+selectColumnToken.columnName+"] not found in table["+table.tableName+"] on parsing sql["+sql+"]" );
							return -25;
						}
						selectColumnTokenList.add(selectColumnToken);
						
						token = token2 ;
						break;
					} else if( token2.equalsIgnoreCase(".") ) {
						//        1    2
						// SELECT table.
						String token3 = lexicalParser.getSqlToken() ;
						if( token3 == null ) {
							//        1    23
							// SELECT table.\0
							System.out.println( "sql["+sql+"] invalid" );
							return -261;
						} else {
							//        1    23
							// SELECT table.coumn
							selectColumnToken = new SqlActionSelectColumnToken() ;
							selectColumnToken.tableName = token1 ;
							selectColumnToken.table = SqlActionFromTableToken.findTable( fromTableTokenList , selectColumnToken.tableName ) ;
							if( selectColumnToken.table == null ) {
								System.out.println( "\t" + "*** ERROR : table["+selectColumnToken.tableName+"] not found in database["+database.databaseName+"] on parsing sql["+sql+"]" );
								return -262;
							}
							selectColumnToken.columnName = token3 ;
							selectColumnToken.column = SqlActionColumn.findColumn( selectColumnToken.table.columnList, selectColumnToken.columnName ) ;
							if( selectColumnToken.column == null ) {
								System.out.println( "\t" + "*** ERROR : column["+selectColumnToken.columnName+"] not found in table["+selectColumnToken.tableName+"] on parsing sql["+sql+"]" );
								return -263;
							}
							selectColumnTokenList.add(selectColumnToken);
							
							String token4 = lexicalParser.getSqlToken() ;
							if( token4 == null ) {
								//        1    23     4
								// SELECT table.column\0
								System.out.println( "sql["+sql+"] invalid" );
								return -264;
							} else if( token4.equals(",") ) {
								//        1    23     4
								// SELECT table.column,
								
								continue;
							} else if( token4.equalsIgnoreCase("FROM") ) {
								//        1    23      4
								// SELECT table.column FROM
								
								token = token4 ;
								break;
							} else {
								System.out.println( "sql["+sql+"] invalid" );
								return -267;
							}
						}
					}
				}
			} else if( token.equalsIgnoreCase("INSERT") ) {
				if( sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_NONE )
					sqlPredicate = SqlActionPredicateEnum.SQLACTION_PREDICATE_INSERT ;
				
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					System.out.println( "sql["+sql+"] invalid" );
					return -31;
				}
				if( ! token1.equalsIgnoreCase("INTO") ) {
					System.out.println( "sql["+sql+"] invalid" );
					return -52;
				} else {
					token = token1 ;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					System.out.println( "sql["+sql+"] invalid" );
					return -33;
				} else {
					insertTableName = token2 ;
				}
				
				String token3 = lexicalParser.getSqlToken() ;
				if( token3 == null ) {
					token = null ;
					break;
				} else {
					System.out.println( "sql["+sql+"] invalid" );
					return -34;
				}
			} else if( token.equalsIgnoreCase("UPDATE") ) {
				if( sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_NONE )
					sqlPredicate = SqlActionPredicateEnum.SQLACTION_PREDICATE_UPDATE ;
				
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					System.out.println( "sql["+sql+"] invalid" );
					return -31;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					//        1    2
					// UPDATE table\0
					System.out.println( "sql["+sql+"] invalid" );
					return -32;
				}
				if( token2.equalsIgnoreCase("SET") ) {
					//        1     2
					// UPDATE table SET
					updateTableName = token1 ;
					token = token2 ;
				} else {
					System.out.println( "sql["+sql+"] invalid" );
					return -33;
				}
			} else if( token.equalsIgnoreCase("DELETE") ) {
				if( sqlPredicate == SqlActionPredicateEnum.SQLACTION_PREDICATE_NONE )
					sqlPredicate = SqlActionPredicateEnum.SQLACTION_PREDICATE_DELETE ;
				
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					//        1
					// DELETE \0
					System.out.println( "sql["+sql+"] invalid" );
					return -41;
				}
				if( ! token1.equalsIgnoreCase("FROM") ) {
					//        1
					// DELETE !FROM
					System.out.println( "sql["+sql+"] invalid" );
					return -42;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					//        1   2
					// DELETE FROM\0
					System.out.println( "sql["+sql+"] invalid" );
					return -43;
				}
				
				deleteTableName = token2 ;
				
				String token3 = lexicalParser.getSqlToken() ;
				token = token3 ;
			} else if( token.equalsIgnoreCase("SET") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -51;
					}
					
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -52;
					}
					
					String token3 = lexicalParser.getSqlToken() ;
					if( token3 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -53;
					}
					
					//     1     23
					// SET column=?
					if( token3.equals("?") ) {
						setColumnToken = new SqlActionSetColumnToken() ;
						setColumnToken.tableName = table.tableName ;
						setColumnToken.table = table ;
						setColumnToken.columnName = token1 ;
						setColumnToken.column = SqlActionColumn.findColumn( setColumnToken.table.columnList, setColumnToken.columnName ) ;
						if( setColumnToken.column == null ) {
							System.out.println( "\t" + "*** ERROR : column["+setColumnToken.columnName+"] not found in table["+setColumnToken.tableName+"] on parsing sql["+sql+"]" );
							return -552;
						}
						setColumnToken.columnValue = token3 ;
						setColumnTokenList.add(setColumnToken);
					}
					
					String token6 = lexicalParser.getSqlToken() ;
					if( token6 == null ) {
						//     1     236
						// SET column=?\0
						token = null ;
						break;
					}
					if( token6.equals(",") ) {
						//     1     23  6
						// SET column=?,
						;
					} else if( token6.equalsIgnoreCase("WHERE") ) {
						//     1     23 6
						// SET column=? WHERE
						token = token6 ;
						break;
					} else {
						System.out.println( "sql["+sql+"] invalid" );
						return -56;
					}
				}
			} else if( token.equalsIgnoreCase("FROM") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						token = null ;
						break;
					} else if( token1.equalsIgnoreCase("WHERE") || token1.equalsIgnoreCase("GROUP") || token1.equalsIgnoreCase("ORDER") || token1.equalsIgnoreCase("HAVING") ) {
						token = token1 ;
						break;
					}
				}
			} else if( token.equalsIgnoreCase("WHERE") ) {
				hasWhereStatement = true ;
				
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -61;
					}
					
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -62;
					}
					
					String token3 = lexicalParser.getSqlToken() ;
					if( token3 == null ) {
						System.out.println( "sql["+sql+"] invalid" );
						return -63;
					}
					
					if( token2.equals(".") ) {
						//       1    23
						// WHERE table.column
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -641;
						}
						
						String token5 = lexicalParser.getSqlToken() ;
						if( token5 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -642;
						}
						
						String token6 = lexicalParser.getSqlToken() ;
						if( token6 == null ) {
							System.out.println( "sql["+sql+"] invalid" );
							return -642;
						}
						
						if( token6.equals(".") ) {
							String token7 = lexicalParser.getSqlToken() ;
							if( token7 == null ) {
								System.out.println( "sql["+sql+"] invalid" );
								return -6431;
							}
							
							token9 = lexicalParser.getSqlToken() ;
							if( token9 == null ) {
								token = null ;
								break;
							}
							
						} else {
							//       1    23     45
							// WHERE table.column=column2
							if( token5.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.operator = token4 ;
								whereColumnToken.tableName = token1 ;
								whereColumnToken.table = SqlActionFromTableToken.findTable( fromTableTokenList , whereColumnToken.tableName ) ;
								if( whereColumnToken.table == null ) {
									System.out.println( "\t" + "*** ERROR : table["+whereColumnToken.tableName+"] not found in database["+database.databaseName+"] on parsing sql["+sql+"]" );
									return -6441;
								}
								whereColumnToken.columnName = token3 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -6442;
								}
								whereColumnTokenList.add(whereColumnToken);
							}
							
							token9 = token6 ;
						}
					} else {
						//       1     23
						// WHERE column=column2
						
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							//       1     23      4
							// WHERE column=column2\0
							if( token1.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.operator = token2 ;
								whereColumnToken.tableName = table.tableName ;
								whereColumnToken.table = table ;
								whereColumnToken.columnName = token3 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -654;
								}
								whereColumnTokenList.add(whereColumnToken);
							} else if( token3.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.tableName = table.tableName ;
								whereColumnToken.table = table ;
								whereColumnToken.columnName = token1 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -656;
								}
								whereColumnToken.operator = token2 ;
								whereColumnTokenList.add(whereColumnToken);
							}
							
							token = null ;
							break;
						}
						
						if( token4.equals(".") ) {
							//       1      23     45
							// WHERE column1=table2.column2
							String token5 = lexicalParser.getSqlToken() ;
							if( token5 == null ) {
								System.out.println( "sql["+sql+"] invalid" );
								return -661;
							}
							
							if( token1.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.operator = token2 ;
								whereColumnToken.tableName = token3 ;
								whereColumnToken.table = SqlActionFromTableToken.findTable( fromTableTokenList , whereColumnToken.tableName ) ;
								if( whereColumnToken.table == null ) {
									System.out.println( "\t" + "*** ERROR : table["+whereColumnToken.tableName+"] not found in database["+database.databaseName+"] on parsing sql["+sql+"]" );
									return -664;
								}
								whereColumnToken.columnName = token5 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -665;
								}
								whereColumnTokenList.add(whereColumnToken);
							}
							
							token9 = lexicalParser.getSqlToken() ;
							if( token9 == null ) {
								token = null ;
								break;
							}
						} else {
							//       1      23      4
							// WHERE column1=column2
							if( token1.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.operator = token2 ;
								whereColumnToken.tableName = table.tableName ;
								whereColumnToken.table = table ;
								whereColumnToken.columnName = token3 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -671;
								}
								whereColumnTokenList.add(whereColumnToken);
							} else if( token3.equals("?") ) {
								whereColumnToken = new SqlActionWhereColumnToken() ;
								whereColumnToken.tableName = table.tableName ;
								whereColumnToken.table = table ;
								whereColumnToken.columnName = token1 ;
								whereColumnToken.column = SqlActionColumn.findColumn( whereColumnToken.table.columnList, whereColumnToken.columnName ) ;
								if( whereColumnToken.column == null ) {
									System.out.println( "\t" + "*** ERROR : column["+whereColumnToken.columnName+"] not found in table["+whereColumnToken.tableName+"] on parsing sql["+sql+"]" );
									return -672;
								}
								whereColumnToken.operator = token2 ;
								whereColumnTokenList.add(whereColumnToken);
							}
							
							token9 = token4 ;
						}
					}
					
					if( token9.equalsIgnoreCase("AND") || token9.equalsIgnoreCase("OR") ) {
						;
					} else {
						// WHERE ... others
						token = token9 ;
						break;
					}
				}
			} else if( token.equalsIgnoreCase("GROUP") || token.equalsIgnoreCase("ORDER") || token.equalsIgnoreCase("HAVING") ) {
				otherTokens = " " + token + lexicalParser.getRemainSqlTokens() ;
				token = null ;
				break;
			} else {
				System.out.println( "token["+token+"] invalid" );
				return -9;
			}
		}
		
		return 0;
	}
	
	public int parseStatementSyntaxForAdvancedMode_FROM( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception {
		SqlActionFromTableTokenForAdvancedMode		fromTableTokenForAdvancedMode = null ;
		
		SqlActionLexicalParser lexicalParser = new SqlActionLexicalParser() ;
		lexicalParser.setSqlString(sql);
		
		fromTableTokenForAdvancedModeList = new LinkedList<SqlActionFromTableTokenForAdvancedMode>() ;
		
		while(true) {
			String token = lexicalParser.getSqlToken() ;
			if( token == null )
				break;
			
			if( token.equalsIgnoreCase("SELECT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_SELECT ;
			} else if( token.equalsIgnoreCase("INSERT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_INSERT ;
			} else if( token.equalsIgnoreCase("UPDATE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_UPDATE ;
			} else if( token.equalsIgnoreCase("DELETE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_DELETE ;
			} else if( token.equalsIgnoreCase("FROM") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_FROM ;
			} else if( token.equalsIgnoreCase("SET") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_SET ;
			} else if( token.equalsIgnoreCase("WHERE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_WHERE ;
			} else if( token.equalsIgnoreCase("GROUP") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_GROUP ;
			} else if( token.equalsIgnoreCase("ORDER") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_ORDER ;
			} else if( token.equalsIgnoreCase("JAVING") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_HAVING ;
			} else if( token.equalsIgnoreCase("OFFSET") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_OFFSET ;
			} else if( token.equalsIgnoreCase("LIMIT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_LIMIT ;
			} else {
				if( sqlStatement == SqlActionStatementEnum.SQLACTION_STATEMENT_FROM || sqlStatement == SqlActionStatementEnum.SQLACTION_STATEMENT_UPDATE ) {
					int beginPos = token.indexOf("#{") ;
					if( beginPos >= 0 ) {
						int endPos = token.indexOf("}") ;
						if( endPos < 0 ) {
							System.out.println( "expect '}' in '"+token+"'" );
							return -101;
						}
						
						String tableName = token.substring( beginPos+2, endPos ) ;
						fromTableTokenForAdvancedMode = new SqlActionFromTableTokenForAdvancedMode() ;
						fromTableTokenForAdvancedMode.table = SqlActionTable.fetchTableMetadataInDatabase( dbserverConf, sqlactionConf, conn, database, table, tableName ) ;
						if( fromTableTokenForAdvancedMode.table == null ) {
							System.out.println( "\t" + "*** ERROR : tableName["+tableName+"] not found in database["+database.databaseName+"]" );
							return -102;
						}
						fromTableTokenForAdvancedModeList.add(fromTableTokenForAdvancedMode);
					}
				}
			}
		}
		
		return 0;
	}
	
	public int parseStatementSyntaxForAdvancedMode_ExceptFROM( DbServerConf dbserverConf, SqlActionConf sqlactionConf, Connection conn, SqlActionDatabase database, SqlActionTable table ) throws Exception {
		SqlActionSelectColumnTokenForAdvancedMode	selectColumnTokenForAdvancedMode = null ;
		SqlActionWhereColumnTokenForAdvancedMode	whereColumnTokenForAdvancedMode = null ;
		
		SqlActionLexicalParser lexicalParser = new SqlActionLexicalParser() ;
		lexicalParser.setSqlString(sql);
		
		selectColumnTokenForAdvancedModeList = new LinkedList<SqlActionSelectColumnTokenForAdvancedMode>() ;
		whereColumnTokenForAdvancedModeList = new LinkedList<SqlActionWhereColumnTokenForAdvancedMode>() ;
		
		while(true) {
			String token = lexicalParser.getSqlToken() ;
			if( token == null )
				break;
			
			if( token.equalsIgnoreCase("SELECT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_SELECT ;
			} else if( token.equalsIgnoreCase("INSERT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_INSERT ;
			} else if( token.equalsIgnoreCase("UPDATE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_UPDATE ;
			} else if( token.equalsIgnoreCase("DELETE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_DELETE ;
			} else if( token.equalsIgnoreCase("FROM") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_FROM ;
			} else if( token.equalsIgnoreCase("SET") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_SET ;
			} else if( token.equalsIgnoreCase("WHERE") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_WHERE ;
			} else if( token.equalsIgnoreCase("GROUP") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_GROUP ;
			} else if( token.equalsIgnoreCase("ORDER") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_ORDER ;
			} else if( token.equalsIgnoreCase("JAVING") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_HAVING ;
			} else if( token.equalsIgnoreCase("OFFSET") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_OFFSET ;
			} else if( token.equalsIgnoreCase("LIMIT") ) {
				sqlStatement = SqlActionStatementEnum.SQLACTION_STATEMENT_LIMIT ;
			} else {
				if( sqlStatement == SqlActionStatementEnum.SQLACTION_STATEMENT_SELECT ) {
					int beginPos = token.indexOf("#{") ;
					if( beginPos >= 0 ) {
						int endPos = token.indexOf("}") ;
						if( endPos < 0 ) {
							System.out.println( "expect '}' in '"+token+"'" );
							return -101;
						}
						
						int dotPos = token.indexOf(".",beginPos+2) ;
						if( dotPos < 0 ) {
							System.out.println( "expect '}' in '"+token+"'" );
							return -1;
						}
						
						selectColumnTokenForAdvancedMode = new SqlActionSelectColumnTokenForAdvancedMode() ;
						String javaSauClassName = token.substring( beginPos+2, dotPos ) ;
						String javaPropertyName =  token.substring( dotPos+1, endPos ) ;
						selectColumnTokenForAdvancedMode.table = SqlActionTable.findTableByJavaSauClassName( database.tableList, javaSauClassName ) ;
						if( selectColumnTokenForAdvancedMode.table == null ) {
							System.out.println( "\t" + "*** ERROR : javaSauClassName["+javaSauClassName+"] not found" );
							return -1;
						}
						selectColumnTokenForAdvancedMode.column = SqlActionColumn.findColumnByJavaPropertyName( selectColumnTokenForAdvancedMode.table.columnList, javaPropertyName ) ;
						if( selectColumnTokenForAdvancedMode.column == null ) {
							System.out.println( "\t" + "*** ERROR : javaPropertyName["+javaPropertyName+"] not found in javaSaoClassName["+javaSauClassName+"]" );
							return -1;
						}
						selectColumnTokenForAdvancedModeList.add(selectColumnTokenForAdvancedMode);
					}
				} else if( sqlStatement == SqlActionStatementEnum.SQLACTION_STATEMENT_WHERE || sqlStatement == SqlActionStatementEnum.SQLACTION_STATEMENT_SET ) {
					int beginPos = token.indexOf("#{") ;
					if( beginPos >= 0 ) {
						int endPos = token.indexOf("}") ;
						if( endPos < 0 ) {
							System.out.println( "expect '}' in '"+token+"'" );
							return -101;
						}
						
						int dotPos = token.indexOf(".",beginPos+2) ;
						if( dotPos < 0 ) {
							System.out.println( "expect '}' in '"+token+"'" );
							return -1;
						}
						
						whereColumnTokenForAdvancedMode = new SqlActionWhereColumnTokenForAdvancedMode() ;
						String javaSauClassName = token.substring( beginPos+2, dotPos ) ;
						String javaPropertyName =  token.substring( dotPos+1, endPos ) ;
						whereColumnTokenForAdvancedMode.table = SqlActionTable.findTableByJavaSauClassName( database.tableList, javaSauClassName ) ;
						if( whereColumnTokenForAdvancedMode.table == null ) {
							System.out.println( "\t" + "*** ERROR : javaSauClassName["+javaSauClassName+"] not found" );
							return -1;
						}
						whereColumnTokenForAdvancedMode.column = SqlActionColumn.findColumnByJavaPropertyName( whereColumnTokenForAdvancedMode.table.columnList, javaPropertyName ) ;
						if( whereColumnTokenForAdvancedMode.column == null ) {
							System.out.println( "\t" + "*** ERROR : javaPropertyName["+javaPropertyName+"] not found in javaSaoClassName["+javaSauClassName+"]" );
							return -1;
						}
						whereColumnTokenForAdvancedModeList.add(whereColumnTokenForAdvancedMode);
					}
				}
			}
		}
		
		return 0;
	}
}
