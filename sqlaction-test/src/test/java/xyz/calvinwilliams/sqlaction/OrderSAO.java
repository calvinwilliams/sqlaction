package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderSAO {

	int				id ; // 编号
	int				userId ; // 用户编号
	String			itemName ; // 商品名称
	int				amount ; // 数量
	BigDecimal		totalPrice ;

	// SELECT * FROM order WHERE user_id=?
	public static int SqlAction_SELECT_ALL_FROM_order_WHERE_user_id_E( Connection conn, List<OrderSAO> selectOutputList, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM order WHERE user_id=?") ;
		prestmt.setInt( 1, whereInput.userId );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			OrderSAO selectOutput = new OrderSAO() ;
			selectOutput.id = rs.getInt( 1 ) ;
			selectOutput.userId = rs.getInt( 2 ) ;
			selectOutput.itemName = rs.getString( 3 ) ;
			selectOutput.amount = rs.getInt( 4 ) ;
			selectOutput.totalPrice = rs.getBigDecimal( 5 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// SELECT user.name,user.address,order.item_name,order.amount,order.total_price FROM user,order WHERE user.id=? AND user.id=order.user_id
	public static int SqlAction_SELECT_name_j_address_j_item_name_j_amount_j_total_price_FROM_order_WHERE_id_E_AND_id_E_order( Connection conn, List<OrderSAO> selectOutputList, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name,address,item_name,amount,total_price FROM order WHERE id=? AND id=?") ;
		prestmt.setInt( 1, whereInput.id );
		prestmt.setInt( 2, order );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			OrderSAO selectOutput = new OrderSAO() ;
			selectOutput.name = rs.getString( 1 ) ;
			selectOutput.address = rs.getString( 2 ) ;
			selectOutput.itemName = rs.getString( 3 ) ;
			selectOutput.amount = rs.getInt( 4 ) ;
			selectOutput.totalPrice = rs.getBigDecimal( 5 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// SELECT u.name,u.address,o.item_name,o.amount,o.total_price FROM user u,order o WHERE u.id=? AND u.id=o.user_id
	public static int SqlAction_SELECT_name_j_address_j_item_name_j_amount_j_total_price_FROM_order_WHERE_id_E_AND_id_E_o( Connection conn, List<OrderSAO> selectOutputList, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name,address,item_name,amount,total_price FROM order WHERE id=? AND id=?") ;
		prestmt.setInt( 1, whereInput.id );
		prestmt.setInt( 2, o );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			OrderSAO selectOutput = new OrderSAO() ;
			selectOutput.name = rs.getString( 1 ) ;
			selectOutput.address = rs.getString( 2 ) ;
			selectOutput.itemName = rs.getString( 3 ) ;
			selectOutput.amount = rs.getInt( 4 ) ;
			selectOutput.totalPrice = rs.getBigDecimal( 5 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// INSERT INTO order
	public static int SqlAction_INSERT_INTO_order( Connection conn, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("INSERT INTO order (,user_id,item_name,amount,total_price) VALUES (,?,?,?,?)") ;
		prestmt.setInt( 1, whereInput.userId );
		prestmt.setString( 2, whereInput.itemName );
		prestmt.setInt( 3, whereInput.amount );
		prestmt.setBigDecimal( 4, whereInput.totalPrice );
		return prestmt.executeUpdate() ;
	}

	// UPDATE order SET total_price=? WHERE id=?
	public static int SqlAction_UPDATE_order_SET_total_price_E_WHERE_id_E( Connection conn, OrderSAO setInput, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE order SET total_price=? WHERE id=?") ;
		prestmt.setBigDecimal( 1, setInput.totalPrice );
		prestmt.setInt( 2, whereInput.id );
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM order WHERE id=?
	public static int SqlAction_DELETE_FROM_order_WHERE_id_E( Connection conn, OrderSAO setInput, OrderSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM order WHERE id=?") ;
		prestmt.setInt( 1, whereInput.id );
		return prestmt.executeUpdate() ;
	}
}
