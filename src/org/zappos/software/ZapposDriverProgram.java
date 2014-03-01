package org.zappos.software;

/**
 * @author Gaurav
 *  Main driver program to run the code
 *  Takes two command line parameters
 * 	 - Number of gifts (int)
 *   - Total price of all gifts (double)
 *  
 *   Prints all the possible list of products with the given inputs on command line itself. 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.zappos.software.data.Product;
import org.zappos.software.range.ZapposUtilityFunction;

public class ZapposDriverProgram {

	private static final String zapposAPIKey = "b05dcd698e5ca2eab4a0cd1eee4117e7db2a10c4";
	private static final String PROBLEM = "Problem";
	
	public static void main(String[] args) {
		int numberOfGifts; 
		double totalCostOfGifts;
		// flag to check if any result is found in first iteration else increase range.
		boolean isResultFound = false;
		ZapposGetAPIData zapposGetAPIData = new ZapposGetAPIData();
		ZapposModifyInputData zapposModifyInputData = new ZapposModifyInputData();
		
		// Take user inputs from the user
		Scanner inputParams = new Scanner(System.in);
		System.out.println("Enter the number of gifts...");
		numberOfGifts = Integer.parseInt(inputParams.next());
		System.out.println("Enter the total cost of gifts...");
		totalCostOfGifts = Integer.parseInt(inputParams.next());
		
		double average = totalCostOfGifts/numberOfGifts;
		double limit = 0.0;
		do{
			//calculate start and end of range to search
			double upperLimit = average + limit;
			double lowerLimit = average - limit;
			double startOfRange = lowerLimit > 0 ? lowerLimit: 0;
			double endOfRange = lowerLimit <= totalCostOfGifts? lowerLimit: totalCostOfGifts;
			
			// get different price ranges from Zappos API
			String priceRangeURL = "http://api.zappos.com/Search?includes=[%22facets%22]&excludes=[%22results%22]&facets=[%22priceFacet%22]&key="+zapposAPIKey;
			String priceRangeURLResponse = zapposGetAPIData.getHTTPResponseFromZappos(priceRangeURL);
			if(priceRangeURLResponse.equals(PROBLEM)){
				System.out.println("Problem in connecting to Internet");
			}else{
				// Get the actual range of a data based on total target Cost
				String ActualRangeBasedOnTotalCost = zapposGetAPIData.getActualRangeBasedOnInputCost(priceRangeURLResponse,totalCostOfGifts);
				if(ActualRangeBasedOnTotalCost.equals(PROBLEM)){
					System.out.println("Check internet connection...");
				}else{
					// get total number of pages based on total number of products
					int totalNumberOfProducts = zapposGetAPIData.getNumOfItemsInRange();
					int totalNumberOfPages = (int) Math.ceil(totalNumberOfProducts/100);
					
					/* 
					 * Get page number to start looking for relative products.
					 * This is to minimize the search from page 0 and start searching from closest pages to value.
					 */
					
					ZapposUtilityFunction zapposUtilityFunction = new ZapposUtilityFunction();
					int pageNum = zapposUtilityFunction.getStartPageFromRange(ActualRangeBasedOnTotalCost, startOfRange);
					
					for(int i=0; i<= totalNumberOfPages ; i++){
						//loop through pages and compute the necessary product data
						String productURLPage = "http://api.zappos.com/Search?term=&filters={\"priceFacet\":[\""+ ActualRangeBasedOnTotalCost + "\"]}&key="+zapposAPIKey+"&limit=100&page=" + i +"&sort={%22price%22:%22asc%22}";
						String response =  zapposGetAPIData.getHTTPResponseFromZappos(productURLPage);
						if(zapposModifyInputData.computeData(response,startOfRange,endOfRange,numberOfGifts)){
							break;
						}
					}
					
					//get map of unique product ids and product data necessary for computation  
					HashMap<Integer,Product> productList = zapposModifyInputData.getUniqueProductIdMap();
					//gets all possible combinations based on data and inputs
					List<ArrayList<Product>> resultProductList = zapposModifyInputData.getPossibleCombinations(productList,numberOfGifts,totalCostOfGifts);
					if(!resultProductList.isEmpty()){
						//display list of products found so far
						zapposUtilityFunction.displayProducts(resultProductList);
						isResultFound = true;
					}else{
						//increase limit of search by 0.5 cents and continue the search.
						limit += 0.5;
					}
				}
			}
			// repeat if the result list calculated is empty
		}while(!isResultFound);
	}

}
