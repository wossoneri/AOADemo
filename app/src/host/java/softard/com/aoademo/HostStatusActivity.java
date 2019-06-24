package softard.com.aoademo;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

public class HostStatusActivity extends StatusMonitorActivity {

    private final static String TAG = "WOW";
    UsbManager usbManager;
    UsbDevice currentDevice;
    boolean usbInited;
    private final BroadcastReceiver mUsbPermissionActionReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constraints.USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(
                            UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //user choose YES for your previously popup window asking for grant perssion for this usb device
                        if (null != usbDevice) {
                            afterGetUsbPermission(usbDevice);
                        }
                    } else {
                        //user choose NO for your previously popup window asking for grant perssion for this usb device
                        Toast.makeText(context,
                                String.valueOf("Permission denied for device" + usbDevice),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //host枚举所有设备
        HashMap<String, UsbDevice> usbList = usbManager.getDeviceList();
        dumpStatus("usbList size is " + usbList.size());

        if (searchForUsbAccessory(usbList)) {
            return;
        }

        for (UsbDevice device : usbList.values()) {
            initAccessory(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private boolean searchForUsbAccessory(final HashMap<String, UsbDevice> deviceList) {
        Log.i(TAG, "searchForUsbAccessory: deviceList=" + deviceList);
        for (UsbDevice device : deviceList.values()) {
            if (isUsbAccessory(device)) {
                Log.i(TAG, "searchForUsbAccessory(if (isUsbAccessory(device))): " + device);
                return true;
            }
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isUsbAccessory(final UsbDevice device) {
        Log.i(TAG, "isUsbAccessory: device=[name=" + device.getDeviceName() +
                ", manufacturerName=" + device.getManufacturerName() +
                ", productName=" + device.getProductName() +
                ", deviceId=" + device.getDeviceId() +
                ", productId=" + device.getProductId() +
                ", deviceProtocol=" + device.getDeviceProtocol() + "]");
        return (device.getProductId() == 0x2d00) || (device.getProductId() == 0x2d01);
    }

    private void initAccessory(final UsbDevice device) {
        usbInited = false;
        currentDevice = device;
        Log.i(TAG, "initAccessory: device=[name=" + device.getDeviceName() +
                ", manufacturerName=" + device.getManufacturerName() +
                ", productName=" + device.getProductName() +
                ", deviceId=" + device.getDeviceId() +
                ", productId=" + device.getProductId() +
                ", deviceProtocol=" + device.getDeviceProtocol() + "]");

        IntentFilter filter = new IntentFilter(Constraints.USB_PERMISSION);
        registerReceiver(mUsbPermissionActionReceiver, filter);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(Constraints.USB_PERMISSION), 0);

        if (usbManager.hasPermission(device)) {
            //if has already got permission, just goto connect it
            //that means: user has choose yes for your previously popup window asking for grant perssion for this usb device
            //and also choose option: not ask again
            afterGetUsbPermission(device);
        } else {
            //this line will let android popup window, ask user whether to allow this app to have permission to operate this usb device
            usbManager.requestPermission(device, mPermissionIntent);
        }

    }


    private void afterGetUsbPermission(UsbDevice usbDevice) {
        //call method to set up device communication
        //Toast.makeText(this, String.valueOf("Got permission for usb device: " + usbDevice), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, String.valueOf("Found USB device: VID=" + usbDevice.getVendorId() + " PID=" + usbDevice.getProductId()), Toast.LENGTH_LONG).show();

        doYourOpenUsbDevice(usbDevice);
    }

    private void doYourOpenUsbDevice(UsbDevice usbDevice) {
        //now follow line will NOT show: User has not given permission to device UsbDevice
        UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
        //add your operation code here


        Log.i(TAG, "initAccessory: conneciton=" + connection);
        if (connection == null) {
            usbInited = false;
        }

        int recv = connection.controlTransfer(0x80, 51, 0, 0, new byte[]{}, 0, 100);
        Log.i(TAG, "recv = " + recv);
        initStringControlTransfer(connection, 0, "quandoo"); // MANUFACTURER
        initStringControlTransfer(connection, 1, "Android2AndroidAccessory"); // MODEL
        initStringControlTransfer(connection, 2,
                "showcasing android2android USB communication"); // DESCRIPTION
        initStringControlTransfer(connection, 3, "0.1"); // VERSION
        initStringControlTransfer(connection, 4, "http://quandoo.de"); // URI
        initStringControlTransfer(connection, 5, "42"); // SERIAL

        recv = connection.controlTransfer(0x40, 53, 0, 0, new byte[]{}, 0, 100);
        Log.i(TAG, "recv = " + recv);
        connection.close();
        usbInited = true;
    }

    private void initStringControlTransfer(final UsbDeviceConnection deviceConnection,
                                           final int index,
                                           final String string) {
        Log.i(TAG, "initStringControlTransfer: deviceConnection=" + deviceConnection +
                ", index=" + index + ", string=" + string);
        int recv = deviceConnection.controlTransfer(0x40, 52, 0, index,
                string.getBytes(), string.length(), 100);
        Log.i(TAG, "recv = " + recv);
    }

}
