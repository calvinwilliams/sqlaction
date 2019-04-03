/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.gencode;

import java.util.*;

public class SqlActionSyntaxParser {

	public String							selectHint = null ;
	public boolean							selectAllColumn = false ;
	public List<SqlActionSelectColumnToken>	selectColumnTokenList = null ;
	public List<SqlActionFromTableToken>	fromTableTokenList = null ;
	
	public String							insertTableName = null ;
	
	public String							updateTableName = null ;
	public List<SqlActionSetColumnToken>	setColumnTokenList = null ;
	
	public String							deleteTableName = null ;
	
	public List<SqlActionWhereColumnToken>	whereColumnTokenList = null ;
	
	public String							otherTokens = null ;
	
	public String							methodName = null ;
	
	public int parseSyntax( String sql ) {
		int							beginMetaData ;
		int							endMetaData ;
		SqlActionSelectColumnToken	selectColumnToken = null ;
		SqlActionFromTableToken		fromTableToken = null ;
		SqlActionSetColumnToken		setColumnToken = null ;
		SqlActionWhereColumnToken	whereColumnToken = null ;
		String						token9 = null ;
		
		beginMetaData = sql.indexOf( "@@METHOD(" ) ;
		if( beginMetaData >= 0 ) {
			endMetaData = sql.indexOf( ")", beginMetaData ) ;
			if( endMetaData == -1 ) {
				System.out.println( "sql["+sql+"] invalid" );
				return -1;
			}
			methodName = sql.substring( beginMetaData+9, endMetaData ) ;
		}
		
		beginMetaData = sql.indexOf( "@@" ) ;
		if( beginMetaData >= 0 ) {
			sql = sql.substring( 0, beginMetaData ) ;
		}
		
		SqlActionLexicalParser lexicalParser = new SqlActionLexicalParser() ;
		lexicalParser.setSqlString(sql);
		
		selectColumnTokenList = new LinkedList<SqlActionSelectColumnToken>() ;
		fromTableTokenList = new LinkedList<SqlActionFromTableToken>() ;
		setColumnTokenList = new LinkedList<SqlActionSetColumnToken>() ;
		whereColumnTokenList = new LinkedList<SqlActionWhereColumnToken>() ;
		
		String	token = lexicalParser.getSqlToken() ;
		if( token == null )
			return 0;
		while( token != null ) {
			if( token.equalsIgnoreCase("SELECT") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						return -11;
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
							return -12;
						}
					} else if( token1.length() > 2 && ( token1.charAt(0) == '/' || token1.charAt(1) == '*' ) ) {
						selectHint = token1 ;
						continue;
					}
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						// SELECT column1\0
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
						token = null ;
						break;
					} else if( token2.equalsIgnoreCase("FROM") ) {
						// SELECT column FROM
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
						token = token2 ;
						break;
					} else if( token2.equals(".") ) {
						// SELECT table1.column1
						String token3 = lexicalParser.getSqlToken() ;
						
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.tableAliasName = token1 ;
						selectColumnToken.columnName = token3 ;
						selectColumnTokenList.add(selectColumnToken);
						
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							// SELECT table1.column1\0
							token = null ;
							break;
						}
						if( token4.equalsIgnoreCase("FROM") ) {
							// SELECT table1.column1 FROM
							token = token4 ;
							break;
						} else if( token4.equals(",") ) {
							// SELECT table1.column1,
						} else {
							return -13;
						}
					} else if( token2.equals(",") ) {
						// SELECT column1,column2
						selectColumnToken = new SqlActionSelectColumnToken() ;
						selectColumnToken.columnName = token1 ;
						selectColumnTokenList.add(selectColumnToken);
					}
				}
			} else if( token.equalsIgnoreCase("INSERT") ) {
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					return -21;
				}
				if( ! token1.equalsIgnoreCase("INTO") ) {
					return -22;
				} else {
					token = token1 ;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					return -23;
				} else {
					insertTableName = token2 ;
				}
				
				String token3 = lexicalParser.getSqlToken() ;
				if( token3 == null ) {
					token = null ;
					break;
				} else {
					return -24;
				}
			} else if( token.equalsIgnoreCase("UPDATE") ) {
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					return -31;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					// UPDATE table\0
					return -32;
				}
				if( token2.equalsIgnoreCase("SET") ) {
					// UPDATE table SET
					updateTableName = token1 ;
					token = token2 ;
				} else {
					return -32;
				}
			} else if( token.equalsIgnoreCase("DELETE") ) {
				String token1 = lexicalParser.getSqlToken() ;
				if( token1 == null ) {
					return -41;
				}
				if( ! token1.equalsIgnoreCase("FROM") ) {
					return -42;
				}
				
				String token2 = lexicalParser.getSqlToken() ;
				if( token2 == null ) {
					return -43;
				}
				
				deleteTableName = token2 ;
				
				String token3 = lexicalParser.getSqlToken() ;
				token = token3 ;
			} else if( token.equalsIgnoreCase("SET") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						return -41;
					}
					
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						return -42;
					}
					
					String token3 = lexicalParser.getSqlToken() ;
					if( token3 == null ) {
						return -43;
					}
					
					if( token2.equals(".") ) {
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							return -441;
						}
						
						String token5 = lexicalParser.getSqlToken() ;
						if( token5 == null ) {
							return -442;
						}
						
						// SET table.column = ?
						setColumnToken = new SqlActionSetColumnToken() ;
						setColumnToken.tableName = token1 ;
						setColumnToken.columnName = token3 ;
						if( ! token4.equals("=") ) {
							return -443;
						}
						setColumnToken.columnValue = token5 ;
						setColumnTokenList.add(setColumnToken);
					} else {
						// SET column = ?
						setColumnToken = new SqlActionSetColumnToken() ;
						setColumnToken.columnName = token1 ;
						if( ! token2.equals("=") ) {
							return -444;
						}
						setColumnToken.columnValue = token3 ;
						setColumnTokenList.add(setColumnToken);
					}
					
					String token6 = lexicalParser.getSqlToken() ;
					if( token6 == null ) {
						// SET column = ?\0
						token = null ;
						break;
					}
					if( token6.equals(",") ) {
						;
					} else if( token6.equalsIgnoreCase("WHERE") ) {
						// SET column = ? WHERE
						token = token6 ;
						break;
					} else {
						return -45;
					}
				}
			} else if( token.equalsIgnoreCase("FROM") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						return -51;
					}
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						//      1
						// FROM table\0
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = null ;
						break;
					}
					if( token2.equalsIgnoreCase("WHERE") ) {
						//      1     2
						// FROM table WHERE
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
						token = token2 ;
						break;
					} else if( token2.equals(",") ) {
						//      1     2
						// FROM table1,
						fromTableToken = new SqlActionFromTableToken() ;
						fromTableToken.tableName = token1 ;
						fromTableTokenList.add(fromTableToken);
					} else {
						if( token2.equalsIgnoreCase("WHERE") || token2.equalsIgnoreCase("GROUP") || token2.equalsIgnoreCase("ORDER") || token2.equalsIgnoreCase("HAVING") ) {
							//      1      2
							// FROM table1 (WHERE|ORDER|HAVING)
							fromTableToken = new SqlActionFromTableToken() ;
							fromTableToken.tableName = token1 ;
							fromTableTokenList.add(fromTableToken);
							
							token = token2 ;
							break;
						} else {
							//      1      2
							// FROM table1 t1
							fromTableToken = new SqlActionFromTableToken() ;
							fromTableToken.tableName = token1 ;
							fromTableToken.tableAliasName = token2 ;
							fromTableTokenList.add(fromTableToken);
							
							String token3 = lexicalParser.getSqlToken() ;
							if( token3 == null ) {
								//      1      2 3
								// FROM table1 t1\0
								token = null ;
								break;
							}
							if( token3.equals(",") ) {
								//      1      2 3
								// FROM table1 t1,
							} else {
								//      1      2  3
								// FROM table1 t1 ...
								token = token3 ;
								break;
							}
						}
					}
				}
			} else if( token.equalsIgnoreCase("WHERE") ) {
				while(true) {
					String token1 = lexicalParser.getSqlToken() ;
					if( token1 == null ) {
						return -61;
					}
					
					String token2 = lexicalParser.getSqlToken() ;
					if( token2 == null ) {
						return -62;
					}
					
					String token3 = lexicalParser.getSqlToken() ;
					if( token3 == null ) {
						return -63;
					}
					
					if( token2.equals(".") ) {
						// table.column
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							return -641;
						}
						
						String token5 = lexicalParser.getSqlToken() ;
						if( token5 == null ) {
							return -642;
						}
						
						String token6 = lexicalParser.getSqlToken() ;
						if( token6 == null ) {
							return -642;
						}
						
						if( token6.equals(".") ) {
							String token7 = lexicalParser.getSqlToken() ;
							if( token7 == null ) {
								return -6431;
							}
							
							// table.column = table2.column2
							whereColumnToken = new SqlActionWhereColumnToken() ;
							whereColumnToken.tableAliasName = token1 ;
							whereColumnToken.columnName = token3 ;
							whereColumnToken.operator = token4 ;
							whereColumnToken.tableAliasName2 = token5 ;
							whereColumnToken.columnName2 = token7 ;
							whereColumnTokenList.add(whereColumnToken);
							
							token9 = lexicalParser.getSqlToken() ;
							if( token9 == null ) {
								token = null ;
								break;
							}
							
						} else {
							// table.column = column2
							whereColumnToken = new SqlActionWhereColumnToken() ;
							whereColumnToken.tableAliasName = token1 ;
							whereColumnToken.columnName = token3 ;
							whereColumnToken.operator = token4 ;
							whereColumnToken.columnName2 = token5 ;
							whereColumnTokenList.add(whereColumnToken);
							
							token9 = token6 ;
						}
					} else {
						// column = x
						String token4 = lexicalParser.getSqlToken() ;
						if( token4 == null ) {
							whereColumnToken = new SqlActionWhereColumnToken() ;
							whereColumnToken.columnName = token1 ;
							whereColumnToken.operator = token2 ;
							whereColumnToken.columnName2 = token3 ;
							whereColumnTokenList.add(whereColumnToken);
							token = null ;
							break;
						}
						
						if( token4.equals(".") ) {
							// column1 = table2.column2
							String token5 = lexicalParser.getSqlToken() ;
							if( token5 == null ) {
								return -6441;
							}
							
							whereColumnToken = new SqlActionWhereColumnToken() ;
							whereColumnToken.columnName = token1 ;
							whereColumnToken.operator = token2 ;
							whereColumnToken.tableName2 = token3 ;
							whereColumnToken.columnName2 = token5 ;
							whereColumnTokenList.add(whereColumnToken);
							
							token9 = lexicalParser.getSqlToken() ;
							if( token9 == null ) {
								token = null ;
								break;
							}
						} else {
							// column1 = column2
							whereColumnToken = new SqlActionWhereColumnToken() ;
							whereColumnToken.columnName = token1 ;
							whereColumnToken.operator = token2 ;
							whereColumnToken.columnName2 = token3 ;
							whereColumnTokenList.add(whereColumnToken);
							
							token9 = token4 ;
						}
					}
					
					if( token9.equalsIgnoreCase("AND") ) {
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
	
	public boolean isFromTableNameExist( String tableName ) {
		for( SqlActionFromTableToken tt : fromTableTokenList ) {
			if( tt.tableName.equalsIgnoreCase(tableName) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public String findFromTableFromAliasName( String tableAliasName ) {
		for( SqlActionFromTableToken tt : fromTableTokenList ) {
			if( tt.tableAliasName.equalsIgnoreCase(tableAliasName) ) {
				return tt.tableName;
			}
		}
		
		return null;
	}
}
