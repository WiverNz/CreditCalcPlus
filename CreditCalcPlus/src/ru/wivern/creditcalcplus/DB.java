package ru.wivern.creditcalcplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
	  private static final String DB_NAME = "HistoryDB";
	  private static final int DB_VERSION = 1;
	  private static final String DB_TABLE = "actionHistory";  
	  public static final String COLUMN_ID = "_id";
	 // public static final String COLUMN_DATE = "datePay";
	  public static final String COLUMN_AMOUNT = "amountPay";
	  public static final String COLUMN_INTEREST = "interest";
	  public static final String COLUMN_PERIOD = "period";
	  public static final String COLUMN_CREDIT = "credit";
	  public static final String COLUMN_CURDATE = "curDate";
	  
	  private static final String DB_CREATE = 
	    "create table " + DB_TABLE + "(" +
	    COLUMN_ID + " integer primary key autoincrement, " +
	    COLUMN_CURDATE + " long, " +
	    COLUMN_INTEREST + " text, " +
	    COLUMN_PERIOD + " text, " +
	    COLUMN_CREDIT + " text, " +
	    COLUMN_AMOUNT + " text" +
	    ");";
	  
	  private SQLiteDatabase mDB;
	  private DBHelper mDBHelper;
	  private final Context mCtx;
	  
	  public DB(Context ctx) {
		  this.mCtx = ctx;
	  }
	  
	  // открыть подключение
	  public void openReadDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
	    mDB = mDBHelper.getReadableDatabase();
	  }
	  
	  public void openWriteDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	  }
	  public Cursor getData(long dateFrom){
		  Cursor cur = mDB.query(DB_TABLE, new String[] {COLUMN_ID, "date(" + COLUMN_CURDATE + ",'unixepoch')" + " || ': ' || " + COLUMN_CREDIT + " || '/' || " + COLUMN_INTEREST + " || '/' || " + COLUMN_PERIOD + " || '/' || " + COLUMN_AMOUNT +" as rec"}, COLUMN_CURDATE + ">=" + dateFrom, null, null, null,COLUMN_ID + " desc limit 10");
		  return cur;
	  }
	  //очистить таблицу
	  public void delData() {
		    //mDB.delete(DB_TABLE, null, null);
		    mDB.execSQL("drop table " + DB_TABLE);
		    mDB.execSQL(DB_CREATE);
		  }
	  // закрыть подключение
	  public void close() {
	    if (mDBHelper!=null) mDBHelper.close();
	    if (mDB!=null){
	    	mDB.close();}
	  }
	  
	  // добавить запись в DB_TABLE
	  public void addRec(long sysDateLONG, String credit, String interest, String period, String amountPay) {
	    ContentValues cv = new ContentValues();
	    cv.put(COLUMN_CURDATE, sysDateLONG);
	    cv.put(COLUMN_CREDIT, credit);
	    cv.put(COLUMN_INTEREST, interest);
	    cv.put(COLUMN_PERIOD, period);
	    cv.put(COLUMN_AMOUNT, amountPay);
	    mDB.insert(DB_TABLE, null, cv);
	  }
	  private class DBHelper extends SQLiteOpenHelper {
			public DBHelper(Context context, String name, CursorFactory factory,int version) {
			      super(context, name, factory, version);
			    }
			@Override
			public void onCreate(SQLiteDatabase db) {
			      db.execSQL(DB_CREATE);
			    }

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}
      }

	  
}
