package deepble.navigation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	protected static final String MAIN = "main";
//	protected static final UUID UUID_USER_DEFINED_NAME =
//			UUID.fromString(ProximityGattAttributes.USER_DEFINED_NAME);
	protected static final String DEVICE_NAME = "device_name";
	protected static final String ANCHOR_DEVICE_NAME = "anchor_device_name";
	protected static final String IS_ANCHOR = "is_anchor";
	protected static final String TOAST = "toast";
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final int MAX_SUPPORTED = 7;
    
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
	
	private String mConnectedDeviceName;
    // String buffer for outgoing messages
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // Member object for the bluetooth services
    private BluetoothService mService;
    
    
//	 private BluetoothGattCharacteristic mUserName;
//	private BluetoothGattCharacteristic mIsAnchor;
	private SharedPreferences mPreferences;
	private Button mAnchorButton;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPreferences = getSharedPreferences(MAIN, MODE_PRIVATE);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
		mAnchorButton = (Button) findViewById(R.id.set_anchor);
//		mUserName = 
//				new BluetoothGattCharacteristic(UUID_USER_DEFINED_NAME,
//						BluetoothGattCharacteristic.PROPERTY_READ,
//						BluetoothGattCharacteristic.PERMISSION_WRITE);
//		String username = mPreferences.getString(DEVICE_NAME, "My Device");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mService == null) setup();
        }
    }
	
	public synchronized void onResume() { 
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth services
              mService.start();
            }
        }
        
        if(mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
        	mAnchorButton.setText(R.string.set_anchor);
        }

    }
	
	private void setup() {
        // Initialize the array adapter for the device thread
        mService = new BluetoothService(this, mHandler);
        sendMessage(mPreferences.getString(DEVICE_NAME, "My Device"));
    }
	@Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mService != null) mService.stop();
    }
    
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mService.write(send);
        }
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                }
                
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Editor editor = mPreferences.edit();
            	editor.putString(ANCHOR_DEVICE_NAME, readMessage);
            	editor.commit();
            	mService.stop();
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            } else {
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

	
	public void onClickAnchor(View view) {
		SharedPreferences.Editor editor = mPreferences.edit();
		String deviceName = mPreferences.getString(DEVICE_NAME, "My Device");
		editor.putString(ANCHOR_DEVICE_NAME, deviceName);
		editor.commit();
		if (mPreferences.getBoolean(IS_ANCHOR, false)){
			
			editor.putBoolean(IS_ANCHOR, true);
			editor.commit();
			ensureDiscoverable();
			mAnchorButton.setText("Anchored\n(Click to revert)");
		} else {
			editor.putBoolean(IS_ANCHOR, false);
			editor.commit();
			if (mBluetoothAdapter.getScanMode
			mAnchorButton.setText(R.string.set_anchor);		
		}
//		mIsAnchor = 
//				new BluetoothGattCharacteristic(UUID_USER_DEFINED_NAME,
//						BluetoothGattCharacteristic.PROPERTY_READ,
//						BluetoothGattCharacteristic.PERMISSION_WRITE);
	}
	
	public void onClickViewInformation(View view) {
		Intent intent = new Intent(mContext, DeviceContextActivity.class);
		startActivity(intent);
	}
	
	public void onClickViewDevices(View view){
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        
	}
	
	private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
}
