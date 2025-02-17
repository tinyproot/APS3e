package aenu.aps3e;
import java.io.*;
import android.os.*;
import android.content.*;
import android.app.*;
import android.view.*;
import android.widget.*;

public class InstallGame
{
	private static final int INSTALL_FAILED=0xAE000000;
    private static final int INSTALL_DONE=0xAE000001;
	
	static File game_dir(String serial){
		return new File(Environment.getExternalStorageDirectory(),"aps3e/config/dev_hdd0/game/"+serial);
	}
	
	/*static boolean is_installed(String serial){
		File isted_f=new File(game_dir(serial),".installed");
		return isted_f.exists();
	}
	
	static void set_installed(String serial) throws IOException{
		File isted_f=new File(game_dir(serial),".installed");
		isted_f.createNewFile();
	}*/
	
    private final Handler install_firmware_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            load_dialog.hide();
            load_dialog.dismiss();
            load_dialog=null;

            install_firmware_thread=null;

            try
			{
				if (msg.what == INSTALL_FAILED){
					Toast.makeText(context, "加载游戏失败", 1000).show();
				}
				else if (msg.what == INSTALL_DONE)
					;//InstallGame.set_installed(serial);
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
				game_dir(serial).mkdirs();
				if(res_path.endsWith(".iso"))
				Emulator.get.inatall_iso(res_path,game_dir(serial).getAbsolutePath());
				else if(res_path.endsWith(".pkg"))
					Emulator.get.install_pkg(res_path);
				else
					throw new RuntimeException();
                //Emulator.get.install_firmware(firmware_path);
                install_firmware_handler.sendEmptyMessage(INSTALL_DONE);
            }catch(Exception e){
                ByteArrayOutputStream log_stream=new ByteArrayOutputStream();
                PrintStream print=new PrintStream(log_stream);
                e.printStackTrace(print);
                android.util.Log.e("aps3e_java",log_stream.toString());
                install_firmware_handler.sendEmptyMessage(INSTALL_FAILED);                        
            }
        }     
    };

	Context context;
	String res_path;
	String serial;

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

	void set(Context context,String res_path,String serial){
		this.context=context;
		this.res_path=res_path;
		this.serial=serial;
	}

	void call(){
		load_dialog=createLoadingDialog(context.getText(R.string.installing_game));
        load_dialog.show();
        install_firmware_thread.start();
	}

    public static synchronized void install_iso(Context view_ctx,String iso_path,String serial){
		InstallGame t=new InstallGame();
		t.set(view_ctx,iso_path,serial);
        t.call();
    }
	
	public static synchronized void install_pkg(Context view_ctx,String pkg_path,String serial){
		InstallGame t=new InstallGame();
		t.set(view_ctx,pkg_path,serial);
        t.call();
    }
}
