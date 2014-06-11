package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ru.wivern.creditcalcplus.UpdateStruct.PartRepStruct;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class DB {
	private static final String DB_NAME		= "HistoryDB";
	private static final int DB_VERSION		= 2;
	private static final String DB_TABLE	= "actionHistory";
	private static final String DB_TABLE_PR	= "actionHistoryPR";
	
	public static final String COLUMN_ID			= ContactsContract.Contacts._ID;
	public static final String COLUMN_CURDATE		= "curr_date";
	public static final String COLUMN_TYPE_CREDIT	= "type";
	public static final String COLUMN_PERIOD		= "period";
	public static final String COLUMN_CREDIT		= "credit";
	public static final String COLUMN_INTEREST		= "interest";
	public static final String COLUMN_DATE			= "date";
	public static final String COLUMN_PARTREPAY_ID	= "part";
	public static final String COLUMN_FISTPROC		= "first_proc";
	public static final String COLUMN_COMISSION		= "comission";
	public static final String COLUMN_TYPECOMISS	= "type_comiss";
	
	public static final String COLUMNS_MAIN[] = new String[] { COLUMN_ID, COLUMN_DATE, COLUMN_CREDIT, COLUMN_PERIOD, COLUMN_INTEREST};
	
	public static final String COLUMN_ID_PR			= "id_pr";
	public static final String COLUMN_TYPE_PR		= "type_pr";
	public static final String COLUMN_DATE_PR		= "date_pr";
	public static final String COLUMN_SUMM_PR		= "summ_pr";
	
	public static final String COLUMNS_PART[] = new String[] { COLUMN_ID, COLUMN_DATE_PR, COLUMN_SUMM_PR};
	
	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID				+ " INTEGER NOT NULL, "
			+ COLUMN_CURDATE		+ " INTEGER, "
			+ COLUMN_TYPE_CREDIT	+ " INTEGER, "
			+ COLUMN_PERIOD			+ " INTEGER, "
			+ COLUMN_CREDIT			+ " INTEGER, "
			+ COLUMN_INTEREST		+ " REAL, "
			+ COLUMN_DATE			+ " LONG, "		// as Unix Time
			+ COLUMN_PARTREPAY_ID	+ " INTEGER, "
			+ COLUMN_FISTPROC		+ " INTEGER, "	// boolean
			+ COLUMN_COMISSION		+ " REAL, "
			+ COLUMN_TYPECOMISS		+ " INTEGER, "
			+ "CONSTRAINT PK_" + DB_TABLE + " PRIMARY KEY (" + COLUMN_ID + ")"
			+ ");";
	
	private static final String DB_PR_CREATE = "create table " + DB_TABLE_PR + "("
			+ COLUMN_ID				+ " INTEGER NOT NULL, "
			+ COLUMN_ID_PR			+ " INTEGER, "
			+ COLUMN_TYPE_PR		+ " INTEGER, "
			+ COLUMN_DATE_PR		+ " LONG, "		// as Unix Time
			+ COLUMN_SUMM_PR		+ " INTEGER, "
			+ "CONSTRAINT FK_" + DB_TABLE_PR + "_" + COLUMN_ID_PR + " FOREIGN KEY (" + COLUMN_ID_PR + ") REFERENCES " + DB_TABLE + "(" + COLUMN_ID + "), "
			+ "CONSTRAINT PK_" + DB_TABLE_PR + " PRIMARY KEY (" + COLUMN_ID + ")"
			+ ");";

	private SQLiteDatabase mDB;
	private DBHelper mDBHelper;
	private final Context mCtx;

	private static final SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
	public DB(Context ctx) {
		this.mCtx = ctx;
	}

	public void openReadDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getReadableDatabase();
	}

	public void openWriteDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	public Cursor getMainData(long dateFrom) {
		String date_from_args[] = new String[] {Long.toString(dateFrom)};
		Cursor cur = mDB.query(DB_TABLE, COLUMNS_MAIN, COLUMN_CURDATE + " >= ?" , date_from_args,
				null, null, COLUMN_ID + " desc limit 10");
		return cur;
	}

	public Cursor getPartData(long part_id) {
		String data_from_args[] = new String[] {Long.toString(part_id)};
		Cursor cur = mDB.query(DB_TABLE_PR, COLUMNS_PART, COLUMN_ID_PR + " = ?" , data_from_args,
				null, null, COLUMN_ID + " desc limit 10");
		return cur;
	}
	
	public void delData() {
		// mDB.delete(DB_TABLE, null, null);
		ReCreate(mDB);
	}

	public void close() {
		if (mDBHelper != null)
			mDBHelper.close();
		if (mDB != null) {
			mDB.close();
		}
	}

	public void addRec(UpdateStruct upd_struct) {
		int i;
		long sysDateLONG = (System.currentTimeMillis() / 1000);
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_CURDATE, sysDateLONG);
		try {
			cv.put(COLUMN_TYPE_CREDIT,	upd_struct.type);
			cv.put(COLUMN_CREDIT,		upd_struct.summa);
			cv.put(COLUMN_PERIOD,		upd_struct.period);
			cv.put(COLUMN_INTEREST,		upd_struct.percent);
			cv.put(COLUMN_DATE,			upd_struct.date.getTimeInMillis());
			cv.put(COLUMN_PARTREPAY_ID,	0);
			cv.put(COLUMN_FISTPROC,		upd_struct.firstOnlyProc);
			cv.put(COLUMN_COMISSION,	upd_struct.comission);
			cv.put(COLUMN_TYPECOMISS,	upd_struct.comission_type);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		long currID = mDB.insert(DB_TABLE, null, cv);
		if(currID != -1)
		{
			for (i = 0; i < upd_struct.part.size(); i++) {
				ContentValues cv_part = new ContentValues();
				PartRepStruct prs = upd_struct.part.get(i);
				cv_part.put(COLUMN_ID_PR,	currID);
				cv_part.put(COLUMN_TYPE_PR,	prs.typePartRep);
				cv_part.put(COLUMN_DATE_PR,	prs.partRepDate.getTimeInMillis());
				cv_part.put(COLUMN_SUMM_PR,	prs.partRepSumm);
				mDB.insert(DB_TABLE_PR, null, cv_part);
			}
		}
	}

	public static void CreateDB(SQLiteDatabase db)
	{
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(DB_CREATE);
		db.execSQL(DB_PR_CREATE);
	}
	
	public static void ReCreate(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PR);
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		CreateDB(db);
	}
	
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			CreateDB(db);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion < 2)
			{
				ReCreate(db);
			}
		}

	}

	public static String getDateTimeString(Calendar curr_calendar) {
		return m_dateFormat.format(curr_calendar.getTime());
	}
	
	public static Calendar getDateTime(String currText)
	{
		Calendar currDate = Calendar.getInstance();
		if (TextUtils.isEmpty(currText) == false) {
			try {
				currDate.setTime(MainActivity.m_date_format.parse(currText));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		
		return currDate;
	}
}
