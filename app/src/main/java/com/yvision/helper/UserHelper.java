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
import com.yvision.model.EmployeeInfoModel;
import com.yvision.model.OldEmployeeModel;
import com.yvision.model.UpgradeModel;
import com.yvision.model.VisitorBModel;
import com.yvision.utils.APIUtils;
import com.yvision.utils.ConfigUtil;
import com.yvision.utils.JSONUtils;
import com.yvision.utils.Utils;
import com.yvision.utils.WebUrl;

import org.json.JSONArray;
import org.json.JSONException;

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
    static UserEntity mCurrentUser = null;
    static String configUserManager = null;//

    /**
     * (1)获取用户id(工号)
     */
    static String workId = null;

    public static String getCongfigworkId() {
        if (workId == null) {
            ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
            workId = config.getworkId();
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
            String workId = config.getworkId();
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
        try {
            ConfigUtil configUtil = new ConfigUtil(context);
            configUtil.setAutoLogin(false);
            //修改自动登录的判断
            MyApplication.getInstance().setIsLogin(false);
        } finally {
            mCurrentUser = null;
        }
    }

    /**
     * 01 密码登录
     */
    public static void loginByPs(Context context, String storeId, String workId, String password, String clientID) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context))
            throw new MyException(R.string.network_invalid);
        HttpResult httpResult = APIUtils.postForObject(WebUrl.LOGIN_POST,
                HttpParameter.create().
                        add("adminUserName", storeId).//storeid
                        add("userName", workId).//workid
                        add("password", password).

                        add("MAC", Utils.getPhoneModel()).//手机mac
                        add("IP", Utils.getLocalIpAddress()).//手机ip
                        add("deviceType", Utils.getPhoneModel()).//手机设备类型
                        add("deviceName", Utils.getPhonePRODUCT()).//手机设备名称

                        add("Remark", "").//
                        add("DeviceSN", "").//
                        add("DeviceInfo", clientID + "@1001"));//

        if (httpResult.hasError()) {
            throw httpResult.getError();
        }


        UserEntity userEntity = new UserEntity();
        userEntity.setEmployeeID(JSONUtils.getString(httpResult.jsonObject,"EmployeeID"));
        userEntity.setStoreID(JSONUtils.getString(httpResult.jsonObject,"StoreID"));
        userEntity.setStoreUserId(JSONUtils.getString(httpResult.jsonObject,"StoreUserId"));
        userEntity.setworkId(workId);
        userEntity.setPassword(password);
        userEntity.setstoreId(storeId);
        userEntity.setClientID(clientID);
        // ConfigUtil中断保存，在退出后重新登录用getAccount()调用
        ConfigUtil config = new ConfigUtil(MyApplication.getInstance());
        config.setstoreId(storeId);// 保存公司编号
        config.setworkId(workId);// 保存工号
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
        /**
         * json转换到userManager中，现在不需要--0802
         */
        // // Gson.jar包,解析HttpResult中的jsonArray 给对象UserManagers
        // UserEntity userManagers = null;
        // List<UserEntity> list = new ArrayList<UserEntity>();
        // JSONArray jsonArray = httpResult.jsonArray;
        // Iterable<JsonElement> iterable = (Iterable<JsonElement>) jsonArray;
        // Iterator<JsonElement> iterator = iterable.iterator();
        // while(iterator.hasNext()){
        // JsonElement element = (JsonElement)iterator.next();
        // userManagers = new Gson().fromJson(element, UserEntity.class);
        // list.add(userManagers);
        // //数组赋值
        // userManagers.setList(list);
        // }
        // mCurrentUser = userManagers;//将登陆成功的对象信息，赋值给全局变量
        /**
         * 完成代码后，需要加上--0802
         */
        // if (httpResult.hasError()) {
        // throw httpResult.getError();
        // }

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
                HttpParameter.create().add("storeUserID", getCurrentUser().getstoreId() + "").// 登录时，返回的storeUserID ??
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
     * 11 GetAttendanceRecordByPage
     * 获取用户所有考勤数据
     */
    public static ArrayList<AttendModel> GetAttendanceRecordByPage(Context context,
                                                                   String maxTime,
                                                                   String minTime,
                                                                   int pageSize,
                                                                   int timespan,
                                                                   String storeID,
                                                                   String employeeID,
                                                                   int attendType) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        HttpResult hr = APIUtils.postForObject(WebUrl.GET_ATTENDANCE_BYPAGE,
                HttpParameter.create().
                        add("minTime", minTime).
                        add("maxTime", maxTime).
                        add("pageSize", pageSize + "").
                        add("timespan", timespan + "").
                        add("storeID", storeID).
                        add("employeeID", employeeID).
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
     * 12获取登录人信息
     */
    public static EmployeeInfoModel GetEmployeeInfoByID(Context context, String employeeID) throws MyException {
        if (!NetworkManager.isNetworkAvailable(context)) {
            throw new MyException(R.string.network_invalid);// 亲，您的网络不给力，请检查网络
        }
        String url = WebUrl.GET_ONE＿EMPLOYEE_INFO + "/" + employeeID;
        HttpResult hr = APIUtils.getForObject(url);
        if (hr.hasError()) {
            throw hr.getError();
        }
        if (hr.jsonObject != null) {
            return (new Gson()).fromJson(hr.jsonObject.toString(), EmployeeInfoModel.class);
        } else {
            throw new MyException("未获取返回信息！");
        }
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

    public static int registerNew(Context context, HttpParameter params, File picPath) throws MyException{
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

    public static String registerOld(Context context, HttpParameter params, File picPath) throws MyException{
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
        Log.d("SJY","获取老员工列表" + WebUrl.GET_OLD_EMPLOYEE_LIST+"--公司编号："+mCurrentUser.getStoreID());
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

}
