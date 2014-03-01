package org.zappos.software.range;
/**
 * @author Gaurav
 * This class contains all the utility methods required for this project
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zappos.software.ZapposGetAPIData;
import org.zappos.software.data.Product;

public class ZapposUtilityFunction {
	public HashMap<String, Integer> RangePageNumbersMap;
	public ZapposGetAPIData zapposGetAPIData;
	private static final String zapposAPIKey = "b05dcd698e5ca2eab4a0cd1eee4117e7db2a10c4";
	
	public ZapposUtilityFunction(){
		zapposGetAPIData = new ZapposGetAPIData();
		RangePageNumbersMap = new HashMap<String,Integer>();
	}
	
	// created a manual map of price range and the number of pages it contains
	public void updateRangePageNumbersMap(){
		RangePageNumbersMap.put("$50.00 and Under", 458);
		RangePageNumbersMap.put("$100.00 and Under", 936);
		RangePageNumbersMap.put("$200.00 and Under", 1230);
		RangePageNumbersMap.put("$200.00 and Over", 160);
	}
	
	// this method gets the page number of search 
	public int getStartPageFromRange(String rangeBasedOnCost,double startRange){
		int page = 0;
		updateRangePageNumbersMap();
		int totalNumOfPages = RangePageNumbersMap.get(rangeBasedOnCost);
		for(int i=0; i<=totalNumOfPages; i+=25){
			String urlString = "http://api.zappos.com/Search?term=&filters={\"priceFacet\":[\""+ rangeBasedOnCost + "\"]}&key="+zapposAPIKey+"&limit=100&page=" + i +"&sort={%22price%22:%22asc%22}";
			String response = zapposGetAPIData.getHTTPResponseFromZappos(urlString);
			if(!response.equals(null)){
				double priceOfFirstItem = zapposGetAPIData.getPriceOfFirstProductOnPage(zapposGetAPIData.getResultJSONArray(response));
				if(startRange<=priceOfFirstItem){
					page = i-25;
					break;
				}
			}
		}
		return page;
	}
	
	// display final product list based on input params
	public void displayProducts(List<ArrayList<Product>> list){
		for(int i=0; i<list.size();i++){
			System.out.println("Unique product list");
			ArrayList<Product> combList = list.get(i);
			for(int j=0;j<combList.size(); j++){
				Product p = combList.get(j);
				p.displayProductInformation();
			}
		}
	}
}
