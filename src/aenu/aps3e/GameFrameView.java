package aenu.aps3e;
import android.view.SurfaceView;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.Surface;
import android.util.*;
import aenu.aps3e.Emulator;

public class GameFrameView extends SurfaceView implements SurfaceHolder.Callback
{
	public Emulator.MetaInfo meta_info;
	
    public GameFrameView(Context ctx){
        super(ctx);
        getHolder().addCallback(this);
    }
	
	public GameFrameView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context,attrs);
		//setLayerType(SurfaceView.LAYER_TYPE_SOFTWARE,null);
        getHolder().addCallback(this);
	}

    public GameFrameView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
		super(context,attrs,defStyleAttr);
        getHolder().addCallback(this);
		
	}

    public GameFrameView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context,attrs,defStyleAttr,defStyleRes);
        getHolder().addCallback(this);
	}
	

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
		if(meta_info==null){System.exit(1);}
		Emulator.get.boot(holder.getSurface(),meta_info);
    }

    @Override
    public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
    {
        // TODO: Implement this method
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder p1)
    {
        //Emulator.get.quit();
    }
};
