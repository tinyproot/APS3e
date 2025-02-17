package aenu.aps3e;
import java.io.*;
import android.os.*;
import android.icu.text.*;
import android.content.*;

public class Logger
{ 
	public static void proc_info(){
		File proc_dir=new File("/proc/self");
		File[] files=proc_dir.listFiles();
	}
	
	public static void start_record(final Context ctx){
		new Thread(){
			public void run(){
				try
				{
					File log_file=new File(ctx.getExternalFilesDir("logs"),"aps3e_"+android.os.Process.myPid()+".txt");
					String cmd=String.format("/system/bin/logcat -f %s *:I",log_file.getAbsolutePath());
					Runtime.getRuntime().exec(cmd).getInputStream();
				}
				catch (IOException e)
				{
					try{
					File log_file=new File(ctx.getExternalFilesDir("logs"),"aps3e_fail.txt");
					if(!log_file.exists())log_file.createNewFile();
					FileOutputStream fout=new FileOutputStream(log_file,true);
					PrintStream print=new PrintStream(fout);
					e.printStackTrace(print);
					print.close();
					fout.close();
					}catch(Exception _e){}
				}
			}
		}.start();
	}
}
