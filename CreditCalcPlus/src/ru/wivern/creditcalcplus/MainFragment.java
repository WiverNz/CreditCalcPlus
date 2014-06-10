package ru.wivern.creditcalcplus;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

// need change datepicker to get edittext not from etCurrSelectedDateForm
public class MainFragment extends Fragment implements OnClickListener, OnEditorActionListener, OnDateSetListener, OnTouchListener, IUpdateData, OnSeekBarChangeListener, OnItemSelectedListener {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int DATE_DIALOG_ID = 1;
    // comment
    TableLayout	tblMData;
    RadioGroup	rgTypeOfCredit;
    EditText	etSumma;
    SeekBar		sbSumma;
    EditText	etPercent;
    EditText	etPeriod;
    EditText	etFirstDate;
    
    EditText	etComission;
    Spinner		spComissionType;
    
    CheckBox cbFirstOnlyProc;
    
    EditText etCurrSelectedDateForm;		// for Date dialog
    
    Button btnClearPartRep;
    Button btnAddPartRep;
    ViewGroup tblPartRep;
    
    TableLayout tblButtons;
    Button btnSaveHistory;
    Button btnClose;
    Button btnSettings;
    
    EditText etInMonth;
    EditText etOverPay;
    
    IUpdateData updInterface;
    
    DatePickerFragment m_datePicker;
    
    ViewGroup m_container = null;
    
    int m_countOfPartRep = 0;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    
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
        tblMData = (TableLayout) rootView.findViewById(R.id.tblMData);
        rgTypeOfCredit = (RadioGroup) rootView.findViewById(R.id.rgTypeOfCredit);
        
        etSumma = (EditText) rootView.findViewById(R.id.etSumma);
        etSumma.setOnEditorActionListener(this);
        //imm.hideSoftInputFromWindow(etSumma.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        
        sbSumma = (SeekBar) rootView.findViewById(R.id.sbSumma);
        sbSumma.setOnSeekBarChangeListener(this);
        sbSumma.setMax(1000000);
        //sbSumma.setOnEditorActionListener(this); 
        
        etPercent = (EditText) rootView.findViewById(R.id.etPercent);
        etPercent.setOnEditorActionListener(this);
        
        etPeriod = (EditText) rootView.findViewById(R.id.etPeriod);
        etPeriod.setOnEditorActionListener(this);

        etFirstDate = (EditText) rootView.findViewById(R.id.etFirstDate);
        etFirstDate.setOnTouchListener(this);
        etFirstDate.setOnEditorActionListener(this);
        etFirstDate.setInputType(InputType.TYPE_NULL);

        etComission = (EditText) rootView.findViewById(R.id.etComission);
        etComission.setOnEditorActionListener(this);
//        Resources res = getResources();
//        ArrayList<String> spComissionTypeList = new ArrayList<String>();
//        Collections.addAll(spComissionTypeList, res.getStringArray(R.array.spComissionTypeList));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_comission_item, getResources().getStringArray(R.array.spComissionTypeList));
        adapter.setDropDownViewResource(R.layout.spinner_comission_dropdown_item);
        spComissionType = (Spinner) rootView.findViewById(R.id.spComissionType);
        spComissionType.setAdapter(adapter);
        spComissionType.setPromptId(R.string.spComissionType);
        spComissionType.setSelection(0);
        spComissionType.setOnItemSelectedListener(this);
        
        cbFirstOnlyProc = (CheckBox) rootView.findViewById(R.id.cbFirstOnlyProc);
        //cbFirstOnlyProc.setOnTouchListener(this);
        
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
        
        etInMonth = (EditText) rootView.findViewById(R.id.etInMonth);
        etInMonth.setOnEditorActionListener(this); 
        
        etOverPay = (EditText) rootView.findViewById(R.id.etOverPay);
        etOverPay.setOnEditorActionListener(this); 
        
        m_datePicker = new DatePickerFragment();
        
        etCurrSelectedDateForm = null;
        
        MainActivity ma = (MainActivity) this.getActivity();
		updInterface = null;
		try {
			updInterface = (IUpdateData) ma;
		} catch (ClassCastException e) {
			throw new ClassCastException(ma.toString()
					+ " must implement IUpdateData");
		}
        ma.SetFragment(MainActivity.MAIN_FRAGMENT, this);
        ma.UpdateFromFragment(this);
        
