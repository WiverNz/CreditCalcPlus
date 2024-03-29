package ru.wivern.creditcalcplus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.wivern.creditcalcplus.UpdateStruct.PartRepStruct;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, IUpdateData {
	public static final int MAIN_FRAGMENT		= 0;
	public static final int TABLE_FRAGMENT		= 1;
	public static final int GRAPHIC_FRAGMENT	= 2;
	public static final int HISTORY_FRAGMENT	= 3;
	
	private static final int COUNT_FRAGMENTS	= HISTORY_FRAGMENT + 1;
	
	public static final int TYPE_ANNUITY		= 0;
	public static final int TYPE_DIFFERENTIATED	= 1;
	
	public static final int TYPE_PR_PERIOD	= 0;
	public static final int TYPE_PR_DEBT	= 1;
	
	public static final int TYPE_COMISSION_PERC_SUMM	= 0;
	public static final int TYPE_COMISSION_PERC_CRED	= 1;
	public static final int TYPE_COMISSION_RUB			= 2;
	
	private UpdateStruct m_data = new UpdateStruct();
	
	private Fragment m_listFragment[];
	
	public static final String LOG_TAG = "LOG_INFO";
	
	public static SimpleDateFormat m_date_format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
	
    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    public static Context m_context;
    
    public static final int NUM_OF_PAY_COLUMN			= 0;
    public static final int DATE_PAY_COLUMN				= 1;
    public static final int SUMM_PAY_COLUMN				= 2;
    public static final int SUMM_CREDIT_COLUMN			= 3;
    public static final int SUMM_PROCENT_COLUMN			= 4;
    public static final int SUMM_COMMISSION_COLUMN		= 5;
    public static final int SUMM_REST_FOR_PAY_COLUMN	= 6;
    public static final int SUMM_PART_RE_PAY_COLUMN		= 7;

    public static final int NUMB_OF_COLUMNS				= 8;
    
    SharedPreferences m_sp;
    
    public static String m_currLanguage = "default";
    private Locale m_locale;
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(UpdateStruct.class.getCanonicalName(), (Parcelable) m_data);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_sp = PreferenceManager.getDefaultSharedPreferences(this);
        UpdateLanguage();
        setContentView(R.layout.activity_main);
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        m_listFragment = new Fragment[COUNT_FRAGMENTS];
        
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        MainActivity.m_context = this;
        
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);	// do not show the keyboard on start
        if(savedInstanceState == null)
        {
        	SetTestData();
        }
        else
        {
        	m_data = (UpdateStruct) savedInstanceState.getParcelable(UpdateStruct.class.getCanonicalName());
        }
		Log.d(LOG_TAG, "MainActivity onCreate summa " + m_data.summa + " activity " + this.hashCode() + " size " + m_data.part.size());
		//m_sp.edit().clear().commit();
    }

	private void SetTestData() {
		m_data.type				= MainActivity.TYPE_ANNUITY;
		m_data.period			= 24;
		m_data.summa			= 120000;
		m_data.percent			= 15.9;
		m_data.date				= Calendar.getInstance();
		m_data.date.set(2014, 3, 27);
		m_data.comission		= 0;
		m_data.comission_type	= MainActivity.TYPE_COMISSION_PERC_SUMM;
		m_data.part				= new ArrayList<PartRepStruct>();
	}

	@Override
	public void UpdateInputData(UpdateStruct upd_struct) {
		int i;
		if(upd_struct.type >= 0)
		{
			m_data.type		= upd_struct.type;
		}
		if(upd_struct.period >= 0)
		{
			m_data.period	= upd_struct.period;
		}
		if(upd_struct.summa >= 0)
		{
			m_data.summa		= upd_struct.summa;
		}
		if(upd_struct.percent >= 0)
		{
			m_data.percent	= upd_struct.percent;
		}
		if(upd_struct.date != null)
		{
			m_data.date = upd_struct.date;
		}
		if(upd_struct.comission >= 0)
		{
			m_data.comission = upd_struct.comission;
		}
		if(upd_struct.comission_type >= 0)
		{
			m_data.comission_type = upd_struct.comission_type;
		}
		m_data.firstOnlyProc = upd_struct.firstOnlyProc;
		m_data.part.clear();
		for(i=0; i< upd_struct.part.size(); i++)
		{
			m_data.part.add(upd_struct.part.get(i));
		}

		Log.d(LOG_TAG, "MainActivity UpdateInputData summa " + m_data.summa + " activity " + this.hashCode() + " size " + upd_struct.part.size());
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
			Intent intent = new Intent();
	        intent.setClass(this, SetPreferenceActivity.class);
	        startActivityForResult(intent, 0); 
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdateFromFragment(Object currFragment)
    {
    	UpdateFragmentData(currFragment);
    }
    
    private void UpdateFragmentData(Object currFragment)
    {
    	IUpdateData updInterface = null;
        if(currFragment != null)
        {
    		try {
    			updInterface = (IUpdateData) currFragment;
    		} catch (ClassCastException e) {
    			throw new ClassCastException(currFragment.toString()
    					+ " must implement IUpdateData");
    		}
    		
    		if (updInterface != null)
    		{
    			updInterface.UpdateInputData(m_data);
    		}
        }
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
    	//Log.d(LOG_TAG, "onTabSelected " + tab.getPosition());
    	Object currFragment = null;
    	MainFragment mf = (MainFragment) m_listFragment[MAIN_FRAGMENT];
    	TableFragment tf = null;
    	GraphicFragment gf = null;
    	//HistoryFragment hf = null;
        switch (tab.getPosition())
        {
        case MAIN_FRAGMENT:
        	currFragment = mf;
    		break;
        case TABLE_FRAGMENT:
        	tf = (TableFragment) m_listFragment[TABLE_FRAGMENT];
        	if(tf != null)
        	{
	        	currFragment = tf;
	        	HideKeyboard();			// do not show the keyboard when open table fragment
	        	if(mf != null)
	        	{
	        		mf.UpdateMainData();
	        	}
	        	UpdateFragmentData(currFragment);
        	}
    		break;
        case GRAPHIC_FRAGMENT:
        	gf = (GraphicFragment) m_listFragment[GRAPHIC_FRAGMENT];
        	if(gf != null)
        	{
	        	currFragment = gf;
	        	HideKeyboard();			// do not show the keyboard when open table fragment
	        	if(mf != null)
	        	{
	        		mf.UpdateMainData();
	        	}
	        	UpdateFragmentData(currFragment);
        	}
    		break;
        case HISTORY_FRAGMENT:
        	//hf = (HistoryFragment) m_listFragment[HISTORY_FRAGMENT];
    		break;
        }

        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
            case MAIN_FRAGMENT:
            	m_listFragment[MAIN_FRAGMENT] = MainFragment.newInstance(MAIN_FRAGMENT);
                return m_listFragment[MAIN_FRAGMENT];
            case TABLE_FRAGMENT:
            	m_listFragment[TABLE_FRAGMENT] = TableFragment.newInstance(TABLE_FRAGMENT);
            	return m_listFragment[TABLE_FRAGMENT];
            case GRAPHIC_FRAGMENT:
            	m_listFragment[GRAPHIC_FRAGMENT] = GraphicFragment.newInstance(GRAPHIC_FRAGMENT);
                return m_listFragment[GRAPHIC_FRAGMENT];
            case HISTORY_FRAGMENT:
            	m_listFragment[HISTORY_FRAGMENT] = HistoryFragment.newInstance(HISTORY_FRAGMENT);
                return m_listFragment[HISTORY_FRAGMENT];
            }
            return null;
        }

        @Override
        public int getCount() {
            return COUNT_FRAGMENTS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case MAIN_FRAGMENT:
                    return getString(R.string.TITLE_MAIN).toUpperCase(l);
                case TABLE_FRAGMENT:
                    return getString(R.string.TITLE_TABLE).toUpperCase(l);
                case GRAPHIC_FRAGMENT:
                    return getString(R.string.TITLE_GRAPHIC).toUpperCase(l);
                case HISTORY_FRAGMENT:
                    return getString(R.string.TITLE_HISTORY).toUpperCase(l);
            }
            return null;
        }
    }
    public void HideKeyboard()
    {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	if (getCurrentFocus() != null) {
    		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    public void SetFragment(int position, Fragment currFrag) {
        switch (position)
        {
        case MAIN_FRAGMENT:
        	m_listFragment[MAIN_FRAGMENT] = currFrag;
        	break;
        case TABLE_FRAGMENT:
        	m_listFragment[TABLE_FRAGMENT] = currFrag;
        	break;
        case GRAPHIC_FRAGMENT:
        	m_listFragment[GRAPHIC_FRAGMENT] = currFrag;
        	break;
        case HISTORY_FRAGMENT:
        	m_listFragment[HISTORY_FRAGMENT] = currFrag;
        	break;
        }
    }
    
	public static ArrayList<SparseArray<Object>> UpdateArrayList(
			UpdateStruct upd_struct) {
		ArrayList<SparseArray<Object>> curr_data = new ArrayList<SparseArray<Object>>();
		int i, j;
		Calendar currDayOfPay = (Calendar) upd_struct.date.clone();
		Calendar prevDayOfPay = (Calendar) upd_struct.date.clone();
		int currNOfPeriods = upd_struct.period;

		double currMounhtPerc = upd_struct.percent / (12 * 100);
		double summOfMonthPay = 0;
		double currSummOfMonthPay = 0;
		double currRestForPay = upd_struct.summa;
		double currProcSumm = 0;
		double currCredSumm = 0;
		double currCommission = 0;
		double currPartRepSumm = 0;
		int currNOfPay = 0;
		if(currNOfPeriods <= 0)
		{
			return curr_data;
		}
		ArrayList<PartRepStruct> part = new ArrayList<PartRepStruct>();
		switch (upd_struct.type) {
		case MainActivity.TYPE_ANNUITY:
			if (upd_struct.firstOnlyProc == true) {
				currSummOfMonthPay = currRestForPay
						* (currMounhtPerc + currMounhtPerc
								/ (Math.pow(1 + currMounhtPerc,
										currNOfPeriods - 1) - 1));
			} else {
				currSummOfMonthPay = currRestForPay
						* (currMounhtPerc + currMounhtPerc
								/ (Math.pow(1 + currMounhtPerc, currNOfPeriods) - 1));
			}
			break;
		case MainActivity.TYPE_DIFFERENTIATED:
			if (upd_struct.firstOnlyProc == true) {
				if(currNOfPeriods > 0)
				{
					currCredSumm = currRestForPay / (currNOfPeriods);
				}
				else
				{
					currCredSumm = currRestForPay;
				}
			}
			else
			{
				currCredSumm = currRestForPay / currNOfPeriods;
			}
			break;
		}
		for (i = 0; i < currNOfPeriods; i++) {

			currPartRepSumm = 0;
			SparseArray<Object> currItem = new SparseArray<Object>();
			part.clear();
			for(j=0; j<upd_struct.part.size(); j++)
			{
				UpdateStruct.PartRepStruct prs = upd_struct.part.get(j);
				if(prs.partRepDate.compareTo(prevDayOfPay) > 0 && prs.partRepDate.compareTo(currDayOfPay) <= 0)
				{
					part.add(prs);
					currPartRepSumm = currPartRepSumm + prs.partRepSumm;
				}
			}
			currNOfPay = i + 1;

			switch (upd_struct.type) {
			case MainActivity.TYPE_ANNUITY:
				currProcSumm = currRestForPay * currMounhtPerc;
				if(upd_struct.firstOnlyProc == true)
				{
					if(i==0)
					{
						summOfMonthPay = currProcSumm;
					}
					else
					{
						summOfMonthPay = currSummOfMonthPay;
					}
				}
				else
				{
					summOfMonthPay = currSummOfMonthPay;
				}
				summOfMonthPay = Math.min(summOfMonthPay, currRestForPay + currProcSumm - currPartRepSumm);
				currCredSumm = summOfMonthPay - currProcSumm;
				break;
			case MainActivity.TYPE_DIFFERENTIATED:
				currProcSumm = currRestForPay * currMounhtPerc;
				if(upd_struct.firstOnlyProc == true)
				{
					if(i==0)
					{
						currCredSumm = 0;
					}
					else if (i==1)
					{
						if(currNOfPeriods - i > 0)
						{
							currCredSumm = currRestForPay / (currNOfPeriods - i);
						}
						else
						{
							currCredSumm = currRestForPay;
						}
					}
					
				}
				summOfMonthPay = currCredSumm + currProcSumm;
				break;
			}
			
			if(currRestForPay <= 0)
			{
				break;
			}
			else if(currRestForPay < currCredSumm)
			{
				currRestForPay = 0;
			}
			else
			{
				currRestForPay = currRestForPay - currCredSumm;
			}

			if(upd_struct.comission > 0)
			{
				switch(upd_struct.comission_type)
				{
				case MainActivity.TYPE_COMISSION_PERC_SUMM:
					currCommission = upd_struct.summa * upd_struct.comission / 100.;
					break;
				case MainActivity.TYPE_COMISSION_PERC_CRED:
					currCommission = currRestForPay * upd_struct.comission / 100.;
					break;
				case MainActivity.TYPE_COMISSION_RUB:
					currCommission = upd_struct.comission;
					break;
				}
				summOfMonthPay = summOfMonthPay + currCommission;
			}

			currItem.put(NUM_OF_PAY_COLUMN,			currNOfPay);									// Int
			currItem.put(DATE_PAY_COLUMN,			currDayOfPay.clone());							// Calendar
			currItem.put(SUMM_PAY_COLUMN,			summOfMonthPay + currPartRepSumm);				// Double
			currItem.put(SUMM_CREDIT_COLUMN,		currCredSumm);									// Double
			currItem.put(SUMM_PROCENT_COLUMN,		currProcSumm);									// Double
			currItem.put(SUMM_COMMISSION_COLUMN,	currCommission);								// Double
			currItem.put(SUMM_REST_FOR_PAY_COLUMN,	Math.abs(currRestForPay - currPartRepSumm));	// Double
			currItem.put(SUMM_PART_RE_PAY_COLUMN,	currPartRepSumm);								// Double

			curr_data.add(currItem);
			
			prevDayOfPay = (Calendar) currDayOfPay.clone();
			currDayOfPay.add(Calendar.MONTH, 1);
			
			if(part.size() > 0)
			{
				currRestForPay = currRestForPay - currPartRepSumm;
				for(PartRepStruct prs : part)
				{
					switch(prs.typePartRep)
					{
					case MainActivity.TYPE_PR_DEBT:
						switch(upd_struct.type)
						{
						case MainActivity.TYPE_ANNUITY:
							currSummOfMonthPay = currRestForPay
							* (currMounhtPerc + currMounhtPerc
									/ (Math.pow(1 + currMounhtPerc,
											currNOfPeriods - i - 1) - 1));
							break;
						case MainActivity.TYPE_DIFFERENTIATED:
							currCredSumm = currRestForPay / currNOfPeriods;
							break;
						}
						break;
					case MainActivity.TYPE_PR_PERIOD:
						switch(upd_struct.type)
						{
						case MainActivity.TYPE_ANNUITY:
							currNOfPeriods = currNOfPay + RoundDoubleToUpInt(Math.log(summOfMonthPay/(summOfMonthPay-currMounhtPerc*currRestForPay)) / Math.log(1 + currMounhtPerc));
							break;
						case MainActivity.TYPE_DIFFERENTIATED:
							break;
						}
						break;
					}
				}
			}
		}

		return curr_data;
	}
	
	static int RoundDoubleToUpInt(double val)
	{
		int retVal = (int) val;
		if(val > (double) retVal)
		{
			retVal = retVal + 1;
		}
		
		return retVal;
	}
	
	public static double CalcSummByColumn(ArrayList<SparseArray<Object>> curr_data, int nOfColumn)
	{
		double retVal = 0;
		int i;
		SparseArray<Object> currItem;
		Double currVal = (double) 0;
		for(i=0; i<curr_data.size(); i++)
		{
			currItem = curr_data.get(i);
			if(currItem != null)
			{
				try
				{
					currVal = (Double) currItem.get(nOfColumn);
				}					
				catch (ClassCastException e)
				{
					e.printStackTrace();
				}
				finally
				{
					retVal = retVal + currVal;
				}
			}
		}
		return retVal;
	}
	
	public static double CalcAverageByColumn(ArrayList<SparseArray<Object>> curr_data, int nOfColumn)
	{
		double retVal = 0;
		double n;
		int i;
		SparseArray<Object> currItem;
		Double currVal = (double) 0;
		n = 0;
		for(i=0; i<curr_data.size(); i++)
		{
			currItem = curr_data.get(i);
			if(currItem != null)
			{
				try
				{
					currVal = (Double) currItem.get(nOfColumn);
				}					
				catch (ClassCastException e)
				{
					e.printStackTrace();
				}
				finally
				{
					retVal = retVal + currVal;
					n = n + 1;
				}
			}
		}
		
		retVal = retVal / n;

		return retVal;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		m_locale = new Locale(m_currLanguage);
        Locale.setDefault(m_locale);
        Configuration config = new Configuration();
        config.locale = m_locale;
        getBaseContext().getResources().updateConfiguration(config, null);   
	}

	@Override
	protected void onResume() {
		super.onResume();
		UpdateLanguage();
	}

	private void UpdateLanguage()
	{	
		m_currLanguage	= m_sp.getString("language", "default");
        if (m_currLanguage.equals("default")) {m_currLanguage=getResources().getConfiguration().locale.getCountry();}
        m_locale = new Locale(m_currLanguage);
        Locale.setDefault(m_locale);
        Configuration config = new Configuration();
        config.locale = m_locale;
        getBaseContext().getResources().updateConfiguration(config, null);
	}
	
	public UpdateStruct GetUpdateStruct()
	{
		return m_data;
	}
}
