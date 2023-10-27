package handlers.hidden;

import accounts.AccountHandler;
import csrf.CSRFHelper.FormType;
import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

/**
 * Servlet implementation class HiddenSetMessageTimesHandler
 */
public class HiddenSetMessageTimesHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HiddenSetMessageTimesHandler() {
		super(true);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(true){throw new NotImplementedException();}

		AccountHandler ah = null;if((ah=AccountHandler.checkAccount(request,response))==null){return;}
		tryCsrf(FormType.UserSettings, request, response);
		//TODO replace this w/ ics file loc!
		//if (!RoadSQLHelper.query().containsKey(getParameter("road", request, response))) {
		//	nonceAlertRedirect("Error, no road found", "/", response);
		//}
		ah.getValue().setMessageTimes(getParameter("road", request, response));
		redirect("/account", response);
	}
}
