// This file generated by sqlaction v0.2.4.0
// WARN : DON'T MODIFY THIS FILE

package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserBaseSAO {

	int				id ;
	String			name ;
	String			gender ;
	int				age ;
	String			address ;
	int				level ;

	int				_count_ ; // defining for 'SELECT COUNT(*)'

	// SELECT * FROM user_base
	public static int SELECT_ALL_FROM_user_base( Connection conn, List<UserBaseSAO> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM user_base" ) ;
		while( rs.next() ) {
			UserBaseSAO userBase = new UserBaseSAO() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getInt( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.level = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT * FROM user_base WHERE name=?
	public static int SELECT_ALL_FROM_user_base_WHERE_name_E_( Connection conn, List<UserBaseSAO> userBaseListForSelectOutput, String _1_name ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT * FROM user_base WHERE name=?" ) ;
		prestmt.setString( 1, _1_name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAO userBase = new UserBaseSAO() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getInt( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.level = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT name,address FROM user_base WHERE age<=? AND gender=?
	public static int SELECT_name_j_address_FROM_user_base_WHERE_age_LE_AND_gender_E_( Connection conn, List<UserBaseSAO> userBaseListForSelectOutput, int _1_age, String _2_gender ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "SELECT name,address FROM user_base WHERE age<=? AND gender=?" ) ;
		prestmt.setInt( 1, _1_age );
		prestmt.setString( 2, _2_gender );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserBaseSAO userBase = new UserBaseSAO() ;
			userBase.name = rs.getString( 1 ) ;
			userBase.address = rs.getString( 2 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		prestmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT * FROM user_base ORDER BY name DESC
	public static int SELECT_ALL_FROM_user_base_ORDER_BY_name_DESC( Connection conn, List<UserBaseSAO> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM user_base ORDER BY name DESC" ) ;
		while( rs.next() ) {
			UserBaseSAO userBase = new UserBaseSAO() ;
			userBase.id = rs.getInt( 1 ) ;
			userBase.name = rs.getString( 2 ) ;
			userBase.gender = rs.getString( 3 ) ;
			userBase.age = rs.getInt( 4 ) ;
			userBase.address = rs.getString( 5 ) ;
			userBase.level = rs.getInt( 6 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// SELECT gender,count(*) FROM user_base GROUP BY gender
	public static int SELECT_gender_j_count_ALL_FROM_user_base_GROUP_BY_gender( Connection conn, List<UserBaseSAO> userBaseListForSelectOutput ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery( "SELECT gender,count(*) FROM user_base GROUP BY gender" ) ;
		while( rs.next() ) {
			UserBaseSAO userBase = new UserBaseSAO() ;
			userBase.gender = rs.getString( 1 ) ;
			userBase._count_ = rs.getInt( 2 ) ;
			userBaseListForSelectOutput.add(userBase) ;
		}
		rs.close();
		stmt.close();
		return userBaseListForSelectOutput.size();
	}

	// INSERT INTO user_base @@SELECTSEQ(user_base_seq_id) @@SELECTKEY(id)
	public static int INSERT_INTO_user_base( Connection conn, UserBaseSAO userBase ) throws Exception {
		PreparedStatement prestmt ;
		Statement stmt ;
		ResultSet rs ;
		stmt = conn.createStatement() ;
		rs = stmt.executeQuery( "SELECT NEXTVAL('user_base_seq_id')" ) ;
		rs.next();
		userBase.id = rs.getInt( 1 ) ;
		rs.close();
		stmt.close();
		
		prestmt = conn.prepareStatement( "INSERT INTO user_base (id,name,gender,age,address,level) VALUES (?,?,?,?,?,?)" ) ;
		prestmt.setInt( 1, userBase.id );
		prestmt.setString( 2, userBase.name );
		prestmt.setString( 3, userBase.gender );
		prestmt.setInt( 4, userBase.age );
		prestmt.setString( 5, userBase.address );
		prestmt.setInt( 6, userBase.level );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE user_base SET level=?
	public static int UPDATE_user_base_SET_level_E_( Connection conn, int _1_level_ForSetInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET level=?" ) ;
		prestmt.setInt( 1, _1_level_ForSetInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE user_base SET address='calvin address',level=10 WHERE name='Calvin'
	public static int UPDATE_user_base_SET_address_E_calvin_address_j_level_E_10_WHERE_name_E_Calvin_( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET address='calvin address',level=10 WHERE name='Calvin'" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// UPDATE user_base SET level=? WHERE age>? AND gender=?
	public static int UPDATE_user_base_SET_level_E_WHERE_age_GT_AND_gender_E_( Connection conn, int _1_level_ForSetInput, int _1_age_ForWhereInput, String _2_gender_ForWhereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "UPDATE user_base SET level=? WHERE age>? AND gender=?" ) ;
		prestmt.setInt( 1, _1_level_ForSetInput );
		prestmt.setInt( 2, _1_age_ForWhereInput );
		prestmt.setString( 3, _2_gender_ForWhereInput );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base
	public static int DELETE_FROM_user_base( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base WHERE name='Calvin'
	public static int DELETE_FROM_user_base_WHERE_name_E_Calvin_( Connection conn ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base WHERE name='Calvin'" ) ;
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

	// DELETE FROM user_base WHERE age<>? AND gender<>?
	public static int DELETE_FROM_user_base_WHERE_age_NE_AND_gender_NE_( Connection conn, int _1_age, String _2_gender ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement( "DELETE FROM user_base WHERE age<>? AND gender<>?" ) ;
		prestmt.setInt( 1, _1_age );
		prestmt.setString( 2, _2_gender );
		int count = prestmt.executeUpdate() ;
		prestmt.close();
		return count;
	}

}
