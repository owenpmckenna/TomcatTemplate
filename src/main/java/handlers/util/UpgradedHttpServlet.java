package handlers.util;

import java.io.IOException;

import accounts.AccountHandler;
import accounts.Permissions.PermissionType;
import csrf.CSRFHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;
import roads.main.Timing;
import sql.CentralSQLHolder;

/**
 * Servlet implementation class UserSearchHandler
 */
public class UpgradedHttpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static class RuntimeServerletExit extends RuntimeException {
		public RuntimeServerletExit() {}
		private int sc = -1;
		private boolean redir;
		String alert = "";
		public int getSC() {
			return sc;
		}
		public boolean getRedir() {
			return redir;
		}
		public String getAlert() {
			return alert;
		}
		public RuntimeServerletExit(int sc, String string, boolean redir) {
			super(string);
			this.sc = sc;
			this.redir = redir;
		}
		public RuntimeServerletExit(int sc, String string, String alert) {
			super(string);
			this.sc = sc;
			this.redir = true;
			this.alert = alert;
		}

		private static final long serialVersionUID = -6873114537352409748L;
	}
	long mstimeallowed = 0;
	public UpgradedHttpServlet(boolean time) {
		super();
		mstimeallowed = time ? 1750 : 200;
	}
	public boolean isInt(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
	}
	public void nonceAlertRedirect(String alert, String location, HttpServletResponse resp) throws IOException {
		String nonce = Secure.ofNonce(resp);
		resp.getWriter().append(HTMLHolder.generateRedirectAndAlertHtml(alert, location, nonce));
		throw new RuntimeServerletExit();
	}
	public void redirect(String location, HttpServletResponse resp) throws IOException {
		Secure.ofLockdownScript(resp);
		resp.sendRedirect(location);
		throw new RuntimeServerletExit();
	}
	/**
	 * This method will send the message "Invalid request." and redirect to "/"
	 *
	 */
	public String getParameter(String parameter, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		return getParameter(parameter, "Invalid request.", req, resp);
	}
	public int getParameterInt(String parameter, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		return getParameterInt(parameter, "Invalid request.", req, resp);
	}
	public String getParameter(String parameter, String alert, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		return getParameter(parameter, alert, "/", req, resp);
	}
	public int getParameterInt(String parameter, String alert, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		return getParameterInt(parameter, alert, "/", req, resp);
	}
	public String getParameter(String parameter, String alert, String loc, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String p = req.getParameter(parameter);
		if (p == null || p.length() == 0) {
			nonceAlertRedirect(alert, loc, resp);
		}
		return p;
	}
	public int getParameterInt(String parameter, String alert, String loc, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String p = req.getParameter(parameter);
		if (p == null || p.length() == 0 || !isInt(p)) {
			nonceAlertRedirect(alert, loc, resp);
		}
		return Integer.parseInt(p);
	}
	public boolean tryCsrf(String parameter, CSRFHelper.FormType type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String p = req.getParameter(parameter);
		if (p == null || p.length() == 0 || !CSRFHelper.isCsrfValid(p, type)) {
			nonceAlertRedirect("CSRF token invalid. You may have taken too long to enter data. If the problem persists, please contact the site administrators.", "/", resp);
			return false;
		}
		return true;
	}
	public boolean tryCsrf(CSRFHelper.FormType type, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		return tryCsrf("csrf", type, req, resp);
	}
	public boolean tryPermission(AccountHandler ah, PermissionType pt, HttpServletResponse resp) throws IOException {
		if (ah == null || !ah.getPermission(pt)) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			nonceAlertRedirect("You do not posess this permission.", "/", resp);
			return false;
		}
		return true;
	}
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Timing.ensureTime(() -> {
			try {
				String method = req.getMethod();
				switch (method) {
					case "GET":
						doGet(req, resp);
						break;
					case "HEAD":
						doHead(req, resp);
						break;
					case "POST":
						doPost(req, resp);
						break;
					case "PUT":
						doPut(req, resp);
						break;
					case "DELETE":
						doDelete(req, resp);
						break;
					case "OPTIONS":
						doOptions(req, resp);
						break;
					case "TRACE":
						doTrace(req, resp);
						break;
					default:
						resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Error: invalid http request.");
						break;
				}
			} catch (RuntimeServerletExit rse) {
				//This allows Servlet code to return easily, and increases readability.
				if (rse.getSC() != -1) {
					resp.setStatus(rse.getSC());
					if (rse.redir) {
						if (rse.getAlert() != null) {
							nonceAlertRedirect(rse.getAlert(), rse.getMessage(), resp);
						} else {
							redirect(getServletInfo(), resp	);
						}
					} else {
						resp.getWriter().append(rse.getMessage());
					}
				}
			} finally {
				CentralSQLHolder.close();
			}
		}, mstimeallowed);
	}
}
