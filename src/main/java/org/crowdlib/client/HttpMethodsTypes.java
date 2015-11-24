package org.crowdlib.client;

public enum HttpMethodsTypes {

	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD"), OPTION("OPTION");

	private String text;

	HttpMethodsTypes(String text) {
		this.text = text;
	}

	public static HttpMethodsTypes fromString(String text) {
		if (text != null) {
			for (HttpMethodsTypes m : HttpMethodsTypes.values()) {
				if (text.equalsIgnoreCase(m.text)) {
					return m;
				}
			}
		}
		return null;
	}
}
