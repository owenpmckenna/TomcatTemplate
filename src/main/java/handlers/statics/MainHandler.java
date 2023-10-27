package handlers.statics;

import java.io.IOException;

import accounts.AccountHandler;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.PropertiesManager;
import roads.main.Secure;

/**
 * Servlet implementation class MainHandler
 */
public class MainHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 166L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainHandler() {
		super(false);

		// not sure what to do here, use init instead.
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (PropertiesManager.getPropBool("mainPageRequireLogin", true)) {
			@SuppressWarnings("unused")
			AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		}
		String nonce = Secure.ofNonce(response);
		response.getWriter().append(HTMLHolder.generateBaseHTML(nonce));
	}
}
