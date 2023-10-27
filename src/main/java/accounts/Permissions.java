package accounts;

import java.util.HashMap;
import java.util.Map.Entry;

public class Permissions {
	public static enum PermissionType {
		UsersEditPermissions,
		PropertiesEdit
	}
	private Permissions() {
		permissions = new HashMap<>();
	}
	private HashMap<PermissionType, Boolean> permissions;
	public boolean getPermission(PermissionType per) {
		return permissions.get(per) != null ? permissions.get(per) : false;
	}
	public Boolean setPermission(PermissionType per, boolean value) {
		return permissions.put(per, value);
	}
	
	public static Permissions normalUser() {
		Permissions p = new Permissions();
		p.permissions.put(PermissionType.UsersEditPermissions, false);
		p.permissions.put(PermissionType.PropertiesEdit, false);
		return p;
	}
	public static Permissions superUser() {
		Permissions p = new Permissions();
		p.permissions.put(PermissionType.UsersEditPermissions, true);
		p.permissions.put(PermissionType.PropertiesEdit, true);
		return p;
	}
	public String toString() {
		String s = "";
		for (Entry<PermissionType, Boolean> ent: permissions.entrySet()) {
			s = s + ent.getKey().name() + ":" + ent.getValue().toString() + ";";
		}
		return s;
	}
	public static Permissions ofString(String string) {
		Permissions p = new Permissions();
		for (String s : string.split(";")) {
			if (!s.contentEquals("")) {
				PermissionType pt = PermissionType.valueOf(s.split(":")[0]);
				boolean b = Boolean.parseBoolean(s.split(":")[1]);
				p.permissions.put(pt, b);
			}
		}
		for (PermissionType pt : PermissionType.values()) {
			if (!p.permissions.containsKey(pt)) {
				p.permissions.put(pt, false);
			}
		}
		return p;
	}
}
