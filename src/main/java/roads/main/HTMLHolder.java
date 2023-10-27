package roads.main;

import accounts.AccountHandler;
import accounts.Permissions.PermissionType;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class HTMLHolder {
	public static String getGlobalBarHTML() {
		return "<div id=\"endOfFile\"></div>\r\n"
				+ "<div class=\"navbar\">\r\n"
				+ "  <a href=\"/\">Home</a>\r\n"
				+ "  <a href=\"/about\">About</a>\r\n"
				+ "  <a href=\"/account\">Account</a>\r\n"
				+ "</div>\r\n";
	}
	public static String getGlobalBarCSS(String nonce) {
		return "<meta charset=\"UTF-8\">\r\n"//yes, I know this is not a good idea.
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"//is initial-scale=1 enough?
				+ "<meta name=\"description\" content=\"" + PropertiesManager.getPropStringError("metaDescription") + "\">"
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/globalBar.css\" media=\"all and (min-width: 757px)\" nonce=\"" + nonce + "\"/>\r\n"
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/globalBarSmallScreen.css\" media=\"all and (max-width: 756px)\" nonce=\"" + nonce + "\"/>\r\n"
				+ "<script defer data-domain=\"" + PropertiesManager.getPropStringError("plausibleDomain") + "\" src=\"https://plausible.io/js/plausible.js\" nonce=\"" + nonce + "\"></script>\r\n";
	}
	private static final String BANNER_NULL = "<p class=\"bannerItem\">text here, not too much though...</p>";
	public static String getBannerHTML() {
		String str = PropertiesManager.getPropString("alertBanner", "");
		boolean show = true;
		if (str == null || str.contentEquals("") || str.contentEquals("null")) {
			PropertiesManager.putPropString("alertBanner", BANNER_NULL);
			str = BANNER_NULL;
			show = false;
		}
		if (BANNER_NULL.contentEquals(str)) {
			show = false;
		}
		return show ? ("<div id=\"banner\">\r\n"
				+ str
				+ "\r\n</div>\r\n"
				+ "<div id=\"bannerOffset\"></div>\r\n") : "";
	}
	private static String truncateLastSlash(String s) {
		if (s.contentEquals("")) {
			return "";
		}
		if (s.getBytes()[s.getBytes().length - 1] != "/".getBytes()[0]) {
			return s;
		}
		byte[] d = new byte[s.getBytes().length - 1];
		System.arraycopy(s.getBytes(), 0, d, 0, s.getBytes().length - 1);
		return new String(d);
	}
	static String generateRedirectHtmlNoTags(String location) {
		location = truncateLastSlash(location);
		return "<script type = \"text/javascript\">\r\n"
		+ "window.location = \"" + location + "\";\r\n"
		+ "</script>\r\n";
	}
	public static String generateRedirectAndAlertHtml(String alert, String location, String nonce) {
		location = truncateLastSlash(location);
		return "<!DOCTYPE html>\r\n<html lang=\"en-us\">\r\n"
		+ "<head>\r\n"
		+ "<script type=\"text/javascript\" nonce=\"" + nonce + "\">\r\n"
		+ "alert(\"" + alert + "\");"
		+ "window.location = \"" + location + "\";\r\n"
		+ "</script>\r\n"
		+ "</head>\r\n"
		+ "\r\n"
		+ "<body>\r\n"
		+ "</body>\r\n"
		+ "</html>  ";
	}
	public static String generateBaseHTML(String nonce) {
		return "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en-US\">\r\n"
				+ "<head>\r\n"
				+ "<title>" + PropertiesManager.getPropString("mainTitle", "SMRoads") + "</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/mainPage.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "</head>\r\n"
				+ "<body>\r\n"
				+ getBannerHTML()
				+ "<div class=\"roadData\">"
				+ "<script src=\"/statics/mainPage.js\" nonce=\"" + nonce + "\"></script>"
				+ getGlobalBarHTML()
				+ "</body>\r\n"
				+ "</html>\r\n";
	}
	public static String generateSignupHTML(String csrf, String nonce) {
		String head = "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en-us\">\r\n"
				+ "<head>\r\n"
				+ "<title>Signup</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/signupPage.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "</head>";
		String body = "<body>"
				+ getBannerHTML()
				+ "<form action=\"/signup-handler\" method=\"post\" target=\"_self\">\r\n"
				+ "<input type=\"hidden\" name=\"csrf\" id=\"csrf\" value=\"" + csrf + "\">"
				+ "  <div class=\"container\">\r\n"
				+ "    <h1>Register</h1>\r\n"
				+ "    <p>Please fill in this form to create an account.</p>\r\n"
				+ "    <hr>\r\n"
				+ "\r\n"
				+ "    <label for=\"name\"><b>First and Last Name</b></label>\r\n"
				+ "    <input type=\"text\" placeholder=\"Enter First and Last Name\" name=\"name\" id=\"name\" required>\r\n"
				+ "\r\n"
				+ "    <label for=\"email\"><b>Email</b></label>\r\n"
				+ "    <input type=\"text\" placeholder=\"Enter Email\" name=\"email\" id=\"email\" required>\r\n"
				+ "\r\n"
				+ "    <label for=\"psw\"><b>Password</b></label>\r\n"
				+ "    <label for=\"psw\"> must be more than 8 characters long. See <a href=\"https://www.security.org/how-secure-is-my-password/\">here</a>, <a href=\"https://www.bennish.net/password-strength-checker/\">here</a>, or, mostly as a joke, <a href=\"https://xkcd.com/936/\">here</a>.</label>\r\n"
				+ "    <input type=\"password\" placeholder=\"Enter Password\" name=\"psw\" id=\"psw\" required>\r\n"
				+ "\r\n"
				+ "    <label for=\"psw-repeat\"><b>Repeat Password</b></label>\r\n"
				+ "    <input type=\"password\" placeholder=\"Repeat Password\" name=\"psw-repeat\" id=\"psw-repeat\" required>\r\n"
				+ "    <hr>\r\n"
				+ "    <button type=\"submit\" class=\"registerbtn\">Register</button>\r\n"
				+ "  </div>\r\n"
				+ "  \r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Forgot your password? <a href=\"/reclaim-account\">Reset Password.</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Already have an account? <a href=\"/login\">Sign in</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "</form>\r\n" + getGlobalBarHTML() + "</body></html>";
		return head+body;
	}
	public static String generateLoginHTML(String csrf, String nonce) {
		String head = "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en-us\">\r\n"
				+ "<head>\r\n"
				+ "<title>Login</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/loginPage.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "</head>";
		String body = "<body>"
				+ getBannerHTML()
				+ "<form action=\"/login-handler\" method=\"post\" target=\"_self\">\r\n"
				+ "<input type=\"hidden\" name=\"csrf\" id=\"csrf\" value=\"" + csrf + "\">"
				+ "  <div class=\"container\">\r\n"
				+ "    <h1>Login</h1>\r\n"
				+ "    <p>Please fill in this form to login to your account.</p>\r\n"
				+ "    <hr>\r\n"
				+ "\r\n"
				+ "    <label for=\"username\"><b>Email</b></label>\r\n"
				+ "    <input type=\"text\" placeholder=\"Enter Email\" name=\"username\" id=\"username\" required>\r\n"
				+ "\r\n"
				+ "    <label for=\"psw\"><b>Password</b></label>\r\n"
				+ "    <input type=\"password\" placeholder=\"Enter Password\" name=\"psw\" id=\"psw\" required>\r\n"
				+ "\r\n"
				+ "    <hr>\r\n"
				+ "    <button type=\"submit\" class=\"registerbtn\">Login</button>\r\n"
				+ "  </div>\r\n"
				+ "  \r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Forgot your password? <a href=\"/reclaim-account\">Reset Password.</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Don't have an account? <a href=\"/signup\">Sign up</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "</form>\r\n" + getGlobalBarHTML() + "</body></html>";
		return head+body;
	}
	public static String generateAccountReclaimHTML(String csrf, String nonce) {
		String head = "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en-us\">\r\n"
				+ "<head>\r\n"
				+ "<title>Select Account</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/loginPage.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "</head>";
		String body = "<body>"
				+ getBannerHTML()
				+ "<form action=\"/hidden-send-account-reclaim\" method=\"post\" target=\"_self\">\r\n"
				+ "<input type=\"hidden\" name=\"csrf\" id=\"csrf\" value=\"" + csrf + "\">"
				+ "  <div class=\"container\">\r\n"
				+ "    <h1>Select Accoount</h1>\r\n"
				+ "    <p>Please fill in this form to reset the password to your account.</p>\r\n"
				+ "    <hr>\r\n"
				+ "\r\n"
				+ "    <label for=\"username\"><b>Email</b></label>\r\n"
				+ "    <input type=\"text\" placeholder=\"Enter Email\" name=\"username\" id=\"username\" required>\r\n"
				+ "\r\n"
				+ "    <hr>\r\n"
				+ "    <button type=\"submit\" class=\"registerbtn\">Send Email</button>\r\n"
				+ "  </div>\r\n"
				+ "  \r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Forgot your password? <a href=\"/reclaim-account\">Reset Password.</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Don't have an account? <a href=\"/signup\">Sign up</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "</form>\r\n" + getGlobalBarHTML() + "</body></html>";
		return head+body;
	}
	public static String generateAccountReclaimPasswordInputHTML(String account, String recl, String csrf, String nonce) {
		String head = "<!DOCTYPE html>\r\n"
				+ "<html lang=\"en-us\">\r\n"
				+ "<head>\r\n"
				+ "<title>Reset Password</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/loginPage.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "</head>";
		String body = "<body>"
				+ getBannerHTML()
				+ "<form action=\"/hidden-send-account-reclaim-password\" method=\"post\" target=\"_self\">\r\n"
				+ "<input type=\"hidden\" name=\"csrf\" id=\"csrf\" value=\"" + csrf + "\">"
				+ "  <div class=\"container\">\r\n"
				+ "    <h1>Reset Password</h1>\r\n"
				+ "    <p>Please fill in this form to reset the password to your account.</p>\r\n"
				+ "    <hr>\r\n"
				+ "\r\n"
				+ "    <label for=\"pass\"><b>Password</b></label>\r\n"
				+ "    <label for=\"pass\"> must be more than 8 characters long. See <a href=\"https://www.bennish.net/password-strength-checker/\">here</a>, <a href=\"https://www.security.org/how-secure-is-my-password/\">here</a>, or, mostly as a joke, <a href=\"https://xkcd.com/936/\">here</a>.</label>\r\n"
				+ "    <input type=\"text\" placeholder=\"Enter Password\" name=\"pass\" id=\"pass\" required>\r\n"
				+ "\r\n"
				+ "    <hr>\r\n"
				+ "<input type=\"hidden\" name=\"csrf\" id=\"csrf\" value=\"" + csrf + "\">\r\n"
				+ "<input type=\"hidden\" name=\"recl\" id=\"recl\" value=\"" + recl + "\">\r\n"
				+ "<input type=\"hidden\" name=\"acc\" id=\"acc\" value=\"" + account + "\">\r\n"
				+ "    <button type=\"submit\" class=\"registerbtn\">Reset Password</button>\r\n"
				+ "  </div>\r\n"
				+ "  \r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Forgot your password? <a href=\"/reclaim-account\">Reset Password.</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "  <div class=\"container signin\">\r\n"
				+ "    <p>Don't have an account? <a href=\"/signup\">Sign up</a>.</p>\r\n"
				+ "  </div>\r\n"
				+ "</form>\r\n" + getGlobalBarHTML() + "</body></html>";
		return head+body;
	}
	public static String generateAboutHTML(String nonce) {
		return ("<!DOCTYPE html>\r\n"
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "<title>" + PropertiesManager.getPropString("aboutTitle", "About Us") + "</title>\r\n"
				+ getGlobalBarCSS(nonce)
				+ "</head>\r\n"
				+ "<body>\r\n"
				+ getBannerHTML()
				+ PropertiesManager.getPropStringError("aboutText") + "\r\n"
				+ "<p>Questions, comments? Contact us!</p>"
				+ "<p>Email: " + PropertiesManager.getPropStringError("aboutEmail") + "</p>"
				+ getGlobalBarHTML()
				+ "</body>\r\n"
				+ "</html>\r\n");
	}
	public static void sendNotFound(HttpServletResponse exchange) throws IOException {
		exchange.getWriter().append("<h1>404 Not Found</h1>No context found for request");
	}
	public static String generateRobots() {
		return "User-agent: *\r\nDisallow: \r\n";
	}
	public static String generateUserList(String username) {//remember, if concating a byte[] to a string, it will not work because it will run b.toString, not new String(b)
		if (username == null || username.contentEquals("")) {
			return "<p>Enter a search.</p>";
		}
		List<AccountHandler> ahs = AccountHandler.search(username.toLowerCase());
		if (ahs.isEmpty()) {
			return "<p>No Users found.</p>";
		}
		String html = "<div>\r\n";//can only have one root element
		for (AccountHandler ah : ahs) {
			String name = Base64.getEncoder().encodeToString(ah.getValue().getName().getBytes()).replace('/', '-').replace('+', '_');
			html = html + "<button id=\"" + name + "\" class=\"result\" type=\"button\"\">" + ah.getValue().getName() + "</button>\r\n";
		}
		html = html + "</div>\r\n";
		return html;
	}
	public static String generateUserListJs(String username, String nonce) {//remember, if concating a byte[] to a string, it will not work because it will run b.toString, not new String(b)
		if (username == null || username.contentEquals("")) {
			return "";
		}
		List<AccountHandler> ahs = AccountHandler.search(username.toLowerCase());
		if (ahs.isEmpty()) {
			return "";
		}
		String js = "<script nonce=\"" + nonce + "\">\r\n";
		for (AccountHandler ah : ahs) {
			String name = Base64.getEncoder().encodeToString(ah.getValue().getName().getBytes()).replace('/', '-').replace('+', '_');
			js = js + "document.getElementById(\"" + name + "\").addEventListener(\"click\", function(){window.location = '/user-controls/user?user=" + new String(ah.getValue().getEmail()) + "';});\r\n";
		}
		js = js + "</script>\r\n";
		return js;
	}
	public static String generateUserSearchHTML(String user, String nonce) {
		return "<!DOCTYPE html>"
				+ "<html lang=\"en-us\">"
				+ "<head>"
				+ "<title>Search Users</title>"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/userSearchControls.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "    </head>\r\n"
				+ "    <body>\r\n"
				+ getBannerHTML()
				+ "        <div class=\"search\">\r\n"
				+ "            <form action=\"/user-controls\" method=\"get\" target=\"_self\">\r\n"
				+ "                <label for=\"query\" class=\"queryLb\">Search For a User:</label>\r\n"
				+ "                <input type=\"text\" class=\"query\" name=\"query\">\r\n"
				+ "                <input type=\"submit\" value=\"Submit\" class=\"submit\">\r\n"
				+ "            </form>\r\n"
				+ "        </div>\r\n"
				+ "        <div class=\"results\">"
				+ "<p class=\"srt\">Search Results:</p>"
				+ generateUserList(user)
				+ "</div>"
				+ generateUserListJs(user, nonce)
				+ getGlobalBarHTML()
				+ "</body>"
				+ "</html>";
	}
	static String generateUserControls(AccountHandler user) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div>\r\n");
		for (PermissionType pt : PermissionType.values()) {
			boolean able = user.getPermission(pt);
			sb.append("<p class=\"resultHelper\">Permission ").append(pt.name()).append(" is set to ").append(able ? "enabled" : "disabled").append(".</p>");
			sb.append("<button id=\"").append(pt.name()).append("\" class=\"result\" type=\"button\">Press to ").append((able ? "dis" : "en")).append("able.</button><p></p>\r\n");
		}
		return sb.append("</div>\r\n").toString();
	}
	static String generateUserControlsJS(AccountHandler user, String nonce) {
		String js = "<script nonce=\"" + nonce + "\">\r\n";//can only have one root element
		for (PermissionType pt : PermissionType.values()) {
			boolean able = user.getPermission(pt);
			String link = "/user-controls/user/edit?user=" + new String(user.getValue().getEmail()) + "&permission=" + pt.name() + "&newValue=" + Boolean.toString(!able);
			js = js + "document.getElementById(\"" + pt.name() + "\").addEventListener(\"click\", function(){window.location = '" + link + "';});\r\n";
		}
		js = js + "</script>\r\n";
		return js;
	}
	static String generateUserData(AccountHandler user, boolean b) {
		return "<div>\r\n"
				+ "<p>Name: \"" + user.getValue().getName() + "\"</p>\r\n"
				+ "<p>Email: \"" + new String(user.getValue().getEmail()) + "\"</p>\r\n"
				+ "<p>Date joined: \"" + user.getValue().getDateJoined() + "\"</p>\r\n"
				+ "</div>\r\n";
	}
	public static String generateUserControlHTML(AccountHandler user, String nonce) {
		return "<!DOCTYPE html>"
				+ "<html lang=\"en-us\">"
				+ "<head>"
				+ "<title>Edit User Privilages</title>"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/userSearchControls.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "    </head>\r\n"
				+ "    <body>\r\n"
				+ getBannerHTML()
				+ "        <div class=\"search\">\r\n"
				+ "            <form action=\"/user-controls\" method=\"get\" target=\"_self\">\r\n"
				+ "                <label for=\"query\" class=\"queryLb\">Search For a User:</label>\r\n"
				+ "                <input type=\"text\" class=\"query\" name=\"query\">\r\n"
				+ "                <input type=\"submit\" value=\"Submit\" class=\"submit\">\r\n"
				+ "            </form>\r\n"
				+ "        </div>\r\n"
				+ "        <div class=\"results\">"
				+ "<p class=\"srt\">User Data:</p>"
				+ generateUserData(user, true)
				+ "</div>"
				+ "        <div class=\"results\">"
				+ "<p class=\"srt\">Edit User Privilages:</p>"
				+ generateUserControls(user)
				+ "</div>"
				+ generateUserControlsJS(user, nonce)
				+ getGlobalBarHTML()
				+ "</body>"
				+ "</html>";
	}

	public static String generatePropertiesHTML(String nonce) {
		return "<!DOCTYPE html>"
				+ "<html lang=\"en-us\">"
				+ "<head>"
				+ "<title>Edit Properties</title>"
				+ getGlobalBarCSS(nonce)
				+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/userSearchControls.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
				+ "    </head>\r\n"
				+ "    <body>\r\n"
				+ getBannerHTML()
				+ "        <div class=\"search\">\r\n"
				+ "            <form action=\"/hidden-properties-edit\" method=\"post\" target=\"_self\">\r\n"
				+ "                <label for=\"property\" class=\"queryLb\">Enter Property (case sensitive): </label>\r\n"
				+ "                <input type=\"text\" class=\"query\" name=\"property\">\r\n"
				+ "                <label for=\"value\" class=\"queryLb\">Enter Value (again, case sensitive): </label>\r\n"
				+ "                <input type=\"text\" class=\"query\" name=\"value\">\r\n"
				+ "                <input type=\"submit\" value=\"Submit\" class=\"submit\">\r\n"
				+ "            </form>\r\n"
				+ "        </div>\r\n"
				+ "        <div class=\"results\">"
				+ "<p class=\"srt\">Server Info:</p>"
				+ "<p>Server UUID: " + StartupListener.SERVER.toString() + "</p>"
				+ "<p>Server Startup Date: " + StartupListener.STARTUP.toString() + "</p>"
				+ "</div>"
				+ "        <div class=\"results\">"
				+ "<p class=\"srt\">Properties:</p>"
				+ PropertiesManager.html(PropertiesManager.listFull(), "p", null)
				+ "</div>"
				+ getGlobalBarHTML()
				+ "</body>"
				+ "</html>";
	}
	/**
	 * escape()
	 *
	 * Escape a give String to make it safe to be printed or stored.
	 *
	 * @param s The input String.
	 * @return The output String.
	 * @author Dan: https://stackoverflow.com/a/61628600
	 **/
	private static String escape(String s){
		return s.replace("\\", "\\\\")
				.replace("\t", "\\t")
				.replace("\b", "\\b")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\f", "\\f")
				.replace("'", "\\'")
				.replace("\"", "\\\"");
	}
	private static String[] lenfilter(String[] s) {
		ArrayList<String> inter = new ArrayList<>();
		for (String st : s) {
			if (st != null && st.length() > 0) {
				inter.add(st);
			}
		}
		return inter.toArray(new String[0]);
	}
	public static String generateUserSettingsHTML(AccountHandler acc, String csrf, String nonce) {
		String js = "console.log(\"Do stuff!\")";

		return "<!DOCTYPE html>"
		+ "<html lang=\"en-us\">"
		+ "<head>"
		+ "<title>User Settings</title>"
		+ getGlobalBarCSS(nonce)
		+ "<link type=\"text/css\" rel=\"stylesheet\" href=\"/styles/userSettings.css\" media=\"all\" nonce=\"" + nonce + "\"/>\r\n"
		+ "</head>\r\n"
		+ "<body>\r\n"
		+ getBannerHTML()
		+ "<p class=\"srt\">User Data:</p>"
		+ generateUserData(acc, false)
		+ "<div class=\"results\">\r\n"
		+ "    <p class=\"srt\">Road Status Notifications:</p>\r\n"
		+ "    <p>By enabling any on these times, you agree to recieve SMS messages from us to alert you. Note that an event which is reported outside of your selected times <strong>will not</strong> be sent to you, so even if you didn't recieve a message a road may be closed. Additionally, please note that this data is sent in by community volunteers and may not be up to date.</p>"
		+ "    <noscript>This page requires javascript!</noscript>"
		+ "    <div id=\"tableholder\"></div>"
		+ "</div>"
		+ "<script nonce=\"" + nonce + "\">\r\n"
		+ js
		+ "</script>\r\n"
		+ "<script src=\"/statics/userSettings.js\" nonce=\"" + nonce + "\"></script>\r\n"
		+ getGlobalBarHTML()
		+ "</body>"
		+ "</html>";
	}
}
