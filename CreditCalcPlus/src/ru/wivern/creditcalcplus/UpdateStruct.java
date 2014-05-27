package ru.wivern.creditcalcplus;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateStruct implements Parcelable {
	public int		type;
	public int		period;
	public int		summa;
	public double	percent;
	public Calendar	date;
	public ArrayList<PartRepStruct> part;
	
	public UpdateStruct()
	{
		type			= -1;
		period			= -1;
		summa			= -1;
		percent			= -1;
		date			= Calendar.getInstance();
		part			= new ArrayList<PartRepStruct>();
	}
	
	public static class PartRepStruct {
		public int		typePartRep;
		public Calendar partRepDate;
		public int		partRepSumm;
		public PartRepStruct()
		{
			typePartRep		= -1;
			partRepDate		= Calendar.getInstance();
			partRepSumm		= -1;
		}
	}
	public static final Parcelable.Creator<UpdateStruct> CREATOR = new Parcelable.Creator<UpdateStruct>() {
	    public UpdateStruct createFromParcel(Parcel in) {
	      return new UpdateStruct(in);
	    }

	    public UpdateStruct[] newArray(int size) {
	      return new UpdateStruct[size];
	    }
	  };
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		int i;
		parcel.writeInt(type);
		parcel.writeInt(period);
		parcel.writeInt(summa);
		parcel.writeDouble(percent);
		parcel.writeInt(date.get(Calendar.YEAR));
		parcel.writeInt(date.get(Calendar.MONTH));
		parcel.writeInt(date.get(Calendar.DAY_OF_MONTH));
		parcel.writeInt(part.size());
		for(i=0; i<part.size(); i++)
		{
			PartRepStruct prs = part.get(i);
			parcel.writeInt(prs.typePartRep);
			parcel.writeInt(prs.partRepDate.get(Calendar.YEAR));
			parcel.writeInt(prs.partRepDate.get(Calendar.MONTH));
			parcel.writeInt(prs.partRepDate.get(Calendar.DAY_OF_MONTH));
			parcel.writeInt(prs.partRepSumm);
		}
	}
	
	public UpdateStruct(Parcel parcel)
	{
		int year, month, day;
		int i, size_pr;
		type			= parcel.readInt();
		period			= parcel.readInt();
		summa			= parcel.readInt() + 1;
		percent			= parcel.readDouble();
		year			= parcel.readInt();
		month			= parcel.readInt();
		day				= parcel.readInt();
		date.set(year, month, day);
		size_pr			= parcel.readInt();
		if(part != null)
		{
			part.clear();
		}
		for(i=0; i<size_pr; i++)
		{
			PartRepStruct prs = new PartRepStruct();
			prs.typePartRep		= parcel.readInt();
			year				= parcel.readInt();
			month				= parcel.readInt();
			day					= parcel.readInt();
			prs.partRepDate.set(year, month, day);
			prs.partRepSumm		= parcel.readInt();
			part.add(prs);
		}

	}
}
