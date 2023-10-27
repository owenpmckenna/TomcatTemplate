package handlers.hidden;

import java.io.IOException;

import accounts.Account;
import accounts.AccountHandler;
import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sql.AccountSQLHelper;

/**
 * Servlet implementation class HiddenEmailValidationHandler
 */
public class HiddenAccountReclaimFinalizeHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenAccountReclaimFinalizeHandler() {
		super(true);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		char[] pass = getParameter("pass", request, response).toCharArray();
		tryCsrf(FormType.FinishAccountReclaim, request, response);
		Account acc = AccountSQLHelper.find(getParameter("acc", request, response));
		String recl = request.getParameter("recl");
		if (AccountHandler.isPassGood(pass) && acc != null && acc.getValidationCode().contentEquals(recl)) {
			AccountSQLHelper.resetPassword(acc, pass);
			nonceAlertRedirect("Password Changed! Please sign in.", "/login", response);
		} else {
			nonceAlertRedirect("invalid input: your password may violate the constraints, or you may have taken too long to enter data. You will need to start again from the link in your email.", "/", response);
		}
	}
}
