package roads.main;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

public class Secure {
	private Secure() {}
	private static void normal(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		response.addHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("Report-To", "{ \"group\": \"CSP-endpoint\","
				+ "\"max_age\": 10886400,"
				+ "\"endpoints\": ["
				+ "{ \"url\": \"/report-CSP-violations/\" },"
				+ "] }");
		response.setHeader("X-Frame-Options", "DENY");
	}
	public static final RandomString NONCE_GEN = new RandomString(25);
	public static String ofNonce(HttpServletResponse response) {
		String nonce = NONCE_GEN.nextString();
		normal(response);
		List<String> s1 = new ArrayList<>();
		s1.add("default-src 'self'");
		s1.add("connect-src 'self' plausible.io embed.sublimeads.com sublimeads.com assets.sublimeads.com");
		s1.add("script-src 'nonce-" + nonce +  "'");
		s1.add("style-src 'self' 'nonce-" + nonce +  "'");
		s1.add("report-uri /report-CSP-violations/");
		s1.add("object-src 'none'");
		s1.add("form-action 'self'");
		s1.add("media-src 'none'");
		s1.add("frame-ancestors 'none'");
		s1.add("img-src 'self' assets.sublimeads.com sublimeads.com");
		s1.add("child-src 'none'");
		s1.add("report-to CSP-endpoint");
		s1.add("navigate-to 'self'");
		s1.add("base-uri 'self'");
		StringBuilder sb = new StringBuilder();
		for (String st : s1) {
			sb.append(st).append(";");
		}
		response.setHeader("Content-Security-Policy", sb.toString());
		return nonce;
	}
	public static void ofDefense(HttpServletResponse response) {
		ofLockdownScript(response);
	}
	public static void ofLockdownScript(HttpServletResponse response) {
		normal(response);
		List<String> s1 = new ArrayList<>();
		s1.add("default-src 'none'");
		s1.add("connect-src 'none'");
		s1.add("script-src 'none'");
		s1.add("report-uri /report-CSP-violations/");
		s1.add("object-src 'none'");
		s1.add("form-action 'none'");
		s1.add("media-src 'none'");
		s1.add("frame-ancestors 'none'");
		s1.add("img-src 'none'");
		s1.add("child-src 'none'");
		s1.add("report-to CSP-endpoint");
		s1.add("navigate-to 'self'");
		s1.add("base-uri 'self'");
		StringBuilder sb = new StringBuilder();
		for (String st : s1) {
			sb.append(st).append(";");
		}
		response.setHeader("Content-Security-Policy", sb.toString());
	}
	public static void ofResource(HttpServletResponse response, String type) {
		ofLockdownScript(response);
		response.setCharacterEncoding("UTF-8");
		response.setContentType(type);
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("X-Content-Type-Options", "nosniff");
	}
}
