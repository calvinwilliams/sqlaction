// This file generated by sqlaction v0.2.7.0

package xyz.calvinwilliams.test;

import java.math.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserOrderSAU extends UserOrderSAO {
	
	// SELECT /* blablabla~ */ * FROM user_order @@STATEMENT_INTERCEPTOR()
	public static String STATEMENT_INTERCEPTOR_for_SELECT_HT_blablabla_TH_ALL_FROM_user_order( String statementSql ) {
		
		return statementSql;
	}
	
	// SELECT u.name,u.address,o.item_name,o.amount,o.total_price FROM user_base u,user_order o WHERE u.name=? AND u.id=o.user_id @@STATEMENT_INTERCEPTOR(statementInterceptorForQueryUserAndOrderByName)
	public static String statementInterceptorForQueryUserAndOrderByName( String statementSql ) {
		
		return statementSql;
	}

}