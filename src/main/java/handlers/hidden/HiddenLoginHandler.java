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

/**
 * Servlet implementation class HiddenLoginHandler
 */
public class HiddenLoginHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenLoginHandler() {
		super(true);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		tryCsrf(FormType.Login, request, response);
		AccountHandler ah;
		try {
			ah = AccountHandler.getAccount(getParameter("username", request, response).toCharArray(), getParameter("psw", request, response).toCharArray());
			ah.setHeaders(response);
			redirect("/", response);
		} catch (AuthenticationFailure af) {
			nonceAlertRedirect("Username not found or Password incorrect.", "/login", response);
		}
	}

}
