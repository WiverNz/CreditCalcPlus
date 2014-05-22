package ru.wivern.creditcalcplus;

import java.util.Calendar;

public class UpdateStruct {
	public int		type;
	public int		period;
	public int		summa;
	public double	percent;
	public Calendar	date;
	public int		typePartRep;
	public Calendar partRepDate;
	public int		partRepSumm;
	
	public UpdateStruct()
	{
		type			= -1;
		summa			= -1;
		period			= -1;
		percent			= -1;
		date			= Calendar.getInstance();
		typePartRep		= -1;
		partRepDate		= Calendar.getInstance();
		partRepSumm		= -1;
	}
}
