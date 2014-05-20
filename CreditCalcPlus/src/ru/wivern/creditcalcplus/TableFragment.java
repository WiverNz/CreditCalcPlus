package ru.wivern.creditcalcplus;

import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class TableFragment extends Fragment implements IUpdateData {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    GridView gvMain;
    public static final int DATE_PAY_COLUMN				= 0;
    public static final int SUMM_PAY_COLUMN				= 1;
    public static final int SUMM_CREDIT_COLUMN			= 2;
    public static final int SUMM_PROCENT_COLUMN			= 3;
    public static final int SUMM_COMMISSION_COLUMN		= 4;
    public static final int SUMM_ADD_COMMISSION_COLUMN	= 5;
    public static final int SUMM_REST_FOR_PAY_COLUMN	= 6;
    
    public static final int NUMB_OF_COLUMNS				= 7;
    
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
        gvMain	= (GridView) rootView.findViewById(R.id.gvMain);
        return rootView;
    }

	@Override
	public void UpdateInputData(int type, int period, int summa,
			double percent, Date date, int typePR, Date prDate, int prSumm) {
		Log.d(MainActivity.LOG_TAG, "UpdateInputData " + summa);
		gvMain.setAdapter(new DataTableAdapter(this));
	}
	
	public String GetData(int line, int column)
	{
		return "" + line + " " + column;
	}
	
	public class DataTableAdapter extends BaseAdapter {
		private TableFragment m_tableFragment;
		
		public DataTableAdapter(TableFragment tf)
		{
			m_tableFragment = tf;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int currLine = position / TableFragment.NUMB_OF_COLUMNS;
			int currColumn = position % TableFragment.NUMB_OF_COLUMNS;
			//String currText = TableFragment.GetData(currLine, currColumn);
			String currText = GetData(currLine, currColumn);
			return null;
		}
		
	}
}
