package xyz.calvinwilliams.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Demo {

	public static void main(String[] args) {
		Connection				conn = null ;
		List<SqlactionDemoSAO>	sqlactionDemoList = null ;
		SqlactionDemoSAO		sqlactionDemo = null ;
		SqlactionDemoSAO		sqlactionDemoForSetInput = null ;
		SqlactionDemoSAO		sqlactionDemoForWhereInput = null ;
		int						nret = 0 ;

		// connect to database
		try {
			Class.forName( "com.mysql.jdbc.Driver" );
			conn = DriverManager.getConnection( "jdbc:mysql://127.0.0.1:3306/calvindb?serverTimezone=GMT", "calvin", "calvin" ) ;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			conn.setAutoCommit(false);
			
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList, null ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.name = "Calvin" ;
			nret = SqlactionDemoSAO.SqlAction_DELETE_FROM_sqlaction_demo_WHERE_name_E__( conn, sqlactionDemo ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_DELETE_FROM_sqlaction_demo_WHERE_name_E__ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_DELETE_FROM_sqlaction_demo_WHERE_name_E__ ok , rows["+nret+"] effected" );
			}
			
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList, null ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.name = "Calvin" ;
			sqlactionDemo.address = "My address" ;
			nret = SqlactionDemoSAO.SqlAction_INSERT_INTO_sqlaction_demo( conn, sqlactionDemo ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_INSERT_INTO_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_INSERT_INTO_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList, null ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			sqlactionDemoForSetInput = new SqlactionDemoSAO() ;
			sqlactionDemoForSetInput.address = "My address 2" ;
			sqlactionDemoForWhereInput = new SqlactionDemoSAO() ;
			sqlactionDemoForWhereInput.name = "Calvin" ;
			nret = SqlactionDemoSAO.SqlAction_UPDATE_sqlaction_demo_SET_address_E___WHERE_name_E__( conn, sqlactionDemoForSetInput, sqlactionDemoForWhereInput ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_UPDATE_sqlaction_demo_SET_address_E___WHERE_name_E__ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_UPDATE_sqlaction_demo_SET_address_E___WHERE_name_E__ ok , rows["+nret+"] effected" );
			}
			
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList, null ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SqlAction_SELECT_ALL_FROM_sqlaction_demo ok" );
				for( SqlactionDemoSAO r : sqlactionDemoList ) {
					System.out.println( "    id["+r.id+"] name["+r.name+"] address["+r.address+"]" );
				}
			}
			
			conn.commit();
		} catch(Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				return;
			}
		}
		
		return;
	}

}
