package com.telkomsel.login.login01.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@ConfigurationProperties
@Validated
public class Login {
	
@Autowired 
	
	//get parameter from application.properties
	@Value("${api_key}")
    @NotEmpty
    private String api_key;
	
	@Value("${api_secret}")
    @NotEmpty
    private String api_secret;
	
	@Value("${api_url}")
    @NotEmpty
    private String api_url;
	
public String login(String username, String password) {
		
		String result = "000";

		JSONObject requestBody = constructSMSBody(username, password);
		System.out.println("Request body = " + requestBody.toString());

		// Generate signature
		String x_signature = getXSignature(api_key, api_secret);
		System.out.println("X-Signature = " + x_signature);

		// Response variable
		String mashery_response = null;
		int mashery_response_code = 0;

		try {
			URL obj = new URL(api_url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("api_key", api_key);
			con.setRequestProperty("x-signature", x_signature);
			// con.setReadTimeout(mashery_timeout);
			con.setDoOutput(true);

			// Send post request
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(requestBody.toString());
			wr.flush();
			wr.close();

			mashery_response_code = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			mashery_response = response.toString();

			Map<String, List<String>> map = con.getHeaderFields();
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				// System.out.println("Key : " + entry.getKey()
				// + " ,Value : " + entry.getValue());
				//System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		result = "Resp Code : " + mashery_response_code + ", Resp : " + mashery_response;
		result = this.getSessionID(mashery_response);

		return result;
	}
	
	private static JSONObject constructSMSBody(String username, String password) {

		JSONObject login_body = new JSONObject();
		login_body.put("username", username);
		login_body.put("password", password);

		return login_body;
	}
	
	private static String getXSignature(String api_key, String api_secret) {
		// System.out.println(api_key + api_secret +
		// Long.toString(System.currentTimeMillis() / 1000L));
		return DigestUtils.sha256Hex(api_key + api_secret + Long.toString(System.currentTimeMillis() / 1000L));
	}
	
	private String getSessionID(String json) {
	    
		String SessionID = "";
		
	    JSONObject obj = new JSONObject(json);
	    SessionID = obj.getString("session_id");

	    return SessionID;
	    
	}
}
