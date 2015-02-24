package com.otentico.android.nfc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
	private Bitmap bitmap;
	ImageView iv;

	public ImageLoadTask(ImageView i) {
		this.iv = i;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			URL url;

			Log.d("-- URL ENCODE --", params[0].replace(" ", "%20"));
			url = new URL(params[0].replace(" ", "%20"));

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);

			// CREATE THUMBNAIL
			// thumbnailBitmap = Bitmap.createScaledBitmap(bitmap, 96, 64,
			// false);

			// ////thumbnailBitmap = ThumbnailUtils.extractThumbnail(bitmap,
			// 96, 64);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		iv.setImageBitmap(result);
	}

}
