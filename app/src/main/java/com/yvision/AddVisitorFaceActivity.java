package com.yvision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.yvision.adapter.CommonListAdapter;
import com.yvision.adapter.EmployeeListSpinnerAdapter;
import com.yvision.common.MyException;
import com.yvision.db.entity.UserEntity;
import com.yvision.dialog.DatePickerDialog;
import com.yvision.dialog.Loading;
import com.yvision.dialog.TimePickerDialog;
import com.yvision.dialog.TimePickerDialog.TimePickerDialogCallBack;
import com.yvision.helper.UserHelper;
import com.yvision.inject.ViewInject;
import com.yvision.model.EmployeeListModel;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.ImageUtils;
import com.yvision.utils.PageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 访客登记
 *
 * @author JackSong
 *
 */

public class AddVisitorFaceActivity extends BaseActivity implements CommonListAdapter.AdapterCallBack {
	// back
	@ViewInject(id = R.id.imgBack, click = "forBack")
	ImageView imgBack;

	// 保存
	@ViewInject(id = R.id.toSave, click = "toSave")
	TextView toSave;

	// imgPhoto
	@ViewInject(id = R.id.imgPhoto)
	ImageView imgPhoto;

	// 添加照片
	@ViewInject(id = R.id.getPic, click = "getPciture")
	LinearLayout getPic;

	// 访客姓名
	@ViewInject(id = R.id.VisitorName)
	EditText et_VisitorName;

	// vip(1是 0否)
	@ViewInject(id = R.id.isVip)
	CheckBox checkBoxVip;

	// 受访人ID
	@ViewInject(id = R.id.RespondentID)
	Spinner et_RespondentSpinner;

	// 访问目的
	@ViewInject(id = R.id.Aim)
	EditText et_Aim;

	// 预约到访日期
	@ViewInject(id = R.id.ArrivalTimePlan_date, click = "selectStartDate")
	TextView ArrivalTimePlan_date;

	// 预约到访时间
	@ViewInject(id = R.id.ArrivalTimePlan_time, click = "selectStartTime")
	TextView ArrivalTimePlan_time;

	// 预约离开日期
	@ViewInject(id = R.id.LeaveTimePlan_date, click = "selectEndDate")
	TextView LeaveTimePlan_date;

	// 预约离开时间
	@ViewInject(id = R.id.LeaveTimePlan_time, click = "selectEndTime")
	TextView LeaveTimePlan_time;

	// 所属单位
		@ViewInject(id = R.id.Affilication)
	EditText et_Affilication;

//	 联系方式
	@ViewInject(id = R.id.PhoneNumber)
	EditText PhoneNumber;

	// 欢迎语
	@ViewInject(id = R.id.tv_welcomeWord)
	EditText et_WelcomeWord;

	// 备注
	@ViewInject(id = R.id.Remark)
	EditText et_Remark;
	// 常量
	private final int GET_EMPLOYEELIST_SUCCESS = 501;// 获取受访者
	private final int ADDVISITOR_SUCESS = 502;// 获取受访者
	private final int ADDVISITOR_FAILED = 503;// 获取受访者
	private static final int REQUEST_CAMERAS = 11;//自定义相机
	// 变量
	private VisitorBModel visitorModel;
	private UserEntity userEntity;
	// public String strGender;// 性别
	private File visitorPicPath;
	public String respondentID = "";// 受访者
	public boolean isVip;
	public String ArrivalTimePlan_date_str;
	public String ArrivalTimePlan_time_str;
	public String LeaveTimePlan_date_str;
	public String LeaveTimePlan_time_str;
	ArrayList<EmployeeListModel> employeeList;//受访人列表
	EmployeeListSpinnerAdapter employeeListSpinnerAdapter;//自定义spinneradapter
	private Point mPoint;//获取屏幕像素尺寸

	//ImageLoader变量
//	private CameraGalleryUtils cameraGalleryUtils;// 头像上传工具
//	// 外部jar包：universal-image-loader-1.9.2.jar/异步加载图片
//	private DisplayImageOptions imgOption;
//	// 图片缓存
//	private ImageLoader imgLoader;
//	private String avatarBase64 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_vmain_addvisitorface);
		// 性别监听
		// radioGender.setOnCheckedChangeListener(listener);


		//获取屏幕像素尺寸
		Display display = getWindowManager().getDefaultDisplay();
		mPoint = new Point();
		display.getSize(mPoint);

		// ImageLoader图片处理工具类
//		cameraGalleryUtils = new CameraGalleryUtils(this, this);// 实例化
//		// 图片缓存实例化
//		imgLoader = ImageLoader.getInstance();
//		imgLoader.init(ImageLoaderConfiguration.createDefault(this));
//		imgOption = ImageLoadingConfig.generateDisplayImageOptionsNoCatchDisc(R.mipmap.ic_launcher);

