package ru.wivern.creditcalcplus;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

class MyCursorLoader extends CursorLoader {
	DB db;
	long dateFrom;
	long id;

	public MyCursorLoader(Context context, DB db, long dateFrom, long c_id) {
		super(context);
		this.db = db;
		this.dateFrom = dateFrom;
		this.id = c_id;
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = null;
		if(db != null)
		{
			if (id == -1) {
				cursor = db.getMainData(dateFrom);
			} else {
				cursor = db.getPartData(id);
			}
		}
		return cursor;
	}
}
