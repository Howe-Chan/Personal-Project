package com.chain.morragame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chain.morragame.util.LoadingDialogUtil;
import com.chain.socket.code.Code102;
import com.chain.socket.code.Code102.OnReceiveResultListener;
import com.chain.socket.code.Code104;
import com.chain.socket.model.CMessage;
import com.chain.socket.model.GameMarks;
import com.chain.socket.model.GameResult;
import com.chain.socket.model.ICode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 游戏主界面
 * @author chain
 *
 */
public class GameMainActivity extends Activity implements OnClickListener,OnReceiveResultListener{
	
	//Application
	private APP mApp;
	//局数
	private int gameCount = 1;
	//用户分数
	private int userGrade = 0;
	//电脑分数
	private int computerGrade = 0;

	//TextView
	private TextView mTextView_count;//局数
	private TextView mTextView_userMarks;//用户分数
	private TextView mTextView_userName;//用户名
	private TextView mTextView_computerMarks;//电脑分数
	//ImageView
	private ImageView mImageView_userRollShot;//用户出拳图片滚动
	private ImageView mImageView_computerRollShot;//电脑出拳图片滚动
	//ImageButton
	private ImageButton mImageButton_jd;//剪刀
	private ImageButton mImageButton_st;//石头
	private ImageButton mImageButton_b;//布
	
