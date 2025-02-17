package aenu.aps3e;
import java.io.*;
import android.view.*;

public class Emulator
{
	public static class MetaInfo implements Serializable{
		String path;
		byte[] icon;
		String name;
		String serial;
		String category;
		String version;
	}
	
	public final static Emulator get=new Emulator();
	
	public native MetaInfo meta_info_from_iso(String p) throws RuntimeException;
	
	public native MetaInfo meta_info_from_psf(String p) throws RuntimeException;
	
	public native boolean install_firmware(String pup_file);
	
	public native boolean install_pkg(String pkg_file);
	
	public native boolean inatall_iso(String iso_path,String game_dir);
	
	public native void boot(Surface sf,MetaInfo info);
	
	public native void boot_disc(Surface sf,String iso_path);
	
	public native void key_event(int key_code,boolean pressed);
    
	public native void quit();
}
