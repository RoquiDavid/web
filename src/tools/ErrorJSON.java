package tools;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import bd.DBStatic;

public class ErrorJSON {
	
	/**
	 * Returns a JSONObject that contains the specified message and the specified error code
	 * @param the message which will be in the JSONObject paired with the key "message"
	 * @param the error code which will be in the JSONObject paired with the key "error"
	 * @return a JSONObject with the given parameters 
	 * @throws JSONException that shouldn't happen
	 */
	public static JSONObject serviceRefused(String message, int codeErreur) throws JSONException {
		JSONObject retour = new JSONObject();
		try {
			retour.put("message", message);
			retour.put("error", codeErreur);
		} catch (JSONException e) {
			e.printStackTrace();
			retour.put("error", DBStatic.json_error);
			retour.put("message", "JSON error");
		}
		return retour;
	}
	
	/**
	 * Returns an empty JSONObject
	 * @return an emptyJSONObject
	 */
	public static JSONObject serviceAccepted() {
		return new JSONObject();
	}
	
	
	/**
	 * Returns a JSONObject that contains a pair (key, value) initialized with the given arguments
	 * @param key the key of the pair that will be in the JSONObject
	 * @param value the value which will be paired with the key
	 * @return a JSONObject that contains a pair (key, value) initialized with the given arguments
	 * @throws JSONException that shouldn't happen
	 */
	public static JSONObject serviceAccepted(String key, Object value) throws JSONException {
		JSONObject retour = new JSONObject();
		try {
			retour.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
			retour = ErrorJSON.serviceRefused(e.getMessage(), DBStatic.json_error);
		}
		return retour;
	}
	
	public static void addToJSON(String key, Object value, JSONObject json) throws JSONException {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
			json = serviceRefused(e.getMessage(), DBStatic.json_error);
		}
	}
	
	public static JSONObject exceptionHandler(Exception e) throws JSONException {
		JSONObject output = null;
		if(e instanceof SQLException) {
			return ErrorJSON.serviceRefused("SQL exception", DBStatic.sql_error);
		} else if (e instanceof JSONException) {
			return ErrorJSON.serviceRefused("JSON exceptiobn", DBStatic.json_error);
		} else {
			return ErrorJSON.serviceRefused("JAVA exception", DBStatic.java_error);
		}
	}
}
