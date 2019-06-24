package softard.com.aoademo;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AccessoryStatusActivity extends StatusMonitorActivity {

    private UsbManager usbManager;
    private ParcelFileDescriptor fileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        final UsbAccessory[] accessoryList = usbManager.getAccessoryList();

        if (accessoryList == null || accessoryList.length == 0) {
            dumpStatus("no accessory found");
        } else {
            openAccessory(accessoryList[0]);
        }
    }


    private void openAccessory(UsbAccessory accessory) {
        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {

            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            dumpStatus("try connect");
//            inStream = new FileInputStream(fd);
//            outStream = new FileOutputStream(fd);
//
//            new CommunicationThread().start();
//
//            sendHandler = new Handler() {
//                public void handleMessage(Message msg) {
//                    try {
//                        outStream.write((byte[]) msg.obj);
//                    } catch (final Exception e) {
//                        onError("USB Send Failed " + e.toString() + "\n");
//                    }
//                }
//            };
//
//            onConnected();
        } else {
            dumpStatus("could not connect");
        }
    }
}