	//处理结果
	private GameResult mResult;
	//加载
	private LoadingDialogUtil mLoadDialog;
	//获取数据失败
	private boolean isGetFail = true;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				//关闭加载框
				mLoadDialog.dismiss();
				//新建一个提示框
				new AlertDialog.Builder(GameMainActivity.this).setTitle("提示").setMessage("网络错误，请检查网络连接！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();//关闭页面
					}
				}).show();
				break;

			case 2:
				//关闭加载框
				mLoadDialog.dismiss();
				//新建一个提示框
				new AlertDialog.Builder(GameMainActivity.this).setTitle("提示").setMessage("服务器处理失败，请重试！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						finish();//关闭页面
					}
				}).show();
				break;
				
			case 3:
				//处理结果
				showResult();
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
		//设置页面
		setContentView(R.layout.activity_game_main);
		//Application
		mApp = (APP) getApplication();
		//加载框
		mLoadDialog = new LoadingDialogUtil(this);
		initview();
	}

	/**
	 * 初始化视图
	 */
	private void initview() {
		// TODO Auto-generated method stub
		//TextView
		mTextView_count = (TextView) findViewById(R.id.game_main_count_text);
		mTextView_userMarks = (TextView) findViewById(R.id.game_main_user_marks);
		mTextView_userName = (TextView) findViewById(R.id.game_main_user_name);
		mTextView_computerMarks = (TextView) findViewById(R.id.game_main_computer_marks);
		mTextView_count.setText(""+gameCount);
		mTextView_userMarks.setText(""+userGrade);
		mTextView_userName.setText(mApp.getUserName());
		mTextView_computerMarks.setText(""+computerGrade);
		//ImageView
		mImageView_userRollShot = (ImageView) findViewById(R.id.game_main_user_roll_shot);
		mImageView_computerRollShot = (ImageView) findViewById(R.id.game_main_computer_roll_shot);
		initRollImg();
		//ImageButton
		mImageButton_jd = (ImageButton) findViewById(R.id.game_main_jd_btn);
		mImageButton_st = (ImageButton) findViewById(R.id.game_main_st_btn);
		mImageButton_b = (ImageButton) findViewById(R.id.game_main_b_btn);
		//listener
		mImageButton_jd.setOnClickListener(this);
		mImageButton_st.setOnClickListener(this);
		mImageButton_b.setOnClickListener(this);
		
	}

	/**
	 * 初始化滚动图片
	 */
	private void initRollImg() {
		// TODO Auto-generated method stub
		//设置背景源
		mImageView_userRollShot.setBackgroundResource(R.drawable.user_roll_shot);
		//给背景设置成帧动画
		AnimationDrawable mUserAnimation = (AnimationDrawable) mImageView_userRollShot.getBackground();
		//开始滚动
		mUserAnimation.start();

		mImageView_computerRollShot.setBackgroundResource(R.drawable.computer_roll_shot);
		AnimationDrawable mComputerAnimation = (AnimationDrawable) mImageView_computerRollShot.getBackground();
		mComputerAnimation.start();
	}

	@Override
	public void onClick(View mview) {
		// TODO Auto-generated method stub
		switch (mview.getId()) {
			case R.id.game_main_jd_btn:
				sendMessage(1);
				break;

			case R.id.game_main_st_btn:
				sendMessage(2);				
				break;
	

			case R.id.game_main_b_btn:
				sendMessage(3);	
				break;
	
			default:
				break;
		}
	}
	
	/**
	 * 处理数据
	 */
	private void showResult() {
		// TODO Auto-generated method stub
		//创建AlertDialog
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(GameMainActivity.this);
		View mview = View.inflate(GameMainActivity.this, R.layout.game_result, null);
		TextView mTextView_resultCount = (TextView) mview.findViewById(R.id.game_result_count_text);//第几局
		ImageView mImageView_resultUserShot = (ImageView) mview.findViewById(R.id.game_result_user_shot);//用户出拳
		ImageView mImageView_resultComputerShot = (ImageView) mview.findViewById(R.id.game_result_computer_shot);//电脑出拳
		ImageView mImageView_resultImg = (ImageView) mview.findViewById(R.id.game_result_img);//此局结果
		
		mBuilder.setView(mview);
		//赋值
		mTextView_resultCount.setText(""+gameCount);
		switch (mResult.getUserShot()) {
			case 1:
				mImageView_resultUserShot.setImageResource(R.drawable.user_jd);
				break;
				
			case 2:
				mImageView_resultUserShot.setImageResource(R.drawable.user_st);
				break;
				
			case 3:
				mImageView_resultUserShot.setImageResource(R.drawable.user_b);
				break;
	
			default:
				break;
		}
		switch (mResult.getComputerShot()) {
			case 1:
				mImageView_resultComputerShot.setImageResource(R.drawable.computer_jd);
				break;
				
			case 2:
				mImageView_resultComputerShot.setImageResource(R.drawable.computer_st);
				break;
				
			case 3:
				mImageView_resultComputerShot.setImageResource(R.drawable.computer_b);
				break;
	
			default:
				break;
		}
		if(mResult.getResult() == 0){
			//平局
			gameCount++;
			mImageView_resultImg.setImageResource(R.drawable.dogfall);
		}else if(mResult.getResult() == 1){
			//用户赢
			gameCount++;
			userGrade++;
			mImageView_resultImg.setImageResource(R.drawable.user_win);
		}else if(mResult.getResult() == 2){
			//电脑赢
			gameCount++;
			computerGrade++;
			mImageView_resultImg.setImageResource(R.drawable.computer_win);
		}
		
		//设置标题名
		mBuilder.setTitle("游戏结果");
		mBuilder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
                //更新界面
				mTextView_count.setText(""+gameCount);
				mTextView_userMarks.setText(""+userGrade);
				mTextView_computerMarks.setText(""+computerGrade); 
                //关闭对话框
                dialog.cancel();
                //判断是否游戏结束
                if(computerGrade == 3){
                    showOver();
                }
			}
		});
        //显示创建好的对话框
        Dialog dialog = mBuilder.create();
        dialog.show();
	}

	/**
	 * 游戏结束
	 */
	private void showOver() {
		// TODO Auto-generated method stub
		//创建AlertDialog
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(GameMainActivity.this);
		View mview = View.inflate(GameMainActivity.this, R.layout.game_end_result, null);
		TextView mTextView_overCount = (TextView) mview.findViewById(R.id.game_end_result_count_text);//共几局
		TextView mTextView_overUserGrade = (TextView) mview.findViewById(R.id.game_end_result_user_grade_text);//用户赢几局
		
		mBuilder.setView(mview);
		//赋值
		mTextView_overCount.setText(""+gameCount--);
		mTextView_overUserGrade.setText(""+userGrade);
		//设置标题名
		mBuilder.setTitle("游戏结束");
		mBuilder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
                //上传分数
				GameMarks mGameMarks = new GameMarks();
				mGameMarks.setUserName(mApp.getUserName());
				mGameMarks.setGrade(userGrade);
				ICode mCode104 = new Code104();
				mCode104.send(mGameMarks);
                //关闭界面
                finish();
			}
		});
        //显示创建好的对话框
        Dialog dialog = mBuilder.create();
        dialog.show();
		
	}
	
	/**
	 * 发送命令
	 * @param userShot
	 */
	public void sendMessage(int userShot){
		try{
			//传出的参数
			String userName = mApp.getUserName();
			GameResult mGameResult = new GameResult();
			mGameResult.setUserShot(userShot);
			// 初始化命令102
			ICode mCode102 = new Code102(this);
			// 初始化ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 将GameResult对象转换成json字符串
			String content = objectMapper.writeValueAsString(mGameResult);
			// 包装
			CMessage msg = new CMessage();
			msg.setCode(mCode102.getCode());
			msg.setUserName(userName);
			msg.setContent(content);
			//发送
			mCode102.send(msg);
			//加载中
			mLoadDialog.show();
			
			//先假设获取失败
			isGetFail = true;
			//把之前有可能的runnable都去除掉
			mHandler.removeCallbacks(mRunnable);
			//10秒后如果没接收数据则提示消息
			mHandler.postDelayed(mRunnable, 10000);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//如果获取失败，则提示消息
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isGetFail){
				mHandler.sendEmptyMessage(1);
			}
		}
	};

	@Override
	public void receiveResultSuccess(GameResult gameResult) {
		// TODO Auto-generated method stub
		isGetFail = false;
		mLoadDialog.dismiss();
		if(gameResult==null){
			mHandler.sendEmptyMessage(2);
		}else{
			mResult = gameResult;
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
