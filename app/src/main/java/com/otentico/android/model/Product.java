package com.otentico.android.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    public static String FILENAME = "product_scan_history.txt";
	private static Product instance = new Product();

	private Map<String, Property> props = new HashMap<String, Property>();


    public void populateProduct(JSONObject product) throws JSONException {
		props.clear();
		getProps().put("sku", new Property("SKU", product.getString("sku")));
		getProps().put("desc",
				new Property("Description", product.getString("desc")));
		getProps().put("producer",
				new Property("Producer", product.getString("producer")));
		getProps().put("size", new Property("Size", product.getString("size")));
		getProps().put("color",
				new Property("Color", product.getString("color")));
		getProps().put("salesPrice",
				new Property("Price", product.getString("salesPrice")));
		getProps().put("image",
				new Property("Image", product.getString("image")));
		getProps().put(
				"homologation",
				new Property("Homologation", product.getJSONObject(
						"homologationID").getString("issuerNo")));

	}



	private Product() {
	}

	public Map<String, Property> getProps() {
		return props;
	}

	public static Product getInstance() {
		return instance;
	}

}
