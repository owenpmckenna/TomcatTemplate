package handlers.hidden;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.Permissions.PermissionType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HiddenUserPermissionEditor
 */
public class HiddenUserPermissionEditor extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenUserPermissionEditor() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		tryPermission(ah, PermissionType.UsersEditPermissions, response);
		AccountHandler viewing = AccountHandler.findAccount(getParameter("user", request, response));
		PermissionType permission = PermissionType.valueOf(getParameter("permission", request, response));
		if (viewing == null || permission == null) {
			nonceAlertRedirect("User or Permission not found.", "/user-controls", response);
		}
		boolean newValue = Boolean.parseBoolean(getParameter("newValue", request, response));
		viewing.setPermission(permission, newValue);
		nonceAlertRedirect("Permission " + permission.name() + " set to " + Boolean.toString(newValue) + " against user " + new String(viewing.getValue().getEmail()) + ".", "/user-controls/user?user=" + new String(viewing.getValue().getEmail()), response);
	}

}
