package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Survey {
	private String id;
	private String title;
	private String description;
	private Uri image;
	private List<Item> items = new ArrayList<Item>();

	public Survey(JSONObject data) {
		id = str(data, "surveyId");
		title = str(data, "title");
		description = str(data, "description");
		image = uri(data, "imageUrl");
		try {
			JSONArray itemArray = data.getJSONArray("items");
			for (int i = 0; i < itemArray.length(); i++) {
				items.add(new Item(itemArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			Log.e("Survey", e.getMessage(), e);
		}
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Uri getImage() {
		return image;
	}

	public Item getItem(int id) {
		for (Item item : items)
			if (item.id == id)
				return item;
		return null;
	}

	public List<Item> getItems() {
		return items;
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public class Item implements Comparable<Survey.Item> {
		private int id;
		private String title;
		private String subtitle;
		private String description;
		private Uri image;
		private double rating;
		private int votes;

		private Item(JSONObject itemData) {
			id = num(itemData, "itemId");
			title = str(itemData, "title");
			subtitle = str(itemData, "subtitle");
			description = str(itemData, "description");
			image = uri(itemData, "imageUrl");
			rating = dec(itemData, "rating");
			votes = num(itemData, "votes");
		}

		public int getId() {
			return id;
		}

		public String getSurveyId() {
			return Survey.this.id;
		}

		public String getTitle() {
			return title;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public String getDescription() {
			return description;
		}

		public Uri getImage() {
			return image;
		}

		public double getRating() {
			return rating;
		}

		public int getVotes() {
			return votes;
		}

		@Override
		public String toString() {
			return Survey.this.id + "/" + id + ": " + title + " (" + rating
					+ ")";
		}

		@Override
		public int compareTo(Item another) {
			if (this.rating > another.getRating()) {
				return -1;
			} else if (this.rating == another.getRating()) {
				if (this.votes < another.getRating()) {
					return -1;
				} else {
					return 0;					
				}
			}
			return 1;
		}		
		
	}

	private static String str(JSONObject data, String field) {
		if (data.isNull(field))
			return null;

		try {
			return data.getString(field).trim();
		} catch (JSONException e) {
			Log.e("Survey", e.getMessage(), e);
			return null;
		}
	}

	private static int num(JSONObject data, String field) {
		try {
			return data.getInt(field);
		} catch (JSONException e) {
			Log.e("Survey", e.getMessage(), e);
			return 0;
		}
	}

	private static double dec(JSONObject data, String field) {
		try {
			return data.getDouble(field);
		} catch (JSONException e) {
			Log.e("Survey", e.getMessage(), e);
			return 0;
		}
	}

	private static Uri uri(JSONObject data, String field) {
		if (data.isNull(field))
			return null;

		try {
			String url = data.getString(field).trim();
			if (url.length() == 0)
				return null;
			return Uri.parse(url);
		} catch (JSONException e) {
			Log.e("Survey", e.getMessage(), e);
			return null;
		}
	}
}
