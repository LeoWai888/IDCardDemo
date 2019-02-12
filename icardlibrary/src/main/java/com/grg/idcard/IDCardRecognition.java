package com.grg.idcard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.decard.NDKMethod.BasicOper;

/**
 * 
 *身份证识别
 * 
 *@author zjxin2 on 2016-03-24
 *@version  
 *
 */
public class IDCardRecognition extends Thread {
	private String TAG = getClass().getSimpleName();
	private Context mContext;
	private boolean running = false;

	private boolean isAllowReadCard = true;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mIDCardRecListener.onResp((IDCardMsg) msg.obj);
			}
		};
	};

	public IDCardRecognition(Context context, IDCardRecListener listener) {
		mContext = context;
		mIDCardRecListener = listener;
		connectIdCard();
//		mFileUtils = new FileUtils();
	}

	public static int connectIdCard() {
		int openResult = BasicOper.dc_open("COM", null, "/dev/ttyS1", 115200);
		if (openResult < 0) {
			throw  new RuntimeException("身份证初始化失败");
		}
		return openResult;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void close() {
		this.running = false;
	}

	@Override
	public void run() {
		running = true;
		Looper.prepare();
		IDCardManager mIDCardManager = new IDCardManager();
		while (this.running) {
			if (isAllowReadCard) {
				//GrgLog.w(TAG, "getIDCardMsg_start");
				long startTime = SystemClock.uptimeMillis();
				IDCardMsg msg = mIDCardManager.getIDCardMsg(mContext);
//				mFileUtils.saveCardImg(msg.getPortrait());
				if(msg != null) {
					mHandler.sendMessage(mHandler.obtainMessage(1, msg));
					sleepTime(500);
				}
				msg = null;
			}
			sleepTime(1);
		}
		
		mIDCardManager = null;
		
		super.run();
		running = false;
	}


	private void sleepTime(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return 是否允许读身份证
	 */
	public boolean isAllowReadCard() {
		return isAllowReadCard;
	}


	public void setAllowReadCard(boolean isAllowReadCard) {
		this.isAllowReadCard = isAllowReadCard;
	}

	private IDCardRecListener mIDCardRecListener;

	public interface IDCardRecListener {
		public void onResp(IDCardMsg info);
	}

}
