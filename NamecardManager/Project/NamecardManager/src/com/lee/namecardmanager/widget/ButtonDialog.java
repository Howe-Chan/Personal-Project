package com.lee.namecardmanager.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lee.namecardmanager.R;

/**
 * 按钮Dialog
 * @author lee
 *
 */
public class ButtonDialog extends Dialog{

	public ButtonDialog(Context context) {
		super(context);
	}

	public ButtonDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public ButtonDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder{
		
		private ButtonDialog mDialog;
		private Context mContext;

		private View mView;						//dialog视图
		private TextView mTextView_title;		//标题
		private LinearLayout mLayout_content;	//内容
		private Button mButton_left;			//左按钮
		private Button mButton_right;			//右按钮
		
		public Builder(Context context){
			this.mContext = context;
			initView();
		}

		/**
		 * 初始化视图
		 */
		@SuppressLint("InflateParams")
		private void initView() {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = mInflater.inflate(R.layout.dialog_button, null);
			mTextView_title = (TextView) mView.findViewById(R.id.dialog_button_title);
			mLayout_content = (LinearLayout) mView.findViewById(R.id.dialog_button_content);
			mButton_left = (Button) mView.findViewById(R.id.dialog_button_leftbtn);
			mButton_right = (Button) mView.findViewById(R.id.dialog_button_rightbtn);
		}
		
		/**
		 * 设置标题
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			mTextView_title.setText(title);
            return this;
        }

		/**
		 * 设置中间内容
		 * @param view
		 * @return
		 */
		public Builder setView(View view){
			mLayout_content.removeAllViews();//移除所有子视图
			//重新设置宽高
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			view.setLayoutParams(lp);
			mLayout_content.addView(view);
			return this;
		}

		/**
		 * 设置按钮1
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setLeftButton(String text, android.view.View.OnClickListener listener){
			mButton_left.setText(text);
			mButton_left.setOnClickListener(listener);
			return this;
		}

		/**
		 * 设置按钮2
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setRightButton(String text, android.view.View.OnClickListener listener){
			mButton_right.setText(text);
			mButton_right.setOnClickListener(listener);
			mButton_right.setVisibility(View.VISIBLE);
			return this;
		}
		
		/**
		 * 关闭dialog
		 */
		public void closeDialog(){
			if(mDialog != null){
				mDialog.dismiss();
			}
		}
		
		/**
		 * 创建
		 * @return
		 */
		@SuppressWarnings("static-access")
		public ButtonDialog create(){
			mDialog = new ButtonDialog(mContext, R.style.CustomDialog);
			mDialog.setContentView(mView);
			//设置dialog的大小
			android.view.WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
			lp.width = 600;//dp
			lp.height = lp.WRAP_CONTENT;
			mDialog.getWindow().setAttributes(lp);
			//设置点击除开dialog的其他地方不关闭dialog
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
			return mDialog;
		}
	}
}
