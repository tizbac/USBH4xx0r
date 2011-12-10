package com.tizbac.usbh4xx0r;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.Vector;

import android.util.Log;

public class USBManager {
	Vector<Integer> devs = new Vector<Integer>();
	public USBManager(Vector<Integer> devices) {
		devs = (Vector<Integer>) devices.clone();
	}
	static private String convertStreamToString(InputStream is) { 
		try {
			if ( is.available() > 0 ){
				return new Scanner(is).useDelimiter("\\A").next();
			}else
			{
				return "[NONE]";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "[IOERROR]";
		}
	}
	private int GetFirstCDROM()
	{
		for ( int i = 0; i < devs.size(); i++ )
		{
			if ( devs.get(i) == 1 )
				return i;
			
		}
		return -1;
	}
	public String getMountedISOOnCDROM()
	{
		return getMountedFileForDevice(GetFirstCDROM());
		
	}
	public boolean mountISOOnCDROM(String file)
	{
		return MountFileToDevice(GetFirstCDROM(),file);
	}
	public String getMountedFileForDevice(int index)
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes(String.format("cat /sys/devices/platform/s3c-usbgadget/gadget/lun%d/file\n",index));
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				InputStream s = p.getInputStream();
				return convertStreamToString(s);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public String getCurrentUSBMode()
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("cat /sys/class/android_usb/android0/functions\n");
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				InputStream s = p.getInputStream();
				return convertStreamToString(s);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	public boolean MountFileToDevice(int index,String file)
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			String cmd1;
			cmd1 = String.format("echo \"%s\" > /sys/devices/platform/s3c-usbgadget/gadget/lun%d/file\n",file,index);
			Log.i("USBManager", cmd1);
			os.writeBytes(cmd1);
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if ( p.exitValue() != 255 )
					return true;
				else
					return false;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return false;
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean SwitchToUMS()
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("echo 0 > /sys/class/android_usb/android0/enable\n");
			os.writeBytes("echo 18d1 > /sys/class/android_usb/android0/idVendor\n");
			os.writeBytes("echo 4e22 > /sys/class/android_usb/android0/idProduct\n");
			os.writeBytes("echo mass_storage,adb > /sys/class/android_usb/android0/functions\n");
			os.writeBytes("echo 1 > /sys/class/android_usb/android0/enable\n");
			os.writeBytes("start adbd\n");
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if ( p.exitValue() != 255 )
					return true;
				else
					return false;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean SwitchToMTP()
	{
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("echo 0 > /sys/class/android_usb/android0/enable\n");
			os.writeBytes("echo 04e8 > /sys/class/android_usb/android0/idVendor\n");
			os.writeBytes("echo 6860 > /sys/class/android_usb/android0/idProduct\n");
			os.writeBytes("echo mtp,adb > /sys/class/android_usb/android0/functions\n");
			os.writeBytes("echo 1 > /sys/class/android_usb/android0/enable\n");
			os.writeBytes("start adbd\n");
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if ( p.exitValue() != 255 )
					return true;
				else
					return false;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
}
