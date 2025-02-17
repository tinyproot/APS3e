package aenu.aps3e;
import android.os.*;
import android.app.*;
import android.content.*;
import android.view.*;
import java.io.*;
import android.widget.*;

public class InstallFrimware
{
    private static final int LOAD_FAILED=0xAA000000;
    private static final int LOAD_DONE=0xAA000001;
	
	static final File done_f=new File(Environment.getExternalStorageDirectory(),"aps3e/config/dev_flash/.installed");

    private final Handler install_firmware_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            load_dialog.hide();
            load_dialog.dismiss();
            load_dialog=null;

            install_firmware_thread=null;

            try
			{
				if (msg.what == LOAD_FAILED)
					Toast.makeText(context, "安装固件失败", 1000).show();
				else if (msg.what == LOAD_DONE)
					done_f.createNewFile();
				else
					android.util.Log.w("aps3e_java", "unknown message -- " + msg.what);
			}
			catch (Exception e)
			{}
        }       
    };

    
    private Thread install_firmware_thread=new Thread(){
        @Override
        public void run(){
            try{
                Emulator.get.install_firmware(firmware_path);
                install_firmware_handler.sendEmptyMessage(LOAD_DONE);
            }catch(Exception e){
                ByteArrayOutputStream log_stream=new ByteArrayOutputStream();
                PrintStream print=new PrintStream(log_stream);
                e.printStackTrace(print);
                android.util.Log.e("aps3e_java",log_stream.toString());
                install_firmware_handler.sendEmptyMessage(LOAD_FAILED);                        
            }
        }     
    };
	
	Context context;
	String firmware_path;
	
	private Dialog load_dialog;
	
	public final Dialog createLoadingDialog(CharSequence message){
        ProgressDialog d=new ProgressDialog(context);
        d.setMessage(message);
        d.setCanceledOnTouchOutside(false);
        d.setOnKeyListener(new DialogInterface.OnKeyListener(){
				@Override
				public boolean onKey(DialogInterface p1,int p2,KeyEvent p3){
					return true;
				}         
			});
        return d;
    }   
	
	void set(Context context,String firmware_path){
		this.context=context;
		this.firmware_path=firmware_path;
	}
	
	void call(){
		load_dialog=createLoadingDialog(context.getText(R.string.installing_firmware));
        load_dialog.show();
        install_firmware_thread.start();
	}

    public static synchronized void start_install_firmware(Context view_ctx,String firmware_path){
		InstallFrimware _if=new InstallFrimware();
		_if.set(view_ctx,firmware_path);
        _if.call();
    }

    
}
