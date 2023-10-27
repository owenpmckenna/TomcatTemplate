package handlers.hidden;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.AuthenticationFailure;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HiddenEmailValidationHandler
 */
public class HiddenEmailValidationHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenEmailValidationHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		AccountHandler ah = null;
		try {
			ah = AccountHandler.getAccount(request.getCookies());
			boolean validated = ah.tryValidate(request.getParameter("val"));
			if (validated) {
				redirect("/", response);
			} else {
				nonceAlertRedirect("Validation failed. If this happens again, contact the system administrator.", "/login", response);
			}
		} catch (AuthenticationFailure af) {
			nonceAlertRedirect("Please sign in before pressing the link (sorry).", "/login", response);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
