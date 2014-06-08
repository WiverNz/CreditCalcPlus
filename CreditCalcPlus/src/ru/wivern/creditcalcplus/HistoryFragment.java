package ru.wivern.creditcalcplus;

import java.sql.Date;
import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class HistoryFragment extends Fragment implements OnClickListener, OnTouchListener, OnDateSetListener, LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    Button btnShow;
    EditText editDate;
    ListView listHistory;
    DatePickerFragment m_datePicker;
    long dateFrom;
    SimpleCursorAdapter adapter;
    DB db;
    
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
        listHistory = (ListView) rootView.findViewById(R.id.listHistory);
        db = new DB(this.getActivity());

        String[] from = new String[] { "rec"};
		int[] to = new int[] { R.id.textHistoryList};
        adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.history_list, null, from, to, 0);
        listHistory.setAdapter(adapter);
        
        return rootView;
    }
 
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		  case R.id.btnShow:
			  if(editDate.getText().toString().equals("")){Toast.makeText(this.getActivity(), R.string.ErrDateLoad, Toast.LENGTH_LONG).show();break;}
			  db.openReadDB();
			  this.getActivity().getSupportLoaderManager().restartLoader(0, null, this);
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new MyCursorLoader(this.getActivity(),db,dateFrom);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
}
