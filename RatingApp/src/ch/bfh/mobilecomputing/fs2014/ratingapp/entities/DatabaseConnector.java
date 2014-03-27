package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseConnector {
	private static DatabaseConnector INSTANCE;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;	
	
	public static synchronized void init(Context context) {
		if (INSTANCE == null)
			INSTANCE = new DatabaseConnector(context);
	}
	
	public DatabaseConnector(Context context) {
		this.dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		this.database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public void createRating(Rating rating) {
		ContentValues values = new ContentValues();
		values.put(this.dbHelper.getColumnSurveyid(), rating.getSurveyId());
		values.put(this.dbHelper.getColumnItemid(), rating.getItemId());
		this.database.insert(this.dbHelper.getRatingTableName(), null, values);
	}
	
	public boolean isRatingExist(Rating rating) {
		String query = "SELECT COUNT(*) FROM " + this.dbHelper.getRatingTableName() + " WHERE " + 
						this.dbHelper.getColumnSurveyid() +" = '" + rating.getSurveyId() + "' AND " + 
						this.dbHelper.getColumnItemid() + " = " + rating.getItemId();
		Cursor cursor = this.database.rawQuery(query, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		if (count == 0) {
			return false;
		}
		return true;			
	}
	
	public static DatabaseConnector getInstance() {
		return INSTANCE;
	}
}
