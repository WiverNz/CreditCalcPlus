package ru.wivern.creditcalcplus;


import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	OnDateSetListener m_onDateSet;
	private int m_year, m_month, m_day;
	private int m_id;
	
	public void setCallBack(OnDateSetListener ondate) {
		m_onDateSet = ondate;
	}
	
	public void setIdRes(int id)
	{
		m_id = id;
	}
	
	public int getIdRes()
	{
		return m_id;
	}
	
	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		m_year	= args.getInt("year");
		m_month	= args.getInt("month");
		m_day	= args.getInt("day");
		m_id	= args.getInt("id");
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), m_onDateSet, m_year, m_month, m_day);
    }
}
