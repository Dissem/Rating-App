package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper-Class which creates, if not exists, the SQLite-DB including the rating table
 * and have all information about the SQLite-DB and their including tables.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RatingApp";
    
    private String ratingTableName = "rating";
    private String columnSurveyId = "surveyId";
	private String columnItemId = "itemId";

    public String getRatingTableName() {
		return ratingTableName;
	}

	public String getColumnSurveyid() {
		return columnSurveyId;
	}

	public String getColumnItemid() {
		return columnItemId;
	}
    
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( "CREATE TABLE " + this.ratingTableName + " ("+this.columnSurveyId+" TEXT NOT NULL, "+this.columnItemId+" INTEGER NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}	
}
