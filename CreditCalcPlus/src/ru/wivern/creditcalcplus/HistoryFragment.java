package ru.wivern.creditcalcplus;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;


// Fix: load child twice (if there is no COLUMN_ID in COLUMNS_PART - error)
// Add: load from item in list into main fragment.
// Fix: error - could not stop activity because of db is open
public class HistoryFragment extends Fragment implements OnClickListener, OnTouchListener, OnDateSetListener, LoaderCallbacks<Cursor>, SimpleCursorTreeAdapter.ViewBinder {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    Button btnShow;
    EditText editDate;
    ExpandableListView listHistory;
    DatePickerFragment m_datePicker;
    long dateFrom;
    MyCursorTreeAdapter m_adapter;
    DB db;
    private static final SimpleDateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HistoryFragment newInstance(int sectionNumber) {
    	//fragment=PlaceholderFragment.instantiate(getBaseContext(), MyClass1.class.getName());
    	HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
    }

   //@SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        
        btnShow = (Button) rootView.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
        editDate = (EditText) rootView.findViewById(R.id.editDate);
        editDate.setOnTouchListener(this);
        editDate.setText("2014-4-31");
        Date date = Date.valueOf(editDate.getText().toString());
        dateFrom = date.getTime()/1000;
        m_datePicker = new DatePickerFragment();
        listHistory = (ExpandableListView) rootView.findViewById(R.id.listHistory);
        db = new DB(this.getActivity());

        String from[] = new String[] {DB.COLUMN_ID, DB.COLUMN_DATE, DB.COLUMN_CREDIT, DB.COLUMN_PERIOD, DB.COLUMN_INTEREST};
		int to[] = new int[] {R.id.tvCOLUMN_ID, R.id.tvCOLUMN_CURDATE, R.id.tvCOLUMN_CREDIT, R.id.tvCOLUMN_PERIOD, R.id.tvCOLUMN_INTEREST};
		String childFrom[] = {DB.COLUMN_DATE_PR, DB.COLUMN_SUMM_PR};
	    int childTo[] = new int[] {R.id.tvCOLUMN_DATE_PR, R.id.tvCOLUMN_SUMM_PR};
	    m_adapter = new MyCursorTreeAdapter(this.getActivity(), this, R.layout.history_list, from, to, R.layout.history_list_part, childFrom, childTo);
	    m_adapter.setViewBinder(this);
	    listHistory.setAdapter(m_adapter);
        
//        Loader<Cursor> loader = getLoaderManager().getLoader(-1);
//        if (loader != null && !loader.isReset()) {
//        	this.getActivity().getSupportLoaderManager().restartLoader(-1, null, this);
//        } else {
//        	this.getActivity().getSupportLoaderManager().initLoader(-1, null, this);
//        }
        
        return rootView;
    }
 
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		  case R.id.btnShow:
			  if(editDate.getText().toString().equals("")){Toast.makeText(this.getActivity(), R.string.ErrDateLoad, Toast.LENGTH_LONG).show();break;}
			  db.openReadDB();
			  this.getActivity().getSupportLoaderManager().restartLoader(-1, null, this);
			  break;
		}
	return;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		String currTitle = "";
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			switch(v.getId()){
			case R.id.editDate:
				if(TextUtils.isEmpty(currTitle) == true)
				{
					currTitle = this.getString(R.string.tvPartRepDate);
				}
				Calendar calender = Calendar.getInstance();
				Bundle args = new Bundle();
				args.putInt("year", calender.get(Calendar.YEAR));
				args.putInt("month", calender.get(Calendar.MONTH));
				args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
				args.putInt("id", v.getId());
				m_datePicker.setArguments(args);

				m_datePicker.setCallBack((OnDateSetListener) this);
				m_datePicker.show(getFragmentManager(), currTitle);
				break;
			}
			break;
		}
		return false;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		editDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new MyCursorLoader(this.getActivity(), db, dateFrom, id);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		int id = loader.getId();
		if (id != -1) {
			// child cursor
			if (!cursor.isClosed()) {
				SparseIntArray groupMap = m_adapter.getGroupMap();
				try {
					int groupPos = groupMap.get(id);
					m_adapter.setChildrenCursor(groupPos, cursor);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		} else {
			m_adapter.setGroupCursor(cursor);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		Long currVal = (long) 0;
		TextView currTV = null;
		Calendar currCalendar = Calendar.getInstance();
		switch(view.getId())
		{
		case R.id.tvCOLUMN_CURDATE:
			currVal = cursor.getLong(columnIndex);
			currTV = (TextView) view;
			currCalendar.setTimeInMillis(currVal);
			currTV.setText(m_dateFormat.format(currCalendar.getTime()));
			return true;
		case R.id.tvCOLUMN_DATE_PR:
			currVal = cursor.getLong(columnIndex);
			currTV = (TextView) view;
			currCalendar.setTimeInMillis(currVal);
			currTV.setText(m_dateFormat.format(currCalendar.getTime()));
			return true;
		}
		return false;
	}
	
	class MyCursorTreeAdapter extends SimpleCursorTreeAdapter {
		private MainActivity mActivity;
	    protected final SparseIntArray mGroupMap;
	    private HistoryFragment mFragment;
		public MyCursorTreeAdapter(Context context, Fragment fragment, int groupLayout,
				String[] groupFrom, int[] groupTo, int childLayout,
				String[] childFrom, int[] childTo) {
			super(context, null, groupLayout, groupFrom, groupTo,
					childLayout, childFrom, childTo);
			mActivity	= (MainActivity) context;
			mFragment	= (HistoryFragment) fragment;
	        mGroupMap	= new SparseIntArray();
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			// Logic to get the child cursor on the basis of selected group.
			int groupPos = groupCursor.getPosition();
			int groupId = groupCursor.getInt(groupCursor
					.getColumnIndex(DB.COLUMN_ID));

			mGroupMap.put(groupId, groupPos);
			Loader<Cursor> loader = mActivity.getSupportLoaderManager().getLoader(groupId);
			if (loader != null && !loader.isReset()) {
				mActivity.getSupportLoaderManager().restartLoader(groupId, null, mFragment);
			} else {
				mActivity.getSupportLoaderManager().initLoader(groupId, null, mFragment);
			}

			return null;
		}
		public SparseIntArray getGroupMap() {
			return mGroupMap;
		}
	}
}
