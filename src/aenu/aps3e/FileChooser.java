
package aenu.aps3e;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.os.Handler;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.Manifest;
import java.util.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import aenu.proptest.*;


public class FileChooser extends Activity{
	
	public static final int REQUEST_INSTALL_FIRMWARE=777;
	public static final int REQUEST_INSTALL_GAME=778;

	{System.loadLibrary("e");}

	private File currentDir=Environment.getExternalStorageDirectory();

    private final View.OnClickListener goto_parent_dir_l=new View.OnClickListener(){
        @Override
        public void onClick(View p1){
            File parentFile=currentDir.getParentFile();
            if(parentFile!=null)
                changeDir(parentFile);
        }
    };
	private final AdapterView.OnItemClickListener item_click_l=new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position,long id)
		{
			File f=((FileAdapter)l.getAdapter()).getFile(position);
			if(f.isFile()){
				Intent intent = new Intent();
				intent.putExtra("path", f.getAbsolutePath());
				setResult(RESULT_OK, intent);
				finish(); 
				return;
				/*if(f.getName().equals("PS3UPDAT.PUP")){
					InstallFrimware.start_install_firmware(MainActivity.this,f.getAbsolutePath());
					return;
				}

				if(!f.getName().endsWith(".iso"))
					return;

				if(!InstallFrimware.done_f.exists()){
					Toast.makeText(MainActivity.this,"固件未安装",2000).show();
					return;
				}

				Emulator.MetaInfo meta_info=Emulator.get.meta_info_from(f.getAbsolutePath());

				if(meta_info.icon!=null&&!TMC.is_installed(meta_info.id)){
					TMC._install_iso(MainActivity.this,f.getAbsolutePath(),meta_info.id);
					return;
				}

				if(meta_info.icon!=null){
					//Intent intent = new Intent(MainActivity.this,EmulatorActivity.class);
					Intent intent = new Intent("aenu.intent.action.APS3E");
					intent.setPackage(getPackageName());

					meta_info.icon=null;
					meta_info.name=null;
					intent.putExtra("meta_info",meta_info);
					startActivity(intent);
				}*/
			}        
			else
				changeDir(f);
		}
	};
	
	int get_request_code(){
		return getIntent().getIntExtra("request_code",-1);
		
	}
	
	String get_request_title(){
		int request_code=get_request_code();
		if(request_code==REQUEST_INSTALL_FIRMWARE){
			return getString(R.string.request_title_INSTALL_FIRMWARE);//"选择固件(PS3UPDAT.PUP)";
		}
		
		if(request_code==REQUEST_INSTALL_GAME){
			return getString(R.string.request_title_INSTALL_GAME);//"选择一个PS3 ISO或PKG文件";
		}
		
		throw new RuntimeException();
	}

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
		
		getActionBar().setTitle(get_request_title());
		
        setContentView(R.layout.activity_chooser);

		findViewById(R.id.goto_parent_dir).setOnClickListener(goto_parent_dir_l);
		((ListView)findViewById(R.id.list_view)).setOnItemClickListener(item_click_l);
		
		changeDir(currentDir);
    }

    private void changeDir(File f){
        if(f.canRead())currentDir=f;
        ((TextView)findViewById(R.id.list_path_show))
			.setText(currentDir.getAbsolutePath());

        ((ListView)findViewById(R.id.list_view))
			.setAdapter(new FileAdapter(this,currentDir,get_request_code()));

    }

    private static class FileAdapter extends BaseAdapter {

        private static final FileFilter game_filter_=new FileFilter(){
            private final String suffix[]={
                ".iso",
				".pkg"
            };
            @Override
            public boolean accept(File p1){
                if(p1.isDirectory())
                    return true;
                final String name=p1.getName();
                for(String s:suffix)
                    if(name.endsWith(s))
                        return true;
                return false;
            }         
        };
		
		private static final FileFilter ps3_firmware_filter_=new FileFilter(){
            
            @Override
            public boolean accept(File p1){
                if(p1.isDirectory())
                    return true;
                final String name=p1.getName();
				if(name.equals("PS3UPDAT.PUP"))
					return true;
                return false;
            }         
        };

        private static final Comparator<File> comparator_ = new Comparator<File>(){
            final Collator collator =Collator.getInstance(); 
            @Override
            public int compare(File p1,File p2){
                if(p1.isDirectory()&&p2.isFile())
                    return -1;
                else if(p1.isFile()&&p2.isDirectory())
                    return 1;
                return collator.compare(p1.getName(),p2.getName());
            }
        };

        private File[] fileList_;
        private Context context_; 

        private FileAdapter(Context context,File file,int request_code){
            context_=context;
			
			if(request_code==REQUEST_INSTALL_GAME){
				fileList_=getFileList(file,game_filter_);
			}
			else if(request_code==REQUEST_INSTALL_FIRMWARE){
				fileList_=getFileList(file,ps3_firmware_filter_);
			}
		}

        private File[] getFileList(File dir,FileFilter filter){
            File[] files=dir.listFiles(filter);     

            Arrays.sort(files,comparator_);

            return files;
        }

        public File getFile(int pos){
            return fileList_[pos];
        }

        @Override
        public int getCount(){
            return fileList_.length;
        }

        @Override
        public Object getItem(int p1){
            return fileList_[p1];
        }

        @Override
        public long getItemId(int p1){
            return p1;
        }

        private LayoutInflater getLayoutInflater(){
            return (LayoutInflater)context_.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int pos,View curView,ViewGroup p3){

            File f=fileList_[pos];

            if(curView==null){
                curView=getLayoutInflater().inflate(R.layout.dir_entry,null);
            }


            ImageView icon=(ImageView)curView.findViewById(R.id.file_icon);

            icon.setImageResource(
                f.isDirectory()
                ?R.drawable.ic_folder
                :R.drawable.ic_file);
            
			TextView name=(TextView)curView.findViewById(R.id.file_tag);
            name.setText(f.getName());

            return curView;
        } 
    }//!FileAdapter
}
