/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.sqlaction.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class SqlActionTest {

	public static int testSelectTable( Connection conn ) {

		List<UserSAO>		userList = null ;
		UserSAO				user = null ;
		List<UserSAO>		userListForSelectOutput = null ;
		List<UserOrderSAO>	userOrderListForSelectOutput = null ;
		UserSAO				userForWhereInput = null ;
		int					nret = 0 ;

		try {
			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SELECT_ALL_FROM_user( conn, userList, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SELECT_ALL_FROM_user_WHERE_name_E_( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.age = 3 ;
			user.gender = "M" ;
			nret = UserSAO.SELECT_name_j_address_FROM_user_WHERE_age_LE_AND_gender_E_( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_name_j_address_FROM_user_WHERE_age_LE_AND_gender_E_ failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "SELECT_name_j_address_FROM_user_WHERE_age_LE_AND_gender_E_ ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SELECT_ALL_FROM_user_ORDER_BY_name_DESC( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_ORDER_BY_name_DESC failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_ORDER_BY_name_DESC ok , ["+userList.size()+"]records" );
			}
			
			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"] count___["+u.count___+"]" );
			}
			
			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SELECT_gender_j_count_ALL_FROM_user_GROUP_BY_gender( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_gender_j_count_ALL_FROM_user_GROUP_BY_gender failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_gender_j_count_ALL_FROM_user_GROUP_BY_gender ok , ["+userList.size()+"]records" );
			}
			
			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"] count___["+u.count___+"]" );
			}
			
			userListForSelectOutput = new LinkedList<UserSAO>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAO>() ;
			userForWhereInput = new UserSAO() ;
			userForWhereInput.name = "Calvin" ;
			nret = UserOrderSAO.queryUserAndOrderByName( conn, userListForSelectOutput, userOrderListForSelectOutput, userForWhereInput, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "queryUserAndOrderByName failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "queryUserAndOrderByName ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserSAO u : userListForSelectOutput ) {
				System.out.print( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}
			for( UserOrderSAO o : userOrderListForSelectOutput ) {
				System.out.println( " | " + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}

			userListForSelectOutput = new LinkedList<UserSAO>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAO>() ;
			userForWhereInput = new UserSAO() ;
			userForWhereInput.name = "Calvin" ;
			nret = UserOrderSAO.SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id( conn, userListForSelectOutput, userOrderListForSelectOutput, userForWhereInput, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserSAO u : userListForSelectOutput ) {
				System.out.print( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}
			for( UserOrderSAO o : userOrderListForSelectOutput ) {
				System.out.println( " | " + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}

	public static int testInsertTable( Connection conn ) {

		UserSAO			user = null ;
		List<UserSAO>	userList = null ;
		UserOrderSAO	userOrder = null ;
		int				nret = 0 ;

		try {
			user = new UserSAO() ;
			user.name = "Calvin" ;
			user.gender = "M" ;
			user.age = 30 ;
			user.address = "Calvin address" ;
			user.level = 8 ;
			nret = UserSAO.INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.name = "Dozimoyi" ;
			user.gender = "F" ;
			user.age = 20 ;
			user.address = "Dozimoyi address" ;
			user.level = 7 ;
			nret = UserSAO.INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.name = "Mark" ;
			user.gender = "M" ;
			user.age = 3 ;
			user.address = "Mark address" ;
			user.level = 2 ;
			nret = UserSAO.INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SELECT_ALL_FROM_user_WHERE_name_E_( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ ok , count["+nret+"]" );
			}

			if( userList.size() < 1 ) {
				System.out.println("*** ERROR : Record not found about 'Calvin'");
				return -1;
			}

			userOrder = new UserOrderSAO() ;
			userOrder.userId = userList.get(0).id ;
			userOrder.itemName = "我的商品" ;
			userOrder.amount = 100 ;
			userOrder.totalPrice = 1000.00 ;
			nret = UserOrderSAO.INSERT_INTO_user_order( conn, userOrder ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user_order failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user_order ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int testUpdateTable( Connection conn ) {

		UserSAO			userForSetInput = null ;
		UserSAO			userForWhereInput = null ;
		UserOrderSAO	userOrderForSetInput = null ;
		UserOrderSAO	userOrderForWhereInput = null ;
		UserSAO			user = null ;
		List<UserSAO>	userList = null ;
		int				nret = 0 ;

		try {
			userForSetInput = new UserSAO() ;
			userForSetInput.level = 6 ;
			nret = UserSAO.UPDATE_user_SET_level_E_( conn, userForSetInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_SET_level_E_ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "UPDATE_user_SET_level_E_ ok , rows["+nret+"] effected" );
			}

			nret = UserSAO.UPDATE_user_SET_address_E_calvin_address_j_level_E_10_WHERE_name_E_Calvin_( conn, null, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_SET_address_E_calvin_address_j_level_E_10_WHERE_name_E_Calvin_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "UPDATE_user_SET_address_E_calvin_address_j_level_E_10_WHERE_name_E_Calvin_ ok , rows["+nret+"] effected" );
			}

			userForSetInput = new UserSAO() ;
			userForSetInput.level = 8 ;
			userForWhereInput = new UserSAO() ;
			userForWhereInput.age = 19 ;
			userForWhereInput.gender = "F" ;
			nret = UserSAO.UPDATE_user_SET_level_E_WHERE_age_GT_AND_gender_E_( conn, userForSetInput, userForWhereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_SET_level_E_WHERE_age_GT_AND_gender_E_ failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "UPDATE_user_SET_level_E_WHERE_age_GT_AND_gender_E_ ok , rows["+nret+"] effected" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SELECT_ALL_FROM_user_WHERE_name_E_( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_WHERE_name_E_ ok , ["+nret+"]records" );
			}

			if( userList.size() < 1 ) {
				System.out.println("*** ERROR : Record not found about 'Calvin'");
				return -1;
			}

			userOrderForSetInput = new UserOrderSAO() ;
			userOrderForSetInput.totalPrice = 10000.00 ;
			userOrderForWhereInput = new UserOrderSAO() ;
			userOrderForWhereInput.userId = userList.get(0).id ;
			nret = UserOrderSAO.UPDATE_user_order_SET_total_price_E_WHERE_user_id_E_( conn, userOrderForSetInput, userOrderForWhereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_order_SET_total_price_E_WHERE_user_id_E_ failed["+nret+"]" );
				return -23;
			} else {
				System.out.println( "\t" + "UPDATE_user_order_SET_total_price_E_WHERE_user_id_E_ ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int testDeleteTable1( Connection conn ) {

		UserSAO			user ;
		int				nret = 0 ;

		try {
			nret = UserSAO.DELETE_FROM_user_WHERE_name_E_Calvin_( conn, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_WHERE_name_E_Calvin_ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_WHERE_name_E_Calvin_ ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.age = 3 ;
			user.gender = "M" ;
			nret = UserSAO.DELETE_FROM_user_WHERE_age_NE_AND_gender_NE_( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_WHERE_age_NE_AND_gender_NE_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_WHERE_age_NE_AND_gender_NE_ ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int testDeleteTable2( Connection conn ) {

		int						nret = 0 ;

		try {
			nret = UserSAO.DELETE_FROM_user( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user ok , rows["+nret+"] effected" );
			}

			nret = UserOrderSAO.DELETE_FROM_user_order( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_order failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_order ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static void main(String[] args) {
		Connection				conn = null ;

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

		// test all cases
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println( "TestDeleteTable2 ..." );
		nret = testDeleteTable2( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable2 failed" );
			return;
		} else {
			System.out.println( "TestDeleteTable2 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = testSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestInsertTable ..." );
		nret = testInsertTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestInsertTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestInsertTable ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = testSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestUpdateTable ..." );
		nret = testUpdateTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestUpdateTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestUpdateTable ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = testSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestDeleteTable ..." );
		nret = testDeleteTable1( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable1 failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestDeleteTable1 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = testSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestDeleteTable2 ..." );
		nret = testDeleteTable2( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable2 failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestDeleteTable2 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = testSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}
		
		return;
	}
}
