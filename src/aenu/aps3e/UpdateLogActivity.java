package aenu.aps3e;
import android.app.*;
import android.os.*;
import android.widget.*;

public class UpdateLogActivity extends Activity{
	
	static String log="\n"
	+"0.1(2025-01-06)\n"
	+" *首个版本\n"
	+"0.2(2025-01-13)\n"
	+ " *修正socket无法创建的bug\n"
	+ " *添加更新日志\n"
	+ " *新的用户界面\n"
	+ " *修正了一个iso安装的bug\n"
	+ " *修正了cpu架构检测错误的bug\n"
	+ " *添加.nomedia禁止媒体存储扫描\n"
	+"0.3(2025-01-14)\n"
	+ " *游戏界面设置为全屏\n"
	+ " *日志行为修改\n"
	+ " *初步完善虚拟键盘\n"
	+ " *移除sharedUserId属性用于兼容安卓13+\n"
	+ " *修正了压缩纹理卡死的bug(目前纹理效果很糊😂)\n"
	+ " *虚拟键盘增加L2,L3,R2,R3\n"
	+"0.4(2025-01-17)\n"
	+ " *修正了统一缓冲区更新卡死的BUG(不会卡死了，但完全修正需要大改😭)\n"
	+ " *修正了多线程按键资源访问冲突导致的闪退\n"
	+ " *解决了卡奖杯的问题\n"
	+ " *BC纹理格式支持\n"
	+ " *虚拟键盘位置调整\n"
	+ " *加入了图标(Icons/ui/*)\n"
	+ " *游戏准备阶段消息变更\n"
	+"0.5\n"
	+ " *右摇杆控制修复\n"
	+ " *站点搭建完成，欢迎访问😄\n"
	+ " *支持pkg格式安装\n"
	+ " *初步的英语本地化支持\n"
	+ " \n";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		TextView  tv = new TextView(this);
        tv.setText(log);
		ScrollView sv=new ScrollView(this);
		sv.addView(tv);
        setContentView(sv);
	}
	
}
