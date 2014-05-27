package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

// получить результат datepicker для каждого edittext не через etCurrSelectedDateForm
public class MainFragment extends Fragment implements OnClickListener, OnEditorActionListener, OnDateSetListener, OnTouchListener, IUpdateData {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int DATE_DIALOG_ID = 1;
    RadioGroup rgTypeOfCredit;
    EditText etSumma;
    EditText etPercent;
    EditText etPeriod;
    EditText etFirstDate;
    
    EditText etCurrSelectedDateForm;		// for Date dialog
    
    Button btnClearPartRep;
    Button btnAddPartRep;
    ViewGroup tblPartRep;
    
    TableLayout tblButtons;
    Button btnSaveHistory;
    Button btnClose;
    Button btnSettings;
    
    IUpdateData updInterface;
    
    DatePickerFragment m_datePicker;
    
    SimpleDateFormat m_date_format;
    
    ViewGroup m_container = null;
    
    int m_countOfPartRep = 0;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		updInterface = null;
		try {
			updInterface = (IUpdateData) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IUpdateData");
		}
	}
    public static MainFragment newInstance(int sectionNumber) {
    	//fragment=PlaceholderFragment.instantiate(getBaseContext(), MyClass1.class.getName());
    	MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        m_container = container;
        
        rgTypeOfCredit = (RadioGroup) rootView.findViewById(R.id.rgTypeOfCredit);
        
        etSumma = (EditText) rootView.findViewById(R.id.etSumma);
        etSumma.setOnEditorActionListener(this);
        //imm.hideSoftInputFromWindow(etSumma.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        
        etPercent = (EditText) rootView.findViewById(R.id.etPercent);
        etPercent.setOnEditorActionListener(this);
        
        etPeriod = (EditText) rootView.findViewById(R.id.etPeriod);
        etPeriod.setOnEditorActionListener(this);

        etFirstDate = (EditText) rootView.findViewById(R.id.etFirstDate);
        etFirstDate.setOnTouchListener(this);
        etFirstDate.setOnEditorActionListener(this);
        etFirstDate.setInputType(InputType.TYPE_NULL);
        
        btnClearPartRep = (Button) rootView.findViewById(R.id.btnClearPartRep);
        btnClearPartRep.setOnClickListener(this);
        
        btnAddPartRep = (Button) rootView.findViewById(R.id.btnAddPartRep);
        btnAddPartRep.setOnClickListener(this);
        
        tblPartRep = (ViewGroup) rootView.findViewById(R.id.tblPartRep);
        
        tblButtons = (TableLayout) rootView.findViewById(R.id.tblButtons);
        
        btnSaveHistory = (Button) rootView.findViewById(R.id.btnSaveHistory);
        btnSaveHistory.setOnClickListener(this);
        
        btnSettings = (Button) rootView.findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        
        btnClose = (Button) rootView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        
        m_datePicker = new DatePickerFragment();
        
        etCurrSelectedDateForm = null;
        
        m_date_format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        
        MainActivity ma = (MainActivity) this.getActivity();
        ma.UpdateFromFragment(this);
        ma.SetFragment(MainActivity.MAIN_FRAGMENT, this);
        
        return rootView;
    }

	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.btnAddPartRep:
			AddPartView();
			break;
		case R.id.btnClearPartRep:

			break;
		case R.id.btnSaveHistory:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnSettings:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnClose:
			getActivity().finish();
			break;
		}
	}

	public View AddPartView()
	{
		LayoutInflater layoutInflater = (LayoutInflater) this.getActivity().getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
		View childView = layoutInflater.inflate(R.layout.part_repay_table, null);
		childView.setId(m_countOfPartRep);
	    EditText etPartRepDate;
	    EditText etPartRepSumm;
        etPartRepDate = (EditText) childView.findViewById(R.id.etPartRepDate);
        etPartRepDate.setOnTouchListener(this);
        etPartRepDate.setOnEditorActionListener(this); 
        etPartRepDate.setInputType(InputType.TYPE_NULL);
        
        etPartRepSumm = (EditText) childView.findViewById(R.id.etPartRepSumm);
        etPartRepSumm.setOnEditorActionListener(this);
		tblPartRep.addView(childView);
		m_countOfPartRep = m_countOfPartRep + 1;
		tblButtons.requestLayout();	// Update text on buttons
		
		return childView;
	}
	
	public void SetViewByPartData(View currView, UpdateStruct.PartRepStruct prs)
	{
		if(currView == null)
		{
			return;
		}
	    RadioGroup rgTypeOfRepayment;
	    EditText etPartRepDate;
	    EditText etPartRepSumm;

        rgTypeOfRepayment = (RadioGroup) currView.findViewById(R.id.rgTypeOfRepayment);
        etPartRepDate = (EditText) currView.findViewById(R.id.etPartRepDate);
        etPartRepSumm = (EditText) currView.findViewById(R.id.etPartRepSumm);
        
	    
	    switch(prs.typePartRep)
	    {
	    case MainActivity.TYPE_PR_PERIOD:
	    	rgTypeOfRepayment.check(R.id.rbPartRepPeriod);
	    	break;
	    case MainActivity.TYPE_PR_DEBT:
	    	rgTypeOfRepayment.check(R.id.rbPartRepDebt);
			break;
	    }
	    etPartRepDate.setText(m_date_format.format(prs.partRepDate.getTime()));
	    etPartRepSumm.setText(Integer.toString(prs.partRepSumm));
	}
	
	public void ClearPartViews()
	{
		m_countOfPartRep = 0;
		tblPartRep.removeAllViews();
		tblButtons.requestLayout();	// Update text on buttons
	}
	
	public void UpdateMainData()
	{
		UpdateStruct upd_struct = new UpdateStruct();
		int i;
		String currText;

		currText = etSumma.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			upd_struct.summa = Integer.parseInt(currText);
		} else {
			upd_struct.summa = 0;
		}

		currText = etPercent.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			upd_struct.percent = Double.parseDouble(currText);
		} else {
			upd_struct.percent = 0;
		}

		currText = etPeriod.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			upd_struct.period = Integer.parseInt(currText);
		} else {
			upd_struct.period = 0;
		}

		currText = etFirstDate.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			try {
				upd_struct.date.setTime(m_date_format.parse(currText));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}

		for(i=0; i<m_countOfPartRep; i++)
		{
		    RadioGroup rgTypeOfRepayment;
		    EditText etPartRepDate;
		    EditText etPartRepSumm;
		    UpdateStruct.PartRepStruct prs = new UpdateStruct.PartRepStruct();
			View currView = tblPartRep.findViewById(i);
			if(currView == null)
			{
				continue;
			}
	        rgTypeOfRepayment = (RadioGroup) currView.findViewById(R.id.rgTypeOfRepayment);
	        etPartRepDate = (EditText) currView.findViewById(R.id.etPartRepDate);
	        etPartRepSumm = (EditText) currView.findViewById(R.id.etPartRepSumm);
	        
			currText = etPartRepDate.getText().toString();
			if (TextUtils.isEmpty(currText) == false) {
				try {
					prs.partRepDate.setTime(m_date_format.parse(currText));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			currText = etPartRepSumm.getText().toString();
			if (TextUtils.isEmpty(currText) == false) {
				prs.partRepSumm = Integer.parseInt(currText);
			} else {
				prs.partRepSumm = 0;
			}
			switch(rgTypeOfRepayment.getCheckedRadioButtonId())
			{
			case R.id.rbPartRepPeriod:
				prs.typePartRep = MainActivity.TYPE_PR_PERIOD;
				break;
			case R.id.rbPartRepDebt:
				prs.typePartRep = MainActivity.TYPE_PR_DEBT;
				break;
			}
			
			upd_struct.part.add(prs);
		}

		switch(rgTypeOfCredit.getCheckedRadioButtonId())
		{
		case R.id.rbAnnuity:
			upd_struct.type = MainActivity.TYPE_ANNUITY;
			break;
		case R.id.rbVaried:
			upd_struct.type = MainActivity.TYPE_DIFFERENTIATED;
			break;
		}
		
		if(updInterface != null)
		{
			updInterface.UpdateInputData(upd_struct);
		}

		Log.d(MainActivity.LOG_TAG, "UpdateMainData = " + upd_struct.period);
	}

	@Override
	public boolean onEditorAction(TextView tv, int arg1, KeyEvent arg2) {
		Calendar date = Calendar.getInstance();
		String currText = tv.getText().toString();
		switch(tv.getId())
		{
		case R.id.etFirstDate:
			if(TextUtils.isEmpty(currText) == false)
			{
				try {
					date.setTime(m_date_format.parse(currText)); 
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		
		return false;
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {

		Log.d(MainActivity.LOG_TAG, "Date = " + year + " month " + monthOfYear);
		if(etCurrSelectedDateForm != null)	// need to change this
		{
			etCurrSelectedDateForm.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		String currTitle = "";
		switch(event.getAction())
		{
		case MotionEvent.ACTION_UP:
			switch(v.getId())
			{
			case R.id.etFirstDate:
				if(TextUtils.isEmpty(currTitle) == true)
				{
					currTitle = this.getString(R.string.tvFirstDate);
					etCurrSelectedDateForm = etFirstDate;
				}
			case R.id.etPartRepDate:
				if(TextUtils.isEmpty(currTitle) == true)
				{
					currTitle = this.getString(R.string.tvPartRepDate);
					etCurrSelectedDateForm = (EditText) v;
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
	public void UpdateInputData(UpdateStruct upd_struct) {
		int i;
	    switch(upd_struct.type)
	    {
	    case MainActivity.TYPE_ANNUITY:
	    	rgTypeOfCredit.check(R.id.rbAnnuity);
	    	break;
	    case MainActivity.TYPE_DIFFERENTIATED:
	    	rgTypeOfCredit.check(R.id.rbVaried);
	    	break;
	    }
	    etSumma.setText(Integer.toString(upd_struct.summa));
	    etPercent.setText(Double.toString(upd_struct.percent));
	    etPeriod.setText(Integer.toString(upd_struct.period));
	    etFirstDate.setText(m_date_format.format(upd_struct.date.getTime()));
	    ClearPartViews();
	    for(i=0; i<upd_struct.part.size(); i++)
	    {
	    	View currView = AddPartView();
	    	SetViewByPartData(currView, upd_struct.part.get(i));
	    }
	}
}
