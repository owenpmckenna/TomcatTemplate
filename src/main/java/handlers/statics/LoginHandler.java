package handlers.statics;

import java.io.IOException;

import csrf.CSRFHelper;
import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;

/**
 * Servlet implementation class LoginHandler
 */
public class LoginHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String nonce = Secure.ofNonce(response);
		response.getWriter().append(HTMLHolder.generateLoginHTML(CSRFHelper.addCsrf(FormType.Login), nonce));
	}
}
