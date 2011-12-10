package com.tizbac.usbh4xx0r;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class USBH4xx0rActivity extends Activity {
	USBManager uman;
	TextView isofile;
	private String[] mFileList;
	private File mPath = Environment.getExternalStorageDirectory();
	private String mChosenFile;
	private static final String FTYPE = ".iso";    
	private static final int DIALOG_LOAD_FILE = 1000;
	private void loadFileList(){
		  try{
		     mPath.mkdirs();
		  }
		  catch(SecurityException e){
		     Log.e("D", "unable to write on the sd card " + e.toString());
		  }
		  if(mPath.exists()){
		     FilenameFilter filter = new FilenameFilter(){
		         public boolean accept(File dir, String filename){
		             File sel = new File(dir, filename);
		             return filename.contains(FTYPE) || sel.isDirectory();
		         }
		     };
		     mFileList = mPath.list(filter);
		  }
		  else{
		    mFileList= new String[0];
		  }
		}
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		  AlertDialog.Builder builder = new Builder(this);

		  switch(id){
		  case DIALOG_LOAD_FILE:
		   builder.setTitle("Choose your file");
		   if(mFileList == null){
		     Log.e("D", "Showing file picker before loading the file list");
		     dialog = builder.create();
		     return dialog;
		   }
		     builder.setItems(mFileList, new DialogInterface.OnClickListener(){
		       public void onClick(DialogInterface dialog, int which){
		          mChosenFile = mPath.getPath()+"/"+mFileList[which];
		          uman.mountISOOnCDROM(mChosenFile);
		          
		          isofile.setText(uman.getMountedISOOnCDROM());
		       }
		      });
		  break;
		  }
		  dialog = builder.show();
		  return dialog;
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Vector<Integer> devs = new Vector<Integer>();
        devs.add(0);
        devs.add(1);
        uman = new USBManager(devs);
        final TextView um = (TextView)findViewById(R.id.txtusbmode);
        isofile = (TextView)findViewById(R.id.txtmiso);
        um.setText(uman.getCurrentUSBMode());
        isofile.setText(uman.getMountedISOOnCDROM());
        Button btumsmode = (Button)findViewById(R.id.switchtoUMS);
        btumsmode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				uman.SwitchToUMS();
				um.setText(uman.getCurrentUSBMode());
			}
		});
        Button btmtpmode = (Button)findViewById(R.id.switchtoMTP);
        btmtpmode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				uman.SwitchToMTP();
				um.setText(uman.getCurrentUSBMode());
				
			}
		});
        Button btmiso = (Button)findViewById(R.id.btmountisocd1);
        btmiso.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadFileList();
				showDialog(DIALOG_LOAD_FILE);
				
			}
		});
        
        
    }
}