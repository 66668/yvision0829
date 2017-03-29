package com.yvision.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yvision.R;
import com.yvision.common.HttpParameter;
import com.yvision.common.HttpResult;
import com.yvision.common.MyApplication;
import com.yvision.common.MyException;
import com.yvision.common.NetworkManager;
import com.yvision.db.entity.UserEntity;
import com.yvision.model.AttendModel;
import com.yvision.model.DoorAccessModel;
import com.yvision.model.EmployeeInfoModel;
import com.yvision.model.OldEmployeeModel;
import com.yvision.model.UpgradeModel;
import com.yvision.model.VipModel;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.APIUtils;
import com.yvision.utils.ConfigUtil;
import com.yvision.utils.JSONUtils;
import com.yvision.utils.Utils;
import com.yvision.utils.WebUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理者帮助类
 * <p/>
 * 处理访问服务端信息类 解析js对象，调用Gson外部包：gson-2.2.2.jar
 *
 * @author JackSong
 */
public class UserHelper {
    static UserEntity mCurrentUser;
    static String configUserManager = null;//

    /**
     * (1)获取用户id(工号)
     */
    static String workId = null;

    public static String getCongfigworkId() {
        if (workId == null) {
            ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
            workId = config.getUserName();
        }
        return workId;
    }

    /**
     * (2)获取用户账号
     *
     * @return
     */
    public static UserEntity getCurrentUser() {
        // 调用下边的方法
        return getCurrentUser(true);
    }

    public static UserEntity getCurrentUser(boolean isAutoLoad) {

        if (mCurrentUser == null && isAutoLoad) {// 判断MemberModel类是否为空
            // 中断保存
            ConfigUtil config = new ConfigUtil(MyApplication.getInstance());// 中断保存获取信息
            String workId = config.getUserName();
            if (!"".equals(workId)) {
                // 获取所有当前用户信息，保存到mCurrentUser对象中
                mCurrentUser = config.getUserEntity();
            }
        }
        return mCurrentUser;
    }

    /**
     * json-->VisitorBModel
     *
     * @param context
     * @param data
     * @return
     */
    public static VisitorBModel getVisitorFromJson(Context context, String data) {
        return (new Gson()).fromJson(data, VisitorBModel.class);
    }

