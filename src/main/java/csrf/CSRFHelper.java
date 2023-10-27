package csrf;

import roads.main.RandomString;
import sql.CsrfSQLHelper;

public class CSRFHelper {
	private CSRFHelper() {}
	
	public enum FormType {
		Signup,
		Login,
		StartAccountReclaim,
		FinishAccountReclaim,
		PhoneValidate,
		UserSettings
	}
	
	public static boolean isCsrfValid(String identifier, FormType type) {
		if (identifier == null || identifier.length() == 0) {
			return false;
		}
		return CsrfSQLHelper.csrfExists(identifier, type.ordinal());
	}
	public static final RandomString RS = new RandomString(50);
	public static String addCsrf(FormType type) {
		String s;
		synchronized (RS) {
			s = RS.nextString();
		}
		CsrfSQLHelper.createCsrf(s, type.ordinal());
		return s;
	}
}
