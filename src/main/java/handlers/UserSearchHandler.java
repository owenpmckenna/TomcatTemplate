package handlers;

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
 * Servlet implementation class UserSearchHandler
 */
public class UserSearchHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserSearchHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		String nonce = Secure.ofNonce(response);
		tryPermission(ah, PermissionType.UsersEditPermissions, response);
		response.getWriter().append(HTMLHolder.generateUserSearchHTML(request.getParameter("query"), nonce));
	}

}
