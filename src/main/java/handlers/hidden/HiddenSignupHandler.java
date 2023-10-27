package handlers.hidden;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.AuthenticationFailure;
import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.Utilities;

/**
 * Servlet implementation class HiddenSignupHandler
 */
public class HiddenSignupHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenSignupHandler() {
		super(true);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		tryCsrf(FormType.Signup, request, response);
		String name = getParameter("name", request, response);
		String email = getParameter("email", request, response);
		String psw = getParameter("psw", request, response);
		String pswrepeat = getParameter("psw-repeat", request, response);
		if (!psw.contentEquals(pswrepeat)) {
			nonceAlertRedirect("Password and Repeat Password do not match.", "/signup", response);
		}
		AccountHandler ah = null;
		try {
			ah = AccountHandler.createAccount(name, email.toCharArray(), psw.toCharArray());
			ah.setHeaders(response);
			Utilities.sendAccountValidateEmail(ah);
			redirect("/", response);
		} catch (AuthenticationFailure af) {
			nonceAlertRedirect("Username/Email/Phone/Password contains invalid characters/is too long or Username/Email/Phone already taken.", "/signup", response);
		}
	}
}
