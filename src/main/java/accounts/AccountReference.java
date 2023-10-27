package accounts;

public class AccountReference {
	public long id;
	public AccountReference(AccountHandler a) {
		this(a.getValue());
	}
	public AccountReference(Account a) {
		this(a.getId());
	}
	public AccountReference(long a) {
		this.id = a;
	}
	public AccountHandler getReference() {
		return AccountHandler.getAccount(id);
	}
}
