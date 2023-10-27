package roads.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
/**
 * This should only be used for not often updated parameters. Every update involves opening a file.
 */
public class PropertiesManager {
	private PropertiesManager() {}
	public static final Logger LOG = Logger.getLogger("PropertiesManager");
	public static class PropertyNotFoundException extends RuntimeException {
		public PropertyNotFoundException(String key) {
			super("Key not found: \"" + key + "\".");
		}
		private static final long serialVersionUID = -7737780370790794083L;
	}
	private static String checkNonNull(String s, String key) {
		if (s != null && key != null) {
			return s;
		}
		LOG.severe(() -> "Key not found: \"" + key + "\".");
		throw new PropertyNotFoundException(key);
	}
	private static String throwss(String key) {
		LOG.severe(() -> "Key not found: \"" + key + "\".");
		throw new PropertyNotFoundException(key);
	}
	private static String getOrDefault(String key, String def) {
		return properties.getOrDefault(key, def);
	}
	private static String getOrError(String key) {
		String ret = properties.get(key);
		return ret == null ? (throwss(key)) : ret;
	}
	private static String put(String key, String data) {
		return properties.put(key, data);
	}
	public static boolean keyExists(String key) {
		return properties.containsKey(key);
	}
	private static void envOrDef(Properties p, String key, String val) {
		if (System.getenv("WEBAPP_" + key.toUpperCase()) != null) {
			p.put(key, System.getenv("WEBAPP_" + key.toUpperCase()));
		} else {
			p.put(key, val);
		}
	}
	public static String conv(Map<String, String> map) {
	    StringBuilder mapAsString = new StringBuilder("{");
	    for (Entry<String, String> entry : map.entrySet()) {
	        mapAsString.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
	    }
	    mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
	    return mapAsString.toString();
	}
	private static ConcurrentHashMap<String, String> properties = null;
	public static final File F = new File(System.getenv("WEBAPP_PROPERTIES"), "roadstatus.properties");
	public static void loadProperties() {
		Properties defaults = new Properties();
		try {
			if (F.getParentFile() != null) {
				F.getParentFile().mkdirs();
			}
			if (F.createNewFile()) {
				LOG.info("Properties File Created!");
				envOrDef(defaults, "site", "smroads.com");//needed
				envOrDef(defaults, "noOfForums", "5");
				envOrDef(defaults, "postActionDelay", "15000");
				envOrDef(defaults, "phoneActionDelay", "30000");
				envOrDef(defaults, "mainPageRequireLogin", "true");
				envOrDef(defaults, "validHosts", "");
				envOrDef(defaults, "plausibleDomain", "");//needed
				envOrDef(defaults, "alertBanner", "");
				envOrDef(defaults, "mainTitle", "SMRoads");//needed
				envOrDef(defaults, "aboutTitle", "About Us");
				envOrDef(defaults, "aboutEmail", "");//needed
				envOrDef(defaults, "aboutText", "");//needed
				envOrDef(defaults, "showAds", "true");
				envOrDef(defaults, "email", "");//needed
				envOrDef(defaults, "phone", "");//needed
				envOrDef(defaults, "emailPass", "");//needed
				envOrDef(defaults, "forumModEmails", "");//needed
				envOrDef(defaults, "roadsConfirmEmails", "");//needed
				envOrDef(defaults, "metaDescription", "");//needed    //"An independent and local Signal Mountain site that includes forums and can be used to quickly find out if Roberts Mill, The W Road, and Signal Mountain Blvd are open as an alternative to Facebook."
				envOrDef(defaults, "sublimeCode", "RPD4ovXGda_MaA");//needed

				envOrDef(defaults, "firstRun", "true");//needed
				envOrDef(defaults, "mariadbWait", "10000");
				envOrDef(defaults, "adminName", "");//all below needed on first run
				envOrDef(defaults, "adminEmail", "");
				envOrDef(defaults, "adminPass", "");
				envOrDef(defaults, "roads", "");//a "|" seperated list of roads to create --- TODO fix later, add dynamic road settings like with forums 
				ConcurrentHashMap<String, String> props = new ConcurrentHashMap<>();
				for (Entry<Object, Object> entry : defaults.entrySet()) {
					props.put((String)entry.getKey(), (String)entry.getValue());
				}
				properties = props;//this should be called first-thing
				return;
			}
		} catch (Exception e) {
			LOG.severe(e.toString());
			e.printStackTrace();
		}
		Properties p = new Properties(defaults);
		try (FileInputStream fis = new FileInputStream(F)) {
			p.load(fis);
		} catch (Exception e) {
			LOG.severe(e.toString());
			e.printStackTrace();
		}
		ConcurrentHashMap<String, String> props = new ConcurrentHashMap<>();
		for (Entry<Object, Object> entry : p.entrySet()) {
			props.put((String)entry.getKey(), (String)entry.getValue());
		}
		properties = props;//this should be called first-thing
	}
	private static final String c = "Persistant Properties List"
			+ " (might need sql later, but that would increase over head by a bunch,"
			+ " so, idk, i'll figure it out later.) - Owen at 4:07 PM, Friday, Oct 14, 22 (working on version 1.13)";
	public static void writeProperties() {
		try {
			if (F.createNewFile()) {
				LOG.info("Properties File Created!");
				LOG.info("Properties file created at " + F.getAbsolutePath());
			}
		} catch (Exception e) {
			LOG.severe(e.toString());
			e.printStackTrace();
		}
		try (FileOutputStream fos = new FileOutputStream(F)) {
			Properties props = new Properties();
			props.putAll(properties);
			props.store(fos, c);
		} catch (Exception e) {
			LOG.severe(e.toString());
			e.printStackTrace();
		}
	}