    /**
     * 退出登录
     */
    public static void logout(Context context) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context))
            throw new MyException(R.string.network_invalid);
        try {

            HttpResult httpResult = APIUtils.postForObject(WebUrl.User.QUIT_OUT,
                    HttpParameter.create()
                            .add("storeId", UserHelper.getCurrentUser().getStoreID().trim())
                            .add("userName", UserHelper.getCurrentUser().getUserName().trim())
                            .add("deviceId", UserHelper.getCurrentUser().getDeviceId().trim()));

            if (httpResult.hasError()) {
                throw httpResult.getError();
            }

            ConfigUtil configUtil = new ConfigUtil(context);
            configUtil.setAutoLogin(false);
            //修改自动登录的判断
            MyApplication.getInstance().setIsLogin(false);
        } catch (Exception e) {
            throw new MyException(e.getMessage());
        } finally {
            mCurrentUser = null;
        }
    }

    /**
     * 01 密码登录
     */
    public static void loginByPs(Context context, String adminUserName, String userName, String password, String registRationID) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context))
            throw new MyException(R.string.network_invalid);
        HttpResult httpResult = APIUtils.postForObject(WebUrl.LOGIN_POST,
                HttpParameter.create().
                        add("adminUserName", adminUserName).//storeid
                        add("userName", userName).//workid
                        add("password", password).

                        add("MAC", Utils.getMacByWifi()).//手机mac
                        add("IP", Utils.getIPAddress(context)).//手机ip
                        add("deviceType", Utils.getPhoneModel()).//手机设备类型
                        add("deviceName", userName).//手机设备名称

                        add("Remark", "").//
                        add("DeviceSN", "").//
                        add("DeviceInfo", registRationID + "@1001"));//

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }


        UserEntity userEntity = new UserEntity();
        //返回值
        userEntity.setDeviceId(JSONUtils.getString(httpResult.jsonObject, "DeviceId"));//设备id
        userEntity.setStoreID(JSONUtils.getString(httpResult.jsonObject, "StoreId"));//公司id
        userEntity.setEmployeeId(JSONUtils.getString(httpResult.jsonObject, "EmployeeId"));//员工id
        userEntity.setStoreUserId(JSONUtils.getString(httpResult.jsonObject, "StoreUserId"));//用户id

        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        userEntity.setAdminUserName(adminUserName);
        userEntity.setregistRationID(registRationID);

        // ConfigUtil中断保存，在退出后重新登录用getAccount()调用
        ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
        config.setAdminUserName(adminUserName);// 保存公司编号
        config.setUserName(userName);// 保存用户名
        config.setPassword(password);
        config.setUserEntity(userEntity);// 保存已经登录成功的对象信息
        mCurrentUser = userEntity;// 将登陆成功的对象信息，赋值给全局变量
    }

    /**
     * 02 GetEmployeeListByStoreID
     * <p/>
     * 获取受访者(添加访客时，填写受访者的调用)
     *
     * @throws MyException
     */
    public static JSONArray getEmployeeListByStoreID(Context context, String storeId, String typeN) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context))
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
        //        String newUrl = new String(WebUrl.UserManager.GET_RESPONDENTS + storeId + "/" + typeN);
        String newUrl = new String(WebUrl.GET_RESPONDENTS + storeId + "/" + typeN);
        HttpResult httpResult = APIUtils.getForObject(newUrl);

        return httpResult.jsonArray;
    }

    /**
     * 03 AddOneVisitorRecord  添加访客
     *
     * @throws MyException
     */
    public static String addOneVisitorRecord(Context context, String parameters, File fileName) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.ADD_VISITORRECORD,
                HttpParameter.create().add("obj", parameters),
                fileName);
        if (hr.hasError()) {
            throw hr.getError();
        }

        return hr.Message;
    }

    /**
     * 04 修改登录密码
     * <p/>
     * HttpPost方式 返回值 {"code":"1","message":"保存成功！","result":""}
     */
    public static String changePassword(Context context, String oldUserPassword,
                                        String newUserPassword) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }

        HttpResult httpResult = APIUtils.postForObject(WebUrl.CHANGE_PASSWORD,
                HttpParameter.create().add("storeUserID", getCurrentUser().getStoreUserId() + "").// 登录时，返回的storeUserID ??
                        add("oldUserPassword", oldUserPassword).
                        add("newUserPassword", newUserPassword));
        if (httpResult.hasError()) {
            throw httpResult.getError();
        }
        return httpResult.Message;
    }

    /**
     * 05 GetVisitorWithSameName
     * 搜索相同姓名的信息
     * <p/>
     * HttpPost方式
     *
     * @throws MyException
     */
    public static ArrayList<VisitorBModel> getVisitorWithSameName(Context context, String storeID, String searchName) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }

        HttpResult httpResult = APIUtils.postForObject(WebUrl.GET_VISITOR_WITH_SAME_NAME,
                HttpParameter.create().add("name", searchName).add("storeID", storeID));

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }
        //返回list形式
        return (new Gson()).fromJson(httpResult.jsonArray.toString(),
                new TypeToken<List<VisitorBModel>>() {
                }.getType());
    }

    /**
     * 06 GetOneVisitorRecordByID
     * <p/>
     * 获取一条记录的详细信息
     *
     * @throws MyException
     */
    public static VisitorBModel getOneVisitorRecordByID(Context context, String recordID, String storeID) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }

        String url = WebUrl.GET_ONE＿VISITORRECORD + "/" + recordID + "/" + storeID;
        HttpResult httpResult = APIUtils.getForObject(url);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonObject.toString(), VisitorBModel.class);
    }

    /**
     * 07 DeleteVisitorRecordsByIDList
     * <p/>
     * 删除记录（可多条同时删除）
     * httpPost
     */
    public static String getDeleteVisitorRecordsByIDList(Context context, String storeID, String recordIDList) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.DELET_VISITORRECORD,
                HttpParameter.create().add("recordIDList", recordIDList).add("storeID", storeID));
        if (hr.hasError()) {
            throw hr.getError();
        }
        return hr.Message;
    }

    /**
     * 08 UpdateOneVisitorRecord
     * <p/>
     * 修改访客信息
     */
    public static String updateOneVisitorRecord(Context context, String parameters, File fileName) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.ADD_VISITORRECORD,
                HttpParameter.create().add("obj", parameters),
                fileName);
        if (hr.hasError()) {
            throw hr.getError();
        }
        return hr.Message;
    }

    //0906接口停用
    public static ArrayList<VisitorBModel> getVisitorRecordsByPage(Context context,
                                                                   int pageIndex,//List<VisitorBModel>
                                                                   int pageSize,
                                                                   int timespan,
                                                                   String storeID) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }

        HttpResult httpResult = APIUtils.postForObject(WebUrl.GET_RRECORD_BYPAGE,
                HttpParameter.create().
                        add("pageIndex", pageIndex + "").
                        add("pageSize", pageSize + "").
                        add("timespan", timespan + "").
                        add("storeID", storeID));

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }
        //返回list形式
        return (new Gson()).fromJson(httpResult.jsonArray.toString(),
                new TypeToken<List<VisitorBModel>>() {
                }.getType());
    }

    /**
     * 09 GetVisitorRecordsByPageA
     * timespan 1(今天)/0(全部)
     * spinner 全部/今天
     */
    public static ArrayList<VisitorBModel> getVisitorRecordsByPageA(Context context,
                                                                    String iMaxTime,
                                                                    String iMinTime,
                                                                    int pageSize,
                                                                    int timespan,
                                                                    String storeID) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.GET_RRECORD_BYPAGEA,
                HttpParameter.create().
                        add("iMinTime", iMinTime).
                        add("iMaxTime", iMaxTime).
                        add("pageSize", pageSize + "").
                        add("timespan", timespan + "").
                        add("storeID", storeID));
        if (hr.hasError()) {
            throw hr.getError();
        }
        Log.d("SJY", "UserHelper--hr.jsonArray=" + hr.jsonArray);
        if (hr.jsonArray != null) {
            return (new Gson()).fromJson(hr.jsonArray.toString(),
                    new TypeToken<List<VisitorBModel>>() {
                    }.getType());
        } else {
            return null;
        }
    }

    /**
     * 10 CheckVersion
     * 版本更新
     */
    public static UpgradeModel CheckVersion(Context context, String type) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.getForObject(WebUrl.CLIENT_UPGRADE_URL + "/" + type);
        if (hr.hasError()) {
            throw hr.getError();
        }
        UpgradeModel model = (new Gson()).fromJson(hr.jsonObject.toString(), UpgradeModel.class);
        Log.d("SJY", "hr = " + hr.jsonObject.toString());
        if (!TextUtils.isEmpty(model.getPackageUrl())) {
            model.setIsexistsnewversion(true);
        }
        return model;
    }

    /**
     * 11
     * 获取考勤
     */
    public static ArrayList<AttendModel> GetAttendanceRecordByPage(Context context,
                                                                   String maxTime,
                                                                   String minTime,
                                                                   int pageSize,
                                                                   int timespan,
                                                                   int attendType) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.Attend.GET_ATTENDANCE_BYPAGE,
                HttpParameter.create().
                        add("minTime", minTime).
                        add("maxTime", maxTime).
                        add("pageSize", pageSize + "").
                        add("timespan", timespan + "").
                        add("storeID", UserHelper.getCurrentUser().getStoreID()).
                        add("employeeID", UserHelper.getCurrentUser().getEmployeeId()).
                        add("attendType", attendType + ""));
        if (hr.hasError()) {
            throw hr.getError();
        }
        Log.d("SJY", "UserHelper--hr.jsonArray=" + hr.jsonArray);
        if (hr.jsonArray != null) {
            return (new Gson()).fromJson(hr.jsonArray.toString(),
                    new TypeToken<List<AttendModel>>() {
                    }.getType());
        } else {
            return null;
        }
    }

    /**
     * 11 添加地图考勤
     */
    public static String mapAttend(Context context,
                                   String currentTime,
                                   String address,
                                   double weidu,
                                   double jingdu,
                                   String EmployeeName,
                                   String companyID,
                                   String EmployeeID,
                                   String DepartmentID) throws MyException, JSONException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }


        JSONObject js = new JSONObject();
        js.put("CapTime", currentTime);
        js.put("Location", address);
        js.put("Latitude", weidu + "");
        js.put("Longitude", jingdu + "");

        js.put("EmployeeID", EmployeeID);
        js.put("CompanyID", companyID);
        js.put("DepartmentID", DepartmentID);
        js.put("EmployeeName", EmployeeName);
        js.put("WrokId", UserHelper.getCurrentUser().getUserName());


        HttpResult hr = APIUtils.postForObject(WebUrl.Attend.POST_ATTENDANCE_MAP,
                HttpParameter.create().add("obj", js.toString()));

        if (hr.hasError()) {
            throw hr.getError();
        }

        return currentTime;
    }

    /**
     * 12获取登录人信息
     */
    public static EmployeeInfoModel GetEmployeeInfoByID(Context context) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        String url = WebUrl.Attend.GET_ONE＿EMPLOYEE_INFO + UserHelper.getCurrentUser().getEmployeeId();
        HttpResult hr = APIUtils.getForObject(url);

        if (hr.hasError()) {
            throw hr.getError();
        }

        //        UserEntity userEntity = new UserEntity();
        //        userEntity.setEmployeeId(JSONUtils.getString(hr.jsonObject,"EmployeeId"));
        //        userEntity.setDepartmentID(JSONUtils.getString(hr.jsonObject,"DepartmentID"));
        //        userEntity.setStoreID(JSONUtils.getString(hr.jsonObject,"StoreID"));
        //        userEntity.setWorkId(JSONUtils.getString(hr.jsonObject,"WrokId"));
        //        userEntity.setEmployeeName(JSONUtils.getString(hr.jsonObject,"EmployeeName"));
        //        userEntity.setEmployeeGender(JSONUtils.getString(hr.jsonObject,"EmployeeGender"));
        //
        //        ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
        //        config.setUserEntity(userEntity);// 保存已经登录成功的对象信息
        //        mCurrentUser = userEntity;// 将登陆成功的对象信息，赋值给全局变量

        return (new Gson()).fromJson(hr.jsonObject.toString(), EmployeeInfoModel.class);

    }

    /**
     * 13添加wifi考勤记录
     * addOneWIFIAttendanceRecord
     * post
     */
    public static String addOneWIFIAttendanceRecord(Context context, String jsonStr) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.ADD_ONE＿WIFI_ATTENDANCE,
                HttpParameter.create().add("obj", jsonStr));
        if (hr.hasError()) {
            throw hr.getError();
        }

        return hr.Message;
    }

    /**
     * 14获取人脸库（注册人脸库前使用）
     */

    public static JSONArray getFaceDatabase(Context context) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
        }
        String companyID = UserHelper.getCurrentUser().getStoreID();//公司编号companyID

        HttpResult httpResult = APIUtils.getForObject(WebUrl.GET_FACE_DATEBASE_BY_COMPANYID + companyID);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        //       if(httpResult.jsonArray != null){
        //            return (new Gson()).fromJson(httpResult.jsonArray.toString(),
        //                    new TypeToken<List<GroupModel>>() {
        //                    }.getType());
        //        }else{
        //           //后台js不标准，多这一步保险
        //           return (new Gson()).fromJson(httpResult.resultJsonString.toString(),
        //                   new TypeToken<List<GroupModel>>() {
        //                   }.getType());
        //        }
        Log.d("SJY", "人脸库jsonArray=" + httpResult.jsonArray.toString());
        return httpResult.jsonArray;
    }

    public static JSONArray getDataDepartment(Context context) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
        }

        String companyID = UserHelper.getCurrentUser().getStoreID();//公司编号companyID/ 5596c761-493a-48f4-8f18-1e6191537405
        Log.d("SJY", "UserHelper companyId=" + companyID);

        HttpResult httpResult = APIUtils.getForObject(WebUrl.GET_DEPARTMENT_DATEBASE_BY_COMPANYID + companyID);

        Log.d("SJY", "部门库jsonArray=" + httpResult.jsonArray.toString());

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        //       if(httpResult.jsonArray != null){
        //            return (new Gson()).fromJson(httpResult.jsonArray.toString(),
        //                    new TypeToken<List<GroupModel>>() {
        //                    }.getType());
        //        }else{
        //           //后台js不标准，多这一步保险
        //           return (new Gson()).fromJson(httpResult.resultJsonString.toString(),
        //                   new TypeToken<List<DepartmentModel>>() {
        //                   }.getType());
        //        }
        return httpResult.jsonArray;
    }

    /**
     * 15新员工注册
     *
     * @throws MyException
     * @throws JSONException
     * @throws IOException
     */

    public static int registerNew(Context context, HttpParameter params, File picPath) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
        }

        // 访问服务端：http://192.168.1.127:2016/api/Attend/RegAttFace
        HttpResult httpResult = APIUtils.postForObject(WebUrl.POST_NEW_EMPLOYEE, params, picPath);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return httpResult.code;
    }

    /**
     * 16老员工注册
     *
     * @throws MyException
     * @throws JSONException
     * @throws IOException
     */

    public static String registerOld(Context context, HttpParameter params, File picPath) throws MyException {
        // 判断否有网络连接，有网络连接，不抛异常，无连接，抛异常(logcat)
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络！
        }

        HttpResult httpResult = APIUtils.postForObject(WebUrl.POST_OLD_EMPLOYEE, params, picPath);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }
        return httpResult.resultJsonString;//解析result的数据-- 祖册成功
    }

    /**
     * 获取老员工列表(用于修改老员工图片)
     * get
     */
    public static ArrayList<OldEmployeeModel> getOldEmployList(Context context) throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult httpResult = APIUtils.getForObject(WebUrl.GET_OLD_EMPLOYEE_LIST + mCurrentUser.getStoreID());

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonArray.toString(),
                new TypeToken<List<OldEmployeeModel>>() {
                }.getType());
    }

    /**
     * 获取老员工详细信息
     * get
     */
    public static OldEmployeeModel getOldEmployeeDetails(Context context, String emloyeeID) throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }

        HttpResult httpResult = APIUtils.getForObject(WebUrl.GET_OLD_EMPLOYEE_DETAILS + emloyeeID);
        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonObject.toString(), OldEmployeeModel.class);
    }

    /**
     * vip记录
     */
    public static List<VipModel> getViPList(Context context
            , String maxTime
            , String minTime
            , String timespan) throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }

        Log.d("SJY", "打印vip:" + "maxTime=" + maxTime + "\nminTime=" + minTime + "\nemployeeId=" + UserHelper.getCurrentUser().getEmployeeId()
                + "\nstoreId" + UserHelper.getCurrentUser().getStoreID() + "\ntimespan=" + timespan);

        HttpResult httpResult = APIUtils.postForObject(WebUrl.Vip.GET_VIP_LIST
                , HttpParameter.create()
                        .add("maxTime", maxTime)
                        .add("minTime", minTime)
                        .add("pageSize", "30")
                        .add("employeeId", UserHelper.getCurrentUser().getEmployeeId())
                        .add("storeId", UserHelper.getCurrentUser().getStoreID())
                        .add("timespan", timespan));

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonArray.toString(),
                new TypeToken<List<VipModel>>() {
                }.getType());
    }

    /**
     * 门禁记录
     */
    public static List<DoorAccessModel> getDoorAccessList(Context context
            , String maxTime
            , String minTime
            , String timespan) throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult httpResult = APIUtils.postForObject(WebUrl.DoorAccess.GET_DOOR_ACCESS_LIST
                , HttpParameter.create()
                        .add("maxTime", maxTime)
                        .add("minTime", minTime)
                        .add("pageSize", "20")
                        .add("employeeId", UserHelper.getCurrentUser().getEmployeeId())
                        .add("storeId", UserHelper.getCurrentUser().getStoreID())
                        .add("timespan", timespan));

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonArray.toString(),
                new TypeToken<List<DoorAccessModel>>() {
                }.getType());
    }

    /**
     * 获取考勤详情
     * get
     */
    public static AttendModel getAttendDetail(Context context, String attendId)
            throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult httpResult = APIUtils.getForObject(WebUrl.Attend.EMPLOYEE_DETAIL + attendId);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonObject.toString(), AttendModel.class);
    }

    /**
     * 获取VIP详情
     * get
     */
    public static VipModel getVIPDetail(Context context, String attendId)
            throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult httpResult = APIUtils.getForObject(WebUrl.Vip.VIP_DETAIL + attendId);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonObject.toString(), VipModel.class);
    }


    /**
     * 获取门禁详情
     * get
     */
    public static DoorAccessModel getDoorAccessDetail(Context context, String attendId)
            throws MyException {

        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);
        }
        HttpResult httpResult = APIUtils.getForObject(WebUrl.DoorAccess.DOORACCESS_DETAIL + attendId);

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }

        return (new Gson()).fromJson(httpResult.jsonObject.toString(), DoorAccessModel.class);
    }
}
