package ru.wivern.creditcalcplus;

import java.util.ArrayList;
import java.util.Collections;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.preference.PreferenceFragment;

public class PrefFragment extends PreferenceFragment {
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		// Change black background to white (suppress for setBackgroundDrawable)
    	if (preference!=null)
	    	if (preference instanceof PreferenceScreen)
	        	if (((PreferenceScreen)preference).getDialog()!=null)
	        		((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getActivity().getWindow().getDecorView().getBackground().getConstantState().newDrawable());
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		int i;
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_frag1);
		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		PreferenceCategory prefTablColScr = (PreferenceCategory) preferenceScreen.findPreference("categTablCol");
		ListPreference lpLanguage = (ListPreference) preferenceScreen.findPreference("language");
		lpLanguage.setSummary(this.getActivity().getBaseContext().getResources().getConfiguration().locale.getCountry());
		if(prefTablColScr != null)
		{
			Resources res = getResources();
	        ArrayList<String> colNames = new ArrayList<String>();
	        Collections.addAll(colNames, res.getStringArray(R.array.colNameArray));
	        for(i=0; i<colNames.size(); i++)
	        {
				CheckBoxPreference chb1 = new CheckBoxPreference(this.getActivity());
			    chb1.setKey("col" + (i));
			    chb1.setTitle(colNames.get(i));
			    chb1.setSummaryOn("on");
			    chb1.setSummaryOff("off");
				prefTablColScr.addPreference(chb1);
	        }
		}
	}
}
