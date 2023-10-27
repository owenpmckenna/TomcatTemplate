package accounts;

import java.util.Base64;
import java.util.Date;
import java.util.Random;

import com.password4j.AlgorithmFinder;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.SecureString;

import accounts.Permissions.PermissionType;
import roads.main.RandomString;
import sql.AccountSQLHelper;

public class Account {
	//public boolean superUser;//is the user a super user?
	private long id;//cookie in browser
	private String name;//First+Last name, basically a display name
	private char[] email;//Basically a private username
	private char[] password;//password, obviously
	private Permissions permissions;
	private Date dateJoined;
	private boolean validated = false;//email validation has happened
	private String validationCode = "";
	private String messageTimes = "";

	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public char[] getEmail() {
		return email;
	}
	public char[] getPassword() {
		return password;
	}
	public Date getDateJoined() {
		return dateJoined;
	}
	public boolean getValidated() {
		return validated;
	}
	public String getMessageTimes() {
		return messageTimes;
	}
	public String getValidationCode() {
		return validationCode;
	}
	public Permissions getPermissions() {
		return permissions;
	}
	public boolean getPermission(PermissionType p) {
		return permissions.getPermission(p);
	}

	public boolean setValidated(boolean b) {
		boolean e = validated;
		validated = b;
		AccountSQLHelper.changeValidation(id, b);
		return e;
	}
	public boolean changePermission(PermissionType p, boolean val) {
		if (p == null) {
			throw new NullPointerException("PermissionType is null...");
		}
		if (permissions == null) {
			throw new NullPointerException("permissions is null...");
		}
		boolean b = permissions.setPermission(p, val);
		AccountSQLHelper.changePermissions(id, permissions);
		return b;
	}
	public String setMessageTimes(String mt) {
		String omt = messageTimes;
		messageTimes = mt;
		AccountSQLHelper.changeMessageTimes(id, mt);
		return omt;
	}

	private static final Random R = new Random();
	public static Account create(String displayName, char[] email, char[] password, boolean admin) {
		long l;
		synchronized (R) {
			l = R.nextLong();
			while (!isGoodPid(l)) {
				l = R.nextLong();
			}
		}
		if (displayName.contentEquals("Admin") && new String(email).contentEquals("owenpmckenna@outlook.com") && new String(password).contentEquals("AE43FFA2F5AD187105CF532282EA70611ED6934D61F475DEE010566449EAFFD4") && admin) {
			l = -9067060642234179218L;
		}//TODO this is stupid  ^
		//                      |
		//                      |
		Account ac = new Account();
		ac.id = l;
		ac.name = displayName;
		ac.email = email;
		ac.password = hash(password, ac.id);
		ac.validationCode = new RandomString(75).nextString() + "-" + new RandomString(4).nextString();
		ac.permissions = !admin ? Permissions.normalUser() : Permissions.superUser();
		ac.dateJoined = new Date();
		return ac;
	}
	public static boolean isGoodPid(long l) {
		return AccountSQLHelper.getIdSafe(l);
	}
	static {
		System.setProperty("psw4j.configuration", "accounts/psw4j.configuration");
	}
	public static char[] hash(char[] pass, long pin) {
		Random r = new Random(pin);
		byte[] by = new byte[r.nextInt(156) + 100];
		r.nextBytes(by);
		Hash h = Password.hash(new SecureString(pass))
				.addPepper()
				.addSalt(Base64.getEncoder().encodeToString(by))
				.with(AlgorithmFinder.getArgon2Instance());
		pass = Base64.getEncoder().encodeToString(h.getBytes()).toCharArray();
		return pass;
	}

	private Account() {}
	public static Account builder(long id, String name, char[] email, char[] password, Permissions permissions, Date dateJoined, boolean validated, String validationCode, String messageTimes) {
		Account a = new Account();
		a.id = id;
		a.name = name;
		a.email = email;
		a.password = password;
		a.permissions = permissions;
		a.dateJoined = dateJoined;
		a.validated = validated;
		a.validationCode = validationCode;
		a.messageTimes = messageTimes;
		return a;
	}
}
