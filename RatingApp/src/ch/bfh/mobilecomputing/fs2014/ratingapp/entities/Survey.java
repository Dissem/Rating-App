package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

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

	public Survey(JSONObject data) throws JSONException {
		id = data.getString("surveyId");
		title = data.getString("title");
		description = data.getString("description");
		image = Uri.parse(data.getString("imageUrl"));
		JSONArray itemArray = data.getJSONArray("items");
		for (int i = 0; i < itemArray.length(); i++) {
			items.add(new Item(itemArray.getJSONObject(i)));
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
	public class Item {
		private int id;
		private String title;
		private String description;
		private Uri image;
		private double rating;
		private int votes;

		private Item(JSONObject itemData) throws JSONException {
			id = itemData.getInt("itemId");
			title = itemData.getString("title");
			description = itemData.getString("description");
			image = Uri.parse(itemData.getString("imageUrl"));
			rating = itemData.getDouble("rating");
			votes = itemData.getInt("votes");
		}

		public int getId() {
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
	}
}
