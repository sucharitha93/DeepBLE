package deepble.navigation;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
import java.util.ArrayList;

public class DeviceScanActivity extends Activity {
	// private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	@SuppressWarnings("unused")
    private boolean mScanning;
    private Handler mHandler;
    private LeDeviceListAdapter mLeDeviceListAdapter;
	
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

	@Override
	
	/* 
	 * Initializes Bluetooth adapter (enables devices to discover each other)
	 * 
	 * Ensures Bluetooth is available on the device and it it enabled. If not,
	 * a dialog requesting user permisison to enable Bluetooth is displayed.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		final BluetoothManager bluetoothManager =
		        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_scan);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_scan, menu);
		return true;
	}
	@SuppressWarnings("unused")
	private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
	/**
	 *  The interface used to deliver BLE scan results
	 *  If using an Adapter, the adapter will hold all the devices found
	 *  
	 *  notifyDataSetChanged() 
	 *  Notifies the attached observers that the underlying data has been 
	 *  changed and any View reflecting the data set should refresh itself.
	 *  	May not need this call, since we're not necessarily using ListActivity
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
	        new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, int rssi,
	            byte[] scanRecord) {
	    	runOnUiThread(new Runnable() {
	    		public void run() {
	    			//TODO - Look into LeDeviceListAdapter, what it does, why we need it
	    			mLeDeviceListAdapter = new LeDeviceListAdapter();
	    			mLeDeviceListAdapter.addDevice(device);
	    			//mLeDeviceListAdapter.notifyDataSetChanged();
	    		}
	    	});
	    }
	};
	
	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;
		
		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}
		
		public void addDevice(BluetoothDevice device) {
			if(!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}
		
		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}
		
		public void clear() {
			mLeDevices.clear();
		}
		
		@Override
		public int getCount() {
			return mLeDevices.size();
		}
		
		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}
		
		@Override
		public long getItemId(int i) {
			return i;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Possibly get back to this later.
			return null;
		}
	}

}
