package accounts;

public enum AccountStatus {
	Admin,
	User,
	Viewer;
	public boolean equalOrGreaterThan(AccountStatus st) {
		if (st == AccountStatus.Admin) {
			return true;
		} else if (st == AccountStatus.User) {
			return this != Viewer || this != null;//this != null should always be true, but whatever
		} else if (st == AccountStatus.Viewer) {
			return this != null;
		} else {
			return this == null;
		}
	}
	public static boolean equalOrGreaterThan(AccountStatus acc, AccountStatus needed) {//i'm not 100% sure about this...
		if (acc == AccountStatus.Admin) {
			return true;
		} else if (acc == AccountStatus.User) {
			return needed != Viewer || needed != null;//this != null should always be true, but whatever
		} else if (acc == AccountStatus.Viewer) {
			return needed != null;
		} else {
			return needed == null;
		}
	}
}
