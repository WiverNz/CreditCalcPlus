package ru.wivern.creditcalcplus;

import java.util.Calendar;
import java.util.Date;

public interface IUpdateData {
	void UpdateInputData(int type, int period, int summa, double percent, Calendar date, int typePR, Calendar prDate, int prSumm);
}