	public static String getPropString(String key, String def) {
		return getOrDefault(key, def);
	}
	public static String getPropStringError(String key) {
		return getOrError(key);
	}
	public static void putPropString(String key, String data) {
		put(key, data);
		writeProperties();
	}
	public static long getPropLong(String key, long def) {
		try {
			return Long.parseLong(getOrDefault(key, Long.toString(def)));
		} catch (NumberFormatException | NullPointerException npe) {
			LOG.severe("Key \"" + key + "\" does not contain a parsable Long.");
			return def;
		}
	}
	public static long getPropLongError(String key) {
		try {
			return Long.parseLong(getOrError(key));
		} catch (NumberFormatException | NullPointerException npe) {
			LOG.severe("Key \"" + key + "\" does not contain a parsable Long.");
			throw npe;
		}
	}
	public static void putPropLong(String key, long data) {
		put(key, Long.toString(data));
		writeProperties();
	}
	public static boolean getPropBool(String key, boolean def) {
		String s = checkNonNull(getOrDefault(key, def ? "true" : "false"), key);
		if (s.equalsIgnoreCase("true")) {
			return true;
		} else if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			return def;
		}
	}
	public static boolean getPropBoolError(String key) {
		String s = checkNonNull(getOrError(key), key);
		if (s.equalsIgnoreCase("true")) {
			return true;
		} else if (s.equalsIgnoreCase("false")) {
			return false;
		} else {
			LOG.severe(() -> "Key \"" + key + "\" does not contain a parsable boolean value.");
			throw new PropertyNotFoundException("Key \"" + key + "\" dosen't contain a parsable boolean value.");
		}
	}
	public static void putPropBool(String key, boolean data) {
		put(key, Boolean.toString(data));
		writeProperties();
	}
	public static void wipeProperty(String key) {
		properties.remove(key);
		writeProperties();
	}
	public static String type(String data) {
		boolean dataIsNum;
		try {
			Long.valueOf(data);
			dataIsNum = true;
		} catch (Exception e) {
			dataIsNum = false;
		}
		if (dataIsNum) {
			return "Number";
		} else {
			return data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false") ? "Boolean" : "String";
		}
	}
	public static String toString(String key) {
		String data = getOrError(key);
		String type = type(data);
		return "Property: \"" + key + "\" of type " + type + " is set to \"" + data + "\".";
	}
	public static String[] list() {
		return properties.keySet().toArray(new String[0]);
	}
	public static String[] listFull() {
		String[] strs = properties.keySet().toArray(new String[0]);
		for (int i = 0; i < strs.length; i++) {
			strs[i] = toString(strs[i]);
		}
		return strs;
	}
	public static String html(String[] strs, String type, String cl) {
		StringBuilder end = new StringBuilder();
		for (String s : strs) {
			end.append("<").append(type).append(cl != null && !cl.contentEquals("") ? " class=\"" + cl + "\"" : "").append(">").append(HtmlEncode.htmlEncode(s, true)).append("</").append(type).append(">\r\n");
		}
		return end.toString();
	}
	public static String[] search(String search) {
		String[] strs = null;
		strs = properties.keySet().toArray(new String[0]);
		ArrayList<String> strs0 = new ArrayList<>();
		for (String str : strs) {
			if (str.toLowerCase().contains(search.toLowerCase())) {
				strs0.add(str);
			}
		}
		return strs0.toArray(new String[0]);
	}
	public static String[] searchFull(String key) {
		String[] strs = search(key);
		for (int i = 0; i < strs.length; i++) {
			strs[i] = toString(strs[i]);
		}
		return strs;
	}
}
