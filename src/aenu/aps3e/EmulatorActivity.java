package aenu.aps3e;
import android.app.*;
import android.os.*;
import android.view.*;

public class EmulatorActivity extends Activity
{
	{System.loadLibrary("e");}
	/*
	 enum class APS3E_VKC:u32{
	 none=0,
	 l,u,r,d,
	 square,cross,circle,triangle,
	 lsl,lsu,lsr,lsd,
	 rsl,rsu,rsr,rsd,
	 l1,l2,l3,
	 r1,r2,r3,
	 start,select,
	 ps,
	 };
	*/
	final View.OnTouchListener button_listener=new View.OnTouchListener(){

		void pressed(int code,boolean z){
			Emulator.get.key_event(code,z);
		}
		@Override
		public boolean onTouch(View v, MotionEvent e)
		{
			/*if(e.getAction()!=MotionEvent.ACTION_DOWN||
			 e.getAction()!=MotionEvent.ACTION_UP) return false;*/
			boolean pressed=e.getAction()==MotionEvent.ACTION_DOWN?true:false;
			if(!pressed&&e.getAction()!=MotionEvent.ACTION_UP)return false;
			switch(v.getId()){
				case R.id.pad_left:
					pressed(1,pressed);
					return true;
				case R.id.pad_up:
					pressed(2,pressed);
					return true;
				case R.id.pad_right:
					pressed(3,pressed);
					return true;
				case R.id.pad_down:
					pressed(4,pressed);
					return true;
				case R.id.button_start:
					pressed(23,pressed);
					return true;
				case R.id.button_select:
					pressed(24,pressed);
					return true;
				case R.id.button_x:
					pressed(6,pressed);
					return true;
				case R.id.button_o:
					pressed(7,pressed);
					return true;
				case R.id.button_square:
					pressed(5,pressed);
					return true;
				case R.id.button_triangle:
					pressed(8,pressed);
					return true;
			}
			return false;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		android.util.Log.e("aps3e_java","emu "+android.os.Process.myPid());
		
		//getActionBar().hide();
		setContentView(R.layout.emulator_view);
		GameFrameView gv=(GameFrameView)findViewById(R.id.emulator_view);
		gv.meta_info=(Emulator.MetaInfo)getIntent().getSerializableExtra("meta_info");
		
		final int[] ids={
			R.id.pad_left,
			R.id.pad_up,
			R.id.pad_right,
			R.id.pad_down,
			R.id.button_start,
			R.id.button_select,
			R.id.button_x,
			R.id.button_o,
			R.id.button_square,
			R.id.button_triangle,

		};
		//for(int id:ids)
		//	findViewById(id).setOnTouchListener(button_listener);
			
			
		//for(int id:ids)
		//	findViewById(id).setBackgroundColor(0);
		
        
		
		/*gv.setOnTouchListener(new View.OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					int action = event.getAction();
					if (action == MotionEvent.ACTION_DOWN) {
						//Emulator.get.key_event(6,true);
						Emulator.get.key_event(23,true);
						return true;
					} else if (action == MotionEvent.ACTION_MOVE) {
						// 处理移动事件
						return true;
					} else if (action == MotionEvent.ACTION_UP) {
						//Emulator.get.key_event(6,false);
						Emulator.get.key_event(23,false);
						return true;
					}
					return false;
				}
		});*/
	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		super.onStop();
		//Emulator.get.quit();
	}
	
	

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		System.exit(0);
	}
	
	
    
    /*{
        try{
        System.loadLibrary("emu");
        }catch(Exception e){
            Log.i("aps3e_java","??");
        }
    }*/
	
	
	
	
}
