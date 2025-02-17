
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
import java.io.*;
import android.view.*;

public class MainActivity extends Activity{

	{System.loadLibrary("e");}
	
	private final static File GAME_DIR=new File("/storage/emulated/0/aps3e/config/dev_hdd0/game");
	
	private static final int MENU_ITEM_DELETE = Menu.FIRST;
	
	private final AdapterView.OnItemClickListener item_click_l=new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position,long id)
		{
			if(!InstallFrimware.done_f.exists()){
				//Toast.makeText(MainActivity.this,"固件未安装",2000).show();
				Toast.makeText(MainActivity.this,getString(R.string.firmware_not_install),2000).show();
				return;
			}
			
			Emulator.MetaInfo meta_info=((GameMetaInfoAdapter)l.getAdapter()).getMetaInfo(position);
			Intent intent = new Intent("aenu.intent.action.APS3E");
			intent.setPackage(getPackageName());
			
			intent.putExtra("meta_info",meta_info);
			startActivity(intent);
		}
	};

    private boolean request_perms(){
        final String[] perms=new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
		ArrayList<String> req_perms=new ArrayList<>();

        for(String perm:perms)
            if(checkSelfPermission(perm)!= PackageManager.PERMISSION_GRANTED)
                req_perms.add(perm);

        String[] req_perms_arr=new String[req_perms.size()];
        req_perms.toArray(req_perms_arr);
		if(req_perms.size()!=0){
            requestPermissions((String[])req_perms_arr,777);
			return true;
		}
		return false;
    }
	
	GameMetaInfoAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

		getActionBar().setTitle(getString(R.string.select_game));//"选择游戏");
		android.util.Log.e("aps3e_java","main");

        setContentView(R.layout.activity_main);

		((ListView)findViewById(R.id.game_list)).setOnItemClickListener(item_click_l);
		((ListView)findViewById(R.id.game_list)).setEmptyView(findViewById(R.id.game_list_is_empty));
		//((ListView)findViewById(R.id.game_list)).setOnLongClickListener(item_long_click_l);
		registerForContextMenu(findViewById(R.id.game_list));
		
		if(!request_perms()){
			initialize();
			refresh_game_list();
		}
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
	
	void request_file_select(int request_code){
		Intent it=new Intent(this,FileChooser.class);
		it.putExtra("request_code",request_code);
		startActivityForResult(it,request_code);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
			case R.id.menu_install_firmware:
                request_file_select(FileChooser.REQUEST_INSTALL_FIRMWARE);
                break;    
			case R.id.menu_install_game:
                request_file_select(FileChooser.REQUEST_INSTALL_GAME);
                break;    
			case R.id.menu_refresh_list:
                refresh_game_list();
                break;    
            case R.id.menu_about:
                startActivity(new Intent(this,HelloJni.class));
                break;       
			case R.id.menu_update_log:
				startActivity(new Intent(this,UpdateLogActivity.class));
				break;
        }
        return true;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=RESULT_OK) return;
		String path=data.getStringExtra("path");
		switch(requestCode){
			case FileChooser.REQUEST_INSTALL_GAME:{
				if(path.endsWith(".iso")){
				Emulator.MetaInfo mi=Emulator.get.meta_info_from_iso(path);
				if(mi.icon!=null&&mi.serial!=null){
					InstallGame.install_iso(this,path,mi.serial);
				}
				return;}
					else if(path.endsWith(".pkg")){
						InstallGame.install_pkg(this,path,null);
				}
				return ;
			}
			case FileChooser.REQUEST_INSTALL_FIRMWARE:{
				InstallFrimware.start_install_firmware(this,path);
				return;
			}
			default:
			return;
		}
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // TODO: Implement this method
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 777) {
			for(int result:grantResults){
				if(result != RESULT_OK){
					request_perms();
					return;
				}
			}
			initialize();
			refresh_game_list();
		}
    }


    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void finish(){
        super.finish();
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_ITEM_DELETE, 0, "删除");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		int position = info.position;
		switch (item.getItemId()) {
			case MENU_ITEM_DELETE:
				adapter.del(position);
				refresh_game_list();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
    private void initialize(){
		//private final File APP_DIR=new File(Environment.getExternalStorageDirectory(),"aps3e");

		String[] sub_dir_paths={
			"aps3e",
			"aps3e/config",
			"aps3e/config/dev_flash",
			"aps3e/config/dev_flash2",
			"aps3e/config/dev_flash3",
			"aps3e/config/dev_bdvd",
			"aps3e/config/dev_hdd0",
			"aps3e/config/dev_hdd1",
			"aps3e/config/dev_hdd0/game",
			"aps3e/config/Icons",
			"aps3e/config/Icons/ui",
		};

		for(String sp:sub_dir_paths){
			File dir=new File(Environment.getExternalStorageDirectory(),sp);
			if(!dir.exists()){
				dir.mkdir();
			}
		}
		
		File icons_ui_output_dir=new File(Environment.getExternalStorageDirectory(),"aps3e/config/Icons/ui");
		Application.extractAssetsDir(this,"Icons/ui",icons_ui_output_dir);
		
		File nomedia=new File(Environment.getExternalStorageDirectory(),"aps3e/.nomedia");
		try{
			if(!nomedia.exists())
				nomedia.createNewFile();
		}
		catch (IOException e)
		{}

        /*if(!APP_DIR.exists())
		 APP_DIR.mkdirs();*/
    }

    private void refresh_game_list(){
		if(!GAME_DIR.exists()){
			GAME_DIR.mkdirs();
		}
		adapter=new GameMetaInfoAdapter(this);
        ((ListView)findViewById(R.id.game_list))
			.setAdapter(adapter);
    }

    private static class GameMetaInfoAdapter extends BaseAdapter {
		
		private static final FileFilter filter_=new FileFilter(){
            
            @Override
            public boolean accept(File f){
                if(f.isDirectory()){
					File param=new File(f,"PARAM.SFO");
					if(!param.exists()){
						return false;
					}
					try{
						Emulator.get.meta_info_from_psf(param.getAbsolutePath());
						return true;
					}catch(Exception e){
						return false;
					}
					
				}else{
					return false;
				}
            }         
        };
		
		static byte[] read_png(File f) {
			try {
				FileInputStream fis = new FileInputStream(f);
				byte[] buffer = new byte[(int) f.length()];
				fis.read(buffer);
				return buffer;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		static void deleteDirectory(File directory) {
			if (directory.isDirectory()) {
				File[] files = directory.listFiles();
				if (files!= null) {
					for (File file : files) {
						if (file.isDirectory()) {
							deleteDirectory(file);
						} else {
							file.delete();
						}
					}
				}
			}
			directory.delete();
		}
        /*private static final Comparator<File> comparator_ = new Comparator<File>(){
            final Collator collator =Collator.getInstance(); 
            @Override
            public int compare(File p1,File p2){
                if(p1.isDirectory()&&p2.isFile())
                    return -1;
                else if(p1.isFile()&&p2.isDirectory())
                    return 1;
                return collator.compare(p1.getName(),p2.getName());
            }
        };*/

        private File[] fileList_;
		private Emulator.MetaInfo[] metas;
        private Context context_; 

        private GameMetaInfoAdapter(Context context){
            context_=context;
			fileList_=getFileList(GAME_DIR);
			this.metas=gen_metas(fileList_);
        }
		
		private File[] getFileList(File f){
            File[] files=f.listFiles(filter_);     
            return files;
        }
		
		private Emulator.MetaInfo[] gen_metas(File[] dirs){
			Emulator.MetaInfo[] metas=new Emulator.MetaInfo[dirs.length];
			for(int i=0;i<fileList_.length;i++){
				File param=new File(fileList_[i],"PARAM.SFO");
				metas[i]=Emulator.get.meta_info_from_psf(param.getAbsolutePath());
				File icon=new File(fileList_[i],"ICON0.PNG");
				metas[i].icon=read_png(icon);
				
			}
			return metas;
		}

        public File getGameDir(int pos){
            return fileList_[pos];
        }
		
		public Emulator.MetaInfo getMetaInfo(int pos){
            return metas[pos];
        }
		
		public void del(int pos){
			File dir=fileList_[pos];
			deleteDirectory(dir);
		}

        @Override
        public int getCount(){
            return metas.length;
        }

        @Override
        public Object getItem(int p1){
            return null;
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

            if(curView==null){
                curView=getLayoutInflater().inflate(R.layout.game_item,null);
            }
			
			Emulator.MetaInfo mi=metas[pos];

            ImageView icon=(ImageView)curView.findViewById(R.id.game_icon);
			if(mi.icon==null)
				icon.setImageResource(R.drawable.unknown);
			else
				icon.setImageBitmap(BitmapFactory.decodeByteArray(mi.icon,0,mi.icon.length));
			
			TextView name=(TextView)curView.findViewById(R.id.game_name);
			name.setText(mi.name);
			
            TextView version=(TextView)curView.findViewById(R.id.game_version);
			version.setText(mi.version);
			
			TextView serial=(TextView)curView.findViewById(R.id.game_serial);
			serial.setText(mi.serial);
			
			TextView category=(TextView)curView.findViewById(R.id.game_category);
			category.setText(mi.category);

            return curView;
        } 
    }//!FileAdapter
}
