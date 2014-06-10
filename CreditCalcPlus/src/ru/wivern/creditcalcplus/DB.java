package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.wivern.creditcalcplus.UpdateStruct.PartRepStruct;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
	private static final String DB_NAME		= "HistoryDB";
	private static final int DB_VERSION		= 2;
	private static final String DB_TABLE	= "actionHistory";
	private static final String DB_TABLE_PR	= "actionHistoryPR";
	public static final String COLUMN_ID	= "_id";

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
	
	public static final String COLUMN_ID_PR			= "id_pr";
	public static final String COLUMN_TYPE_PR		= "type_pr";
	public static final String COLUMN_DATE_PR		= "date_pr";
	public static final String COLUMN_SUMM_PR		= "summ_pr";
	
	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID				+ " INTEGER NOT NULL, "
			+ COLUMN_CURDATE		+ " INTEGER, "
			+ COLUMN_TYPE_CREDIT	+ " INTEGER, "
			+ COLUMN_PERIOD			+ " INTEGER, "
			+ COLUMN_CREDIT			+ " INTEGER, "
			+ COLUMN_INTEREST		+ " REAL, "
			+ COLUMN_DATE			+ " INTEGER, "	// as Unix Time
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
			+ COLUMN_DATE_PR		+ " INTEGER, "	// as Unix Time
			+ COLUMN_SUMM_PR		+ " INTEGER, "
			+ "CONSTRAINT FK_" + DB_TABLE_PR + "_" + COLUMN_ID_PR + " FOREIGN KEY (" + COLUMN_ID_PR + ") REFERENCES " + DB_TABLE + "(" + COLUMN_ID + "), "
			+ "CONSTRAINT PK_" + DB_TABLE_PR + " PRIMARY KEY (" + COLUMN_ID + ")"
			+ ");";

	private SQLiteDatabase mDB;
	private DBHelper mDBHelper;
	private final Context mCtx;

	public DB(Context ctx) {
		this.mCtx = ctx;
	}

	// ������� �����������
	public void openReadDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getReadableDatabase();
	}

	public void openWriteDB() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	public Cursor getData(long dateFrom) {
		Cursor cur = mDB.query(DB_TABLE, new String[] {
				COLUMN_ID,
				"date(" + COLUMN_CURDATE + ",'unixepoch')" + " || ': ' || "
						+ COLUMN_CREDIT + " || '/' || " + COLUMN_INTEREST
						+ " || '/' || " + COLUMN_PERIOD + " || '/' || "
						+ COLUMN_COMISSION + " as rec" }, COLUMN_CURDATE + ">="
				+ dateFrom, null, null, null, COLUMN_ID + " desc limit 10");
		return cur;
	}

	// �������� �������
	public void delData() {
		// mDB.delete(DB_TABLE, null, null);
		mDB.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PR);
		mDB.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
		mDB.execSQL(DB_CREATE);
		mDB.execSQL(DB_PR_CREATE);
	}

	// ������� �����������
	public void close() {
		if (mDBHelper != null)
			mDBHelper.close();
		if (mDB != null) {
			mDB.close();
		}
	}

	// �������� ������ � DB_TABLE
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
			cv.put(COLUMN_DATE,			getDateTimeString(upd_struct.date.getTime()));
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
				cv_part.put(COLUMN_DATE_PR,	getDateTimeString(prs.partRepDate.getTime()));
				cv_part.put(COLUMN_SUMM_PR,	prs.partRepSumm);
				mDB.insert(DB_TABLE_PR, null, cv_part);
			}
		}
	}

	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("PRAGMA foreign_keys=ON;");
			db.execSQL(DB_CREATE);
			db.execSQL(DB_PR_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(newVersion == 2)
			{
				// Enable foreign key constraints 
				db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PR);
				db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
				onCreate(db);
			}
		}
	}

	private String getDateTimeString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return dateFormat.format(date);
	}
}
