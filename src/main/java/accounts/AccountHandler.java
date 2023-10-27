package accounts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import accounts.AuthenticationFailure.AccountNotFoundException;
import accounts.AuthenticationFailure.NonvalidEmailException;
import accounts.Permissions.PermissionType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;
import sql.AccountSQLHelper;

public class AccountHandler {
	public static AccountHandler getAccount(long id) throws AuthenticationFailure {
		Account acc = AccountSQLHelper.getAccountFromId(id);
		if (acc == null) {
			throw new AccountNotFoundException();
		}
		if (!acc.getValidated()) {
			throw new NonvalidEmailException();
		}
		return new AccountHandler(acc);
	}
	private static boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (NumberFormatException | NullPointerException exc) {
			return false;
		}
	}
	/**
	 * call this code: <code>AccountHandler ah = null;\r\n if ((ah = AccountHandler.checkAccount(request, response)) == null) {return;}</code>
	 *
	 */
	public static AccountHandler checkAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			AccountHandler ah = getAccount(request.getCookies());
			if (!ah.value.getValidated()) {
				String nonce = Secure.ofNonce(response);
				response.getWriter().append(HTMLHolder.generateRedirectAndAlertHtml("Please press the link in the validation email.", "/login", nonce));
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return null;
			}
			return ah;
		} catch (AccountNotFoundException e) {
			Secure.ofNonce(response);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendRedirect("/signup");
			return null;
		}
	}
	public static AccountHandler getAccount(Cookie[] cookies) throws AuthenticationFailure {
		String value = "";
		if (cookies == null) {
			throw new AccountNotFoundException();
		}
		if (cookies.length == 0) {
			throw new AccountNotFoundException();
		}
		for (Cookie c : cookies) {
			if (c.getName().equalsIgnoreCase("User")) {
				value = c.getValue();
			}
		}
		if (!isLong(value)) {
			throw new AccountNotFoundException();
		}
		Account acc = AccountSQLHelper.getAccountFromId(Long.parseLong(value));
		if (acc == null) {
			throw new AccountNotFoundException();
		}
		return new AccountHandler(acc);
	}
	public static AccountHandler getAccount(char[] email, char[] password) throws AuthenticationFailure {
		if (email.length >= 40 || password.length > 128) {
			throw new AccountNotFoundException();
		}
		email = new String(email).toLowerCase().toCharArray();
		Account acc = AccountSQLHelper.find(new String(email));
		if (acc == null) {
			throw new AccountNotFoundException();
		}
		char[] pass = Account.hash(password, acc.getId());
		if (Arrays.equals(acc.getPassword(), pass) && Arrays.equals(acc.getEmail(), email)) {
			return new AccountHandler(acc);
		}
		throw new AccountNotFoundException();
	}
	public static AccountHandler createAccount(String displayName, char[] email, char[] password) throws AuthenticationFailure {
		return createAccount(displayName, email, password, false);
	}
	public static AccountHandler createSuperAccount(String displayName, char[] email, char[] password) throws AuthenticationFailure {
		return createAccount(displayName, email, password, true);
	}
	public static boolean isGoodString(String s, int maxlen) {//display: 20, email: 40, password: 100
		if (s.getBytes().length <= 4 || s.getBytes().length >= maxlen) {
			return false;
		}
		for (char c : s.toCharArray()) {
			if (!Character.isLetterOrDigit(c) && c != '@' && c != '.' && c != '-' && c != '_' && c != ' ') {
				return false;
			}
		}
		return true;
	}
	public static boolean isPassGood(char[] pass) {
		return pass.length >= 8 && pass.length <= 128;
	}
	public static AccountHandler createAccount(String displayName, char[] email, char[] password, boolean superUser) throws AuthenticationFailure {
		//remember to URLEncode these during serialization
		String usr = new String(email);
		if (!isGoodString(usr, 40)) {
			throw new AuthenticationFailure();
		}
		usr = displayName;
		if (!isGoodString(usr, 20) ||
			password.length <= 8 ||
			password.length > 128 ||
			AccountSQLHelper.getAccountExists(email) ||
			(!superUser && "admin".equalsIgnoreCase(usr))) {
			throw new AuthenticationFailure();
		}
		Account ac = Account.create(displayName, new String(email).toLowerCase().toCharArray(), password, superUser);
		AccountSQLHelper.createNewAccount(ac);
		return new AccountHandler(ac);
	}

	public AccountHandler(Account value) {
		this.value = value;
	}
	public void setHeaders(HttpServletResponse resp) {
		if (value == null) {
			return;
		}
		Cookie c = new Cookie("User", Long.toString(value.getId()));
		c.setPath("/");
		c.setSecure(true);
		c.setHttpOnly(true);
		c.setMaxAge(86400);//one day
		resp.addCookie(c);//how can we set samesite?
	}
	private Account value;
	public Account getValue() throws AuthenticationFailure {
		if (value == null) {
			throw new AuthenticationFailure();
		}
		return value;
	}
	public boolean tryValidate(String val) {
		if (value.getValidationCode().contentEquals(val)) {
			value.setValidated(true);
			return true;
		}
		return false;
	}
	public boolean getPermission(PermissionType p) {
		return getValue().getPermission(p);
	}
	public boolean setPermission(PermissionType p, boolean val) {
		return getValue().changePermission(p, val);
	}
	public boolean canAction() {
		return AccountSQLHelper.canAction(getValue());
	}
	public void increaseActionTime(long msincrease) {
		AccountSQLHelper.increaseActionTime(getValue(), msincrease);
	}
	public static List<AccountHandler> search(String username) {
		ArrayList<AccountHandler> ahs = new ArrayList<>();
		for (Account a : AccountSQLHelper.search(username)) {
			ahs.add(new AccountHandler(a));
		}
		return ahs;
	}
	public static AccountHandler findAccount(String email) {
		Account a = AccountSQLHelper.find(email.toLowerCase());
		if (a == null) {
			return null;
		} else {
			return new AccountHandler(a);
		}
	}
}
