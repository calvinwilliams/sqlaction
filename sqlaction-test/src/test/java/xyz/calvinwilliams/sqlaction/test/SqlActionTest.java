package xyz.calvinwilliams.sqlaction.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class SqlActionTest {

	public static int TestSelectTable( Connection conn ) {

		List<UserSAO>		userList = null ;
		UserSAO				user = null ;
		List<UserSAO>		userListForSelectOutput = null ;
		List<UserOrderSAO>	userOrderListForSelectOutput = null ;
		UserSAO				userForWhereInput = null ;
		int					nret = 0 ;

		try {
			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SqlAction_SELECT_ALL_FROM_user( conn, userList, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.age = 3 ;
			user.gender = "M" ;
			nret = UserSAO.SqlAction_SELECT_name_J_address_FROM_user_WHERE_age_LE___AND_gender_E__( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_name_J_address_FROM_user_WHERE_age_LE___AND_gender_E__ failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_name_J_address_FROM_user_WHERE_age_LE___AND_gender_E__ ok , ["+userList.size()+"]records" );
			}

			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}

			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SqlAction_SELECT_ALL_FROM_user_ORDER_BY_name_DESC( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_ORDER_BY_name_DESC failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_ORDER_BY_name_DESC ok , ["+userList.size()+"]records" );
			}
			
			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"] count___["+u.count___+"]" );
			}
			
			userList = new LinkedList<UserSAO>() ;
			nret = UserSAO.SqlAction_SELECT_gender_J_count____FROM_user_GROUP_BY_gender( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_gender_J_count____FROM_user_GROUP_BY_gender failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_gender_J_count____FROM_user_GROUP_BY_gender ok , ["+userList.size()+"]records" );
			}
			
			for( UserSAO u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"] count___["+u.count___+"]" );
			}
			
			userListForSelectOutput = new LinkedList<UserSAO>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAO>() ;
			userForWhereInput = new UserSAO() ;
			userForWhereInput.name = "Calvin" ;
			nret = UserOrderSAO.SqlAction_SELECT_user_O_name_J_user_O_address_J_user_order_O_item_name_J_user_order_O_amount_J_user_order_O_total_price_FROM_user_J_user_order_WHERE_user_O_name_E___AND_user_O_id_E_user_order_O_user_id( conn, userListForSelectOutput, userOrderListForSelectOutput, userForWhereInput, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_user_O_name_J_user_O_address_J_user_order_O_item_name_J_user_order_O_amount_J_user_order_O_total_price_FROM_user_J_user_order_WHERE_user_O_name_E___AND_user_O_id_E_user_order_O_user_id failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_user_O_name_J_user_O_address_J_user_order_O_item_name_J_user_order_O_amount_J_user_order_O_total_price_FROM_user_J_user_order_WHERE_user_O_name_E___AND_user_O_id_E_user_order_O_user_id ok , ["+userListForSelectOutput.size()+"]records" );
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
			nret = UserOrderSAO.SqlAction_SELECT_u_O_name_J_u_O_address_J_o_O_item_name_J_o_O_amount_J_o_O_total_price_FROM_user_u_J_user_order_o_WHERE_u_O_name_E___AND_u_O_id_E_o_O_user_id( conn, userListForSelectOutput, userOrderListForSelectOutput, userForWhereInput, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_u_O_name_J_u_O_address_J_o_O_item_name_J_o_O_amount_J_o_O_total_price_FROM_user_u_J_user_order_o_WHERE_u_O_name_E___AND_u_O_id_E_o_O_user_id failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_u_O_name_J_u_O_address_J_o_O_item_name_J_o_O_amount_J_o_O_total_price_FROM_user_u_J_user_order_o_WHERE_u_O_name_E___AND_u_O_id_E_o_O_user_id ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserSAO u : userListForSelectOutput ) {
				System.out.print( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] level["+u.level+"]" );
			}
			for( UserOrderSAO o : userOrderListForSelectOutput ) {
				System.out.println( " | " + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}
		} catch (Exception e) {
			return -1;
		}

		return 0;
	}

	public static int TestInsertTable( Connection conn ) {

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
			nret = UserSAO.SqlAction_INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.name = "Dozimoyi" ;
			user.gender = "F" ;
			user.age = 20 ;
			user.address = "Dozimoyi address" ;
			user.level = 7 ;
			nret = UserSAO.SqlAction_INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.name = "Mark" ;
			user.gender = "M" ;
			user.age = 3 ;
			user.address = "Mark address" ;
			user.level = 2 ;
			nret = UserSAO.SqlAction_INSERT_INTO_user( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user ok , rows["+nret+"] effected" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ ok , count["+nret+"]" );
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
			nret = UserOrderSAO.SqlAction_INSERT_INTO_user_order( conn, userOrder ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user_order failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "SqlAction_INSERT_INTO_user_order ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int TestUpdateTable( Connection conn ) {

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
			nret = UserSAO.SqlAction_UPDATE_user_SET_level_E__( conn, userForSetInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_level_E__ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_level_E__ ok , rows["+nret+"] effected" );
			}

			nret = UserSAO.SqlAction_UPDATE_user_SET_address_E__calvin_address__j_level_E_10_WHERE_name_E__Calvin_( conn, null, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_address_E__calvin_address__j_level_E_10_WHERE_name_E__Calvin_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_address_E__calvin_address__j_level_E_10_WHERE_name_E__Calvin_ ok , rows["+nret+"] effected" );
			}

			userForSetInput = new UserSAO() ;
			userForSetInput.level = 8 ;
			userForWhereInput = new UserSAO() ;
			userForWhereInput.age = 19 ;
			userForWhereInput.gender = "F" ;
			nret = UserSAO.SqlAction_UPDATE_user_SET_level_E___WHERE_age_GT___AND__gender_E__( conn, userForSetInput, userForWhereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_level_E___WHERE_age_GT___AND__gender_E__ failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_user_SET_level_E___WHERE_age_GT___AND__gender_E__ ok , rows["+nret+"] effected" );
			}

			userList = new LinkedList<UserSAO>() ;
			user = new UserSAO() ;
			user.name = "Calvin" ;
			nret = UserSAO.SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__( conn, userList, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SqlAction_SELECT_ALL_FROM_user_WHERE_name_E__ ok , ["+nret+"]records" );
			}

			if( userList.size() < 1 ) {
				System.out.println("*** ERROR : Record not found about 'Calvin'");
				return -1;
			}

			userOrderForSetInput = new UserOrderSAO() ;
			userOrderForSetInput.totalPrice = 10000.00 ;
			userOrderForWhereInput = new UserOrderSAO() ;
			userOrderForWhereInput.userId = userList.get(0).id ;
			nret = UserOrderSAO.SqlAction_UPDATE_user_order_SET_total_price_E___WHERE_user_id_E__( conn, userOrderForSetInput, userOrderForWhereInput ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_UPDATE_user_order_SET_total_price_E___WHERE_user_id_E__ failed["+nret+"]" );
				return -23;
			} else {
				System.out.println( "\t" + "SqlAction_UPDATE_user_order_SET_total_price_E___WHERE_user_id_E__ ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int TestDeleteTable1( Connection conn ) {

		UserSAO			user ;
		int				nret = 0 ;

		try {
			nret = UserSAO.SqlAction_DELETE_FROM_user_WHERE_name_E__Calvin_( conn, null ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_WHERE_name_E__Calvin_ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_WHERE_name_E__Calvin_ ok , rows["+nret+"] effected" );
			}

			user = new UserSAO() ;
			user.age = 3 ;
			user.gender = "M" ;
			nret = UserSAO.SqlAction_DELETE_FROM_user_WHERE_age_NE___AND__gender_NE__( conn, user ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_WHERE_age_NE___AND__gender_NE__ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_WHERE_age_NE___AND__gender_NE__ ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				return -2;
			}
			return -1;
		}

		return 0;
	}

	public static int TestDeleteTable2( Connection conn ) {

		int						nret = 0 ;

		try {
			nret = UserSAO.SqlAction_DELETE_FROM_user( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user ok , rows["+nret+"] effected" );
			}

			nret = UserOrderSAO.SqlAction_DELETE_FROM_user_order( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_order failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "SqlAction_DELETE_FROM_user_order ok , rows["+nret+"] effected" );
			}

			conn.commit();
		} catch (Exception e) {
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
		nret = TestDeleteTable2( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable2 failed" );
			return;
		} else {
			System.out.println( "TestDeleteTable2 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = TestSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestInsertTable ..." );
		nret = TestInsertTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestInsertTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestInsertTable ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = TestSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestUpdateTable ..." );
		nret = TestUpdateTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestUpdateTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestUpdateTable ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = TestSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestDeleteTable ..." );
		nret = TestDeleteTable1( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable1 failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestDeleteTable1 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = TestSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}

		System.out.println( "TestDeleteTable2 ..." );
		nret = TestDeleteTable2( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestDeleteTable2 failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestDeleteTable2 ok" );
		}

		System.out.println( "TestSelectTable ..." );
		nret = TestSelectTable( conn ) ;
		if( nret != 0 ) {
			System.out.println( "TestSelectTable failed["+nret+"]" );
			return;
		} else {
			System.out.println( "TestSelectTable ok" );
		}
		
		return;
	}
}