        return rootView;
    }

	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
		case R.id.btnAddPartRep:
			AddPartView();
			break;
		case R.id.btnDelPartRep:
			Button currBtn = (Button) view;
			String currHint = currBtn.getHint().toString();
			if(TextUtils.isEmpty(currHint) == false)
			{
				int currId = Integer.parseInt(currHint);
				if(currId >= 0)
				{
					DelPartView(currId);
				}
			}
			break;
		case R.id.btnClearPartRep:
			ClearPartViews();
			break;
		case R.id.btnSaveHistory:;
		    
			DB db = new DB(this.getActivity());
			db.openWriteDB();
			db.addRec(GetUpdStructFromForm());
			db.close();
			break;
		case R.id.btnSettings:
			Intent intent = new Intent();
	        intent.setClass(getActivity(), SetPreferenceActivity.class);
	        startActivityForResult(intent, 0); 
			break;
		case R.id.btnClose:
			//getActivity().finish();
			System.exit(0);
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
	    Button btnDelPartRep;
        etPartRepDate = (EditText) childView.findViewById(R.id.etPartRepDate);
        etPartRepDate.setOnTouchListener(this);
        etPartRepDate.setOnEditorActionListener(this); 
        etPartRepDate.setInputType(InputType.TYPE_NULL);
        
        etPartRepSumm = (EditText) childView.findViewById(R.id.etPartRepSumm);
        etPartRepSumm.setOnEditorActionListener(this);
        
        btnDelPartRep = (Button) childView.findViewById(R.id.btnDelPartRep);
        btnDelPartRep.setHint("" + m_countOfPartRep);
        btnDelPartRep.setOnClickListener(this);
        
		tblPartRep.addView(childView);
		m_countOfPartRep = m_countOfPartRep + 1;
		tblButtons.requestLayout();	// Update text on buttons
		tblMData.requestLayout();
		
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
	    etPartRepDate.setText(MainActivity.m_date_format.format(prs.partRepDate.getTime()));
	    etPartRepSumm.setText(Integer.toString(prs.partRepSumm));
	}
	
	public void DelPartView(int currId)
	{
		//tblPartRep.removeViewAt(currId);
		View currView = tblPartRep.findViewById(currId);
		if(currView != null)
		{
			tblPartRep.removeView(currView);
			m_countOfPartRep = m_countOfPartRep - 1;
			tblButtons.requestLayout();	// Update text on buttons
			tblMData.requestLayout();
			UpdMonthPay();
		}
	}
	
	public void ClearPartViews()
	{
		m_countOfPartRep = 0;
		tblPartRep.removeAllViews();
		tblButtons.requestLayout();	// Update text on buttons
		tblMData.requestLayout();
		UpdMonthPay();
	}
	
	public UpdateStruct GetUpdStructFromForm()
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
				upd_struct.date.setTime(MainActivity.m_date_format.parse(currText));
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		
		currText = etComission.getText().toString();
		if (TextUtils.isEmpty(currText) == false) {
			upd_struct.comission = Double.parseDouble(currText);
		} else {
			upd_struct.comission = 0;
		}
		
		upd_struct.comission_type = spComissionType.getSelectedItemPosition();

		upd_struct.firstOnlyProc = cbFirstOnlyProc.isChecked();
		
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
					prs.partRepDate.setTime(MainActivity.m_date_format.parse(currText));
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
		
		return upd_struct;
	}
	
	public void UpdateMainData()
	{
		UpdateStruct upd_struct = GetUpdStructFromForm();

		if(updInterface != null)
		{
			updInterface.UpdateInputData(upd_struct);
			Log.d(MainActivity.LOG_TAG, "MainFragment UpdateMainData summa " + upd_struct.summa + " activity " + this.getActivity().hashCode() + " fragment " + this.hashCode()  + " size " + upd_struct.part.size());
		}
	}

	@Override
	public boolean onEditorAction(TextView tv, int arg1, KeyEvent arg2) {
		Calendar date = Calendar.getInstance();
		String currText = tv.getText().toString();
		int currValInt;
		switch(tv.getId())
		{
		case R.id.etSumma:
			if(TextUtils.isEmpty(currText) == true)
			{
				currValInt = 0;
			}
			else
			{
				try
				{
					currValInt = Integer.parseInt(currText);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					currValInt = 0;
				}
			}
			sbSumma.setProgress(currValInt);
			UpdMonthPay();
			break;
		case R.id.etFirstDate:
			if(TextUtils.isEmpty(currText) == false)
			{
				try {
					date.setTime(MainActivity.m_date_format.parse(currText)); 
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.etInMonth:
			Log.d(MainActivity.LOG_TAG, "MainFragment onEditorAction R.id.etInMonth before " + currText);
			currText = removeNotNumerics(currText);
			Log.d(MainActivity.LOG_TAG, "MainFragment onEditorAction R.id.etInMonth after " + currText);
			try
			{
				currValInt = Integer.parseInt(currText);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				currValInt = 0;
			}
			UpdSummByMonthPay(currValInt);
			break;
		default:
			UpdMonthPay();
			break;
		}
		
		return false;
	}

	private String removeNotNumerics(String src)
	{
		return src.replaceAll("[^\\d]", "");
	}

	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {

		Log.d(MainActivity.LOG_TAG, "Date = " + year + " month " + monthOfYear);
		if(etCurrSelectedDateForm != null)	// need to change this
		{
			etCurrSelectedDateForm.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
			UpdMonthPay();
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		String currTitle = "";
		String currText = "";
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
				currText = ((EditText)v).getText().toString();
				if(TextUtils.isEmpty(currTitle) == true)
				{
					currTitle = this.getString(R.string.tvPartRepDate);
					etCurrSelectedDateForm = (EditText) v;
				}
				
				Calendar calender = Calendar.getInstance();
				Bundle args = new Bundle();
				if (TextUtils.isEmpty(currText) == false) {
					try {
						calender.setTime(MainActivity.m_date_format.parse(currText));
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
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
	    sbSumma.setProgress(upd_struct.summa);
	    etPercent.setText(Double.toString(upd_struct.percent));
	    etPeriod.setText(Integer.toString(upd_struct.period));
	    etFirstDate.setText(MainActivity.m_date_format.format(upd_struct.date.getTime()));
	    etComission.setText(Double.toString(upd_struct.comission));
	    spComissionType.setSelection(upd_struct.comission_type);
	    cbFirstOnlyProc.setChecked(upd_struct.firstOnlyProc);
	    ClearPartViews();
	    for(i=0; i<upd_struct.part.size(); i++)
	    {
	    	View currView = AddPartView();
	    	SetViewByPartData(currView, upd_struct.part.get(i));
	    }
	    UpdMonthPay();
	    Log.d(MainActivity.LOG_TAG, "MainFragment UpdateInputData summa " + upd_struct.summa + " activity " + this.getActivity().hashCode() + " fragment " + this.hashCode() + " size " + upd_struct.part.size());
	}
	@Override
	public void onProgressChanged(SeekBar sb, int currVal, boolean fromUser) {
		switch (sb.getId()) {
		case R.id.sbSumma:
			if(fromUser == true)
			{
				currVal = sb.getProgress();
				currVal = currVal - currVal%1000;
				etSumma.setText(Integer.toString(currVal));
			}
			break;

		default:
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar sb) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar sb) {
		int currVal;
		switch (sb.getId()) {
		case R.id.sbSumma:
			currVal = sb.getProgress();
			currVal = currVal - currVal%1000;
			etSumma.setText(Integer.toString(currVal));
			UpdMonthPay();
			break;

		default:
			break;
		}
		
	}
	
	public void UpdMonthPay()
	{
		NumberFormat double_format = NumberFormat.getNumberInstance();
		double_format.setMaximumFractionDigits(0);
		double_format.setParseIntegerOnly(true);
		UpdateStruct upd_struct = GetUpdStructFromForm();
		ArrayList<SparseArray<Object>> curr_data = MainActivity.UpdateArrayList(upd_struct);
		double currProc = MainActivity.CalcAverageByColumn(curr_data, MainActivity.SUMM_PAY_COLUMN);
		double currRepColumn = MainActivity.CalcSummByColumn(curr_data, MainActivity.SUMM_PROCENT_COLUMN) + MainActivity.CalcSummByColumn(curr_data, MainActivity.SUMM_COMMISSION_COLUMN);
		try
		{
			etInMonth.setText(double_format.format((Double)currProc));
			etOverPay.setText(double_format.format((Double)currRepColumn));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}

	}
	
	private void UpdSummByMonthPay(int currValInt)
	{
		NumberFormat double_format = NumberFormat.getNumberInstance();
		double_format.setMaximumFractionDigits(0);
		UpdateStruct upd_struct = GetUpdStructFromForm();
		final double currMounhtPerc = upd_struct.percent / (12 * 100);
		double coef1 = 0;
		int summa = 0;
		switch(upd_struct.type)
		{
		case MainActivity.TYPE_ANNUITY:
			coef1 = 1. - 1./Math.pow(1 + currMounhtPerc, upd_struct.period);
			summa = (int)(((double) currValInt) * coef1 / currMounhtPerc);
			break;
		case MainActivity.TYPE_DIFFERENTIATED:
			summa = (int)((double)(currValInt) / (1./upd_struct.period + currMounhtPerc/2.));
			break;
		}
		upd_struct.percent	= (double)currValInt;
		upd_struct.summa	= summa;
		etSumma.setText(Integer.toString(summa));
		sbSumma.setProgress(summa);
		
		ArrayList<SparseArray<Object>> curr_data = MainActivity.UpdateArrayList(upd_struct);
		double currRepColumn = MainActivity.CalcSummByColumn(curr_data, MainActivity.SUMM_PROCENT_COLUMN) + MainActivity.CalcSummByColumn(curr_data, MainActivity.SUMM_COMMISSION_COLUMN);
		try
		{
			etOverPay.setText(double_format.format((Double)currRepColumn));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	@Override
	public void onDestroy() {
		Log.d(MainActivity.LOG_TAG, "MainFragment onDestroy activity " + this.getActivity().hashCode() + " fragment " + this.hashCode());
		super.onDestroy();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	@Override
	public void onPause() {
		super.onPause();
		UpdateMainData();
	}
}
