package handlers.hidden;

import java.io.IOException;

import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.Utilities;
import sql.AccountSQLHelper;

/**
 * Servlet implementation class HiddenEmailValidationHandler
 */
public class HiddenAccountReclaimHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenAccountReclaimHandler() {
		super(true);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		tryCsrf(FormType.StartAccountReclaim, request, response);
		Utilities.sendAccountReclaimEmail(AccountSQLHelper.find(getParameter("username", request, response)));
		nonceAlertRedirect("Email sent to address associated with account, if any.", "/login", response);
	}
}
