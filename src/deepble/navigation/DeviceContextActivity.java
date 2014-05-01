package deepble.navigation;

import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import deepble.navigation.ProximityGattAttributes;

public class DeviceContextActivity extends Activity {
	
	protected static final String MAIN = "main";
	protected static final String DEVICE_NAME = "device_name";
	protected static final String ANCHOR_DEVICE_NAME = "anchor_device_name";
	protected static final String NAME_ENTERED = "name_entered";
	protected static final UUID UUID_USER_DEFINED_NAME =
            UUID.fromString(ProximityGattAttributes.USER_DEFINED_NAME);
	private SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_context);
		
		mPreferences = getSharedPreferences(MAIN, MODE_PRIVATE);
		final EditText mEdit = (EditText)findViewById(R.id.user_name);
		if (mPreferences.getBoolean(NAME_ENTERED, false)){
			mEdit.setText(mPreferences.getString(DEVICE_NAME, ""));
		}		
		mEdit.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				mEdit.setCursorVisible(true);
				mEdit.setSelected(true);
			}
		});
		mEdit.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
			    	if (actionId == EditorInfo.IME_ACTION_SEND) {
				    	mEdit.setCursorVisible(false);
				    	mEdit.setSelected(false);
			    	}
			        return handled;
			        
			}
		});
		TextView anchorTextView = (TextView)findViewById(R.id.user_anchor);
		String anchorName = mPreferences.getString(ANCHOR_DEVICE_NAME, "None");
		anchorTextView.setText(anchorName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.context, menu);
		return true;
	}
	
	public void onClickSave(View view){
		final EditText mEdit = (EditText)findViewById(R.id.user_name);
		SharedPreferences.Editor editor = mPreferences.edit();
		String device_name = mEdit.getText().toString();
		editor.putString(DEVICE_NAME, device_name);
		editor.putBoolean(NAME_ENTERED, true);
		editor.commit();

		finish();
	}

}
