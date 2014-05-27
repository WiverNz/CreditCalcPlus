package ru.wivern.creditcalcplus;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class TableFragment extends Fragment implements IUpdateData {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private GridView gvMain;
    private TableLayout tblHeader;
    private TableLayout tblMain;
    
    public static final int NUM_OF_PAY					= 0;
    public static final int DATE_PAY_COLUMN				= 1;
    public static final int SUMM_PAY_COLUMN				= 2;
    public static final int SUMM_CREDIT_COLUMN			= 3;
    public static final int SUMM_PROCENT_COLUMN			= 4;
    public static final int SUMM_COMMISSION_COLUMN		= 5;
    public static final int SUMM_REST_FOR_PAY_COLUMN	= 6;

    public static final int NUMB_OF_COLUMNS				= 7;
    
    private static final int[] m_columnWidths = new int[] {8, 10, 10, 10, 10, 10, 10};
    private static final int m_rowHeight = 50;
    private static final int m_headerHeight = 60;
    
    private ArrayList<ArrayList<String>> m_data = new ArrayList<ArrayList<String>>();
    
    private int m_numbOfLines = 0;
    
    ArrayList<String> m_colNamesArrayList = new ArrayList<String>();
    public static AsyncUpdate m_au = null;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance(int sectionNumber) {
    	//fragment=PlaceholderFragment.instantiate(getBaseContext(), MyClass1.class.getName());
    	TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table, container, false);
        tblHeader	= (TableLayout) rootView.findViewById(R.id.tbl_header);
        tblMain		= (TableLayout) rootView.findViewById(R.id.tbl_maintable);
        
        Resources res = getResources();
        Collections.addAll(m_colNamesArrayList, res.getStringArray(R.array.colNameArray));
        
        CreateHeaderOfTable();
        
        MainActivity ma = (MainActivity) this.getActivity();
        ma.SetFragment(MainActivity.TABLE_FRAGMENT, this);
		if(m_au != null)
		{
			m_au.cancel(false);
			m_au = null;
		}
        return rootView;
    }

	@Override
	public void UpdateInputData(UpdateStruct upd_struct) {
		if(m_au != null)
		{
			m_au.cancel(false);
			m_au = null;
		}
		m_au = new AsyncUpdate();
		m_au.SetFragment(this);
		m_au.execute(upd_struct);
	}
	
	static class AsyncUpdate extends AsyncTask<UpdateStruct, TableRow, Void>
	{
		private TableFragment m_tf = null;
		
		public void SetFragment(TableFragment tf)
		{
			m_tf = tf;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			m_tf.tblMain.removeAllViews();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(UpdateStruct... update_structs) {
			UpdateStruct upd_struct = update_structs[0];
			if(upd_struct != null)
			{
				UpdateArrayList(upd_struct);
				UpdateTableViews();
			}
			return null;
		}

		private void UpdateArrayList(UpdateStruct upd_struct)
		{
			int i;
			Calendar currDayOfPay = null;
			SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
			for(i=0; i<m_tf.m_data.size(); i++)
			{
				m_tf.m_data.get(i).clear();
			}
			m_tf.m_data.clear();
			m_tf.m_numbOfLines = upd_struct.period;
			NumberFormat double_format = NumberFormat.getNumberInstance(); 
			double_format.setMaximumFractionDigits(2);
			switch(upd_struct.type)
			{
			case MainActivity.TYPE_ANNUITY:

				Log.d(MainActivity.LOG_TAG, "UpdateInputData " + upd_struct.summa);
				double currMounhtPerc = upd_struct.percent/(12 * 100);
				double summOfMonthPay = upd_struct.summa * (currMounhtPerc + currMounhtPerc/(Math.pow(1+currMounhtPerc, upd_struct.period) - 1));
				double currRestForPay = upd_struct.summa;
				for(i=0; i<m_tf.m_numbOfLines; i++)
				{
					currDayOfPay = (Calendar) upd_struct.date.clone();
					currDayOfPay.add(Calendar.MONTH, i);
					if (isCancelled()) return;
					ArrayList<String> currItem = new ArrayList<String>();
					int currNOfPay = i + 1;
					double currProcSumm = currRestForPay * currMounhtPerc;
					double currCredSumm = summOfMonthPay - currProcSumm;
					currRestForPay = currRestForPay - currCredSumm;
					currItem.add(Integer.toString(currNOfPay));						// NUM_OF_PAY
					currItem.add(date_format.format(currDayOfPay.getTime()));		// DATE_PAY_COLUMN
					currItem.add(double_format.format(summOfMonthPay));				// SUMM_PAY_COLUMN
					currItem.add(double_format.format(currCredSumm));				// SUMM_CREDIT_COLUMN
					currItem.add(double_format.format(currProcSumm));				// SUMM_PROCENT_COLUMN
					currItem.add("0");												// SUMM_COMMISSION_COLUMN
					currItem.add(double_format.format(currRestForPay));				// SUMM_REST_FOR_PAY_COLUMN
					
					m_tf.m_data.add(currItem);
				}
				break;
			}
		}
		
		private void UpdateTableViews()
		{
			int i, j;
			TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			BordersInfo biCurr = new BordersInfo();
			Context context = m_tf.getActivity();
			int screenWidth = m_tf.getResources().getDisplayMetrics().widthPixels;
			for(i=0; i<m_tf.m_data.size(); i++)
			{
				TableRow row = new TableRow(context);
		        row.setLayoutParams(wrapWrapTableRowParams);
		        row.setGravity(Gravity.CENTER);
	            row.setBackgroundColor(Color.BLACK);
				biCurr.SetUpDown(i, m_tf.m_numbOfLines, 1);
	            row.setPadding(0, biCurr.top, 0, biCurr.bottom);
	            ArrayList<String> currItem = m_tf.m_data.get(i);
	            
				for(j=0; j<currItem.size(); j++)
				{
					if (isCancelled()) return;
					biCurr.SetLeftRight(j, NUMB_OF_COLUMNS, 1);
					row.addView(TableFragment.makeTableRowWithText(context, screenWidth, currItem.get(j), m_columnWidths[0], m_rowHeight, biCurr, Color.WHITE));
				}
				publishProgress(row);
			}
		}
		
		@Override
		protected void onProgressUpdate(TableRow... table_rows) {
			super.onProgressUpdate(table_rows);
			if (isCancelled()) return;
			TableRow row = table_rows[0];
			if(row != null && m_tf != null)
			{
				m_tf.tblMain.addView(row);
			}
		}
		
	}

	private void CreateHeaderOfTable()
	{
		int i;
		tblHeader.removeAllViews();
		TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//wrapWrapTableRowParams.setMargins(10, 10, 0, 10);
		//wrapWrapTableRowParams.
		TableRow row = new TableRow(this.getActivity());
        row.setLayoutParams(wrapWrapTableRowParams);
        row.setGravity(Gravity.CENTER);
        row.setBackgroundColor(Color.BLACK);
        row.setPadding(0, 1, 0, 2);
        BordersInfo biCurr = new BordersInfo();
		Context context = this.getActivity();
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		for(i=0; i<NUMB_OF_COLUMNS; i++)
		{
			biCurr.top = 1;
			biCurr.bottom = 1;
			biCurr.SetLeftRight(i, NUMB_OF_COLUMNS, 1);
			row.addView(makeTableRowWithText(context, screenWidth, m_colNamesArrayList.get(i), m_columnWidths[0], m_headerHeight, biCurr, Color.YELLOW));
		}
		tblHeader.addView(row);
	}
	
	public static TextView makeTableRowWithText(Context context, int scrWidth, String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels, BordersInfo rcBorders, int bgColor) {
        
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(rcBorders.left, 0, rcBorders.right, 0);
        TextView recyclableTextView = new TextView(context);
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setTextSize(9);
        recyclableTextView.setGravity(Gravity.CENTER);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth * scrWidth / 100);
        recyclableTextView.setHeight(fixedHeightInPixels);
        recyclableTextView.setBackgroundColor(bgColor);
        recyclableTextView.setLayoutParams(params);
        return recyclableTextView;
    }
	
	static class BordersInfo {
		public int left;
		public int right;
		public int top;
		public int bottom;
		public void SetLeftRight(int currW, int maxW, int length)
		{
			if(currW == 0)
			{
				this.left	= length;
				this.right	= length;
			}
			else
			{
				this.left	= 0;
				this.right	= length;
			}
		}
		
		public void SetUpDown(int currH, int maxH, int length)
		{
			if(currH == 0)
			{
				this.bottom	= length;
				this.top	= 0;
			}
			else
			{
				this.top	= 0;
				this.bottom	= length;
			}
		}
	}
}
