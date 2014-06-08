package ru.wivern.creditcalcplus;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

class MyCursorLoader extends CursorLoader{
	DB db;
	long dateFrom;
	
	public MyCursorLoader(Context context, DB db, long dateFrom) {
	      super(context);
	      this.db = db;
	      this.dateFrom = dateFrom;
	}
	@Override
    public Cursor loadInBackground() {
      Cursor cursor = db.getData(dateFrom);
      return cursor;
    }
}
