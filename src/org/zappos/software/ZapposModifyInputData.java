package org.zappos.software;
/**
 * @author Gaurav
 * This class modifies the data we got from the Zappos API
 * Contains methods to get the product list 
 * and hence the combinations of products based on input params.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.zappos.software.data.Product;

public class ZapposModifyInputData {
	private ArrayList<Product> productListToWork;
	private HashMap<Integer,Product> getUniqueProductIdMap;
	private HashMap<Double,Integer> getPriceProductCounterMap;
	private List<ArrayList<Product>> finalOutputList;
 	private ZapposGetAPIData zapposGetAPIData;
 	private JSONParser jParser;
 	
	public ZapposModifyInputData(){
		zapposGetAPIData = new ZapposGetAPIData();
		finalOutputList = new ArrayList<ArrayList<Product>>();
		productListToWork = new ArrayList<Product>();
		getUniqueProductIdMap = new HashMap<Integer, Product>();
		getPriceProductCounterMap = new HashMap<Double,Integer>();
		jParser = new JSONParser();
	}
	
	// gets map of product id and actual Product associated to it.
	public HashMap<Integer,Product> getUniqueProductIdMap(){
		return getUniqueProductIdMap;
	}
	
	// compute the product data based on zappos api search response, ranges and number of gifts
	public boolean computeData(String response, double startOfRange, double endOfRange, int numberOfGifts){
		boolean isEndOfRangeReached = false;
		try{
			if(!response.equals(null)){
				Object resultObject = jParser.parse(response);
				JSONObject resultJObject = (JSONObject) resultObject;
				
				JSONArray resultArray = (JSONArray) resultJObject.get("results");
				double lastPriceOfProduct = zapposGetAPIData.getPriceOfLastProductOnPage(resultArray);
				double productPrice = 0.0;
				
				int productId, styleId;
				String productName;
				
				//checks if the price of last product on the page is greater than startRange
				if(lastPriceOfProduct > startOfRange){
					for(Object resultObj: resultArray){
						JSONObject resultJObj = (JSONObject)resultObj;
						productPrice = Double.parseDouble(resultJObj.get("price").toString().substring(1));
						//the map below helps to store unique products in the input list
						int currentPriceCount = getPriceProductCounterMap.containsKey(productPrice)?getPriceProductCounterMap.get(productPrice):0;
						getPriceProductCounterMap.put(productPrice,currentPriceCount+1);
						// unique products can occur as the number of gifts in the input list
						if(getPriceProductCounterMap.get(productPrice)<= numberOfGifts){
							// if price of product is greater than start of range
							if(productPrice >= startOfRange){
								productId = Integer.parseInt(resultJObj.get("productId").toString());
								styleId = Integer.parseInt(resultJObj.get("styleId").toString());
								productName = resultJObj.get("productName").toString();
								Product product = new Product();
								product.setPrice(productPrice);
								product.setProductId(productId);
								product.setStyleId(styleId); 
								product.setProductName(productName);
								//update the map of productId and product data
								getUniqueProductIdMap.put(productId, product);
							}
						}
					}
				}
				//if product price is greater than the range end then stop product collection process.
				if(productPrice > endOfRange){
					isEndOfRangeReached = true;
				}
			}else{
				isEndOfRangeReached = false;
			}
		}catch(Exception e){
			
		}
		return isEndOfRangeReached;
	}
	
	// get all the possible combinations of products.
	public List<ArrayList<Product>> getPossibleCombinations(HashMap<Integer,Product> productList,int numberOfGifts,double totalTargetPrice){
		for(Entry<Integer, Product> entry:getUniqueProductIdMap.entrySet()){
			Product currentProduct = entry.getValue();
			productListToWork.add(currentProduct);
		}
		getOutput(new ArrayList<Product>(),numberOfGifts, totalTargetPrice,0);
		return finalOutputList;
	}
	
	// recursive function to get possible combinations.
	public void getOutput(ArrayList<Product> possibleProductList,int numberOfGifts, double totalTargetPrice,int currentCounter){  
        if(totalTargetPrice < 0 || numberOfGifts <= 0){
            return;
        }
        
        if(possibleProductList.size() == numberOfGifts){
            finalOutputList.add(possibleProductList);
            return;
        }
        
        for(int i=currentCounter; i<productListToWork.size();i++){
            Product currentProduct = productListToWork.get(i);
            ArrayList<Product> updatedPossibleProductList = new ArrayList<Product>(possibleProductList);
            if(possibleProductList.size() < numberOfGifts){
            	updatedPossibleProductList.add(currentProduct);
            	double currentPrice = currentProduct.getPrice();
            	getOutput(updatedPossibleProductList,numberOfGifts,(totalTargetPrice-currentPrice), ++currentCounter);   
            }
        }
	}
}
