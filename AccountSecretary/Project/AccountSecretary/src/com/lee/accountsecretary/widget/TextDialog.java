package com.lee.accountsecretary.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lee.accountsecretary.R;

/**
 * 文本提示框
 * @author lee
 *
 */
public class TextDialog extends Dialog{

	public TextDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public TextDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public TextDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static class Builder{
		
		private TextDialog mDialog;
		private Context mContext;

		private View mView;						//dialog视图
		private TextView mTextView_title;		//标题
		private TextView mTextView_content;		//内容
		private Button mButton_ok;			//左按钮
		
		public Builder(Context context){
			this.mContext = context;
			initView();
		}

		/**
		 * 初始化视图
		 */
		private void initView() {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = mInflater.inflate(R.layout.dialog_text, null);
			mTextView_title = (TextView) mView.findViewById(R.id.dialog_text_title);
			mTextView_content = (TextView) mView.findViewById(R.id.dialog_text_content);
			mButton_ok = (Button) mView.findViewById(R.id.dialog_text_okbtn);
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
		 * 设置内容
		 * @param msg
		 * @return
		 */
		public Builder setContent(String msg){
			mTextView_content.setText(msg);
            return this;
		}

		/**
		 * 设置按钮1
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setButton(String text, android.view.View.OnClickListener listener){
			mButton_ok.setText(text);
			mButton_ok.setOnClickListener(listener);
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
		public TextDialog create(){
			mDialog = new TextDialog(mContext, R.style.CustomDialog);
			mDialog.setContentView(mView);
			//设置dialog的大小
			android.view.WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
			lp.width = 400;//dp
			lp.height = lp.WRAP_CONTENT;
			mDialog.getWindow().setAttributes(lp);
			//设置点击除开dialog的其他地方不关闭dialog
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
			return mDialog;
		}
	}
}
