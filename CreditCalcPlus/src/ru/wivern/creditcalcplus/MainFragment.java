package ru.wivern.creditcalcplus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainFragment extends Fragment implements OnClickListener, OnEditorActionListener {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    EditText etSumma;
    EditText etPercent;
    EditText etPeriod;
    
    Button btnSaveHistory;
    Button btnClose;
    IUpdateData updInterface;
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
        
        etSumma = (EditText) rootView.findViewById(R.id.summa);
        etSumma.setOnEditorActionListener(this);
        
        etPercent = (EditText) rootView.findViewById(R.id.percent);
        etPercent.setOnEditorActionListener(this);
        
        etPeriod = (EditText) rootView.findViewById(R.id.period);
        etPeriod.setOnEditorActionListener(this);
       
//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        
        btnSaveHistory = (Button) rootView.findViewById(R.id.btnSaveHistory);
        btnSaveHistory.setOnClickListener(this);
        
        btnClose = (Button) rootView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        
        return rootView;
    }

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
		case R.id.btnSaveHistory:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnClose:
			Toast.makeText(getActivity(), "Пока не работает", Toast.LENGTH_SHORT).show();
			break;
		}
		if (updInterface != null)
		{
			updInterface.UpdateInputData(MainActivity.TYPE_ANNUITY, 1, 2, 3.4);
		}
	}
	
	@Override
	public boolean onEditorAction(TextView tv, int arg1, KeyEvent arg2) {
		int currValSumma		= -1;
		int currValPeriod		= -1;
		double currValPercent	= -1;
		String currText = tv.getText().toString();
		switch(tv.getId())
		{
		case R.id.summa:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValSumma = Integer.parseInt(currText);
			}
			else
			{
				currValSumma = 0;
			}
			break;
		case R.id.percent:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValPercent = Double.parseDouble(currText);
			}
			else
			{
				currValPercent = 0;
			}
			break;
		case R.id.period:
			if(TextUtils.isEmpty(currText) == false)
			{
				currValPeriod = Integer.parseInt(currText);
			}
			else
			{
				currValPeriod = 0;
			}
			break;
		}
		updInterface.UpdateInputData(MainActivity.TYPE_ANNUITY, currValPeriod, currValSumma, currValPercent);

		Log.d(MainActivity.LOG_TAG, "onEditorAction currValPeriod = " + currValPeriod);
		
		return false;
	}
}
