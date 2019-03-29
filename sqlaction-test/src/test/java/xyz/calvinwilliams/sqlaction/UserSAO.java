package xyz.calvinwilliams.sqlaction;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserSAO {

	int				id ; // 编号
	String			name ; // 名字
	String			gender ; // 性别
	short			age ; // 年龄
	String			address ; // 地址
	int				level ; // 级别

	// SELECT * FROM user
	public static int SqlAction_SELECT_ALL_FROM_user( Connection conn, List<UserSAO> selectOutputList ) throws Exception {
		Statement stmt = conn.createStatement() ;
		ResultSet rs = stmt.executeQuery("SELECT * FROM user") ;
		while( rs.next() ) {
			UserSAO selectOutput = new UserSAO() ;
			selectOutput.id = rs.getInt( 1 ) ;
			selectOutput.name = rs.getString( 2 ) ;
			selectOutput.gender = rs.getString( 3 ) ;
			selectOutput.age = rs.getShort( 4 ) ;
			selectOutput.address = rs.getString( 5 ) ;
			selectOutput.level = rs.getInt( 6 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// SELECT * FROM user WHERE name=?
	public static int SqlAction_SELECT_ALL_FROM_user_WHERE_name_E( Connection conn, List<UserSAO> selectOutputList, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT * FROM user WHERE name=?") ;
		prestmt.setString( 1, whereInput.name );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserSAO selectOutput = new UserSAO() ;
			selectOutput.id = rs.getInt( 1 ) ;
			selectOutput.name = rs.getString( 2 ) ;
			selectOutput.gender = rs.getString( 3 ) ;
			selectOutput.age = rs.getShort( 4 ) ;
			selectOutput.address = rs.getString( 5 ) ;
			selectOutput.level = rs.getInt( 6 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// SELECT name,address FROM user WHERE age<? AND gender=?
	public static int SqlAction_SELECT_name_j_address_FROM_user_WHERE_age_LT_AND_gender_E( Connection conn, List<UserSAO> selectOutputList, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("SELECT name,address FROM user WHERE age=? AND gender=?") ;
		prestmt.setShort( 1, whereInput.age );
		prestmt.setString( 2, whereInput.gender );
		ResultSet rs = prestmt.executeQuery() ;
		while( rs.next() ) {
			UserSAO selectOutput = new UserSAO() ;
			selectOutput.name = rs.getString( 1 ) ;
			selectOutput.address = rs.getString( 2 ) ;
			selectOutputList.add(selectOutput);
		}
		return selectOutputList.size();
	}

	// INSERT INTO user
	public static int SqlAction_INSERT_INTO_user( Connection conn, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("INSERT INTO user (,name,gender,age,address,level) VALUES (,?,?,?,?,?)") ;
		prestmt.setString( 1, whereInput.name );
		prestmt.setString( 2, whereInput.gender );
		prestmt.setShort( 3, whereInput.age );
		prestmt.setString( 4, whereInput.address );
		prestmt.setInt( 5, whereInput.level );
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET level=?
	public static int SqlAction_UPDATE_user_SET_level_E( Connection conn, UserSAO setInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET level=?") ;
		prestmt.setInt( 1, setInput.level );
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET name='vincal',level=3 WHERE name='calvin'
	public static int SqlAction_UPDATE_user_SET_name_E__vincal__j_level_E_3_WHERE_name_E__calvin_( Connection conn, UserSAO setInput, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET name=?,level=? WHERE name=?") ;
		prestmt.setString( 1, "vincal" );
		prestmt.setInt( 2, 3 );
		prestmt.setString( 3, "calvin" );
		return prestmt.executeUpdate() ;
	}

	// UPDATE user SET level=? WHERE age>=? AND gender=?
	public static int SqlAction_UPDATE_user_SET_level_E_WHERE_age_GE_AND_gender_E( Connection conn, UserSAO setInput, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("UPDATE user SET level=? WHERE age=? AND gender=?") ;
		prestmt.setInt( 1, setInput.level );
		prestmt.setShort( 2, whereInput.age );
		prestmt.setString( 3, whereInput.gender );
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user
	public static int SqlAction_DELETE_FROM_user( Connection conn, UserSAO setInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user") ;
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user WHERE name<>'calvin'
	public static int SqlAction_DELETE_FROM_user_WHERE_name_NE__calvin_( Connection conn, UserSAO setInput, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user WHERE name=?") ;
		prestmt.setString( 1, "calvin" );
		return prestmt.executeUpdate() ;
	}

	// DELETE FROM user WHERE age<>? AND gender<>?
	public static int SqlAction_DELETE_FROM_user_WHERE_age_NE_AND_gender_NE( Connection conn, UserSAO setInput, UserSAO whereInput ) throws Exception {
		PreparedStatement prestmt = conn.prepareStatement("DELETE FROM user WHERE age=? AND gender=?") ;
		prestmt.setShort( 1, whereInput.age );
		prestmt.setString( 2, whereInput.gender );
		return prestmt.executeUpdate() ;
	}
}
