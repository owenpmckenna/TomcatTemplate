package handlers;

import java.io.IOException;

import accounts.Account;
import csrf.CSRFHelper;
import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;
import sql.AccountSQLHelper;

/**
 * Servlet implementation class UserSearchHandler
 */
public class AccountReclaimGUIHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AccountReclaimGUIHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Account acc = AccountSQLHelper.find(request.getParameter("acc"));
		String recl = getParameter("recl", "input invalid", request, response);
		String nonce = Secure.ofNonce(response);
		if (acc == null) {
			response.getWriter().println(HTMLHolder.generateRedirectAndAlertHtml("input invalid", "/", nonce));
			return;
		}
		if (!acc.getValidationCode().contentEquals(recl)) {
			response.getWriter().println(HTMLHolder.generateRedirectAndAlertHtml("input invalid", "/", nonce));
		}
		String csrf = CSRFHelper.addCsrf(FormType.FinishAccountReclaim);
		response.getWriter().println(HTMLHolder.generateAccountReclaimPasswordInputHTML(new String(acc.getEmail()), recl, csrf, nonce));
	}
}
