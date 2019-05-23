/*
 * sqlaction - SQL action object auto-gencode tool based JDBC for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package xyz.calvinwilliams.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import xyz.calvinwilliams.okjson.OKJSON;

//INSERT INTO user_order VALUES(1,1,"item_name1",100,10000);
//INSERT INTO user_order VALUES(2,1,"item_name2",100,10000);
//INSERT INTO user_order VALUES(3,1,"item_name3",100,10000);
//INSERT INTO user_order VALUES(4,1,"item_name4",100,10000);
//INSERT INTO user_order VALUES(5,1,"item_name5",100,10000);
//INSERT INTO user_order VALUES(6,1,"item_name6",100,10000);
//INSERT INTO user_order VALUES(7,1,"item_name7",100,10000);
//INSERT INTO user_order VALUES(8,1,"item_name8",100,10000);
//INSERT INTO user_order VALUES(9,1,"item_name9",100,10000);
//INSERT INTO user_order VALUES(10,1,"item_name10",100,10000);

public class SqlActionTest {

	public static int testSelectTable( Connection conn ) {

		List<UserBaseSAU>	userList = null ;
		List<UserBaseSAU>	userListForSelectOutput = null ;
		List<UserOrderSAU>	userOrderListForSelectOutput = null ;
		int					nret = 0 ;
		
		try {
			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_ALL_FROM_user_base( conn, userList ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base ok , ["+userList.size()+"]records" );
			}

			for( UserBaseSAU u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}

			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_ALL_FROM_user_base_WHERE_name_E_( conn, userList, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ ok , ["+userList.size()+"]records" );
			}

			for( UserBaseSAU u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}

			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_name_j_address_FROM_user_base_WHERE_age_LE_AND_gender_E_( conn, userList, (short)3, "M" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_name_j_address_FROM_user_base_WHERE_age_LE_AND_gender_E_ failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "SELECT_name_j_address_FROM_user_base_WHERE_age_LE_AND_gender_E_ ok , ["+userList.size()+"]records" );
			}

			for( UserBaseSAU u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}

			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_ALL_FROM_user_base_ORDER_BY_name_DESC( conn, userList ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_ORDER_BY_name_DESC failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_ORDER_BY_name_DESC ok , ["+userList.size()+"]records" );
			}
			
			for( UserBaseSAU u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"] _count_["+u._count_+"]" );
			}
			
			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_gender_j_count_ALL_FROM_user_base_GROUP_BY_gender( conn, userList ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_gender_j_count_ALL_FROM_user_base_GROUP_BY_gender failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_gender_j_count_ALL_FROM_user_base_GROUP_BY_gender ok , ["+userList.size()+"]records" );
			}
			
			for( UserBaseSAU u : userList ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"] _count_["+u._count_+"]" );
			}
			
			userListForSelectOutput = new LinkedList<UserBaseSAU>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
			nret = UserOrderSAU.queryUserAndOrderByName( conn, userListForSelectOutput, userOrderListForSelectOutput, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "queryUserAndOrderByName failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "queryUserAndOrderByName ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserBaseSAU u : userListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}
			for( UserOrderSAU o : userOrderListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}
			
			userListForSelectOutput = new LinkedList<UserBaseSAU>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
			nret = UserOrderSAU.SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_base_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id( conn, userListForSelectOutput, userOrderListForSelectOutput, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_base_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_base_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserBaseSAU u : userListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}
			for( UserOrderSAU o : userOrderListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}
			
			userListForSelectOutput = new LinkedList<UserBaseSAU>() ;
			userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
			nret = UserOrderSAU.statUsersAmountAndTotalPrice( conn, userListForSelectOutput, userOrderListForSelectOutput, 0 ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "statUsersAmountAndTotalPrice failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "statUsersAmountAndTotalPrice ok , ["+userListForSelectOutput.size()+"]records" );
			}

			for( UserBaseSAU u : userListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+u.id+"] name["+u.name+"] gender["+u.gender+"] age["+u.age+"] address["+u.address+"] lvl["+u.lvl+"]" );
			}
			for( UserOrderSAU o : userOrderListForSelectOutput ) {
				System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
			}
			
			for( int pageNum=1 ; ; pageNum++ ) {
				userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
				nret = UserOrderSAU.SELECT_ALL_FROM_user_order_PAGEKEY_id( conn, userOrderListForSelectOutput, 3, pageNum ) ;
				if( nret < 0 ) {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_PAGEKEY_id failed["+nret+"]" );
					return -23;
				} else {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_PAGEKEY_id ok , ["+userOrderListForSelectOutput.size()+"]records" );
					if( userOrderListForSelectOutput.size() == 0 )
						break;
					for( UserOrderSAU o : userOrderListForSelectOutput ) {
						System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
					}
				}
			}
			
			for( int pageNum=1 ; ; pageNum++ ) {
				userOrderListForSelectOutput = new LinkedList<UserOrderSAU>() ;
				nret = UserOrderSAU.SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id( conn, userOrderListForSelectOutput, 3, pageNum ) ;
				if( nret < 0 ) {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id failed["+nret+"]" );
					return -24;
				} else {
					System.out.println( "\t" + "SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id ok , ["+userOrderListForSelectOutput.size()+"]records" );
					if( userOrderListForSelectOutput.size() == 0 )
						break;
					for( UserOrderSAU o : userOrderListForSelectOutput ) {
						System.out.println( "\t\t" + "id["+o.id+"] userId["+o.userId+"] itemName["+o.itemName+"] amount["+o.amount+"] totalPrice["+o.totalPrice+"]" );
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}

	public static int testInsertTable( Connection conn ) {

		UserBaseSAU			user = null ;
		List<UserBaseSAU>	userList = null ;
		UserOrderSAU	userOrder = null ;
		int				nret = 0 ;

		try {
			user = new UserBaseSAU() ;
			user.name = "Calvin" ;
			user.gender = "M" ;
			user.age = 30 ;
			user.address = "Calvin address" ;
			user.lvl = 8 ;
			nret = UserBaseSAU.INSERT_INTO_user_base( conn, user ) ;
			if( nret <= 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user_base failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user_base ok , rows["+nret+"] effected , id["+user.id+"]" );
			}

			user = new UserBaseSAU() ;
			user.name = "Dozimoyi" ;
			user.gender = "F" ;
			user.age = 20 ;
			user.address = "Dozimoyi address" ;
			user.lvl = 7 ;
			nret = UserBaseSAU.INSERT_INTO_user_base( conn, user ) ;
			if( nret <= 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user_base failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user_base ok , rows["+nret+"] effected , id["+user.id+"]" );
			}

			user = new UserBaseSAU() ;
			user.name = "Mark" ;
			user.gender = "M" ;
			user.age = 3 ;
			user.address = "Mark address" ;
			user.lvl = 2 ;
			nret = UserBaseSAU.INSERT_INTO_user_base( conn, user ) ;
			if( nret <= 0 ) {
				System.out.println( "\t" + "INSERT_INTO_user_base failed["+nret+"]" );
				return -13;
			} else {
				System.out.println( "\t" + "INSERT_INTO_user_base ok , rows["+nret+"] effected , id["+user.id+"]" );
			}

			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_ALL_FROM_user_base_WHERE_name_E_( conn, userList, "Calvin" ) ;
			if( nret <= 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ failed["+nret+"]" );
				return -14;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ ok , count["+nret+"]" );
			}

			if( userList.size() < 1 ) {
				System.out.println("*** ERROR : Record not found about 'Calvin'");
				return -1;
			}

			for( int i=0 ; i<10 ; i++ ) {
				userOrder = new UserOrderSAU() ;
				userOrder.userId = userList.get(0).id ;
				userOrder.itemName = "我的商品"+i ;
				userOrder.amount = 100+i ;
				userOrder.totalPrice = 1000.00+i*10 ;
				nret = UserOrderSAU.INSERT_INTO_user_order( conn, userOrder ) ;
				if( nret <= 0 ) {
					System.out.println( "\t" + "INSERT_INTO_user_order failed["+nret+"]" );
					return -21;
				} else {
					System.out.println( "\t" + "INSERT_INTO_user_order ok , rows["+nret+"] effected , id["+userOrder.id+"]" );
				}
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

		List<UserBaseSAU>	userList = null ;
		int				nret = 0 ;

		try {
			nret = UserBaseSAU.UPDATE_user_base_SET_lvl_E_( conn, 6 ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_base_SET_lvl_E_ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "UPDATE_user_base_SET_lvl_E_ ok , rows["+nret+"] effected" );
			}

			nret = UserBaseSAU.UPDATE_user_base_SET_address_E_calvin_address_j_lvl_E_10_WHERE_name_E_Calvin_( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_base_SET_address_E_calvin_address_j_lvl_E_10_WHERE_name_E_Calvin_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "UPDATE_user_base_SET_address_E_calvin_address_j_lvl_E_10_WHERE_name_E_Calvin_ ok , rows["+nret+"] effected" );
			}

			nret = UserBaseSAU.UPDATE_user_base_SET_lvl_E_WHERE_age_GT_AND_gender_E_( conn, 8, (short)19, "F" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "UPDATE_user_base_SET_lvl_E_WHERE_age_GT_AND_gender_E_ failed["+nret+"]" );
				return -21;
			} else {
				System.out.println( "\t" + "UPDATE_user_base_SET_lvl_E_WHERE_age_GT_AND_gender_E_ ok , rows["+nret+"] effected" );
			}

			userList = new LinkedList<UserBaseSAU>() ;
			nret = UserBaseSAU.SELECT_ALL_FROM_user_base_WHERE_name_E_( conn, userList, "Calvin" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ failed["+nret+"]" );
				return -22;
			} else {
				System.out.println( "\t" + "SELECT_ALL_FROM_user_base_WHERE_name_E_ ok , ["+nret+"]records" );
			}

			if( userList.size() < 1 ) {
				System.out.println("*** ERROR : Record not found about 'Calvin'");
				return -1;
			}

			nret = UserOrderSAU.UPDATE_user_order_SET_total_price_E_WHERE_user_id_E_( conn, 10000.00, userList.get(0).id ) ;
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

		int				nret = 0 ;

		try {
			nret = UserBaseSAU.DELETE_FROM_user_base_WHERE_name_E_Calvin_( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_base_WHERE_name_E_Calvin_ failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_base_WHERE_name_E_Calvin_ ok , rows["+nret+"] effected" );
			}

			nret = UserBaseSAU.DELETE_FROM_user_base_WHERE_age_NE_AND_gender_NE_( conn, (short)3, "M" ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_base_WHERE_age_NE_AND_gender_NE_ failed["+nret+"]" );
				return -12;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_base_WHERE_age_NE_AND_gender_NE_ ok , rows["+nret+"] effected" );
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
			nret = UserBaseSAU.DELETE_FROM_user_base( conn ) ;
			if( nret < 0 ) {
				System.out.println( "\t" + "DELETE_FROM_user_base failed["+nret+"]" );
				return -11;
			} else {
				System.out.println( "\t" + "DELETE_FROM_user_base ok , rows["+nret+"] effected" );
			}

			nret = UserOrderSAU.DELETE_FROM_user_order( conn ) ;
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
		SqlActionTestConf		dbserverConf ;
		Connection				conn = null ;

		int						nret = 0 ;

		// connect to database
		dbserverConf = OKJSON.fileToObject( "../../../dbserver.conf.json", SqlActionTestConf.class, OKJSON.OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE ) ;
		if( dbserverConf == null ) {
			System.out.println("dbserver.conf.json not found or content invalid");
			return;
		}
		
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

