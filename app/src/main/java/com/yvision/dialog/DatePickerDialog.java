package com.yvision.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.yvision.R;
import com.yvision.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择弹窗
 * 
 * @author JackSong
 *
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener {
	private Context context;
	private DatePickerDialogCallBack callBack;
	private String dateString = "2016-08-01";
	DatePicker datePicker;

	public DatePickerDialog(Context context, DatePickerDialogCallBack callBack) {
		super(context, R.style.LoadingDialog);
		this.context = context;
		this.callBack = callBack;
		init();
	}

	public DatePickerDialog(Context context, String date, DatePickerDialogCallBack callBack) {
		super(context, R.style.LoadingDialog);
		this.context = context;
		this.callBack = callBack;
		if (!TextUtils.isEmpty(date)) {
			this.dateString = date;
		}
		init();
	}

	public interface DatePickerDialogCallBack {
		
		public void confirm(String date);
	}

	private void init() {

		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_datepicker, null);

		TextView cancelBtn = (TextView) dialogView.findViewById(R.id.cancel_btn);
		TextView confirmBtn = (TextView) dialogView.findViewById(R.id.confirm_btn);
		datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker1);
		initData();

		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		setContentView(dialogView);
		
	}

	private void initData() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(dateString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			int monthOfYear = calendar.get(Calendar.MONTH);
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {

				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					dateString = year + "-" + getFriendlyNum(monthOfYear + 1) + "-" + getFriendlyNum(dayOfMonth);
				}

			});

		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_btn:
			dismiss();
			break;
		case R.id.confirm_btn:
			dateString = datePicker.getYear() + "-" + (getFriendlyNum(datePicker.getMonth() + 1)) + "-"
					+ getFriendlyNum(datePicker.getDayOfMonth());
			callBack.confirm(dateString);
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
