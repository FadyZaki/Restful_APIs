package org.crowdlib.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

import org.crowdlib.exceptions.IllegalRequestFormatException;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ning.http.client.Response.ResponseBuilder;

/**
 * Example of a client implementation.
 */
public class RestfulApiClient {

	private static Client client = ClientBuilder.newClient();
	public static final URI BASE_URI = URI.create("http://localhost:9998/");
	private static final String SEMICOLON = ";";

	public RestfulApiClient() {

	}

	/**
	 * Main method makes a single authenticated request.
	 */
	public static void main(final String[] args) {
		RestfulApiClient restfulApiClient = new RestfulApiClient();
		restfulApiClient.listenToRequests();
	}

	/**
	 * This method just takes input from user and delegate the processing
	 * to handleRequests Method
	 */
	public void listenToRequests(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Please enter your username:");
			String username = br.readLine();
			System.out.println("Please enter your password:");
			String password = br.readLine();
			client.register(HttpAuthenticationFeature.basic(username, password));
			while (true) {
				System.out.println("Press Enter to start a new request ..");
				br.readLine();
				String currentLine = new String("");
				List<String> headers = new ArrayList<String>();
				String body = new String("");
				String clientRequest = new String("");
				List<String> queryParams = new ArrayList<String>();
				System.out.println("Please Enter your request : (eg. Get items/1 )");
				clientRequest = br.readLine();
				System.out.println(
						"Specify your request headers each on a new line (write a semicolon on a new line to end your headers) eg.\n"
								+ "Content-Type: application/json\n" + ";");
				while (!((currentLine = br.readLine()).equals(SEMICOLON))) {
					headers.add(currentLine);
				}

				System.out.println("Specify your request body:");
				body = br.readLine();

				System.out.println(
						"Specify your request query parameters each on a new line (write a semicolon on a new line to end your parameters):");
				while (!((currentLine = br.readLine()).equals(SEMICOLON))) {
					queryParams.add(currentLine);
				}
				handleRequest(clientRequest, headers, body, queryParams);

			}
		} catch (

		IOException e)

		{
			System.err.println("Problem reading client input");
		}
	}

	public void handleRequest(String request, List<String> headers, String body, List<String> queryParams) {

		try {

			String[] requestParts = request.split(" ");
			if(requestParts == null || requestParts.length != 2) throw new IllegalRequestFormatException();
			String requestURI = requestParts[1];
			if(requestURI== null)  throw new IllegalRequestFormatException();
			WebTarget requestTarget = client.target(BASE_URI).path(requestURI);
			System.out.println(requestTarget.toString());

			String requestMethod = requestParts[0];
			if(requestMethod == null)  throw new IllegalRequestFormatException();
			else {
				HttpMethodsTypes requestMethodType = HttpMethodsTypes.fromString(requestMethod);
				if(requestMethodType == null) throw new IllegalRequestFormatException();
				else {
					for (String queryParam : queryParams) {
						String[] queryParts = queryParam.split("=");
						if(queryParts == null || queryParts.length != 2) throw new IllegalRequestFormatException(); 
						if(queryParts[0] == null || queryParts[1] == null) throw new IllegalRequestFormatException(); 
						requestTarget = requestTarget.queryParam(queryParts[0].trim(), queryParts[1].trim());
						System.out.println(requestTarget.toString());
					}
					Invocation.Builder requestBuilder = requestTarget.request(MediaType.APPLICATION_JSON_TYPE);
					for (String header : headers) {
						String[] headerParts = header.split(":");
						if(headerParts == null || headerParts.length != 2) throw new IllegalRequestFormatException();
						if(headerParts[0] == null || headerParts[1] == null) throw new IllegalRequestFormatException();
						requestBuilder.header(headerParts[0].trim(), headerParts[1].trim());
					}

					Response response;
					switch (requestMethodType) {
					case GET:
						response = requestBuilder.get();
						break;
					case POST:
						response = requestBuilder.post(Entity.entity(body, MediaType.TEXT_PLAIN_TYPE));
						break;
					case PUT:
						response = requestBuilder.put(Entity.entity(body, MediaType.TEXT_PLAIN_TYPE));
						break;
					case DELETE:
						response = requestBuilder.delete();
						break;
					default:
						response = requestBuilder.get();
						break;
					}
					System.out.println("Response Headers:");
					for (Map.Entry<String, List<String>> entry : response.getStringHeaders().entrySet()) {
						System.out.println(entry.getKey() + ":" + entry.getValue().get(0));
					}
					int responseStatus = response.getStatus();
					System.out.println("Response Status: " + responseStatus);
					System.out.println("Response Body:");
					String responseBody = response.readEntity(String.class);
					if (response.getHeaderString("content-type") != null
							&& response.getHeaderString("content-type").equals(MediaType.APPLICATION_JSON)) {
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						JsonParser jp = new JsonParser();
						JsonElement je = jp.parse(responseBody);
						String responseInTreeFormat = gson.toJson(je);
						System.out.println(responseInTreeFormat);
					} else {
						System.out.println(responseBody);
					}
				}
			}
		}catch (IllegalRequestFormatException e) {
			System.out.println(e.getMessage());
		}catch (Exception e) {
			System.out.println("Either your request/header format is wrong or the server is not available");
		}
		

	}

}
