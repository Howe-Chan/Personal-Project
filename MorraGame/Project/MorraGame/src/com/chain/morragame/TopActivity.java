package com.chain.morragame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.chain.morragame.util.LoadingDialogUtil;
import com.chain.socket.code.Code103;
import com.chain.socket.code.Code103.OnReceiveTopListener;
import com.chain.socket.model.GameMarks;
import com.chain.socket.model.ICode;

/**
 * 排行榜界面
 * @author chain
 *
 */
public class TopActivity extends Activity implements OnReceiveTopListener{
	
	//ListView
	private ListView mListView_item;//数据视图
	
	//加载
	private LoadingDialogUtil mLoadDialog;
	//查询结果
	List<GameMarks> mlist;
	//获取数据失败
	private boolean isGetFail = true;
	

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				mLoadDialog.dismiss();
				new AlertDialog.Builder(TopActivity.this).setTitle("提示").setMessage("网络错误，请检查网络连接！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
				break;

			case 2:
				mLoadDialog.dismiss();
				new AlertDialog.Builder(TopActivity.this).setTitle("提示").setMessage("数据库中没有数据！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();
					}
				}).show();
				break;
				
			case 3:
				//处理结果
				loadResult();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top);

		//加载框
		mLoadDialog = new LoadingDialogUtil(this);
		initview();
	}

	/**
	 * 初始化视图
	 */
	private void initview() {
		// TODO Auto-generated method stub
		//ListView
		mListView_item = (ListView) findViewById(R.id.top_listview_item);
		
		sendMessage();
	}

	/**
	 * 初始化列表
	 */
	private void sendMessage() {
		// TODO Auto-generated method stub
		//发送命令
		ICode code103 = new Code103(this);
		code103.send(null);
		//加载中
		mLoadDialog.show();
		//当发出消息后10秒没有任何操作则显示提示框
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isGetFail){
					mHandler.sendEmptyMessage(1);
				}
				isGetFail = true;
			}
		}, 10000);
	}

	/**
	 * 加载数据
	 */
	private void loadResult() {
		// TODO Auto-generated method stub
		//创建适配器
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.top_result_item, new String[] {"text1","text2","text3"},
				new int[] { R.id.result_item_text1, R.id.result_item_text2,R.id.result_item_text3 });
		//导入适配器
		mListView_item.setAdapter(adapter);
	}
	
	/**
	 * 解析数据
	 * @return
	 */
	private List<Map<String, Object>> getData() {
		// TODO Auto-generated method stub
		//创建list
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //每条数据(Map)
        Map<String, Object> map = new HashMap<String, Object>();
        //注入数据
    	if(mlist != null){
    		for(int i=0;i<mlist.size();i++){
    			Map<String,Object> mGameMarks = (Map<String, Object>) mlist.get(i);
    	        map = new HashMap<String, Object>();
    	        map.put("text1", i+1);
    	        map.put("text2", mGameMarks.get("userName"));
    	        map.put("text3", mGameMarks.get("grade"));
    	        
        	    list.add(map);
    		}
    	}
    	return list;
	}

	@Override
	public void receiveTopSuccess(List<GameMarks> list) {
		// TODO Auto-generated method stub
		isGetFail = false;
		mLoadDialog.dismiss();
		if(list==null){
			mHandler.sendEmptyMessage(2);
		}else{
			mlist = list;
			mHandler.sendEmptyMessage(3);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
