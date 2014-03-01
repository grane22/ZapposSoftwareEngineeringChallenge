package org.zappos.software;
/**
 * @author Gaurav
 * This class contains methods to get data from the Zappos API
 * 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ZapposGetAPIData {
	JSONParser jParser;
	int numOfItemsinRange; 
	private static final String PROBLEM = "Problem";
	
	public ZapposGetAPIData(){
		 jParser = new JSONParser();
	}
	
	//gets Number of Products in the particular Price range
	public int getNumOfItemsInRange(){
		return numOfItemsinRange;
	}
	
	// get HTTP response from the Zappos API
	public String getHTTPResponseFromZappos(String url){
		StringBuffer response = new StringBuffer();
		try {
			URL obj = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			
			if(responseCode == 200){
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(connection.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				if (connection != null) {
	                connection.disconnect();
	            }
			}else{
				response.append(PROBLEM);
			}
		}catch(Exception e){
			System.out.println("Got an exception at fetching Price range: "+ e);
		}
		return response.toString();
	}

	// get the actual price range based on the Target Cost 
	public String getActualRangeBasedOnInputCost(String response,double cost){
		String actualRange = null;
		try{
			JSONObject jObject = (JSONObject) jParser.parse(response);
			JSONArray priceFacetArray = (JSONArray) jObject.get("facets");
			
			JSONArray facetValuesArray = null;
			for(Object priceFacet: priceFacetArray){
				JSONObject priceObject = (JSONObject) priceFacet;
				facetValuesArray = (JSONArray)priceObject.get("values");	
			}
			
			if(cost <= 50.0){
				actualRange = "$50.00 and Under";
			}else if(cost > 50.0 && cost <= 100.0){
				actualRange = "$100.00 and Under";
			}else if(cost > 100.0 && cost <= 200.0){
				actualRange = "$200.00 and Under";
			}else if(cost >= 200.0){
				actualRange = "$200.00 and Over";
			}
				
			for(Object v : facetValuesArray){
				JSONObject value_obj = (JSONObject) v;
				if (value_obj.get("name").toString().equals(actualRange)){
					numOfItemsinRange = Integer.parseInt(value_obj.get("count").toString());
				}
			}
		}catch(Exception e){
			System.out.println("Error in getting actual range based on cost"+ e);
			actualRange = PROBLEM;
		}
		return actualRange;
	}
	
	// gets the JSONArray based on the response
	public JSONArray getResultJSONArray(String response){
		JSONArray resultArray = null;
		try{
			if(response!=null){
				Object responseObject = jParser.parse(response);
				JSONObject jResponseObject	= (JSONObject) responseObject;
				resultArray = (JSONArray) jResponseObject.get("results");
			}
		}catch(Exception e){
			System.out.println("Error in getting parsing the response"+ e);
		}
		return resultArray;
	}
	
	// get the price of first product on the search result page
	public double getPriceOfFirstProductOnPage(JSONArray resultArray){
		double price = 0;
		Object firstResult = resultArray.get(0);
		JSONObject firstResultJSON = (JSONObject) firstResult;
		price = Double.parseDouble(firstResultJSON.get("price").toString().substring(1));
		return price;
	}
	
	// get the price of last product on the search result page
	public double getPriceOfLastProductOnPage(JSONArray resultArray){
		double price = 0;
		int numberOfProductsOnPage = resultArray.size();
		Object lastProductResult = resultArray.get(numberOfProductsOnPage-1);
		JSONObject firstResultJSON = (JSONObject) lastProductResult;
		price = Double.parseDouble(firstResultJSON.get("price").toString().substring(1));
		return price;
	}
}
