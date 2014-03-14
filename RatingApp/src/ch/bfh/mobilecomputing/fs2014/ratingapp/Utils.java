package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

public class Utils {
	private static Map<Uri, Bitmap> IMAGE_MAP = new HashMap<Uri, Bitmap>();

	public static void setImage(final ImageView view, final Uri uri) {
		if (IMAGE_MAP.containsKey(uri)) {
			view.setImageBitmap(IMAGE_MAP.get(uri));
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final Bitmap bm = BitmapFactory.decodeStream(new URL(
								uri.toString()).openStream());
						IMAGE_MAP.put(uri, bm);
						new Handler(Looper.getMainLooper())
								.post(new Runnable() {
									@Override
									public void run() {
										view.setImageBitmap(bm);
									}
								});
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
