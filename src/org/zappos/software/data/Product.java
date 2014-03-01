package org.zappos.software.data;
/**
 * 
 * @author Gaurav
 *	This class stores the Product details. 
 *	Includes productId, styleId, productName, Price
 *	This helps in printing the result in the end.
 */

public class Product {
	private int productId;
	private int styleId;
	private String productName;
	private double price;
	
	public Product(int productId, int styleId, String productName, double price){
		this.productId = productId;
		this.styleId = styleId;
		this.productName = productName;
		this.price = price;
	}
	
	public Product(){
		this.productId = 0;
		this.styleId = 0;
		this.productName = "null";
		this.price = 0.0;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getStyleId() {
		return styleId;
	}

	public void setStyleId(int styleId) {
		this.styleId = styleId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	// displays Product Information in the end.
	public void displayProductInformation(){
		System.out.println(this.productName);
		System.out.println(this.productId);
		System.out.println(this.styleId);
		System.out.println(this.price);
	}
}
