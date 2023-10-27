package accounts;

public class AuthenticationFailure extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6999377032466699129L;
	public AuthenticationFailure(String s, Exception c) {
		super(s,c);
	}
	public AuthenticationFailure(String string) {
		super(string);
	}
	public AuthenticationFailure() {
		this("FOR INTERNAL USE ONLY");
	}
	public static class AccountNotFoundException extends AuthenticationFailure {}
	public static class NonvalidEmailException extends AuthenticationFailure {}
}