		 //线程获取受访人
		 Loading.run(this, new Runnable() {
			 @Override
			 public void run() {
				 try {
			 		// 获取受访者信息
					JSONArray jsonArray = UserHelper.getEmployeeListByStoreID(AddVisitorFaceActivity.this,
							UserHelper.getCurrentUser().getStoreID(), // storeID（公司编号）
							"1");// typeN(写死了1)
					sendMessage(GET_EMPLOYEELIST_SUCCESS, jsonArray);
				} catch (MyException e) {
					e.printStackTrace();
					sendToastMessage(e.getMessage());
				}
			}
		});

		// spinner监听
		et_RespondentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try {
					if (employeeList == null) {
						return;
					}
					// 获取 EmployeeID
					respondentID = ((EmployeeListModel) employeeListSpinnerAdapter.getItem(position)).getEmployeeId();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		ArrivalTimePlan_date_str = df.format(new Date());// 当前系统日期
		LeaveTimePlan_date_str = df.format(new Date());// 当前系统日期
		ArrivalTimePlan_date.setText(ArrivalTimePlan_date_str);
		LeaveTimePlan_date.setText(LeaveTimePlan_date_str);

		// 时间格式
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		ArrivalTimePlan_time_str = sdf.format(new Date());
		LeaveTimePlan_time_str = sdf.format(new Date());
		ArrivalTimePlan_time.setText(ArrivalTimePlan_time_str);
		ArrivalTimePlan_time.setText(ArrivalTimePlan_time_str);

		// vip监听
		checkBoxVip.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isVip = true;
				} else {
					isVip = false;

				}
			}
		});
	}

	/**
	 * 将数据绑定到spinner中
	 *
	 * @param jsonArray
	 */
	private void bindEmployeeList(JSONArray jsonArray) {
		employeeList = new ArrayList<EmployeeListModel>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				String EmployeeId = jsonArray.getJSONObject(i).getString("EmployeeId");
				String WrokId = jsonArray.getJSONObject(i).getString("WrokId");
				String EmployeeName = jsonArray.getJSONObject(i).getString("EmployeeName");

				Log.d("SJY", "EmployeeId="+ EmployeeId +",WrokId="+WrokId+",EmployeeName="+EmployeeName);//
				EmployeeListModel employeeListModel = new EmployeeListModel();
				employeeListModel.setEmployeeId(EmployeeId);
				employeeListModel.setEmployeeName(EmployeeName);
				employeeListModel.setWrokId(WrokId);
				employeeList.add(employeeListModel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("SJY", "Exception="+e.getMessage());
		}
		employeeListSpinnerAdapter = new EmployeeListSpinnerAdapter(this, employeeList);
		et_RespondentSpinner.setAdapter(employeeListSpinnerAdapter);
		// 将当前登返回值的EmployeeID显示到spinner上
		try {
			userEntity = UserHelper.getCurrentUser();
			Log.d("SJY", "userEntity = "+userEntity.getEmployeeID());//
			// 将id显示到spinner上
			et_RespondentSpinner.setSelection(getEmployeeListIndex(userEntity.getEmployeeID()));//暂时 无用
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	private int getEmployeeListIndex(String EmployeeId) {
		for (int i = 0; i < employeeList.size(); i++) {
			if (EmployeeId == employeeList.get(i).getEmployeeId()) {
				return i;
			}
		}
		return 3;
	}

	// back
	public void forBack(View view) {
		this.finish();
	}

	// 获取人脸图片
	public void getPciture(View view) {
		//方式1 不可用的原因的 图片不可以根据重力感应调整方向
//		cameraGalleryUtils.showChoosePhotoDialog(CameraGalleryUtils.IMG_TYPE_CAMERA);// -99,处理弹窗调用
		//方式2
		Intent mCameraIntent = new Intent(this, MyChangeCameraActivity.class);
		startActivityForResult(mCameraIntent, REQUEST_CAMERAS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//方式1
		//		cameraGalleryUtils.onActivityResultAction(requestCode, resultCode, data);
		//方式2
		if (resultCode != RESULT_OK)
			return;

		if (requestCode == REQUEST_CAMERAS) {
			Uri photoUri = data.getData();
			visitorPicPath = new File(ImageUtils.getImageAbsolutePath(this, photoUri));
			//            picPath = new File(photoUri.toString());
			// Get the bitmap in according to the width of the device
			Bitmap bitmap = ImageUtils.decodeSampledBitmapFromPath(photoUri.getPath(), mPoint.x, mPoint.x);
			imgPhoto.setImageBitmap(bitmap);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// toSave
	public void toSave(View view) {
		// 线程处理保存信息
		Loading.run(this, new Runnable() {
			@Override
			public void run() {
				String RecordID = "";// 记录ID
				String VisitorName = et_VisitorName.getText().toString();// 访客姓名
				String RespondentID = AddVisitorFaceActivity.this.respondentID;// 受访者ID
				String Aim = et_Aim.getText().toString();// 目的
				String Affilication = et_Affilication.getText().toString();// 单位
				String ArrivalTimePlan = ArrivalTimePlan_date_str.trim() + " " + ArrivalTimePlan_time_str.trim() +".111";// 来访时间
				String LeaveTimePlan = LeaveTimePlan_date_str.trim() +" " + LeaveTimePlan_time_str.trim() +".111";// 离开时间
				String WelcomeWord = et_WelcomeWord.getText().toString();// 欢迎语
				String PhoneNumber = AddVisitorFaceActivity.this.PhoneNumber.getText().toString();//手机号
				String Remark = et_Remark.getText().toString();// 备注
				String StoreID = UserHelper.getCurrentUser().getStoreID();// 到访公司
				try {
					// JSONObject
					JSONObject js = new JSONObject();
					js.put("VisitorName", VisitorName);
					js.put("RespondentID", RespondentID);
					js.put("Aim", Aim);
					js.put("Affilication", Affilication);
					js.put("ArrivalTimePlan", ArrivalTimePlan);
					js.put("LeaveTimePlan", LeaveTimePlan);
					js.put("WelcomeWord", WelcomeWord);
					js.put("Remark", Remark);
					js.put("StoreID", StoreID);
					js.put("PhoneNumber", PhoneNumber);
					js.put("isVip", isVip);

					String msg = UserHelper.addOneVisitorRecord(AddVisitorFaceActivity.this, js.toString(), visitorPicPath);
					sendMessage(ADDVISITOR_SUCESS,msg);
				} catch (MyException e) {
					sendMessage(ADDVISITOR_FAILED,e.getMessage());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	@Override
	protected void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
			case GET_EMPLOYEELIST_SUCCESS:
				JSONArray jsonArray = (JSONArray) msg.obj;
				bindEmployeeList(jsonArray);
				break;
			case ADDVISITOR_FAILED:
				PageUtil.DisplayToast((String)msg.obj);
				break;
			case ADDVISITOR_SUCESS:
				sendToastMessage(msg.toString());
				break;
			default:
				break;
		}
	}


	// ImageLoader的图片展示
//	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（1）
//	@Override
//	public void updateAvatarSuccess(int updateType, String picPath, String avatarBase64) {
//		imgPhoto.setImageResource(R.mipmap.ic_launcher);// imageView添加图片
//		// 获取图片路径
//		this.visitorPicPath = new File(picPath);
//		if (!this.visitorPicPath.exists()) {
//			try {
//				this.visitorPicPath.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		if (picPath.toLowerCase().startsWith("http")) {// 将字符参数改写成小写的方式
//			// 外部jar包方法
//			ImageLoader.getInstance().displayImage(picPath, // 已处理图片地址
//					imgPhoto, // imageView
//					imgOption);// jar包类
//		} else {
//			ImageLoader.getInstance().displayImage("file://" + picPath, imgPhoto, // imageView
//					imgOption);// jar包类
//		}
//		this.avatarBase64 = avatarBase64;
//	}
//
//	@Override
//	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（2）
//	public void updateAvatarFailed(int updateType) {
//		Toast.makeText(this, "头像上传失败", Toast.LENGTH_SHORT).show();// 头像上传失败
//	}
//
//	@Override
//	// 实现UpdateAvatarUtil.ChoosePicCallBack的接口方法（3）
//	public void cancel() {
//
//	}

	// CommonListAdapter.AdapterCallBack接口
	@Override
	public void loadMore() {

	}

	// 设置日期
	private final int DATE_START_DATA = 0;
	private final int DATE_END_DATA = 1;

	public void selectStartDate(View view) {
		showDataPickerDialog(DATE_START_DATA, ArrivalTimePlan_date_str);
	}

	public void selectEndDate(View view) {
		showDataPickerDialog(DATE_END_DATA, LeaveTimePlan_date_str);
	}

	private void showDataPickerDialog(final int whichTime, String currentDate) {
		DatePickerDialog dialog = new DatePickerDialog(this,
				currentDate,
				new DatePickerDialog.DatePickerDialogCallBack() {
					@Override
					public void confirm(String date) {
						PageUtil.DisplayToast(date);
						if (whichTime == DATE_START_DATA) {
							ArrivalTimePlan_date.setText(date);
							ArrivalTimePlan_date_str = date;
						} else {
							LeaveTimePlan_date.setText(date);
							LeaveTimePlan_date_str = date;
						}

					}
				});
		dialog.show();
	}

	// 设置时间
	private final int TIME_START_DATA = 2;
	private final int TIME_END_DATA = 3;

	public void selectStartTime(View view) {
		showTimePickerDialog(TIME_START_DATA, ArrivalTimePlan_time_str);
	}

	public void selectEndTime(View view) {
		showTimePickerDialog(TIME_END_DATA, LeaveTimePlan_time_str);
	}

	private void showTimePickerDialog(final int whichTime, String currentTime) {
		TimePickerDialog dialog = new TimePickerDialog(AddVisitorFaceActivity.this,
				LeaveTimePlan_time.getText().toString(), new TimePickerDialogCallBack() {
					@Override
					public void confirm(String date) {
						if(whichTime == TIME_START_DATA){
							ArrivalTimePlan_time.setText(date);
							ArrivalTimePlan_time_str = date;
						}else{
							LeaveTimePlan_time.setText(date);
							LeaveTimePlan_time_str = date;//LeaveTimePlan_time.getText().toString()
						}
					}
				});
		dialog.show();
	}
}
