/*
 * Copyright (C) 2010 The Android Open Source Project
 * Copyright (C) 2011 Adam Nybäck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.anyro.nfc_reader;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.otentico.android.model.Product;
import com.otentico.android.model.Property;
import com.otentico.android.nfc.ImageLoadTask;
import com.otentico.android.nfc.Utils;

public class ProductInformation extends Activity {

	TextView name_1;
	TextView name_2;
	TextView name_3;
	TextView name_4;
	TextView name_5;
	TextView name_6;

	TextView value_1;
	TextView value_2;
	TextView value_3;
	TextView value_4;
	TextView value_5;
	TextView value_6;

	ImageView product_image;

	String company_name;
	String company_logo_url;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		company_name = intent.getExtras().getString(MainScreen.COMPANY_NAME);
		company_logo_url = intent.getExtras().getString(
				MainScreen.COMPANY_IMAGE_URL);


		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.product_information);

		name_1 = (TextView) findViewById(R.id.textName1);
		name_2 = (TextView) findViewById(R.id.textName2);
		name_3 = (TextView) findViewById(R.id.textName3);
		name_4 = (TextView) findViewById(R.id.textName4);
		name_5 = (TextView) findViewById(R.id.textName5);
		name_6 = (TextView) findViewById(R.id.textName6);

		value_1 = (TextView) findViewById(R.id.textValue1);
		value_2 = (TextView) findViewById(R.id.textValue2);
		value_3 = (TextView) findViewById(R.id.textValue3);
		value_4 = (TextView) findViewById(R.id.textValue4);
		value_5 = (TextView) findViewById(R.id.textValue5);
		value_6 = (TextView) findViewById(R.id.textValue6);
		
		Map<String, Property> p = Product.getInstance().getProps();
		

		name_1.setText(p.get("sku").label);
		value_1.setText(p.get("sku").value);

		name_2.setText(p.get("desc").label);
		value_2.setText(p.get("desc").value);

		name_3.setText(p.get("producer").label);
		value_3.setText(p.get("producer").value);

		name_4.setText(p.get("size").label);
		value_4.setText(p.get("size").value);

		name_5.setText(p.get("color").label);
		value_5.setText(p.get("color").value);

		name_6.setText(p.get("homologation").label);
		value_6.setText(p.get("homologation").value);

		product_image = (ImageView) findViewById(R.id.product_image);

		product_image.setImageDrawable(getResources().getDrawable(
				R.drawable.no_image));

		ImageLoadTask imgLoadTask = new ImageLoadTask(product_image);
		imgLoadTask.execute(Utils.HOST + "uploads/" + p.get("image").value);

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
