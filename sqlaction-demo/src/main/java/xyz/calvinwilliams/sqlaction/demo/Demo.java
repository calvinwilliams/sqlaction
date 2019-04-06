package xyz.calvinwilliams.sqlaction.demo;

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

		// Connect to database
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
			
			// Delete records with name
			nret = SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_( conn, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.DELETE_FROM_sqlaction_demo_WHERE_name_E_ ok , rows["+nret+"] effected" );
			}
			
			// Insert record
			sqlactionDemo = new SqlactionDemoSAO() ;
			sqlactionDemo.name = "Calvin" ;
			sqlactionDemo.address = "My address" ;
			nret = SqlactionDemoSAO.INSERT_INTO_sqlaction_demo( conn, sqlactionDemo ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.INSERT_INTO_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.INSERT_INTO_sqlaction_demo ok" );
			}
			
			// Update record with name
			nret = SqlactionDemoSAO.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_( conn, "My address 2", "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.UPDATE_sqlaction_demo_SET_address_E_WHERE_name_E_ ok , rows["+nret+"] effected" );
			}
			
			// Query records
			sqlactionDemoList = new LinkedList<SqlactionDemoSAO>() ;
			nret = SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo( conn, sqlactionDemoList ) ;
			if( nret < 0 ) {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo failed["+nret+"]" );
				conn.rollback();
				return;
			} else {
				System.out.println( "SqlactionDemoSAO.SELECT_ALL_FROM_sqlaction_demo ok" );
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
