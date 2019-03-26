package xyz.calvinwilliams.sqlaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import xyz.calvinwilliams.okjson.OKJSON;

public class SqlActionTest {

	public static int TestSelectTable( Connection conn ) throws Exception {
		
		List<MyDemoTableSAO>	selectOutputList = null ;
		MyDemoTableSAO			whereInput ;
		int						nret = 0 ;
		
		try {
			selectOutputList = new LinkedList<MyDemoTableSAO>() ;
			nret = MyDemoTableSAO.SqlAction_SELECT_ALL_FROM_my_demo_table( conn, selectOutputList ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_my_demo_table failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_my_demo_table ok" );
			}
			
			for( MyDemoTableSAO t : selectOutputList ) {
				System.out.println( "\t\t" + "id["+t.id+"] code["+t.code+"] name["+t.name+"] fnameCn["+t.fnameCn+"] price["+t.price+"] createDate["+t.createDate+"] createTime["+t.createTime+"] createTimestamp["+t.createTimestamp+"]" );
			}
			
			selectOutputList = new LinkedList<MyDemoTableSAO>() ;
			whereInput = new MyDemoTableSAO() ;
			whereInput.code = "my_code_2" ;
			nret = MyDemoTableSAO.SqlAction_SELECT_name_FROM_my_demo_table_WHERE_code_E( conn, selectOutputList, whereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_name_FROM_my_demo_table_WHERE_code_E failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_name_FROM_my_demo_table_WHERE_code_E ok" );
			}
			
			for( MyDemoTableSAO t : selectOutputList ) {
				System.out.println( "\t\t" + "name["+t.name+"]" );
			}
		} catch (Exception e) {
			throw e;
		}
		
		return 0;
	}
	
