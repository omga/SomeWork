package se.anyro.nfc_reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.otentico.android.nfc.ImageLoadTask;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class AuthenticatedScreen extends ActionBarActivity {

	public static final String NFC_UID = "NFC_UID";
	Button btnProductInformation;
	Button btnBrandInformation;
	String company_name;
	String company_logo_url;
    private Toolbar mToolbar;

	ImageView imgCompanyLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		company_name = intent.getExtras().getString(MainScreen.COMPANY_NAME);
		company_logo_url = intent.getExtras().getString(
				MainScreen.COMPANY_IMAGE_URL);


		setContentView(R.layout.authenticated_screen);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
		btnProductInformation = (Button) findViewById(R.id.btnProductInformation);
		imgCompanyLogo = (ImageView) findViewById(R.id.company_logo);

		imgCompanyLogo.setImageDrawable(getResources().getDrawable(
				R.drawable.logo));

//		ImageLoadTask imgLoadTask = new ImageLoadTask(imgCompanyLogo);
//		imgLoadTask.execute(company_logo_url);

		btnProductInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent actitvity_intent = new Intent(AuthenticatedScreen.this,
						ProductInformation.class);

				actitvity_intent
						.putExtra(MainScreen.COMPANY_NAME, company_name);
				actitvity_intent.putExtra(MainScreen.COMPANY_IMAGE_URL,
						company_logo_url);
				startActivity(actitvity_intent);

			}
		});

	}

}
