package ru.wivern.creditcalcplus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SetPreferenceActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefFragment()).commit();
	}

}
