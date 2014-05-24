package ru.wivern.creditcalcplus;


import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, IUpdateData {
	private static final int MAIN_FRAGMENT		= 0;
	private static final int TABLE_FRAGMENT		= 1;
	private static final int GRAPHIC_FRAGMENT	= 2;
	private static final int HISTORY_FRAGMENT	= 3;
	
	private static final int COUNT_FRAGMENTS	= HISTORY_FRAGMENT + 1;
	
	public static final int TYPE_ANNUITY		= 0;
	public static final int TYPE_DIFFERENTIATED	= 1;
	
	public static final int TYPE_PR_PERIOD	= 0;
	public static final int TYPE_PR_DEBT	= 1;
	
//	private int m_type;
//	private int m_period;
//	private int m_summa;
//	private double m_percent;
//	private Calendar m_date;
//	private int m_typePartRep;
//	private Calendar m_partRepDate;
//	private int m_partRepSumm;
	
	private UpdateStruct m_data = new UpdateStruct();
	
	private Fragment m_listFragment[];
	
	public static final String LOG_TAG = "LOG_INFO";
	
    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    public static Context m_context;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        
        SetTestData();
    }

	private void SetTestData() {
		m_data.type			= MainActivity.TYPE_ANNUITY;
		m_data.period		= 24;
		m_data.summa		= 120000;
		m_data.percent		= 15.9;
		m_data.date			= Calendar.getInstance();
		m_data.date.set(2014, 3, 27);
		m_data.typePartRep	= MainActivity.TYPE_PR_DEBT;
		m_data.partRepDate	= Calendar.getInstance();
		m_data.partRepSumm	= 0;
	}

	@Override
	public void UpdateInputData(UpdateStruct upd_struct) {
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
		if(upd_struct.typePartRep >= 0)
		{
			m_data.typePartRep = upd_struct.typePartRep;
		}
		if(upd_struct.partRepDate != null)
		{
			m_data.partRepDate = upd_struct.partRepDate;
		}
		if(upd_struct.partRepSumm >= 0)
		{
			m_data.partRepSumm = upd_struct.partRepSumm;
		}

		Log.d(LOG_TAG, "UpdateInputData type " + m_data.type + " period " + m_data.period + " summa " + m_data.summa + " percent " + m_data.percent
				+ " date " + m_data.date + " typePR " + m_data.typePartRep + " partRepDate " + m_data.partRepDate + " partRepSumm " + m_data.partRepSumm);
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
    	Log.d(LOG_TAG, "onOptionsItemSelected " + item.getItemId());
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
    	Log.d(LOG_TAG, "onTabSelected " + tab.getPosition());
    	Object currFragment = null;
    	MainFragment mf = (MainFragment) m_listFragment[MAIN_FRAGMENT];
    	TableFragment tf = null;
    	GraphicFragment gf = null;
    	HistoryFragment hf = null;
        switch (tab.getPosition())
        {
        case MAIN_FRAGMENT:
        	currFragment = mf;
    		break;
        case TABLE_FRAGMENT:
        	tf = (TableFragment) m_listFragment[TABLE_FRAGMENT];
        	currFragment = tf;
        	mf.UpdateMainData();
        	UpdateFragmentData(currFragment);
    		break;
        case GRAPHIC_FRAGMENT:
        	gf = (GraphicFragment) m_listFragment[GRAPHIC_FRAGMENT];
    		break;
        case HISTORY_FRAGMENT:
        	hf = (HistoryFragment) m_listFragment[HISTORY_FRAGMENT];
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
}
