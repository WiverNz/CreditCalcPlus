package ru.wivern.creditcalcplus;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

public class PrefFragment extends PreferenceFragment {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_frag1);
	}
}
