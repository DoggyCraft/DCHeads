package dk.doggycraft.dcheads;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FreshCoalAPI
{
	private Heads	plugin = null;
	
	FreshCoalAPI(Heads p)
	{
		this.plugin = p;
	}
	
	public void load()
	{
		// Nothing to see here
	}
	
	public Map<Integer, List<String>> getHeadsOnSite(String searchKeyword) {
		try 
		{
			// setting up the POST URL
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("https://freshcoal.com/php_includes/search_query.php");

			// Request parameters and other properties
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("search", searchKeyword));
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			// Execute and get the response
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// let's do stuff
					String ServerResponse = EntityUtils.toString(entity);
					if(ServerResponse.contains("Minimum length is 3")) {
						return null;
					}
					else if(ServerResponse.contains("No results matching your search were found")) {
						return null;
					}
					else {
						// Converting output to JSON
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(ServerResponse);
						JSONArray json = (JSONArray) obj;
						
						// Setup for the map
						Map<Integer, List<String>> headMap = new HashMap<Integer, List<String>>();
						
						// Creating the map
						for (int i = 0; i < json.size(); ++i) {
							// Get data for each iteration
						    JSONObject singleobj = (JSONObject) json.get(i);
						    String name = (String) singleobj.get("HeadName");
							String texture = (String) singleobj.get("Url");
							
							String texture2 = "{\"textures\":{\"SKIN\":{\"url\":\"" + (String) texture + "\"}}}";
							
							// Get Base64 of texture...
							String encodedTexture = Base64.getEncoder().encodeToString(texture2.getBytes());
							
							// Test System.out.println("Current Iteration: " + i + ". Values: Name: " + name + " - Texture: " + encodedtexture);
							
							List<String> head = new ArrayList<String>();
							head.add(name);
							head.add(encodedTexture);
							
							headMap.put(i, head);
						}
						
						return headMap;
					}
					
					
				}
			} finally {
				response.close();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
		plugin.logDebug("Returning null from FreshCoalAPI...");
		return null;
	}
}