	public static int TestInsertTable( Connection conn ) throws Exception {
		
		MyDemoTableSAO			valuesInput ;
		int						nret = 0 ;
		
		try {
			valuesInput = new MyDemoTableSAO() ;
			valuesInput.code = "my_code_1" ;
			valuesInput.name = "my_name_1" ;
			valuesInput.fnameCn = "我的中文名字1" ;
			valuesInput.price = new BigDecimal(100000000.00) ;
			valuesInput.createDate = new java.sql.Date(new java.util.Date().getTime()) ;
			valuesInput.createTime = new java.sql.Time(new java.util.Date().getTime()) ;
			valuesInput.createTimestamp = new java.sql.Timestamp(new java.util.Date().getTime()) ;
			nret = MyDemoTableSAO.SqlAction_INSERT_INTO_my_demo_table( conn, valuesInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table ok" );
			}
			
			valuesInput = new MyDemoTableSAO() ;
			valuesInput.code = "my_code_2" ;
			valuesInput.name = "my_name_2" ;
			valuesInput.fnameCn = "我的中文名字2" ;
			valuesInput.price = new BigDecimal(100000000.00) ;
			valuesInput.createDate = new java.sql.Date(new java.util.Date().getTime()) ;
			valuesInput.createTime = new java.sql.Time(new java.util.Date().getTime()) ;
			valuesInput.createTimestamp = new java.sql.Timestamp(new java.util.Date().getTime()) ;
			nret = MyDemoTableSAO.SqlAction_INSERT_INTO_my_demo_table( conn, valuesInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table ok" );
			}
			
			valuesInput = new MyDemoTableSAO() ;
			valuesInput.code = "my_code_3" ;
			valuesInput.name = "my_name_3" ;
			valuesInput.fnameCn = "我的中文名字3" ;
			valuesInput.price = new BigDecimal(100000000.00) ;
			valuesInput.createDate = new java.sql.Date(new java.util.Date().getTime()) ;
			valuesInput.createTime = new java.sql.Time(new java.util.Date().getTime()) ;
			valuesInput.createTimestamp = new java.sql.Timestamp(new java.util.Date().getTime()) ;
			nret = MyDemoTableSAO.SqlAction_INSERT_INTO_my_demo_table( conn, valuesInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_my_demo_table ok" );
			}
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
		
		return 0;
	}
	
	public static int TestUpdateTable( Connection conn ) throws Exception {
		
		MyDemoTableSAO			setInput ;
		MyDemoTableSAO			whereInput ;
		int						nret = 0 ;
		
		try {
			setInput = new MyDemoTableSAO() ;
			setInput.fnameCn = "我的中文名字*" ;
			nret = MyDemoTableSAO.SqlAction_UPDATE_my_demo_table_SET_fname_cn( conn, setInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_my_demo_table_SET_fname_cn failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_my_demo_table_SET_fname_cn ok" );
			}
			
			setInput = new MyDemoTableSAO() ;
			setInput.fnameCn = "我的中文名字22" ;
			whereInput = new MyDemoTableSAO() ;
			whereInput.code = "my_code_2" ;
			nret = MyDemoTableSAO.SqlAction_UPDATE_my_demo_table_SET_fname_cn_WHERE_code_E( conn, setInput, whereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_my_demo_table_SET_fname_cn_WHERE_code_E failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_my_demo_table_SET_fname_cn_WHERE_code_E ok" );
			}
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
		
		return 0;
	}
	
	public static int TestDeleteTable1( Connection conn ) throws Exception {
		
		MyDemoTableSAO			whereInput ;
		int						nret = 0 ;
		
		try {
			whereInput = new MyDemoTableSAO() ;
			whereInput.code = "my_code_2" ;
			nret = MyDemoTableSAO.SqlAction_DELETE_FROM_my_demo_table_WHERE_code_NE( conn, whereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_my_demo_table_WHERE_code_NE failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_my_demo_table_WHERE_code_NE ok" );
			}
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
		
		return 0;
	}
	
	public static int TestDeleteTable2( Connection conn ) throws Exception {
		
		MyDemoTableSAO			whereInput ;
		int						nret = 0 ;
		
		try {
			nret = MyDemoTableSAO.SqlAction_DELETE_FROM_my_demo_table( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_my_demo_table failed["+nret+"]" );
				return -1;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_my_demo_table ok" );
			}
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw e;
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		
		Path					currentPath ;
		Path					dbserverConfJsonFilePath ;
		String					dbserverConfJsonFileContent ;
		DbServerConf			dbserverConf ;
		
		Connection				conn = null ;
		
		int						nret = 0 ;
		
		// load dbserver.conf.json
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
		
		// query database metadata
		try {
			Class.forName( dbserverConf.driver );
			conn = DriverManager.getConnection( dbserverConf.url, dbserverConf.user, dbserverConf.pwd ) ;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// test all cases
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println( "TestSelectTable ..." );
			nret = TestSelectTable( conn ) ;
			System.out.println( "TestSelectTable ok" );
		} catch (Exception e1) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			e1.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestInsertTable ..." );
			nret = TestInsertTable( conn ) ;
			System.out.println( "TestInsertTable ok" );
		} catch (Exception e) {
			System.out.println( "TestInsertTable failed["+nret+"]" );
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestSelectTable ..." );
			nret = TestSelectTable( conn ) ;
			System.out.println( "TestSelectTable ok" );
		} catch (Exception e1) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			e1.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestUpdateTable ..." );
			nret = TestUpdateTable( conn ) ;
			System.out.println( "TestUpdateTable ok" );
		} catch (Exception e) {
			System.out.println( "TestUpdateTable failed["+nret+"]" );
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestSelectTable ..." );
			nret = TestSelectTable( conn ) ;
			System.out.println( "TestSelectTable ok" );
		} catch (Exception e1) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			e1.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestDeleteTable ..." );
			nret = TestDeleteTable1( conn ) ;
			System.out.println( "TestDeleteTable ok" );
		} catch (Exception e) {
			System.out.println( "TestDeleteTable failed["+nret+"]" );
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestSelectTable ..." );
			nret = TestSelectTable( conn ) ;
			System.out.println( "TestSelectTable ok" );
		} catch (Exception e1) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			e1.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestDeleteTable ..." );
			nret = TestDeleteTable2( conn ) ;
			System.out.println( "TestDeleteTable ok" );
		} catch (Exception e) {
			System.out.println( "TestDeleteTable failed["+nret+"]" );
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println( "TestSelectTable ..." );
			nret = TestSelectTable( conn ) ;
			System.out.println( "TestSelectTable ok" );
		} catch (Exception e1) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			e1.printStackTrace();
			return;
		}
	}

}
