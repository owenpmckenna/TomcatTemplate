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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import accounts.Account;
import accounts.Permissions;

public class AccountSQLHelper {
	private AccountSQLHelper() {}//invisible
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
		if (tableExists("ACCOUNTS")) {
			return;
		}
		String sql="create table IF NOT EXISTS ACCOUNTS"
				+ "(ID BIGINT(255) NOT NULL,"
				+ "NAME VARCHAR(25) NOT NULL,"
				+ "EMAIL VARCHAR(50) NOT NULL UNIQUE,"
				+ "PASSWORD VARCHAR(684) NOT NULL,"
				+ "PERMISSIONS VARCHAR(255) NOT NULL,"
				+ "DATE_JOINED BIGINT(255) NOT NULL,"
				+ "VALIDATED boolean NOT NULL,"
				+ "VALIDATION_CODE VARCHAR(255) NOT NULL,"
				+ "VERSION INT NOT NULL,"
				+ "LAST_ACTION BIGINT(255) NOT NULL,"
				+ "MESSAGETIMES TEXT NOT NULL,"
				+ "PRIMARY KEY (ID));";
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
	public static void createNewAccount(Account acc) {
		String sql="SELECT * FROM ACCOUNTS limit 1";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			rs.moveToInsertRow();
			rs.updateLong("ID", acc.getId());
			rs.updateString("NAME", safe(acc.getName()));
			rs.updateString("EMAIL", safe(new String(acc.getEmail())));
			rs.updateString("PASSWORD", new String(acc.getPassword()));
			rs.updateString("PERMISSIONS", acc.getPermissions().toString());
			rs.updateLong("DATE_JOINED", acc.getDateJoined().getTime());
			rs.updateBoolean("VALIDATED", acc.getValidated());
			rs.updateString("VALIDATION_CODE", acc.getValidationCode());
			rs.updateInt("VERSION", 1);
			rs.updateLong("LAST_ACTION", System.currentTimeMillis());
			rs.updateString("MESSAGETIMES", acc.getMessageTimes());
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
	public static void changeValidation(long accountId, boolean validated) {
		String sql="SELECT * FROM ACCOUNTS "
				+ "WHERE ID=" + accountId + "; ";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			rs.first();
			rs.updateBoolean("VALIDATED", validated);
			rs.updateRow();
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
	public static void changePermissions(long accountId, Permissions newPermissions) {
		String sql="SELECT * FROM ACCOUNTS "
				+ "WHERE ID=" + Long.toString(accountId) + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			if (!rs.next()) {
				return;
			}
			rs.updateString("PERMISSIONS", newPermissions.toString());
			rs.updateRow();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public static Account getAccountFromResultSet(ResultSet rs) throws SQLException {
		long id = rs.getLong("ID");
		String name = unsafe(rs.getString("NAME"));
		char[] email = unsafe(rs.getString("EMAIL")).toCharArray();
		char[] password = rs.getString("PASSWORD").toCharArray();
		Permissions permissions = Permissions.ofString(rs.getString("PERMISSIONS"));
		Date dateJoined = new Date(rs.getLong("DATE_JOINED"));
		boolean validated = rs.getBoolean("VALIDATED");
		String validationCode = rs.getString("VALIDATION_CODE");
		String messageTimes = rs.getString("MESSAGETIMES");
		return Account.builder(id, name, email, password, permissions, dateJoined, validated, validationCode, messageTimes);
	}
	public static Account getAccountFromId(long id) {
		String sql="select * from ACCOUNTS where ID=" + Long.toString(id) + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement()) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					Account a = getAccountFromResultSet(rs);
					rs.close();
					return a;
				}
			}
			rs.close();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
	}
	public static Account getAccountFromLogin(char[] email, char[] password) {
		String safeEmail = safe(new String(email));
		String safePassword = safe(new String(password));//MUST EXPLICITLY SAY THAT THE PASSWORD IS CASE SENSITIVE
		String sql="select * from ACCOUNTS where EMAIL=? and PASSWORD= BINARY ?;";
		notifySql(sql);
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, safeEmail);
			st.setString(2, safePassword);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					Account a = getAccountFromResultSet(rs);
					rs.close();
					return a;
				}
			}
			rs.close();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
	}
	public static boolean getAccountExists(char[] email) {
		String safeEmail = safe(new String(email));
		String sql="select * from ACCOUNTS where EMAIL=?;";
		notifySql(sql);
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, safeEmail);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					rs.close();
					return true;
				}
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
	public static List<Account> search(String search) {
		String sql="select * from ACCOUNTS where NAME like ? or EMAIL like ?";
		notifySql(sql);
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, "%" + safe(search) + "%");
			st.setString(2, "%" + safe(search) + "%");
			ResultSet rs = st.executeQuery();
			ArrayList<Account> accounts = new ArrayList<>();
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					Account a = getAccountFromResultSet(rs);
					accounts.add(a);
				}
			}
			return accounts;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} finally {
			close();
		}
	}
	public static Account find(String email) {
		if (email == null) {
			return null;
		}
		String sql="select * from ACCOUNTS where EMAIL=?";
		notifySql(sql);
		try (PreparedStatement st = getConnection().prepareStatement(sql)) {
			st.setString(1, safe(email));
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					return getAccountFromResultSet(rs);
				}
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			close();
		}
	}
	public static boolean getIdSafe(long id) {
		String sql="select NAME from ACCOUNTS where ID=" + id + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement()) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					rs.close();
					return false;
				}
			}
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} finally {
			close();
		}
	}
	public static void resetPassword(Account acc, char[] pass) {
		pass = Account.hash(pass, acc.getId());
		String sql="select * from ACCOUNTS where ID=" + acc.getId() + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					rs.updateString("PASSWORD", new String(pass));
					rs.updateRow();
					rs.close();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public static boolean canAction(Account acc) {
		String sql="select * from ACCOUNTS where ID=" + acc.getId() + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					long s = rs.getLong("LAST_ACTION");
					rs.close();
					return s < System.currentTimeMillis();
				}
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
	public static void increaseActionTime(Account acc, long msincrease) {
		String sql="select * from ACCOUNTS where ID=" + acc.getId() + ";";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("NAME") != null) {
					rs.updateLong("LAST_ACTION", System.currentTimeMillis() + msincrease);
					rs.updateRow();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}
	public static void changeMessageTimes(long accountId, String mt) {
		String sql="SELECT * FROM ACCOUNTS "
				+ "WHERE ID=" + accountId + "; ";
		notifySql(sql);
		try (Statement st = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			ResultSet rs = st.executeQuery(sql);
			rs.first();
			rs.updateString("MESSAGETIMES", mt);
			rs.updateRow();
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
}
