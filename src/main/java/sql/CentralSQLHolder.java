package sql;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class CentralSQLHolder {
	private CentralSQLHolder() {}
	private static BasicDataSource bds = null;
	private static Context initCtx = null;
	private static Context envCtx = null;
	static {
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			bds = (BasicDataSource) envCtx.lookup("jdbc/db0");
		} catch (NamingException/* | SQLException*/ e) {
			e.printStackTrace();
		}
	}
	static class ConnectionHolder {
		Connection conn;
		long lastvalidated;
		public ConnectionHolder(Connection conn) {
			this.conn = conn;
			lastvalidated = System.currentTimeMillis();
		}
		public boolean needsValidation() {
			return (System.currentTimeMillis() - lastvalidated > 54000);
		}
		public void validate() {
			lastvalidated = System.currentTimeMillis();
		}
	}
	private static ConcurrentHashMap<Long, ConnectionHolder> connections = new ConcurrentHashMap<>();
	public static Connection getConnection() throws SQLException {
		ConnectionHolder c = connections.get(Thread.currentThread().getId());
		if (c == null) {
			Connection co = null;
			synchronized (bds) {
				co = bds.getConnection();
			}
			connections.put(Thread.currentThread().getId(), new ConnectionHolder(co));
			return co;
		}
		if (c.needsValidation()) {
			if (c.conn.isClosed() || !c.conn.isValid(1)) {
				c.conn.close();
				Connection co = null;
				synchronized (bds) {
					co = bds.getConnection();
				}
				co.setAutoCommit(true);
				connections.put(Thread.currentThread().getId(), new ConnectionHolder(co));
				return co;
			} else {
				c.validate();
			}
		}
		return c.conn;
	}
	public static void close() {
		try {
			connections.get(Thread.currentThread().getId()).conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException npe) {
			//Ignored because we don't care if one didn't exist in the first place
		}
		connections.remove(Thread.currentThread().getId());
	}
	public static void endAll() throws SQLException {
		ConcurrentHashMap<Long, ConnectionHolder> connectionsc = connections;
		connections = null;
		for (Entry<Long, ConnectionHolder> l : connectionsc.entrySet()) {
			l.getValue().conn.close();
		}
		connectionsc.clear();
	}
	public static String safe(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}
	public static String unsafe(String s) {
		return URLDecoder.decode(s, StandardCharsets.UTF_8);
	}
	public static void notifySql(String s) {
		//System.out.println(s);//debug
	}
	public static boolean tableExists(String tableName) {
		try {
			DatabaseMetaData meta = getConnection().getMetaData();
			ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
			boolean b = resultSet.next();
			resultSet.close();
			return b;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		} finally {
			close();
		}
		//thx https://www.baeldung.com/jdbc-check-table-exists
	}
	public static void stop() {
		try {
			endAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			bds.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			envCtx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		try {
			initCtx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
