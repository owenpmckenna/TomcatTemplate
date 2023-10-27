package roads.main;

import java.io.IOException;

import jakarta.servlet.ServletException;

public class Timing {
	public interface ExecuteServerlet {
		void runServerlet() throws ServletException, IOException;
	}
	
	
	public static void safeTimeShort(ExecuteServerlet ex) throws ServletException, IOException {
		ensureTime(ex, 200);
	}
	public static void safeTimeLong(ExecuteServerlet ex) throws ServletException, IOException {
		ensureTime(ex, 1750);
	}
	
	private static boolean timeUp(long start, long to) {
		return (System.nanoTime()-start) > to;
	}
	public static void ensureTime(ExecuteServerlet ex, long timems) throws ServletException, IOException {
		long starttime = System.currentTimeMillis();
		ex.runServerlet();
		if (System.currentTimeMillis()-starttime > timems) {
			new Exception("Time overflow of " + (System.currentTimeMillis()-starttime-timems) + "!!!").printStackTrace();
		}
		long timeleft = timems-(System.currentTimeMillis()-starttime);
		try {
			Thread.sleep(timeleft);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
