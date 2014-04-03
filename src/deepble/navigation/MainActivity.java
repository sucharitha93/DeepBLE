package deepble.navigation;

import deepble.navigation.*;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	
	protected static final String MAIN = "main";
	protected static final String IS_ANCHOR = "is_anchor";
	
	private SharedPreferences mPreferences;
	private LinearLayout mMainLinearLayout;
	private Button mAnchorButton;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClickAnchor(View view) {
		if (!mPreferences.getBoolean(IS_ANCHOR, false)) {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putBoolean(IS_ANCHOR, true);
			editor.commit();
			mMainLinearLayout.setBackgroundColor(getResources().getColor(R.color.background_color));
			mAnchorButton.setText(getResources().getString(R.string.set_anchor));
			displayDialogs();
		} else {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putBoolean(IS_ANCHOR, false);
			editor.commit();
			mMainLinearLayout.setBackgroundColor(getResources().getColor(R.color.background_color_awake));
			mAnchorButton.setText(getResources().getString(R.string.set_device));
//			Intent intent = new Intent(mContext, NAME_OF_ANCHOR_ACTIVITY.class);
//			startActivity(intent);
		}
	}
	
	private void displayDialogs() {
		AlertDialog.Builder podcastDialogBuilder = new AlertDialog.Builder(mContext);
//		podcastDialogBuilder.setTitle(getResources().getString(R.string.));
//		podcastDialogBuilder.setMessage(getResources().getString(R.string.);
	}
}
