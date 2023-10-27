package handlers.statics;

import java.io.IOException;

import handlers.util.UpgradedHttpServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roads.main.HTMLHolder;
import roads.main.Secure;

/**
 * Servlet implementation class RobotsHandler
 */
public class RobotsHandler extends UpgradedHttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RobotsHandler() {
		super(false);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Secure.ofResource(response, "text/plain");
		response.getWriter().append(HTMLHolder.generateRobots());
	}

}
