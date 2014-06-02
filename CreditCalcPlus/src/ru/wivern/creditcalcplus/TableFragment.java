package ru.wivern.creditcalcplus;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

// ��� �������� ��������� asynctask
public class TableFragment extends Fragment implements IUpdateData {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private GridView gvMain;
    private TableLayout tblHeader;
    private TableLayout tblMain;
    
    private static final boolean[] m_columnVisible = new boolean[] {true, true, true, true, true, true, true, true};
    
    private static final int[] m_columnWidths = new int[] {4, 10, 10, 10, 10, 10, 10, 10};
    private static final int m_rowHeight = 25;
    private static final int m_headerHeight = 60;
    
    private ArrayList<SparseArray<Object>> m_data = new ArrayList<SparseArray<Object>>();
    
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
//		if(m_au != null)
//		{
//			m_au.cancel(false);
//			m_au = null;
//		}
        return rootView;
    }

	@Override
	public void UpdateInputData(UpdateStruct upd_struct) {
		if(m_au != null)
		{
			m_au.cancel(false);
//			try {
//				m_au.get();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (CancellationException e) {
//				e.printStackTrace();
//			}
			m_au = null;
		}
		m_au = new AsyncUpdate();
		m_au.SetFragment(this);
		m_au.execute(upd_struct);
	}
	
	static class AsyncUpdate extends AsyncTask<UpdateStruct, TableRow, Void>
	{
		private TableFragment m_tf = null;
		private UpdateStruct upd_struct = null;
		
		public void SetFragment(TableFragment tf)
		{
			m_tf = tf;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			m_tf.tblMain.removeAllViews();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(UpdateStruct... update_structs) {
			upd_struct = update_structs[0];
			if(upd_struct != null)
			{
				m_tf.m_data = MainActivity.UpdateArrayList(upd_struct);
				UpdateTableViews();
			}
			return null;
		}
		
		private void UpdateTableViews()
		{
			int i, j;
			int currColor=0;
			TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			NumberFormat double_format = NumberFormat.getNumberInstance();
			double_format.setMaximumFractionDigits(2);
			BordersInfo biCurr = new BordersInfo();
			Context context = m_tf.getActivity();
			final int color1 = Color.WHITE;
			final int color2 = context.getResources().getColor(R.color.blueback2);
			final int color3 = context.getResources().getColor(R.color.blueback3);
			int screenWidth = m_tf.getResources().getDisplayMetrics().widthPixels;
			AddSummRow(context, wrapWrapTableRowParams, screenWidth);
			Calendar currDayPay;
			Calendar prevDayPay;
			Calendar currDay = Calendar.getInstance();
			if(biCurr.UpdLeftRight() == false)
			{
				return;
			}
			for(i=0; i<m_tf.m_data.size(); i++)
			{
				TableRow row = new TableRow(context);
		        row.setLayoutParams(wrapWrapTableRowParams);
		        row.setGravity(Gravity.CENTER);
	            row.setBackgroundColor(Color.BLACK);
				biCurr.SetUpDown(i, m_tf.m_data.size(), 1);
	            row.setPadding(0, biCurr.top, 0, biCurr.bottom);
	            SparseArray<Object> currItem = m_tf.m_data.get(i);
	            if(i%2 == 0)
	            {
	            	currColor = color1;
	            }
	            else
	            {
	            	currColor = color2;
	            }
				currDayPay   = (Calendar) ((Calendar)currItem.get(MainActivity.DATE_PAY_COLUMN)).clone();
				prevDayPay   = (Calendar) currDayPay.clone();
				prevDayPay.add(Calendar.MONTH, -1);
				if(currDayPay.compareTo(currDay) >= 0 && prevDayPay.compareTo(currDay) < 0)
				{
					currColor = color3;
				}
				for(j=0; j<currItem.size(); j++)
				{
					if (isCancelled()) return;
	            	if(m_columnVisible[j] == false)
	            	{
	            		continue;
	            	}
					String currItemText = "";
					Object currItemCol = currItem.get(j);
					try
					{
						switch (j) {
						case MainActivity.NUM_OF_PAY_COLUMN:
							currItemText = ((Integer)currItemCol).toString();
							break;
						case MainActivity.DATE_PAY_COLUMN:
							currItemText = MainActivity.m_date_format.format(((Calendar)currItemCol).getTime());	
							break;
						case MainActivity.SUMM_PAY_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						case MainActivity.SUMM_CREDIT_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						case MainActivity.SUMM_PROCENT_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						case MainActivity.SUMM_COMMISSION_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						case MainActivity.SUMM_REST_FOR_PAY_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						case MainActivity.SUMM_PART_RE_PAY_COLUMN:
							currItemText = double_format.format((Double)currItemCol);
							break;
						}
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
					}
					catch (ClassCastException e)
					{
						e.printStackTrace();
					}
					if(TextUtils.isEmpty(currItemText) == true)
					{
						currItemText = "";
					}
					biCurr.SetLeftRight(j, 1);

					row.addView(TableFragment.makeTableRowWithText(context, screenWidth, currItemText, m_columnWidths[j], m_rowHeight, biCurr, currColor, 7));
				}
				publishProgress(row);
			}
		}
		
		private void AddSummRow(Context context, TableRow.LayoutParams wrapWrapTableRowParams, int screenWidth)
		{
			int i;
			double currVal = 0;
			BordersInfo biCurr = new BordersInfo();
			TableRow row = new TableRow(context);
	        row.setLayoutParams(wrapWrapTableRowParams);
	        row.setGravity(Gravity.CENTER);
            row.setBackgroundColor(Color.BLACK);
            row.setPadding(0, 0, 0, 1);
    		NumberFormat double_format = NumberFormat.getNumberInstance(); 
    		double_format.setMaximumFractionDigits(2);
    		if(biCurr.UpdLeftRight() == false)
    		{
    			return;
    		}
            for(i=0; i<MainActivity.NUMB_OF_COLUMNS; i++)
            {
            	if(m_columnVisible[i] == false)
            	{
            		continue;
            	}
            	String currItemText = "";
				switch (i) {
				case MainActivity.NUM_OF_PAY_COLUMN:
					currItemText = m_tf.getActivity().getString(R.string.textResult);
					break;
				case MainActivity.DATE_PAY_COLUMN:
					break;
				case MainActivity.SUMM_PAY_COLUMN:
					currVal = MainActivity.CalcSummByColumn(m_tf.m_data, i);
					currItemText = double_format.format(currVal);
					break;
				case MainActivity.SUMM_CREDIT_COLUMN:
					break;
				case MainActivity.SUMM_PROCENT_COLUMN:
					currVal = MainActivity.CalcSummByColumn(m_tf.m_data, i);
					currItemText = double_format.format(currVal);
					break;
				case MainActivity.SUMM_COMMISSION_COLUMN:
					currVal = MainActivity.CalcSummByColumn(m_tf.m_data, i);
					currItemText = double_format.format(currVal);
					break;
				case MainActivity.SUMM_REST_FOR_PAY_COLUMN:
					if(upd_struct != null)
					{
						currItemText = String.valueOf(upd_struct.summa);
					}

					break;
				}
            	biCurr.SetLeftRight(i, 1);
            	row.addView(TableFragment.makeTableRowWithText(context, screenWidth, currItemText, m_columnWidths[i], m_rowHeight, biCurr, Color.LTGRAY, 7));
            }
            publishProgress(row);
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
		if(biCurr.UpdLeftRight() == false)
		{
			return;
		}
		for(i=0; i<MainActivity.NUMB_OF_COLUMNS; i++)
		{
        	if(m_columnVisible[i] == false)
        	{
        		continue;
        	}
			biCurr.top = 1;
			biCurr.bottom = 1;
			biCurr.SetLeftRight(i, 1);
			row.addView(makeTableRowWithText(context, screenWidth, m_colNamesArrayList.get(i), m_columnWidths[i], m_headerHeight, biCurr, Color.YELLOW, 9));
		}
		tblHeader.addView(row);
	}
	
	public static TextView makeTableRowWithText(Context context, int scrWidth, String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels, BordersInfo rcBorders, int bgColor, int textSize) {
        
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(rcBorders.left, 0, rcBorders.right, 0);
        TextView recyclableTextView = new TextView(context);
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setTextSize(textSize);
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
		int leftFCol = -1;
		int rightLCol = -1;
		
		public boolean UpdLeftRight()
		{
			int i, j;
			leftFCol = -1;
			rightLCol = -1;
			for(i=0; i<MainActivity.NUMB_OF_COLUMNS; i++)
			{
				j = MainActivity.NUMB_OF_COLUMNS - i - 1;
	        	if(leftFCol == -1 && m_columnVisible[i] == true)
	        	{
	        		leftFCol = i;
	        	}
	        	if(rightLCol == -1 && m_columnVisible[j] == true)
	        	{
	        		rightLCol = j;
	        	}
			}
			if(leftFCol == -1 || rightLCol == -1)
			{
				return false;
			}
			
			return true;
		}
		
		public void SetLeftRight(int currW, int length)
		{
			if(currW == leftFCol)
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
