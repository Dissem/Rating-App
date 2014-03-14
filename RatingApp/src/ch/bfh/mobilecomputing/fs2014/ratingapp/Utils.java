package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

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
					} catch (Exception e) {
						e.printStackTrace();
						showToast(view.getContext(), R.string.image_load_error,
								Toast.LENGTH_SHORT);
					}
				}
			}).start();
		}
	}

	public static void showToast(final Context context, final int message,
			final int duration) {
		showToast(context, context.getResources().getString(message), duration);
	}

	public static void showToast(final Context context,
			final CharSequence message, final int duration) {
		if (Thread.currentThread() == context.getMainLooper().getThread()) {
			Toast.makeText(context, message, duration).show();
		} else {
			Handler h = new Handler(context.getMainLooper());

			h.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, message, duration).show();
				}
			});
		}
	}
}
