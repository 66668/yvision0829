package com.yvision.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yvision.R;
import com.yvision.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePickerDialog extends Dialog implements View.OnClickListener{
	private Context context;
	private TimePickerDialogCallBack callBack;
	private String timeString = "19:03:10";
	TimePicker timePicker;
	
	public TimePickerDialog(Context context, TimePickerDialogCallBack callBack){
		super(context, R.style.LoadingDialog);
		this.context = context;
		this.callBack = callBack;
		init();
	}
	
	public TimePickerDialog(Context context, String date, TimePickerDialogCallBack callBack){
		super(context, R.style.LoadingDialog);
		this.context = context;
		this.callBack = callBack;
		if (!TextUtils.isEmpty(date)) {
			this.timeString = date;
		}
		init();
	}
	
	public interface TimePickerDialogCallBack{
		public void confirm(String date);
	}
	
	private void init(){
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_timepicker, null);
		TextView cancelBtn = (TextView) dialogView.findViewById(R.id.cancel_btn);
		TextView confirmBtn = (TextView) dialogView.findViewById(R.id.confirm_btn);
		timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);
		initTime();//格式
		
		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		
		setContentView(dialogView);
	}
	
	private void initTime(){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date date = sdf.parse(timeString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			final int second = calendar.get(Calendar.SECOND);
			timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					timeString = getFriendlyNum(hourOfDay) +":"+ getFriendlyNum(minute)+":00";
				}
			});
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_btn:
			dismiss();
			break;
		case R.id.confirm_btn:
//			timeString = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute()+":"+"00";
			callBack.confirm(timeString);
			dismiss();
			break;
		default:
			break;
		}
	}
	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		int screenWidth = (int) (Utils.getScreenWidth((Activity) context));
		lp.width = screenWidth - screenWidth * 60 / 640;
		lp.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(lp);
	}
	
	private String getFriendlyNum(int num) {
		if (num < 10) {
			return "0" + num;
		} else {
			return num + "";
		}
	}
}
