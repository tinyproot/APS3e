package aenu.aps3e;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import android.*;
import java.util.*;
import android.content.pm.*;
import android.content.*;
import android.app.*;
import java.io.*;
import android.content.res.*;

public class Application extends android.app.Application
{
	
	public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps!= null) {
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }
	
	public static void extractAssetsDir(Context context, String assertDir, File outputDir) {
        AssetManager assetManager = context.getAssets();
        try {
            // 创建输出目录，如果不存在
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String[] filesToExtract = assetManager.list(assertDir);
            if (filesToExtract!= null) {
                for (String file : filesToExtract) {
					File outputFile = new File(outputDir, file);
					if(outputFile.exists())continue;
					
                    InputStream in = assetManager.open(assertDir + "/" + file);
                    FileOutputStream out = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer))!= -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();
		getExternalFilesDir("logs").mkdirs();
		getExternalFilesDir("aps3e").mkdirs();
		
		if("aenu.aps3e".equals(getCurrentProcessName(this)))
			Logger.start_record(this);
			
		Thread.setDefaultUncaughtExceptionHandler(exception_handler);
    }
    
    static ExceptionHandler exception_handler=new ExceptionHandler();
    
    static class ExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread p1, Throwable p2){
            try
            {
                ByteArrayOutputStream err=new ByteArrayOutputStream();
                PrintStream print=new PrintStream(err);
                p2.printStackTrace(print);
                android.util.Log.e("aps3e_java",err.toString());
            }
            catch (Exception e)
            {}
        }

        
    }
}
