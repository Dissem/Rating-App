package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;

public class ItemAdapter extends ArrayAdapter<Survey.Item> {

	public ItemAdapter(Context context, int resource, List<Survey.Item> projects) {
		super(context, resource, projects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			row = inflater.inflate(R.layout.item_row, null);
		}
		
		Survey.Item item = getItem(position);
		
		TextView rank = (TextView) row.findViewById(R.id.txtRank);
		TextView name = (TextView) row.findViewById(R.id.txtTitle);
		TextView authors = (TextView) row.findViewById(R.id.txtAuthors);
		TextView rating = (TextView) row.findViewById(R.id.txtRating);
		RatingBar ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
		
		if (rank != null) {
			rank.setText(item.getId() + ".");
		}
		if (name != null) {
			name.setText(item.getTitle());
		}
		if (authors != null) {
			authors.setText("[Team members]"); // TODO
		}
		if (rating != null) {
			rating.setText(String.valueOf(item.getRating()));
		}
		if (ratingBar != null) {
//			if (!item.isRated()) {
//				rating.setVisibility(TextView.GONE);
//				ratingBar.setVisibility(RatingBar.GONE);
//			} else {
				ratingBar.setRating((float) item.getRating());	
//			}			
		}
		
		return row;
	}
	

}
