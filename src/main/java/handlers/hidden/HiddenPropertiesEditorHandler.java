package handlers.hidden;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.Permissions.PermissionType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.PropertiesManager;

/**
 * Servlet implementation class HiddenUserPermissionEditor
 */
public class HiddenPropertiesEditorHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenPropertiesEditorHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		tryPermission(ah, PermissionType.PropertiesEdit, response);
		String key = getParameter("property", request, response);
		String value = getParameter("value", request, response);
		PropertiesManager.putPropString(key, value);
		nonceAlertRedirect("Property \\\"" + key + "\\\" set to \\\"" + value + "\\\".", "/properties-edit", response);
	}

}
