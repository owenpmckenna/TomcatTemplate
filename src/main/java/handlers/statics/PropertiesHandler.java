package handlers.statics;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.Permissions.PermissionType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;

/**
 * Servlet implementation class AboutHandler
 */
public class PropertiesHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PropertiesHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		tryPermission(ah, PermissionType.PropertiesEdit, response);
		String nonce = Secure.ofNonce(response);
		response.getWriter().append(HTMLHolder.generatePropertiesHTML(nonce));
	}

}
