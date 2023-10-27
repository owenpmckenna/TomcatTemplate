package roads.main;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import accounts.AccountHandler;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import sql.AccountSQLHelper;
import sql.CentralSQLHolder;
import sql.CsrfSQLHelper;

public class StartupListener implements ServletContextListener {
	public static final UUID SERVER = UUID.randomUUID();
	protected static final Date STARTUP = new Date();

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> handle;
	public static void pokeJDBC() {
		AccountSQLHelper.find("owenpmckenna@outlook.com");
		CsrfSQLHelper.csrfExists("nullCsrf", 0);
	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			sce.getServletContext().log("Beginning to launch RoadStatusJSPServer...");
			long time = System.currentTimeMillis();
			PropertiesManager.loadProperties();
			Thread.sleep(PropertiesManager.getPropLong("mariadbWait", 5000));
			AccountSQLHelper.setup();
			CsrfSQLHelper.setup();
			scheduler = Executors.newScheduledThreadPool(1);
			Runnable toRun = StartupListener::pokeJDBC;
			handle = scheduler.scheduleAtFixedRate(toRun, 180, 180, TimeUnit.SECONDS);
			try {
				AccountHandler admin = AccountHandler.createSuperAccount("Admin", PropertiesManager.getPropStringError("email").toCharArray(), PropertiesManager.getPropStringError("emailPass").toCharArray());
				admin.getValue().setValidated(true);
			} catch (Exception eee) {
				eee.printStackTrace();
			}
			long ms = (System.currentTimeMillis() - time);
			sce.getServletContext().log("RoadStatusJSPServer launched in [" + ms + "] milliseconds.");
		} catch (InterruptedException ie) { 
			Thread.currentThread().interrupt();
		} catch (Exception t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sce.getServletContext().log("Beginning to shutdown RoadStatusJSPServer...");
		long time = System.currentTimeMillis();
		try {
			CentralSQLHolder.endAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		handle.cancel(false);
		scheduler.shutdown();
		long ms = (System.currentTimeMillis() - time);
		sce.getServletContext().log("RoadStatusJSPServer shutdown in [" + ms + "] milliseconds.");
	}

}
