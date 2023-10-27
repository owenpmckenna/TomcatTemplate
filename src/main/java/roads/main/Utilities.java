package roads.main;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import accounts.Account;
import accounts.AccountHandler;

public class Utilities {
	public static final Logger DEBUG_LOG = Logger.getLogger("debug");
	public static final PrintWriter DEBUG_PW =  new PrintWriter(OutputStream.nullOutputStream()) {
		@Override
		public void print(String s) {
			Utilities.DEBUG_LOG.severe(s);
		}
		@Override
		public void print(Object s) {
			Utilities.DEBUG_LOG.severe(s.toString());
		}
		@Override
		public void println(String s) {
			Utilities.DEBUG_LOG.severe(s);
		}
		@Override
		public void println(Object s) {
			Utilities.DEBUG_LOG.severe(s.toString());
		}
	};
	private Utilities() {}
	public static String safe(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}
	public static String unsafe(String s) {
		return URLDecoder.decode(s, StandardCharsets.UTF_8);
	}
	public static final Executor exe = Executors.newCachedThreadPool();
	public static void sendAccountValidateEmail(AccountHandler ah) {
		String link = PropertiesManager.getPropStringError("site") + "/validate-account?val=" + ah.getValue().getValidationCode();
		String msg ="Press the link below to validate your account.\r\n"
				+ "This Message is Auto-Generated.\r\n"
				+ link;
		exe.execute(() -> {
				try {
					Mailer.mail(msg ,new String(ah.getValue().getEmail()),"Validate your account");
				} catch (Exception t) {
					Mailer.LOG.error("An error occured while sending mail.", t);
				}
			});
	}
	public static void sendAccountReclaimEmail(Account user) {
		if (user == null) {
			return;
		}
		String link = PropertiesManager.getPropStringError("site") + "/account-reclaim-gui?acc=" + safe(new String(user.getEmail())) + "&recl=" + user.getValidationCode();
		String msg = "Press the link below to reset your account password.\r\n"
				+ "If you didn't mean to get this email, ignore it.\r\n"
				+ "This message is auto-generated.\r\n"
				+ link;
		exe.execute(() -> {
				try {
					Mailer.mail(msg, new String(user.getEmail()), "Reset Account Password");
				} catch (Exception t) {
					Mailer.LOG.error("An error occured while sending mail.", t);
				}
			});
	}
}
