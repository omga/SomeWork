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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.otentico.android.model.DrawerItem;
import com.otentico.android.model.Product;
import com.otentico.android.nfc.NFCAuthInfoTask;
import com.otentico.android.nfc.OnTaskCompleted;
import com.otentico.android.nfc.Utils;

import se.anyro.nfc_reader.font.RobotoTextView;
import se.anyro.nfc_reader.view.kbv.KenBurnsView;


public class MainScreen extends BaseFragmentActivity implements OnTaskCompleted {

	public static final String NFC_UID = "NFC_UID";
	public static final String COMPANY_NAME = "COMPANY_NAME";
	public static final String COMPANY_IMAGE_URL = "COMPANY_IMAGE_URL";
	public static final String PRODUCT_IMAGE = "PRODUCT_IMAGE";

    public static final String LEFT_MENU_OPTION_1 = "Left Menu Option 1";
    public static final String LEFT_MENU_OPTION_2 = "Left Menu Option 2";



    private KenBurnsView mKenBurns;

    private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;
	private AlertDialog mDialog;


    // Set this value to TRUE if you want to mock the NFC tag info for tests
	private boolean debug = false;

    @Override
    protected int getActivityLayout() {
        return R.layout.main_screen;
    }

    @Override
    protected int getDrawerLayout() {
        return R.id.drawer_layout;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mKenBurns.setImageResource(R.drawable.background_ot);
        RobotoTextView welcome1 = (RobotoTextView) findViewById(R.id.welcome_text_1);
        RobotoTextView welcome2 = (RobotoTextView) findViewById(R.id.welcome_text_2);
        ImageView nfcScanImg = (ImageView) findViewById(R.id.nfc_scan_img);
        nfcScanImg.setAlpha(0.0f);
        welcome1.setAlpha(0.0f);
        welcome2.setAlpha(0.0f);
        animationAlpha(nfcScanImg,500);
        animationAlpha(welcome1,1000);
        animationAlpha(welcome2,1500);


        if (debug) {
			resolveIntentMock(getIntent());
		} else {
			resolveIntent(getIntent());
		}
		mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null)
				.create();
		mAdapter = NfcAdapter.getDefaultAdapter(this);

		if (!debug && mAdapter == null) {
//			showMessage(R.string.error, R.string.no_nfc);
//			finish();
			return;
		}

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				"Message from NFC Reader :-)", Locale.ENGLISH, true) });
	}

	private void showMessage(int title, int message) {
		mDialog.setTitle(title);
		mDialog.setMessage(getText(message));
		mDialog.show();
	}

	private NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			if (!mAdapter.isEnabled()) {
				showWirelessSettingsDialog();
			}
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
			mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
			mAdapter.disableForegroundNdefPush(this);
		}
	}

	private void showWirelessSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.nfc_disabled);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						finish();
					}
				});
		builder.create().show();
		return;
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			// Unknown tag type
			Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			String nfc_uid = Utils.dumpTagData(tag).replace(' ', ':');

			NFCAuthInfoTask auth_task = new NFCAuthInfoTask(this);

			try {
				Address address = Utils.getAddress(this);
                String identity = Utils.getIdentity(this);
                String androidID = Utils.getAndroidID(this);
				if (address == null) {
					auth_task.execute(nfc_uid, "Not Found", "Not Found", identity, androidID);
					return;
				}
				auth_task.execute(nfc_uid, address.getCountryName(), address.getLocality(), identity, androidID);
			} catch (IOException e) {
				auth_task.execute(nfc_uid, "Not Found", "Not Found", "Not Found", "Not Found");
			}

		}
	}

	private void resolveIntentMock(Intent intent) {

		//String nfc_uid = "54:d1:53:6c:0d:1a:d4:b4:14:d0:20:16";
		String nfc_uid = "80:26:f5:8a:6c:f1:04";
		NFCAuthInfoTask auth_task = new NFCAuthInfoTask(this);

		// auth_task.execute(nfc_uid, "Not Found", "Not Found");

		try {
			Address address = Utils.getAddress(this);
			String identity = Utils.getIdentity(this);
            String androidID = Utils.getAndroidID(this);
            Toast.makeText(this,"addr:"+address+" idt: "+identity,Toast.LENGTH_LONG).show();
            if (address == null) {
				auth_task.execute(nfc_uid, "Not Found", "Not Found",identity, androidID);
				return;
			}
			auth_task.execute(nfc_uid, address.getCountryName(), address.getLocality(),identity, androidID);
		} catch (IOException e) {
            Log.e("resolveIntentMock","addr exptn: " + e.getMessage());
			auth_task.execute(nfc_uid, "Not Found", "Not Found","Not Found", "Not Found");
		}

	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		if (debug) {
			resolveIntentMock(intent);
		} else {
			resolveIntent(intent);
		}
	}


    private void animationAlpha(View v,int delay) {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(v, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(delay);
        alphaAnimation.setDuration(1000);
        alphaAnimation.start();
    }

    @Override
	public void onTaskCompleted(String result) {

		Log.d("MainScreen", "onTaskCompleted: " + result);

		try {
			JSONObject prod = new JSONObject(result);
			if (prod.length() > 0) {

				Product.getInstance().populateProduct(prod);
				Intent i = new Intent(this, AuthenticatedScreen.class);
				i.putExtra(MainScreen.COMPANY_NAME, "Otentico");
				i.putExtra(MainScreen.COMPANY_IMAGE_URL,
						"http://db.avaliatech.com/uploads/54bc380d7a63bfe599e82fef.jpg");
				startActivity(i);

			} else {

				Intent actitvity_intent = new Intent(this,
						NotAuthenticatedScreen.class);
				startActivity(actitvity_intent);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
