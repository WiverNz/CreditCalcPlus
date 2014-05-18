package ru.wivern.creditcalcplus;

import java.util.Date;

public interface IUpdateData {
	void UpdateInputData(int type, int period, int summa, double percent, Date date, int typePR, Date prDate, int prSumm);
}
