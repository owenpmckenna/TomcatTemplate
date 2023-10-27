package sql;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class CsrfSQLHelper {
	private CsrfSQLHelper() {}
	public static Connection getConnection() throws SQLException {
		return CentralSQLHolder.getConnection();
	}
	public static void close() {
		CentralSQLHolder.close();
	}
	public static void endAll() throws SQLException {
		CentralSQLHolder.endAll();
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
	public static void setup() {
		if (tableExists("CSRF")) {
			return;
		}
		String sql="create table IF NOT EXISTS CSRF"
				+ "(TYPE TINYINT NOT NULL,"
				+ "VALUE VARCHAR(50) NOT NULL,"
				+ "DATETIME BIGINT(255) NOT NULL,"
				+ "PRIMARY KEY (VALUE));";
		notifySql(sql);
		try (Statement st = getConnection().createStatement()) {
			st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public static void stop() {
		try {
			endAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void createCsrf(String csrf, int type) {
		tryClean();
		String sql="SELECT * FROM CSRF limit 1";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			rs.moveToInsertRow();
			rs.updateString("VALUE", safe(csrf));
			rs.updateInt("TYPE", type);
			rs.updateLong("DATETIME", new Date().getTime());
			rs.insertRow();
			if (!getConnection().getAutoCommit()) {
				getConnection().commit();
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public static boolean csrfExists(String csrf, int type) {
		tryClean();
		String sql="SELECT * FROM CSRF WHERE VALUE=? AND TYPE=?;";
		notifySql(sql);
		try (PreparedStatement st = getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			st.setString(1, safe(csrf));
			st.setInt(2, type);
			ResultSet rs = st.executeQuery();
			if (!rs.next()) {
				return false;
			}
			if (unsafe(rs.getString("VALUE")).contentEquals(csrf)) {
				long e = rs.getLong("DATETIME");
				rs.deleteRow();
				return timeValid(e);
			}
			rs.close();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			close();
		}
	}
	private static boolean timeValid(long data) {
		return (new Date().getTime()-data < 30*60*1000);
	}
	private static final Object cleanerLock = new Object();
	private static long lastCleaned = new Date().getTime();
	private static boolean recleanTimeValid() {
		return (new Date().getTime()-lastCleaned > 60*60*1000);
	}
	private static void tryClean() {
		synchronized (cleanerLock) {
			if (!recleanTimeValid()) {
				return;
			}
			lastCleaned = new Date().getTime();
		}
		String sql="SELECT * FROM CSRF;";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (!timeValid(rs.getLong("DATETIME"))) {
					rs.deleteRow();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
}
