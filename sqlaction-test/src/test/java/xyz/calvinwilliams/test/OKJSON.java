package xyz.calvinwilliams.test;

import java.util.*;
import java.lang.reflect.*;

/**
 * @ClassName   : OKJSON
 * @Description : Util tool class for serialing/deserialing object
 * @author      : calvin
 * @date        : 2019-03-17
 *
 */
public class OKJSON {
	final public static int	OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE = 1 ;
	final public static int	OKJSON_OTIONS_PRETTY_FORMAT_ENABLE = 2 ;
	final public static int	OKJSON_OTIONS_STRICT_POLICY = 4 ;
	
	final private static int	OKJSON_ERROR_NEW_OBJECT = -31 ;
	
	private static ThreadLocal<OkJsonGenerator>			okjsonGeneratorCache ;
	private static ThreadLocal<OkJsonParser>			okjsonParserCache ;
	
	private static ThreadLocal<Integer>	errorCode = new ThreadLocal<Integer>() ;
	private static ThreadLocal<String>	errorDesc = new ThreadLocal<String>() ;
	
	public static Integer getErrorCode() {
		return errorCode.get();
	}
	
	public static String getErrorDesc() {
		return errorDesc.get();
	}
	
	/**
	 * @Title         : objectToString 
	 * @Description   : Convert object to json string
	 * @param object  : Input object 
	 * @param options : Convert options in OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE|OKJSON_OTIONS_PRETTY_FORMAT_ENABLE
	 * @return        : 0 if convert successful , not 0 if failed
	 */
	public static String objectToString( Object object, int options ) {
		OkJsonGenerator okjsonGenerator ;
		
		if( okjsonGeneratorCache == null ) {
			okjsonGeneratorCache = new ThreadLocal<OkJsonGenerator>() ;
			if( okjsonGeneratorCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonGenerator>") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonGenerator = new OkJsonGenerator() ;
			if( okjsonGenerator == null ) {
				errorDesc.set("New object failed for OkJsonGenerator") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonGeneratorCache.set(okjsonGenerator);
		} else {
			okjsonGenerator = okjsonGeneratorCache.get();
		}
		
		if( (options&OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			okjsonGenerator.setDirectAccessPropertyEnable(true);
		if( (options&OKJSON_OTIONS_PRETTY_FORMAT_ENABLE) != 0 )
			okjsonGenerator.setPrettyFormatEnable(true);
		
		String string = okjsonGenerator.objectToString(object) ;
		
		errorCode.set(okjsonGenerator.getErrorCode());
		errorDesc.set(okjsonGenerator.getErrorDesc());
		
		return string;
	}
	
	/**
	 * @Title         : stringToObject 
	 * @Description   : Convert json string to object
	 * @param string  : Input json string 
	 * @param clazz   : output object for clazz
	 * @param options : Convert options in OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE|OKJSON_OTIONS_STRICT_POLICY
	 * @return        : 0 if convert successful , not 0 if failed
	 */
	public static <T> T stringToObject( String string, Class<T> clazz, int options ) {
		OkJsonParser okjsonParser ;
		
		if( okjsonParserCache == null ) {
			okjsonParserCache = new ThreadLocal<OkJsonParser>() ;
			if( okjsonParserCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonParser>") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonParser = new OkJsonParser() ;
			if( okjsonParser == null ) {
				errorDesc.set("New object failed for okjsonParser") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonParserCache.set(okjsonParser);
		} else {
			okjsonParser = okjsonParserCache.get();
		}
		
		if( (options&OKJSON_OTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			okjsonParser.setDirectAccessPropertyEnable(true);
		if( (options&OKJSON_OTIONS_STRICT_POLICY) != 0 )
			okjsonParser.setStrictPolicyEnable(true);
		
		T object ;
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		object = okjsonParser.stringToObject(string, object);
		
		errorCode.set(okjsonParser.getErrorCode());
		errorDesc.set(okjsonParser.getErrorDesc());
		
		return object;
	}
}

/**
 * @ClassName   : OkJsonParser
 * @Description : Util tool class for deserialing to object
 * @author      : calvin
 * @date        : 2019-03-17
 *
 */
class OkJsonParser {
	private boolean				strictPolicyEnable ;
	private boolean				directAccessPropertyEnable ;
	private boolean				prettyFormatEnable ;
	
	private Integer				errorCode ;
	private String				errorDesc ;
	
	enum TokenType {
		TOKEN_TYPE_LEFT_BRACE , // {
		TOKEN_TYPE_RIGHT_BRACE , // }
		TOKEN_TYPE_LEFT_BRACKET, // [
		TOKEN_TYPE_RIGHT_BRACKET, // ]
		TOKEN_TYPE_COLON, // :
		TOKEN_TYPE_COMMA, // ,
		TOKEN_TYPE_STRING, // "ABC"
		TOKEN_TYPE_INTEGER, // 123
		TOKEN_TYPE_DECIMAL, // 123.456
		TOKEN_TYPE_BOOL, // true or false
		TOKEN_TYPE_NULL // null
	}
	
	private static ThreadLocal<HashMap<String,HashMap<String,Field>>>			stringMapFieldsCache ;
	private static ThreadLocal<HashMap<String,HashMap<String,Method>>>			stringMapMethodsCache ;
	private static ThreadLocal<StringBuilder>									fieldStringBuilderCache ;
	
	private int					jsonOffset ;
	private int					jsonLength ;
	
	private TokenType			tokenType ;
	private int					beginOffset ;
	private int					endOffset ;
	private boolean				booleanValue ;
	
	final private static int	OKJSON_ERROR_END_OF_BUFFER = 1 ;
	final private static int	OKJSON_ERROR_UNEXPECT = -4 ;
	final private static int	OKJSON_ERROR_EXCEPTION = -8 ;
	final private static int	OKJSON_ERROR_INVALID_BYTE = -11 ;
	final private static int	OKJSON_ERROR_FIND_FIRST_LEFT_BRACE = -21 ;
	final private static int	OKJSON_ERROR_NAME_INVALID = -22 ;
	final private static int	OKJSON_ERROR_EXPECT_COLON_AFTER_NAME = -23 ;
	final private static int	OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = -24 ;
	final private static int	OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -26 ;
	final private static int	OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = -28 ;
	final private static int	OKJSON_ERROR_NEW_OBJECT = -31 ;
	
	/**
	 * @Title               : tokenJsonString 
	 * @Description         : Token out a string from json char array
	 * @param jsonCharArray : Input json char array 
	 * @return              : 0 if convert successful , not 0 if failed
	 */
	private int tokenJsonString( char[] jsonCharArray ) {
		StringBuilder	fieldStringBuilder ;
		char			ch ;
		
		fieldStringBuilder = fieldStringBuilderCache.get();
		fieldStringBuilder.setLength(0);
		
		jsonOffset++;
		beginOffset = jsonOffset ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( ch == '"' ) {
				tokenType = TokenType.TOKEN_TYPE_STRING ;
				if( fieldStringBuilder.length() > 0 ) {
					if( jsonOffset > beginOffset ) {
						if( fieldStringBuilder.length() > 0 )
							fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
					}
				} else {
					endOffset = jsonOffset-1 ;
				}
				jsonOffset++;
				return 0;
			} else if ( ch == '\\' ) {
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == '"' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '"' );
				} else if( ch == '\\' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\\' );
				} else if( ch == '/' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( "\\/" );
				} else if( ch == 'b' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\b' );
				} else if( ch == 'f' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\f' );
				} else if( ch == 'n' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\n' );
				} else if( ch == 'r' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\r' );
				} else if( ch == 't' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( '\t' );
				} else if( ch == 'u' ) {
					if( fieldStringBuilder.length() == 0 )
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
							jsonOffset++;
							if( jsonOffset >= jsonLength ) {
								return OKJSON_ERROR_END_OF_BUFFER;
							}
							ch = jsonCharArray[jsonOffset] ;
							if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
								jsonOffset++;
								if( jsonOffset >= jsonLength ) {
									return OKJSON_ERROR_END_OF_BUFFER;
								}
								ch = jsonCharArray[jsonOffset] ;
								if( ('0'<=ch && ch<='9') || ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
									String unicodeString = "0x" + jsonCharArray[jsonOffset-3] + jsonCharArray[jsonOffset-2] + jsonCharArray[jsonOffset-1] + jsonCharArray[jsonOffset] ;
									int unicodeInt = Integer.decode(unicodeString).intValue() ;
									if( fieldStringBuilder.length() == 0 )
										fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-4-beginOffset-1 );
									fieldStringBuilder.append( (char)unicodeInt );
									beginOffset = jsonOffset + 1 ;
								} else {
									fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
									beginOffset = jsonOffset ;
									jsonOffset--;
								}
							} else {
								fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
								beginOffset = jsonOffset ;
								jsonOffset--;
							}
						} else {
							fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
							beginOffset = jsonOffset ;
							jsonOffset--;
						}
					} else {
						fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset );
						beginOffset = jsonOffset ;
						jsonOffset--;
					}
				} else {
					fieldStringBuilder.append( jsonCharArray, beginOffset, jsonOffset-beginOffset-1 );
					fieldStringBuilder.append( ch );
				}
			}
			
			jsonOffset++;
		}
		
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	/**
	 * @Title               : tokenJsonNumber 
	 * @Description         : Token out a number from json char array
	 * @param jsonCharArray : Input json char array 
	 * @return              : 0 if convert successful , not 0 if failed
	 */
	private int tokenJsonNumber( char[] jsonCharArray ) {
		char	ch ;
		boolean	decimalPointFlag ;
		
		beginOffset = jsonOffset ;
		
		ch = jsonCharArray[jsonOffset] ;
		if( ch == '-' ) {
			jsonOffset++;
		}
		
		decimalPointFlag = false ;
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( '0' <= ch && ch <= '9' ) {
				jsonOffset++;
			} else if( ch == '.' ) {
				decimalPointFlag = true ;
				jsonOffset++;
			} else if( ch == 'e' || ch == 'E' ) {
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == '-' || ch == '+' ) {
					jsonOffset++;
				} else if( '0' <= ch && ch <= '9' ) {
					jsonOffset++;
				}
			} else {
				if( decimalPointFlag == true )
					tokenType = TokenType.TOKEN_TYPE_DECIMAL ;
				else
					tokenType = TokenType.TOKEN_TYPE_INTEGER ;
				endOffset = jsonOffset-1 ;
				return 0;
			}
		}
		
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	/**
	 * @Title               : tokenJsonWord 
	 * @Description         : Token out a word from json char array
	 * @param jsonCharArray : Input json char array 
	 * @return              : 0 if convert successful , not 0 if failed
	 */
	private int tokenJsonWord( char[] jsonCharArray ) {
		char	ch ;
		
		while( jsonOffset < jsonLength ) {
			ch = jsonCharArray[jsonOffset] ;
			if( ch == ' ' || ch == '\b' || ch == '\t' || ch == '\f' || ch == '\r' || ch == '\n' ) {
				jsonOffset++;
			} else if( ch == '{' ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '}'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACE ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '['  ) {
				tokenType = TokenType.TOKEN_TYPE_LEFT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == ']'  ) {
				tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACKET ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '"'  ) {
				return tokenJsonString( jsonCharArray ) ;
			} else if( ch == ':'  ) {
				tokenType = TokenType.TOKEN_TYPE_COLON ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == ','  ) {
				tokenType = TokenType.TOKEN_TYPE_COMMA ;
				beginOffset = jsonOffset ;
				endOffset = jsonOffset ;
				jsonOffset++;
				return 0;
			} else if( ch == '-' || ( '0' <= ch && ch <= '9' ) ) {
				return tokenJsonNumber( jsonCharArray ) ;
			} else if( ch == 't' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'r' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'u' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 'e' ) {
							tokenType = TokenType.TOKEN_TYPE_BOOL ;
							booleanValue = true ;
							endOffset = jsonOffset ;
							jsonOffset++;
							return 0;
						}
					}
				}
			} else if( ch == 'f' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'a' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 's' ) {
							jsonOffset++;
							if( jsonOffset >= jsonLength ) {
								return OKJSON_ERROR_END_OF_BUFFER;
							}
							ch = jsonCharArray[jsonOffset] ;
							if( ch == 'e' ) {
								tokenType = TokenType.TOKEN_TYPE_BOOL ;
								booleanValue = false ;
								endOffset = jsonOffset ;
								jsonOffset++;
								return 0;
							}
						}
					}
				}
			} else if( ch == 'n' ) {
				beginOffset = jsonOffset ;
				jsonOffset++;
				if( jsonOffset >= jsonLength ) {
					return OKJSON_ERROR_END_OF_BUFFER;
				}
				ch = jsonCharArray[jsonOffset] ;
				if( ch == 'u' ) {
					jsonOffset++;
					if( jsonOffset >= jsonLength ) {
						return OKJSON_ERROR_END_OF_BUFFER;
					}
					ch = jsonCharArray[jsonOffset] ;
					if( ch == 'l' ) {
						jsonOffset++;
						if( jsonOffset >= jsonLength ) {
							return OKJSON_ERROR_END_OF_BUFFER;
						}
						ch = jsonCharArray[jsonOffset] ;
						if( ch == 'l' ) {
							tokenType = TokenType.TOKEN_TYPE_NULL ;
							booleanValue = true ;
							endOffset = jsonOffset ;
							jsonOffset++;
							return 0;
						}
					}
				}
			} else {
				errorDesc = "Invalid byte '" + ch + "'" ;
				return OKJSON_ERROR_INVALID_BYTE;
			}
		}
		
		return OKJSON_ERROR_END_OF_BUFFER;
	}
	
	/**
	 * @Title                  : addArrayObject 
	 * @Description            : Add a element to List object
	 * @param jsonCharArray    : Input json char array 
	 * @param valueTokenType   : List element type enum
	 * @param valueBeginOffset : Token begin offset from json char array
	 * @param valueEndOffset   : Token end offset from json char array
	 * @param object           : List object wait for add
	 * @param field            : List element type
	 * @return                 : 0 if convert successful , not 0 if failed
	 */
	private int addArrayObject( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field ) {

		try {
			Class<?> clazz = field.getType() ;
			if( clazz == ArrayList.class || clazz == LinkedList.class ) {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClass = (Class<?>) pt.getActualTypeArguments()[0] ;
				if( typeClass == String.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
						String value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Byte.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Byte value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Short.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Short value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Integer.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Integer value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Long.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
						Long value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Float.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Float value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Double.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
						Double value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
						((List<Object>) object).add( value );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( typeClass == Boolean.class ) {
					if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
						((List<Object>) object).add( booleanValue );
					} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
						;
					}
				} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
					;
				} else {
					if( strictPolicyEnable == true )
						return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
				}
			} else {
				if( strictPolicyEnable == true )
					return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return OKJSON_ERROR_EXCEPTION;
		}

		return 0;
	}

	/**
	 * @Title                  : stringToArrayObject 
	 * @Description            : array data convert to list object
	 * @param jsonCharArray    : Input json char array 
	 * @param object           : List object wait for converting
	 * @param field            : List element type
	 * @return                 : 0 if convert successful , not 0 if failed
	 */
	private int stringToArrayObject( char[] jsonCharArray, Object object, Field field ) {
		
		TokenType			valueTokenType ;
		int					valueBeginOffset ;
		int					valueEndOffset ;
		
		int					nret ;
		
		while(true) {
			// token "value" or '{'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE ) {
				try {
					if( field != null ) {
						Class<?> clazz = field.getType() ;
						if( clazz == ArrayList.class || clazz == LinkedList.class ) {
							Type type = field.getGenericType() ;
							ParameterizedType pt = (ParameterizedType) type ;
							Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0] ;
							Object childObject = typeClazz.newInstance() ;
							nret = stringToObjectProperties( jsonCharArray , childObject ) ;
							if( nret != 0 )
								return nret;
							
							((List<Object>) object).add( childObject );
						}
					} else {
						nret = stringToObjectProperties( jsonCharArray , null ) ;
						if( nret != 0 )
							return nret;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else if( tokenType == TokenType.TOKEN_TYPE_STRING || tokenType == TokenType.TOKEN_TYPE_INTEGER || tokenType == TokenType.TOKEN_TYPE_DECIMAL || tokenType == TokenType.TOKEN_TYPE_BOOL ) {
				;
			} else {
				int beginPos = endOffset - 16 ;
				if( beginPos < 0 )
					beginPos = 0 ;
				errorDesc = "unexpect \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			// token ',' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				if( object != null && field != null ) {
					errorCode = addArrayObject( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset , object, field ) ;
					if( errorCode != 0 )
						return errorCode;
				}
				
				if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET )
					break;
			} else {
				errorDesc = "unexpect \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
			}
		}
		
		return 0;
	}
		
	/**
	 * @Title                  : setObjectProperty 
	 * @Description            : set property in object
	 * @param jsonCharArray    : Input json char array 
	 * @param valueTokenType   : List element type enum
	 * @param valueBeginOffset : Token begin offset from json char array
	 * @param valueEndOffset   : Token end offset from json char array
	 * @param object           : List object wait for converting
	 * @param field            : List element type
	 * @param method           : 
	 * @return                 : 0 if convert successful , not 0 if failed
	 */
	private int setObjectProperty( char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field, Method method ) {
		
		StringBuilder	fieldStringBuilder ;

		fieldStringBuilder = fieldStringBuilderCache.get();
		
		if( field.getType() == String.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_STRING ) {
				try {
					String value ;
					if( fieldStringBuilder.length() > 0 ) {
						value = fieldStringBuilder.toString() ;
					} else {
						value = new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1) ;
					}
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Byte.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Byte	value = Byte.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Short.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Short	value = Short.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Integer.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Integer	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Long.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
				try {
					Long	value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Float.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
				try {
					Float	value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Double.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
				try {
					Double	value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType() == Boolean.class ) {
			if( valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
				try {
					Boolean	value = Boolean.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)) ;
					if( method != null ) {
						method.invoke(object, value);
					} else if( directAccessPropertyEnable == true ) {
						field.set( object, value );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			}
		} else if( field.getType().getName().equals("byte") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				byte	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).byteValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setByte( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("short") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				short	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).shortValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setShort( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("int") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				int	value = Integer.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).intValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setInt( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("long") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER ) {
			try {
				long	value = Long.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).longValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setLong( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("float") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				float	value = Float.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).floatValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setFloat( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("double") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL ) {
			try {
				double	value = Double.valueOf(new String(jsonCharArray,valueBeginOffset,valueEndOffset-valueBeginOffset+1)).doubleValue() ;
				if( method != null ) {
					method.invoke(object, value);
				} else if( directAccessPropertyEnable == true ) {
					field.setDouble( object, value );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( field.getType().getName().equals("boolean") && valueTokenType == TokenType.TOKEN_TYPE_BOOL ) {
			try {
				if( method != null ) {
					method.invoke(object, booleanValue);
				} else if( directAccessPropertyEnable == true ) {
					field.setBoolean( object, booleanValue );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else if( valueTokenType == TokenType.TOKEN_TYPE_NULL ) {
			try {
				if( method != null ) {
					method.invoke(object, null);
				} else if( directAccessPropertyEnable == true ) {
					field.set( object, null );
				}
			} catch (Exception e) {
				e.printStackTrace();
				return OKJSON_ERROR_EXCEPTION;
			}
		} else {
			if( strictPolicyEnable == true )
				return OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
		}
		
		return 0;
	}
	
	/**
	 * @Title                  : stringToObjectProperties 
	 * @Description            : convert json char array to object's properties 
	 * @param jsonCharArray    : Input json char array 
	 * @param object           : List object wait for converting
	 * @return                 : 0 if convert successful , not 0 if failed
	 */
	private int stringToObjectProperties( char[] jsonCharArray, Object object ) {
		
		Class					clazz ;
		HashMap<String,Field>	stringMapFields ;
		HashMap<String,Method>	stringMapMethods ;
		Field[]					fields ;
		Field					field ;
		Method					method ;
		TokenType				fieldNameTokenType ;
		int						fieldNameBeginOffset ;
		int						fieldNameEndOffset ;
		String					fieldName ;
		TokenType				valueTokenType ;
		int						valueBeginOffset ;
		int						valueEndOffset ;
		
		int						nret ;
		
		if( object != null ) {
			clazz = object.getClass();
			
			stringMapFields = stringMapFieldsCache.get().get( clazz.getName() ) ;
			if( stringMapFields == null ) {
				stringMapFields = new HashMap<String,Field>() ;
				stringMapFieldsCache.get().put( clazz.getName(), stringMapFields ) ;
			}
			
			stringMapMethods = stringMapMethodsCache.get().get( clazz.getName() ) ;
			if( stringMapMethods == null ) {
				stringMapMethods = new HashMap<String,Method>() ;
				stringMapMethodsCache.get().put( clazz.getName(), stringMapMethods ) ;
			}
			
			if( stringMapFields.isEmpty() ) {
				fields = clazz.getDeclaredFields() ;
				for( Field f : fields ) {
	                f.setAccessible(true);
	                
					fieldName = f.getName();
					stringMapFields.put(fieldName, f);
					
					try {
						method = clazz.getDeclaredMethod( "set" + fieldName.substring(0,1).toUpperCase(Locale.getDefault()) + fieldName.substring(1), f.getType() ) ;
						method.setAccessible(true);
						stringMapMethods.put(fieldName, method);
					} catch (NoSuchMethodException e2) {
						;
					} catch (SecurityException e2) {
						;
					}
				}
			}
		} else {
			stringMapFields = null ;
			stringMapMethods = null ;
		}
		
		while(true) {
			// token "name"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			fieldNameTokenType = tokenType ;
			fieldNameBeginOffset = beginOffset ;
			fieldNameEndOffset = endOffset ;
			fieldName = new String(jsonCharArray,fieldNameBeginOffset,fieldNameEndOffset-fieldNameBeginOffset+1) ;
			
			if( object != null ) {
				field = stringMapFields.get(fieldName) ;
				if( field == null ) {
					if( strictPolicyEnable == true )
						return OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT;
				}
				
				method = stringMapMethods.get(fieldName) ;
			} else {
				field = null ;
				method = null ;
			}
			
			if( tokenType != TokenType.TOKEN_TYPE_STRING ) {
				errorDesc = "expect a name but \""+String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1)+"\"" ;
				return OKJSON_ERROR_NAME_INVALID;
			}
			
			// token ':' or ',' or '}' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COLON ) {
				;
			} else if( tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE ) {
				clazz = field.getType() ;
				if( clazz == ArrayList.class || clazz == LinkedList.class ) {
					nret = addArrayObject( jsonCharArray, fieldNameTokenType, fieldNameBeginOffset, fieldNameEndOffset, object, field ) ;
					if( nret != 0 )
						return nret;
					
					if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE )
						break;
				}
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			} else {
				errorDesc = "expect ':' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
			
			// token '{' or '[' or "value"
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			valueTokenType = tokenType ;
			valueBeginOffset = beginOffset ;
			valueEndOffset = endOffset ;
			
			if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE || tokenType == TokenType.TOKEN_TYPE_LEFT_BRACKET ) {
				try {
					Object childObject ;
					
					if( field != null ) {
						childObject = field.getType().newInstance() ;
						if( childObject == null )
							return OKJSON_ERROR_UNEXPECT;
					} else {
						childObject = null ;
					}
					
					if( tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE ) {
						nret = stringToObjectProperties( jsonCharArray, childObject ) ;
					} else {
						nret = stringToArrayObject( jsonCharArray, childObject, field ) ;
					}
					if( nret != 0 )
						return nret;
					
					if( field != null ) {
						field.set( object, childObject );
					}
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_EXCEPTION;
				}
			} else {
				if( object != null && field != null ) {
					nret = setObjectProperty( jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, object, field, method ) ;
					if( nret != 0 )
						return nret;
				}
			}
			
			// token ',' or '}' or ']'
			nret = tokenJsonWord( jsonCharArray ) ;
			if( nret == OKJSON_ERROR_END_OF_BUFFER ) {
				break;
			}
			if( nret != 0 ) {
				return nret;
			}
			
			if( tokenType == TokenType.TOKEN_TYPE_COMMA ) {
				;
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE ) {
				break;
			} else if( tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET ) {
				break;
			} else {
				errorDesc = "expect ',' or '}' or ']' but \"" + String.copyValueOf(jsonCharArray,beginOffset,endOffset-beginOffset+1) + "\"" ;
				return OKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
			}
		}
		
		return 0;
	}
	
	/**
	 * @Title                  : stringToObject 
	 * @Description            : convert json char array to object 
	 * @param jsonCharArray    : Input json char array 
	 * @param object           : object wait for converting
	 * @return                 : 0 if convert successful , not 0 if failed
	 */
	public <T> T stringToObject( String jsonString, T object ) {
		
		char[]	jsonCharArray ;
		
		jsonCharArray = jsonString.toCharArray() ;
		jsonOffset = 0 ;
		jsonLength = jsonCharArray.length ;
		
		if( stringMapFieldsCache == null ) {
			stringMapFieldsCache = new ThreadLocal<HashMap<String,HashMap<String,Field>>>() ;
			if( stringMapFieldsCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			stringMapFieldsCache.set(new HashMap<String,HashMap<String,Field>>());
		}
		
		if( stringMapMethodsCache == null ) {
			stringMapMethodsCache = new ThreadLocal<HashMap<String,HashMap<String,Method>>>() ;
			if( stringMapMethodsCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			stringMapMethodsCache.set(new HashMap<String,HashMap<String,Method>>());
		}
		
		if( fieldStringBuilderCache == null ) {
			fieldStringBuilderCache = new ThreadLocal<StringBuilder>() ;
			if( fieldStringBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldStringBuilderCache.set(new StringBuilder(1024));
		}
		
		errorCode = tokenJsonWord( jsonCharArray ) ;
		if( errorCode != 0 ) {
			return null;
		}
		
		if( tokenType != TokenType.TOKEN_TYPE_LEFT_BRACE ) {
			errorCode = OKJSON_ERROR_FIND_FIRST_LEFT_BRACE ;
			return null;
		}
		
		errorCode = stringToObjectProperties( jsonCharArray, object ) ;
		if( errorCode != 0 )
			return null;
		
		return object;
	}
	
	public boolean isStrictPolicyEnable() {
		return strictPolicyEnable;
	}

	public void setStrictPolicyEnable(boolean strictPolicyEnable) {
		this.strictPolicyEnable = strictPolicyEnable;
	}

	public boolean isDirectAccessPropertyEnable() {
		return directAccessPropertyEnable;
	}

	public void setDirectAccessPropertyEnable(boolean directAccessPropertyEnable) {
		this.directAccessPropertyEnable = directAccessPropertyEnable;
	}

	public boolean isPrettyFormatEnable() {
		return prettyFormatEnable;
	}

	public void setPrettyFormatEnable(boolean prettyFormatEnable) {
		this.prettyFormatEnable = prettyFormatEnable;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public OkJsonParser() {
		this.strictPolicyEnable = false ;
		this.directAccessPropertyEnable = false ;
		this.prettyFormatEnable = false ;
		this.errorCode = 0 ;
		this.errorDesc = null ;
	}
}

/**
 * @ClassName   : OkJsonGenerator
 * @Description : Util tool class for serialing from object
 * @author      : calvin
 * @date        : 2019-03-17
 *
 */
class OkJsonGenerator {
	
	enum ClassFieldType {
		CLASSFIELDTYPE_STRING ,
		CLASSFIELDTYPE_NOT_STRING ,
		CLASSFIELDTYPE_LIST ,
		CLASSFIELDTYPE_SUBCLASS
	}
	
	class OkJsonClassField {
		char[]			fieldName ;
		char[]			fieldNameQM ;
		ClassFieldType	type ;
		Field			field ;
		Method			getMethod ;
	}
	
	private static ThreadLocal<HashMap<String,LinkedList<OkJsonClassField>>>	classMapFieldListCache ;
	private static ThreadLocal<OkJsonCharArrayBuilder>							jsonByteArrayBuilderCache ;
	private static ThreadLocal<OkJsonCharArrayBuilder>							fieldByteArrayBuilderCache ;
	private static ThreadLocal<HashMap<Class,Boolean>>							basicTypeClassMapBooleanCache ;
	
	private boolean				strictPolicyEnable ;
	private boolean				directAccessPropertyEnable ;
	private boolean				prettyFormatEnable ;
	
	private Integer				errorCode ;
	private String				errorDesc ;
	
	final private static int	OKJSON_ERROR_END_OF_BUFFER = 1 ;
	final private static int	OKJSON_ERROR_UNEXPECT = -4 ;
	final private static int	OKJSON_ERROR_EXCEPTION = -8 ;
	final private static int	OKJSON_ERROR_INVALID_BYTE = -11 ;
	final private static int	OKJSON_ERROR_FIND_FIRST_LEFT_BRACE = -21 ;
	final private static int	OKJSON_ERROR_NAME_INVALID = -22 ;
	final private static int	OKJSON_ERROR_EXPECT_COLON_AFTER_NAME = -23 ;
	final private static int	OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = -24 ;
	final private static int	OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -26 ;
	final private static int	OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = -28 ;
	final private static int	OKJSON_ERROR_NEW_OBJECT = -31 ;
	
	final private static char	SEPFIELD_CHAR = ',' ;
	final private static char[]	SEPFIELD_CHAR_PRETTY = ", \n".toCharArray() ;
	final private static char	ENTER_CHAR = '\n' ;
	final private static String	NULL_STRING = "null" ;
	
	/**
	 * @Title                      : objectToListString 
	 * @Description                : Convert list object to json char array
	 * #param array                : List elements in object 
	 * @param arrayCount           : List element count
	 * @param jsonCharArrayBuilder : Output to json char array 
	 * @param depth                : Output depth layout
	 * @return                     : 0 if convert successful , not 0 if failed
	 */
	private int objectToListString( List<Object> array, int arrayCount, Field field, OkJsonCharArrayBuilder jsonCharArrayBuilder, int depth ) {
		
		HashMap<Class,Boolean>	basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
		int						arrayIndex ;
		int						nret ;
		
		try {
				Type type = field.getGenericType() ;
				ParameterizedType pt = (ParameterizedType) type ;
				Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0] ;
				Boolean b = basicTypeClassMapBoolean.get( typeClazz ) ;
				if( b == null )
					b = false ;
				if(	b ) {
						arrayIndex = 0 ;
						for( Object object : array ) {
							arrayIndex++;
							if( arrayIndex > 1 ) {
								if( prettyFormatEnable ) {
									jsonCharArrayBuilder.appendCharArrayWith3( SEPFIELD_CHAR_PRETTY );
								} else {
									jsonCharArrayBuilder.appendChar(SEPFIELD_CHAR);
								}
							}
							
							if( typeClazz == String.class && object != null ) {
								String str = (String)object ;
								jsonCharArrayBuilder.appendTabs(depth+1).appendJsonQmStringQm(str);
							} else {
								jsonCharArrayBuilder.appendTabs(depth+1).appendJsonString(object.toString());
							}
						}
				} else {
					arrayIndex = 0 ;
					for( Object object : array ) {
						arrayIndex++;
						if( arrayIndex > 1 ) {
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendCharArrayWith3(SEPFIELD_CHAR_PRETTY);
							} else {
								jsonCharArrayBuilder.appendChar(SEPFIELD_CHAR);
							}
						}
						
						if( object != null ) {
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendTabs(depth+1).appendString("{\n");
							} else {
								jsonCharArrayBuilder.appendChar('{');
							}
							nret = objectToPropertiesString( object, jsonCharArrayBuilder, depth+1 ) ;
							if( nret != 0 )
								return nret;
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendTabs(depth+1).appendString("}");
							} else {
								jsonCharArrayBuilder.appendTabs(depth+1).appendChar('}');
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return OKJSON_ERROR_EXCEPTION;
		}
		
		jsonCharArrayBuilder.appendChar(ENTER_CHAR);
		
		return 0;
	}
	
	/**
	 * @Title                      : unfoldEscape 
	 * @Description                : Unfold json escape char to json char array 
	 * #param value                : source string 
	 * @return                     : null if convert failed, string if convert successful
	 */
	private String unfoldEscape( String value ) {
		
		OkJsonCharArrayBuilder	fieldCharArrayBuilder = fieldByteArrayBuilderCache.get() ;
		char[]					jsonCharArrayBuilder ;
		int						jsonCharArrayLength ;
		int						jsonCharArrayIndex ;
		int						segmentBeginOffset ;
		int						segmentLen ;
		char					c ;
		
		if( value == null )
			return null;
		
		jsonCharArrayBuilder = value.toCharArray() ;
		jsonCharArrayLength = value.length() ;
		
		fieldCharArrayBuilder.setLength(0);
		
		segmentBeginOffset = 0 ;
		for( jsonCharArrayIndex = 0 ; jsonCharArrayIndex < jsonCharArrayLength ; jsonCharArrayIndex++ ) {
			c = jsonCharArrayBuilder[jsonCharArrayIndex] ;
			if( c == '\"' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\\"".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( c == '\\' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\\\".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if( c == '/' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\/".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\t' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\t".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\f' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\f".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\b' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\b".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\n' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\n".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			} else if ( c == '\r' ) {
				segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
				if( segmentLen > 0 )
					fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
				fieldCharArrayBuilder.appendCharArray( "\\r".toCharArray() ); 
				segmentBeginOffset = jsonCharArrayIndex + 1 ;
			}
		}
		if( fieldCharArrayBuilder.getLength() > 0 && segmentBeginOffset < jsonCharArrayIndex ) {
			segmentLen = jsonCharArrayIndex-segmentBeginOffset ;
			if( segmentLen > 0 )
				fieldCharArrayBuilder.appendBytesFromOffsetWithLength( jsonCharArrayBuilder, segmentBeginOffset, segmentLen );
		}
		
		if( fieldCharArrayBuilder.getLength() == 0 )
			return value;
		else
			return fieldCharArrayBuilder.toString();
	}
	
	/**
	 * @Title                      : objectToPropertiesString 
	 * @Description                : Convert object to json char array
	 * #param object               : source object
	 * @param jsonCharArrayBuilder : Output to json char array 
	 * @param depth                : Output depth layout
	 * @return                     : 0 if convert successful , not 0 if failed
	 */
	private int objectToPropertiesString( Object object, OkJsonCharArrayBuilder jsonCharArrayBuilder, int depth ) {
		
		HashMap<Class,Boolean>			basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
		Class<?>						clazz ;
		LinkedList<OkJsonClassField>	classFieldList ;
		Field[]							fields ;
		String							methodName ;
		int								fieldIndex ;
		
		int								nret = 0 ;
		
		clazz = object.getClass();
		
		classFieldList = classMapFieldListCache.get().get( clazz.getName() ) ;
		if( classFieldList == null ) {
			classFieldList = new LinkedList<OkJsonClassField>() ;
			classMapFieldListCache.get().put( clazz.getName(), classFieldList ) ;
		}
		
		if( classFieldList.isEmpty() ) {
			OkJsonClassField classField ;
			
			fields = clazz.getDeclaredFields() ;
			for( Field f : fields ) {
				f.setAccessible(true);
				
				classField = new OkJsonClassField() ;
				classField.fieldName = f.getName().toCharArray() ;
				classField.fieldNameQM = ('\"'+f.getName()+'\"').toCharArray() ;
				classField.field = f ;
				if( f.getType() == String.class )
					classField.type = ClassFieldType.CLASSFIELDTYPE_STRING ;
				else if( basicTypeClassMapBoolean.get( f.getType() ) != null || f.getType().isPrimitive() )
					classField.type = ClassFieldType.CLASSFIELDTYPE_NOT_STRING ;
				else if( f.getType() == ArrayList.class || f.getType() == LinkedList.class )
					classField.type = ClassFieldType.CLASSFIELDTYPE_LIST ;
				else
					classField.type = ClassFieldType.CLASSFIELDTYPE_SUBCLASS ;
				
				try {
						if( f.getType() == Boolean.class || f.getType().getName().equals("boolean") ) {
							methodName = "is" + f.getName().substring(0,1).toUpperCase(Locale.getDefault()) + f.getName().substring(1) ;
						} else {
							methodName = "get" + f.getName().substring(0,1).toUpperCase(Locale.getDefault()) + f.getName().substring(1) ;
						}
						classField.getMethod = clazz.getDeclaredMethod( methodName ) ;
						classField.getMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
					;
				} catch (Exception e) {
					e.printStackTrace();
					return OKJSON_ERROR_UNEXPECT;
				}
				
				classFieldList.add(classField);
			}
		}
		
		fieldIndex = 0 ;
		for( OkJsonClassField classField : classFieldList ) {
			fieldIndex++;
			if( fieldIndex > 1 ) {
				if( prettyFormatEnable ) {
					jsonCharArrayBuilder.appendCharArrayWith3( SEPFIELD_CHAR_PRETTY );
				} else {
					jsonCharArrayBuilder.appendChar( SEPFIELD_CHAR );
				}
			}
			
			switch( classField.type ) {
				case CLASSFIELDTYPE_STRING :
					String string = null ;
					if( classField.getMethod != null ) {
						try {
							string = (String)(classField.getMethod.invoke( object )) ;
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					} else {
						try {
							string = (String)(classField.field.get( object ));
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					}
					string = unfoldEscape( (String)string ) ;
					if( string != null ) {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndQmStringQmPretty(classField.fieldName,string);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndQmStringQm(classField.fieldName,string);
						}
					} else {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(classField.fieldName,NULL_STRING);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName,NULL_STRING);
						}
					}
					break;
				case CLASSFIELDTYPE_NOT_STRING :
					Object value = null ;
					if( classField.getMethod != null ) {
						try {
							value = classField.getMethod.invoke( object );
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					} else {
						try {
							value = classField.field.get( object );
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					}
					if( value != null ) {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(classField.fieldName,value.toString());
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName,value.toString());
						}
					} else {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(classField.fieldName,NULL_STRING);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName,NULL_STRING);
						}
					}
					break;
				case CLASSFIELDTYPE_LIST :
					List<Object> array ;
					if( classField.getMethod != null ) {
						try {
							array = (List<Object>)(classField.getMethod.invoke(object)) ;
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					} else {
						try {
							array = (List<Object>)(classField.field.get(object));
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					}
					if( array != null ) {
						int arrayCount = array.size() ;
						if( arrayCount > 0 ) {
							if( prettyFormatEnable ) {
								jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(classField.fieldName,'[');
								nret = objectToListString( array, arrayCount, classField.field, jsonCharArrayBuilder, depth+1 ) ;
								if( nret != 0 )
									return nret;
								jsonCharArrayBuilder.appendTabs(depth+1).appendChar(']');
							} else {
								jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(classField.fieldName,'[');
								nret = objectToListString( array, arrayCount, classField.field, jsonCharArrayBuilder, depth+1 ) ;
								if( nret != 0 )
									return nret;
								jsonCharArrayBuilder.appendCloseByte(']');
							}
						}
					} else {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(classField.fieldName,NULL_STRING);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName,NULL_STRING);
						}
					}
					break;
				case CLASSFIELDTYPE_SUBCLASS :
					Object subObject;
					if( classField.getMethod != null ) {
						try {
							subObject = classField.getMethod.invoke( object );
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					} else {
						try {
							subObject = classField.field.get( object ) ;
						} catch (Exception e) {
							e.printStackTrace();
							return OKJSON_ERROR_EXCEPTION;
						}
					}
					if( subObject != null ) {
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndOpenBytePretty(classField.fieldName,'{');
							nret = objectToPropertiesString( subObject, jsonCharArrayBuilder, depth+1 ) ;
							if( nret != 0 )
								return nret;
							jsonCharArrayBuilder.appendTabs(depth+1).appendChar('}');
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(classField.fieldName,'{');
							nret = objectToPropertiesString( subObject, jsonCharArrayBuilder, depth+1 ) ;
							if( nret != 0 )
								return nret;
							jsonCharArrayBuilder.appendCloseByte('}');
						}
					} else { 
						if( prettyFormatEnable ) {
							jsonCharArrayBuilder.appendTabs(depth+1).appendJsonNameAndColonAndStringPretty(classField.fieldName,NULL_STRING);
						} else {
							jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName,NULL_STRING);
						}
					}
					break;
			}
		}
		
		if( prettyFormatEnable ) {
			jsonCharArrayBuilder.appendChar(ENTER_CHAR);
		}
		
		return 0;
	}
	
	/**
	 * @Title                      : objectToString 
	 * @Description                : Convert object to json char array
	 * #param object               : source object
	 * @return                     : null if convert failed , not object if convert successful
	 */
	public String objectToString( Object object ) {
		
		OkJsonCharArrayBuilder	jsonCharArrayBuilder ;
		OkJsonCharArrayBuilder	fieldCharArrayBuilder ;
		HashMap<Class,Boolean>	basicTypeClassMapString ;
		
		if( classMapFieldListCache == null ) {
			classMapFieldListCache = new ThreadLocal<HashMap<String,LinkedList<OkJsonClassField>>>() ;
			if( classMapFieldListCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			classMapFieldListCache.set(new HashMap<String,LinkedList<OkJsonClassField>>());
		}
		
		if( jsonByteArrayBuilderCache == null ) {
			jsonByteArrayBuilderCache = new ThreadLocal<OkJsonCharArrayBuilder>() ;
			if( jsonByteArrayBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonCharArrayBuilder = new OkJsonCharArrayBuilder(1024) ;
			if( jsonCharArrayBuilder == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			jsonByteArrayBuilderCache.set(jsonCharArrayBuilder);
		} else {
			jsonCharArrayBuilder = jsonByteArrayBuilderCache.get() ;
		}
		jsonCharArrayBuilder.setLength(0);
		
		if( fieldByteArrayBuilderCache == null ) {
			fieldByteArrayBuilderCache = new ThreadLocal<OkJsonCharArrayBuilder>() ;
			if( fieldByteArrayBuilderCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldCharArrayBuilder = new OkJsonCharArrayBuilder(1024) ;
			if( fieldCharArrayBuilder == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			fieldByteArrayBuilderCache.set(fieldCharArrayBuilder);
		}
		
		if( basicTypeClassMapBooleanCache == null ) {
			basicTypeClassMapBooleanCache = new ThreadLocal<HashMap<Class,Boolean>>() ;
			if( basicTypeClassMapBooleanCache == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			basicTypeClassMapString = new HashMap<Class,Boolean>() ;
			if( basicTypeClassMapString == null ) {
				errorDesc = "New object failed for clazz" ;
				errorCode = OKJSON_ERROR_NEW_OBJECT;
				return null;
			}
			basicTypeClassMapString.put( String.class, new Boolean(true) );
			basicTypeClassMapString.put( Byte.class, new Boolean(true) );
			basicTypeClassMapString.put( Short.class, new Boolean(true) );
			basicTypeClassMapString.put( Integer.class, new Boolean(true) );
			basicTypeClassMapString.put( Long.class, new Boolean(true) );
			basicTypeClassMapString.put( Float.class, new Boolean(true) );
			basicTypeClassMapString.put( Double.class, new Boolean(true) );
			basicTypeClassMapString.put( Boolean.class, new Boolean(true) );
			basicTypeClassMapBooleanCache.set(basicTypeClassMapString);
		}
		
		if( prettyFormatEnable ) {
			jsonCharArrayBuilder.appendCharArray( "{\n".toCharArray() );
		} else {
			jsonCharArrayBuilder.appendChar( '{' );
		}
		
		errorCode = objectToPropertiesString( object, jsonCharArrayBuilder, 0 );
		if( errorCode != 0 )
			return null;
		
		if( prettyFormatEnable ) {
			jsonCharArrayBuilder.appendCharArray( "}\n".toCharArray() );
		} else {
			jsonCharArrayBuilder.appendChar( '}' );
		}
		
		return jsonCharArrayBuilder.toString();
	}
	
	public boolean isStrictPolicyEnable() {
		return strictPolicyEnable;
	}

	public void setStrictPolicyEnable(boolean strictPolicyEnable) {
		this.strictPolicyEnable = strictPolicyEnable;
	}

	public boolean isDirectAccessPropertyEnable() {
		return directAccessPropertyEnable;
	}

	public void setDirectAccessPropertyEnable(boolean directAccessPropertyEnable) {
		this.directAccessPropertyEnable = directAccessPropertyEnable;
	}

	public boolean isPrettyFormatEnable() {
		return prettyFormatEnable;
	}

	public void setPrettyFormatEnable(boolean prettyFormatEnable) {
		this.prettyFormatEnable = prettyFormatEnable;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public OkJsonGenerator() {
		this.strictPolicyEnable = false ;
		this.directAccessPropertyEnable = false ;
		this.prettyFormatEnable = false ;
		this.errorCode = 0 ;
		this.errorDesc = null ;
	}
}

/**
 * @ClassName   : OkJsonCharArrayBuilder
 * @Description : Util tool class for appending char array on serialing
 * @author      : calvin
 * @date        : 2019-03-17
 *
 */
class OkJsonCharArrayBuilder {
	
	private char[]		buf ;
	private int			bufSize ;
	private int			bufLength ;
	
	final private static String	TABS = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" ;
	
	public OkJsonCharArrayBuilder() {
		this( 16 );
	}
	
	public OkJsonCharArrayBuilder( int initBufSize ) {
		this.buf = new char[ initBufSize ] ;
		this.bufSize = initBufSize ;
		this.bufLength = 0 ;
	}
	
	private void resize( int newSize ) {
		char[]		newBuf ;
		int			newBufSize ;

		if( bufSize < 10240240 ) {
			newBufSize = bufSize * 2 ;
		} else {
			newBufSize = bufSize + 10240240 ;
		}
		if( newBufSize < newSize )
			newBufSize = newSize ;
		newBuf = new char[ newBufSize ] ;
		System.arraycopy(buf, 0, newBuf, 0, bufLength);
		buf = newBuf ;
		bufSize = newBufSize ;
	}
	
	public OkJsonCharArrayBuilder appendChar( char c ) {
		int		newBufLength = bufLength + 1 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = c ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCharArray( char[] charArray ) {
		int		newBufLength = bufLength + charArray.length ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( charArray, 0, buf, bufLength, charArray.length ); bufLength = newBufLength ;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCharArrayWith3( char[] charArray ) {
		int		newBufLength = bufLength + 3 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = charArray[0] ; bufLength++;
		buf[bufLength] = charArray[1] ; bufLength++;
		buf[bufLength] = charArray[2] ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCharArrayWith4( char[] charArray ) {
		int		newBufLength = bufLength + 4 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = charArray[0] ; bufLength++;
		buf[bufLength] = charArray[1] ; bufLength++;
		buf[bufLength] = charArray[2] ; bufLength++;
		buf[bufLength] = charArray[4] ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendString( String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + strLength ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		str.getChars(0, strLength, buf, bufLength); bufLength = strLength ;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendBytesFromOffsetWithLength( char[] charArray, int offset, int len ) {
		int		newBufLength = bufLength + len ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		System.arraycopy( charArray, offset, buf, bufLength, len ); bufLength = newBufLength ;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendTabs( int tabCount ) {
		int		newBufLength = bufLength + tabCount ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		if( tabCount <= TABS.length() ) {
			System.arraycopy( TABS.toCharArray(), 0, buf, bufLength, tabCount); bufLength+=tabCount;
		} else {
			for( int i = 1 ; i < tabCount ; i++ ) {
				buf[bufLength] = '\t' ; bufLength++;
			}
		}
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndOpenByte( char[] name, char c ) {
		int		newBufLength = bufLength + name.length+4 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = c ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndOpenBytePretty( char[] name, char c ) {
		int		newBufLength = bufLength + name.length+7 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = c ; bufLength++;
		buf[bufLength] = '\n' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendCloseByte( char c ) {
		int		newBufLength = bufLength + 1 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = c ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndCharArray( char[] name, char[] str ) {
		int		newBufLength = bufLength + name.length+str.length+3 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndCharArrayPretty( char[] name, char[] str ) {
		int		newBufLength = bufLength + name.length+str.length+5 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		System.arraycopy( str, 0, buf, bufLength, str.length ); bufLength+=str.length;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndString( char[] name, String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + name.length+strLength+3 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndStringPretty( char[] name, String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + name.length+strLength+6 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		buf[bufLength] = ' ' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndQmStringQm( char[] name, String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + name.length+strLength+5 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = '"' ; bufLength++;
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		buf[bufLength] = '"' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonNameAndColonAndQmStringQmPretty( char[] name, String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + name.length+strLength+7 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		System.arraycopy( name, 0, buf, bufLength, name.length); bufLength+=name.length;
		buf[bufLength] = '"' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = ':' ; bufLength++;
		buf[bufLength] = ' ' ; bufLength++;
		buf[bufLength] = '"' ; bufLength++;
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		buf[bufLength] = '"' ; bufLength++;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonString( String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + strLength ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		
		return this;
	}
	
	public OkJsonCharArrayBuilder appendJsonQmStringQm( String str ) {
		int		strLength = str.length() ;
		int		newBufLength = bufLength + strLength + 2 ;
		
		if( newBufLength > bufSize )
			resize( newBufLength );
		
		buf[bufLength] = '"' ; bufLength++;
		str.getChars(0, strLength, buf, bufLength); bufLength+=strLength;
		buf[bufLength] = '"' ; bufLength++;
		
		return this;
	}
	
	public int getLength() {
		return bufLength;
	}
	
	public void setLength( int length ) {
		bufLength = length ;
	}
	
	@Override
	public String toString() {
		return new String( buf, 0, bufLength ) ;
	}
}
