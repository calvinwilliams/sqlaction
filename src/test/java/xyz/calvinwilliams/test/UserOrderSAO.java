// This file generated by sqlaction v0.2.8.0
// WARN : DON'T MODIFY THIS FILE

package xyz.calvinwilliams.test;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserOrderSAO {

	int				id ; // 编号 // PRIMARY KEY
	int				userId ; // 用户编号
	String			itemName ; // 商品名称
	int				amount ; // 数量
	double			totalPrice ;

	int				_count_ ; // defining for 'SELECT COUNT(*)'

	// SELECT /* blablabla~ */ * FROM user_order @@STATEMENT_INTERCEPTOR()
	/*
	public static String STATEMENT_INTERCEPTOR_for_SELECT_HT_blablabla_TH_ALL_FROM_user_order( String statementSql ) {
		
		return statementSql;
	}
	*/
	public static int SELECT_HT_blablabla_TH_ALL_FROM_user_order( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( UserOrderSAU.STATEMENT_INTERCEPTOR_for_SELECT_HT_blablabla_TH_ALL_FROM_user_order("SELECT /* blablabla~ */ * FROM user_order") ) ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		stmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT * FROM user_order WHERE user_id=?
	public static int SELECT_ALL_FROM_user_order_WHERE_user_id_E_( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_UserOrderSAU_userId ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE user_id=?" ) ;
		prestmt.setInt( 1, _1_UserOrderSAU_userId );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT * FROM user_order @@PAGEKEY(id)
	public static int SELECT_ALL_FROM_user_order_PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE id>=(SELECT id FROM user_order ORDER BY id LIMIT ?,1) ORDER BY id LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT * FROM user_order WHERE item_name<>'' @@PAGEKEY(id) @@PAGESORT(DESC)
	public static int SELECT_ALL_FROM_user_order_WHERE_item_name_NE__PAGEKEY_id( Connection conn, List<UserOrderSAU> userOrderListForSelectOutput, int _1_pageSize, int _2_pageNum ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_order WHERE id<=(SELECT id FROM user_order ORDER BY id DESC LIMIT ?,1) AND item_name<>'' ORDER BY id DESC LIMIT ?" ) ;
		prestmt.setInt( 1, _1_pageSize*(_2_pageNum-1) );
		prestmt.setInt( 2, _1_pageSize );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userOrder.id = rs.getInt( 1 ) ;
			userOrder.userId = rs.getInt( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userOrderListForSelectOutput.size();
	}

	// SELECT user_base.name,user_base.address,user_order.item_name,user_order.amount,user_order.total_price
	// 					FROM user_base,user_order
	// 					WHERE user_base.name=? AND user_base.id=user_order.user_id
	// 					@@METHOD(queryUserAndOrderByName)
	public static int queryUserAndOrderByName( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, List<UserOrderSAU> userOrderListForSelectOutput, String _1_UserBaseSAU_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT user_base.name,user_base.address,user_order.item_name,user_order.amount,user_order.total_price FROM user_base,user_order WHERE user_base.name=? AND user_base.id=user_order.user_id" ) ;
		prestmt.setString( 1, _1_UserBaseSAU_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userBase.name = rs.getString( 1 ) ;
			userBase.address = rs.getString( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userBaseListForSelectOutput.add(userBase) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT u.name,u.address,o.item_name,o.amount,o.total_price FROM user_base u,user_order o WHERE u.name=? AND u.id=o.user_id @@STATEMENT_INTERCEPTOR(statementInterceptorForQueryUserAndOrderByName)
	/*
	public static String statementInterceptorForQueryUserAndOrderByName( String statementSql, String _1_UserBaseSAU_name ) {
		
		return statementSql;
	}
	*/
	public static int SELECT_u_O_name_j_u_O_address_j_o_O_item_name_j_o_O_amount_j_o_O_total_price_FROM_user_base_u_j_user_order_o_WHERE_u_O_name_E_AND_u_O_id_E_o_O_user_id( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, List<UserOrderSAU> userOrderListForSelectOutput, String _1_UserBaseSAU_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( UserOrderSAU.statementInterceptorForQueryUserAndOrderByName("SELECT u.name,u.address,o.item_name,o.amount,o.total_price FROM user_base u,user_order o WHERE u.name=? AND u.id=o.user_id", _1_UserBaseSAU_name) ) ;
		prestmt.setString( 1, _1_UserBaseSAU_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userBase.name = rs.getString( 1 ) ;
			userBase.address = rs.getString( 2 ) ;
			userOrder.itemName = rs.getString( 3 ) ;
			userOrder.amount = rs.getInt( 4 ) ;
			userOrder.totalPrice = rs.getDouble( 5 ) ;
			userBaseListForSelectOutput.add(userBase) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT user_base.name				#{UserBaseSAU.name}
	// 					,user_order.item_name			#{UserOrderSAU.itemName}
	// 					,SUM(user_order.amount)			#{UserOrderSAU.amount}
	// 					,SUM(user_order.total_price)	#{UserOrderSAU.totalPrice}
	// 					FROM user_base		#{user_base}
	// 						,user_order		#{user_order}
	// 					WHERE user_order.user_id IN (
	// 												SELECT id
	// 												FROM user_base
	// 												WHERE id>=?		#{UserOrderSAU.id}
	// 											)
	// 						AND user_order.user_id=user_base.id
	// 					GROUP BY user_base.name
	// 					ORDER BY user_base.name
	// 					@@ADVANCEDMODE @@METHOD(statUsersAmountAndTotalPrice)
	public static int statUsersAmountAndTotalPrice( Connection conn, List<UserBaseSAU> userBaseListForSelectOutput, List<UserOrderSAU> userOrderListForSelectOutput, int _1_UserOrderSAU_id ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT user_base.name ,user_order.item_name ,SUM(user_order.amount) ,SUM(user_order.total_price) FROM user_base ,user_order WHERE user_order.user_id IN ( SELECT id FROM user_base WHERE id>=? ) AND user_order.user_id=user_base.id GROUP BY user_base.name ORDER BY user_base.name" ) ;
		prestmt.setInt( 1, _1_UserOrderSAU_id );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAU userBase = new UserBaseSAU() ;
			UserOrderSAU userOrder = new UserOrderSAU() ;
			userBase.name = rs.getString( 1 ) ;
			userOrder.itemName = rs.getString( 2 ) ;
			userOrder.amount = rs.getInt( 3 ) ;
			userOrder.totalPrice = rs.getDouble( 4 ) ;
			userBaseListForSelectOutput.add(userBase) ;
			userOrderListForSelectOutput.add(userOrder) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// INSERT INTO user_order @@SELECTSEQ(user_order_seq_id) @@SELECTKEY(id)
	public static int INSERT_INTO_user_order( Connection conn, UserOrderSAU userOrder ) throws Exception {
		PreparedStatement prestmt ;
		Statement stmt ;
		ResultSet rs ;
		prestmt = conn.prepareStatement( "INSERT INTO user_order (user_id,item_name,amount,total_price) VALUES (?,?,?,?)" ) ;
		prestmt.setInt( 1, userOrder.userId );
		prestmt.setString( 2, userOrder.itemName );
		prestmt.setInt( 3, userOrder.amount );
		prestmt.setDouble( 4, userOrder.totalPrice );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		if( count != 1 )
			return count;
		
		stmt = conn.createStatement() ;
		rs = stmt.executeQuery( "SELECT LAST_INSERT_ID()" ) ;
		rs.next();
		userOrder.id = rs.getInt( 1 ) ;
		rs.close();
		stmt.close();
		
		return count;
	}

	// UPDATE user_order SET total_price=? WHERE user_id=?
	public static int UPDATE_user_order_SET_total_price_E_WHERE_user_id_E_( Connection conn, double _1_totalPrice_ForSetInput, int _1_userId_ForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_order SET total_price=? WHERE user_id=?" ) ;
		prestmt.setDouble( 1, _1_totalPrice_ForSetInput );
		prestmt.setInt( 2, _1_userId_ForWhereInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_order
	public static int DELETE_FROM_user_order( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_order" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_order WHERE user_id=? #{UserOrderSAU.userId} @@ADVANCEDMODE @@METHOD(removeUserOrder)
	public static int removeUserOrder( Connection conn, int _1_userId ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_order WHERE user_id=?" ) ;
		prestmt.setInt( 1, _1_userId );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

}
