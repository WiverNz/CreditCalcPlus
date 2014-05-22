package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.DatePickerDialog.OnDateSetListener;

// скрыть клавиатуру при вводе даты
// получить результат datepicker для каждого edittext
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
    
    RadioGroup rgTypeOfRepayment;
    EditText etPartRepDate;
    EditText etPartRepSumm;
    
    Button btnSaveHistory;
    Button btnClose;
    Button btnSettings;
    
    IUpdateData updInterface;
    
    DatePickerFragment m_datePicker;
    
    SimpleDateFormat m_date_format;
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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        
        rgTypeOfCredit = (RadioGroup) rootView.findViewById(R.id.rgTypeOfCredit);
        
        etSumma = (EditText) rootView.findViewById(R.id.etSumma);
        etSumma.setOnEditorActionListener(this);
        
        etPercent = (EditText) rootView.findViewById(R.id.etPercent);
        etPercent.setOnEditorActionListener(this);
        
        etPeriod = (EditText) rootView.findViewById(R.id.etPeriod);
        etPeriod.setOnEditorActionListener(this);

        etFirstDate = (EditText) rootView.findViewById(R.id.etFirstDate);
        etFirstDate.setOnTouchListener(this);
        etFirstDate.setOnEditorActionListener(this);
        
        rgTypeOfRepayment = (RadioGroup) rootView.findViewById(R.id.rgTypeOfRepayment);
        
        etPartRepDate = (EditText) rootView.findViewById(R.id.etPartRepDate);
        etPartRepDate.setOnTouchListener(this);
        etPartRepDate.setOnEditorActionListener(this); 
        
        etPartRepSumm = (EditText) rootView.findViewById(R.id.etPartRepSumm);
        etPartRepDate.setOnEditorActionListener(this);
        
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
        
        return rootView;
    }

	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.btnSaveHistory:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnSettings:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnClose:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	@Override
	public boolean onEditorAction(TextView tv, int arg1, KeyEvent arg2) {
		int currType				= -1;
		int currValSumma			= -1;
		int currValPeriod			= -1;
		double currValPercent		= -1;
		Calendar currValFirstDate	= Calendar.getInstance();
		int currTypeOfRepayment		= -1;
		Calendar currValPartRepDate	= Calendar.getInstance();
		int currValPartRepSumm		= -1;
		
		String currText = tv.getText().toString();
		switch(tv.getId())
		{
		case R.id.etSumma:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValSumma = Integer.parseInt(currText);
			}
			else
			{
				currValSumma = 0;
			}
			break;
		case R.id.etPercent:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValPercent = Double.parseDouble(currText);
			}
			else
			{
				currValPercent = 0;
			}
			break;
		case R.id.etPeriod:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValPeriod = Integer.parseInt(currText);
			}
			else
			{
				currValPeriod = 0;
			}
			break;
		case R.id.etFirstDate:
			if(TextUtils.isEmpty(currText) == false)
			{
				//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);  
				try {
					currValFirstDate.setTime(m_date_format.parse(currText)); 
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.etPartRepDate:
			if(TextUtils.isEmpty(currText) == false)
			{
				//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);  
				try {
					currValPartRepDate.setTime(m_date_format.parse(currText)); 
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.etPartRepSumm:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValPartRepSumm = Integer.parseInt(currText);
			}
			else
			{
				currValPartRepSumm = 0;
			}
			break;
		}
		switch(rgTypeOfCredit.getCheckedRadioButtonId())
		{
		case R.id.rbAnnuity:
			currType = MainActivity.TYPE_ANNUITY;
			break;
		case R.id.rbVaried:
			currType = MainActivity.TYPE_DIFFERENTIATED;
			break;
		}
		
		switch(rgTypeOfRepayment.getCheckedRadioButtonId())
		{
		case R.id.rbPartRepPeriod:
			currTypeOfRepayment = MainActivity.TYPE_PR_PERIOD;
			break;
		case R.id.rbPartRepDebt:
			currTypeOfRepayment = MainActivity.TYPE_PR_DEBT;
			break;
		}
		
		updInterface.UpdateInputData(currType, currValPeriod, currValSumma, currValPercent, currValFirstDate, currTypeOfRepayment, currValPartRepDate, currValPartRepSumm);

		Log.d(MainActivity.LOG_TAG, "onEditorAction currValPeriod = " + currValPeriod);
		
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
					etCurrSelectedDateForm = etPartRepDate;
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
	
	public void hideSoftKeyboard() {

	}
	@Override
	public void UpdateInputData(int type, int period, int summa,
			double percent, Calendar date, int typePR, Calendar prDate, int prSumm) {
	    switch(type)
	    {
	    case MainActivity.TYPE_ANNUITY:
	    	rgTypeOfCredit.check(R.id.rbAnnuity);
	    	break;
	    case MainActivity.TYPE_DIFFERENTIATED:
	    	rgTypeOfCredit.check(R.id.rbVaried);
	    	break;
	    }
	    etSumma.setText(Integer.toString(summa));;
	    etPercent.setText(Double.toString(percent));;
	    etPeriod.setText(Integer.toString(period));;
	    etFirstDate.setText(m_date_format.format(date.getTime()));
	    
	    switch(typePR)
	    {
	    case MainActivity.TYPE_PR_PERIOD:
	    	rgTypeOfRepayment.check(R.id.rbPartRepPeriod);
	    	break;
	    case MainActivity.TYPE_PR_DEBT:
	    	rgTypeOfRepayment.check(R.id.rbPartRepDebt);
			break;
	    }
	    etPartRepDate.setText(m_date_format.format(prDate.getTime()));;
	    etPartRepSumm.setText(Integer.toString(prSumm));;
		
	}
}
