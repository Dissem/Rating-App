package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.DatabaseConnector;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Rating;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey;

public class ItemAdapter extends ArrayAdapter<Survey.Item> {
	
	public ItemAdapter(Context context, int resource, List<Survey.Item> items) {
		super(context, resource, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.item_row, null);
		}

		Survey.Item item = getItem(position);
		Rating internalRating = new Rating(item.getSurveyId(), item.getId());

		ImageView medal = (ImageView) row.findViewById(R.id.imgMedal);
		TextView rank = (TextView) row.findViewById(R.id.txtRank);
		TextView title = (TextView) row.findViewById(R.id.txtTitle);
		TextView subtitle = (TextView) row.findViewById(R.id.txtSubtitle);
		TextView rating = (TextView) row.findViewById(R.id.txtRating);
		TextView votes = (TextView) row.findViewById(R.id.txtVotes);
		RatingBar ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);

		if (rank != null) {
			if (!DatabaseConnector.getInstance().isRatingExist(internalRating)) {
				rank.setText("#");
				
				medal.setVisibility(ImageView.GONE);
			} else {
				rank.setVisibility(TextView.GONE);
				medal.setVisibility(ImageView.VISIBLE);
				
				int rankNr = item.getRank();
				
				if (rankNr == 1) {					
					medal.setImageResource(R.drawable.medal_gold);
				} else if (rankNr == 2) {
					medal.setImageResource(R.drawable.medal_silver);					
				} else if (rankNr == 3) {
					medal.setImageResource(R.drawable.medal_bronze);					
				} else {
					rank.setVisibility(TextView.VISIBLE);
					rank.setText(item.getRank() + ".");
				}
			}
		}

		if (title != null) {
			title.setText(item.getTitle());
		}

		if (subtitle != null) {
			String text = ((item.getSubtitle() == null) ? "" : item
					.getSubtitle());
			subtitle.setText(text);
		}

		if (rating != null) {
			if (!DatabaseConnector.getInstance().isRatingExist(internalRating)) {
				rating.setText(R.string.text_not_yet_rated);
			} else {
				rating.setText(String.format("%.2f", item.getRating()));
			}
		}

		if (votes != null) {
			if (DatabaseConnector.getInstance().isRatingExist(internalRating)) {
				if (item.getVotes() == 1) {
					votes.setText("(" + item.getVotes() + " vote)");
				} else {
					votes.setText("(" + item.getVotes() + " votes)");
				}
			}
		}

		if (ratingBar != null) {
			if (!DatabaseConnector.getInstance().isRatingExist(internalRating)) {
				ratingBar.setEnabled(false);
			} else {
				ratingBar.setRating((float) item.getRating());
			}
		}

		return row;
	}

}
