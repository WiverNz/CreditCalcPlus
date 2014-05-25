package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
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

// скрыть клавиатуру при вводе даты
// получить результат datepicker для каждого edittext
public class MainFragment extends Fragment implements OnClickListener, OnEditorActionListener, OnDateSetListener, OnTouchListener, IUpdateData {
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(MainActivity.LOG_TAG, "onStop");
	}
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(MainActivity.LOG_TAG, "onDetach");

	}
	@Override
	public void onStart() {
		super.onStart();
		Log.d(MainActivity.LOG_TAG, "onStart");

	}
	@Override
	public void onPause() {
		super.onPause();
		Log.d(MainActivity.LOG_TAG, "onPause");
	}
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
			getActivity().finish();
			break;
		}
	}

	public void UpdateMainData()
	{
		UpdateStruct upd_struct = new UpdateStruct();
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
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
			// java.util.Locale.US);
			try {
				upd_struct.date.setTime(m_date_format.parse(currText));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}

		currText = etPartRepDate.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
			// java.util.Locale.US);
			try {
				upd_struct.partRepDate.setTime(m_date_format.parse(currText));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}

		currText = etPartRepSumm.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			upd_struct.partRepSumm = Integer.parseInt(currText);
		} else {
			upd_struct.partRepSumm = 0;
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
		
		switch(rgTypeOfRepayment.getCheckedRadioButtonId())
		{
		case R.id.rbPartRepPeriod:
			upd_struct.typePartRep = MainActivity.TYPE_PR_PERIOD;
			break;
		case R.id.rbPartRepDebt:
			upd_struct.typePartRep = MainActivity.TYPE_PR_DEBT;
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
		UpdateStruct upd_struct = new UpdateStruct();
		
		String currText = tv.getText().toString();
		switch(tv.getId())
		{
		case R.id.etSumma:
			if(TextUtils.isEmpty(currText) == false)
			{
				upd_struct.summa = Integer.parseInt(currText);
			}
			else
			{
				upd_struct.summa = 0;
			}
			break;
		case R.id.etPercent:
			if(TextUtils.isEmpty(currText) == false)
			{
				upd_struct.percent = Double.parseDouble(currText);
			}
			else
			{
				upd_struct.percent = 0;
			}
			break;
		case R.id.etPeriod:
			if(TextUtils.isEmpty(currText) == false)
			{
				upd_struct.period = Integer.parseInt(currText);
			}
			else
			{
				upd_struct.period = 0;
			}
			break;
		case R.id.etFirstDate:
			if(TextUtils.isEmpty(currText) == false)
			{
				//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);  
				try {
					upd_struct.date.setTime(m_date_format.parse(currText)); 
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
					upd_struct.partRepDate.setTime(m_date_format.parse(currText)); 
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.etPartRepSumm:
			if(TextUtils.isEmpty(currText) == false)
			{
				upd_struct.partRepSumm = Integer.parseInt(currText);
			}
			else
			{
				upd_struct.partRepSumm = 0;
			}
			break;
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
		
		switch(rgTypeOfRepayment.getCheckedRadioButtonId())
		{
		case R.id.rbPartRepPeriod:
			upd_struct.typePartRep = MainActivity.TYPE_PR_PERIOD;
			break;
		case R.id.rbPartRepDebt:
			upd_struct.typePartRep = MainActivity.TYPE_PR_DEBT;
			break;
		}
		
		updInterface.UpdateInputData(upd_struct);

		Log.d(MainActivity.LOG_TAG, "onEditorAction currValPeriod = " + upd_struct.period);
		
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
	public void UpdateInputData(UpdateStruct upd_struct) {
	    switch(upd_struct.type)
	    {
	    case MainActivity.TYPE_ANNUITY:
	    	rgTypeOfCredit.check(R.id.rbAnnuity);
	    	break;
	    case MainActivity.TYPE_DIFFERENTIATED:
	    	rgTypeOfCredit.check(R.id.rbVaried);
	    	break;
	    }
	    etSumma.setText(Integer.toString(upd_struct.summa));;
	    etPercent.setText(Double.toString(upd_struct.percent));;
	    etPeriod.setText(Integer.toString(upd_struct.period));;
	    etFirstDate.setText(m_date_format.format(upd_struct.date.getTime()));
	    
	    switch(upd_struct.typePartRep)
	    {
	    case MainActivity.TYPE_PR_PERIOD:
	    	rgTypeOfRepayment.check(R.id.rbPartRepPeriod);
	    	break;
	    case MainActivity.TYPE_PR_DEBT:
	    	rgTypeOfRepayment.check(R.id.rbPartRepDebt);
			break;
	    }
	    etPartRepDate.setText(m_date_format.format(upd_struct.partRepDate.getTime()));;
	    etPartRepSumm.setText(Integer.toString(upd_struct.partRepSumm));;
		
	}
}
