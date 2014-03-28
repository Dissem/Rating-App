package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
    private String ratingTableName = "rating";
    private static final String DATABASE_NAME = "RatingApp";
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
    
	public  DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( "CREATE TABLE " + this.ratingTableName + " ("+this.columnSurveyId+" TEXT NOT NULL, "+this.columnItemId+" INTEGER NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
