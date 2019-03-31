package xyz.calvinwilliams.sqlaction;

import java.util.*;

public class DbServerConf {
	public String							dbms ;
	public String							driver ;
	public String							url ;
	public String							user ;
	public String							pwd ;
	public LinkedList<UserDefineDataTypes>	userDefineDataTypes ;
}

class UserDefineDataTypes {
	public String		source ;
	public String		redefine ;
}
