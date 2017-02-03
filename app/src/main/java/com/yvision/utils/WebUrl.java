package com.yvision.utils;

public class WebUrl {
    /**
     * 根接口
     */
    // 云端
//    public static final String LOGIN_URL = "http://101.201.72.112:9016/";//公司使用地址
    public static final String LOGIN_URL = "http://101.201.72.112:7016/";//测试地址
//    public static final String LOGIN_URL = "http://192.168.1.245:1132/";
//    public static final String LOGIN_URL = "http://192.168.1.85:9011/";//江苏银行


    /**
     * 注册根目录
     */
    public static final String REGISTER_FLODER = "openapi/";

    /**
     * 管理根目录
     */
    public static final String LOGIN_FLODER_USER = "openapi/User/";


    /**
     * 访客根目录
     */
    public static final String LOGIN_FLODER_MAIN = "openapi/Main/";


    /**
     * 使用者管理
     *
     * @author JackSong
     */
    public static class UserManager {
    }

    /**
     * 01 密码登录
     * 测试url:http://192.168.1.127:9012/openapi/User/LoginByPassword
     */
    public static final String LOGIN_POST = LOGIN_URL + LOGIN_FLODER_USER + "LoginByPassword";


    /**
     * 02 修改登录密码
     * http://192.168.1.127:9012/openapi/User/ChangePasswordN
     */
    public static final String CHANGE_PASSWORD = LOGIN_URL + LOGIN_FLODER_USER + "ChangePasswordN";


    /**
     * 03 获取受访者
     * <p>
     * get
     * <p>
     * http://192.168.1.127:9012/openapi/Main/GetEmployeeListByStoreID/{storeID}/{typeN}
     */
    public static final String GET_RESPONDENTS = LOGIN_URL + LOGIN_FLODER_MAIN + "GetEmployeeListByStoreID/";


    /**
     * 访客管理
     *
     * @author JackSong
     */
    public class VisitorManager {
    }

    /**
     * 01 获取所有信息列表 GetVisitorRecordsByPage 0819之前使用接口
     * http://192.168.1.127:9012/openapi/Main/GetVisitorRecordsByPage
     */
    public static final String GET_RRECORD_BYPAGE =LOGIN_URL + LOGIN_FLODER_MAIN + "GetVisitorRecordsByPage";

    /**
     * 02获取特定时间的记录 GetVisitorRecordsByPageA 0819以后使用的接口
     */
    public static final String GET_RRECORD_BYPAGEA = LOGIN_URL + LOGIN_FLODER_MAIN + "GetVisitorRecordsByPageA";
    /**
     * 03 添加访客
     * http://192.168.1.127:9012/openapi/Main/AddOneVisitorRecord
     */
    public static final String ADD_VISITORRECORD = LOGIN_URL + LOGIN_FLODER_MAIN + "AddOneVisitorRecord";


    /**
     * 04 删除一条记录
     * http://192.168.1.127:9012/openapi/Main/DeleteVisitorRecordsByIDList
     */
    public static final String DELET_VISITORRECORD = LOGIN_URL + LOGIN_FLODER_MAIN + "DeleteVisitorRecordsByIDList";


    /**
     * 05 获取一条记录的详细信息
     * http://192.168.1.127:9012/openapi/Main/GetOneVisitorRecordByID/{recordID}/{storeId}
     */
    public static final String GET_ONE＿VISITORRECORD = LOGIN_URL + LOGIN_FLODER_MAIN + "GetOneVisitorRecordByID";

    /**
     * 06 搜索 GetVisitorWithSameName
     * 获取同名访客信息
     * http://192.168.1.127:9012/openapi/Main/GetVisitorWithSameName
     */
    public static final String GET_VISITOR_WITH_SAME_NAME = LOGIN_URL + LOGIN_FLODER_MAIN + "GetVisitorWithSameName";


    /**
     * 07 修改  UpdateOneVisitorRecord 
     * http://192.168.1.127:9012/openapi/Main/UpdateOneVisitorRecord
     */
    public static final String UPDATE_ONE_VISITORRECORD = LOGIN_URL + LOGIN_FLODER_MAIN + "UpdateOneVisitorRecord";

    /**
     * 08 更新版本 CheckVersion 
     */
    public static final String CLIENT_UPGRADE_URL = LOGIN_URL + LOGIN_FLODER_MAIN + "CheckVersion";


    /**
     * 考勤管理
     */

    public class AttendManager {
    }

    /**
     * 01获取全部记录
     * <p>
     * http://101.201.72.112:9016/openapi/Main/GetAttendanceRecordByPage
     * httppost
     */

    public static final String GET_ATTENDANCE_BYPAGE = LOGIN_URL + LOGIN_FLODER_MAIN + "GetAttendanceRecordByPage";

    /**
     * 02 根据employeeID查看登录人信息
     * <p>
     * http://101.201.72.112:9016/openapi/Main/GetEmployeeInfoByID
     * httpget
     */
    public static final String GET_ONE＿EMPLOYEE_INFO = LOGIN_URL + LOGIN_FLODER_MAIN + "GetEmployeeInfoByID";

    /**
     * 03 添加一条地图考勤记录
     * http://101.201.72.112:9016/openapi/Main/AddOneMapAttendanceRecord
     * httppost
     */
    public static final String ADD_ONE＿MAP_ATTENDANCE = LOGIN_URL + LOGIN_FLODER_MAIN + "AddOneMapAttendanceRecord";

    /**
     * 04 五、添加一条地wifi考勤记录
     * http://101.201.72.112:9016/openapi/Main/AddOneWIFIAttendanceRecord
     * httppost
     */
    public static final String ADD_ONE＿WIFI_ATTENDANCE = LOGIN_URL + LOGIN_FLODER_MAIN + "AddOneWIFIAttendanceRecord";

    /**
     *
     */




    /**
     * 因设置get/set功能public static final String-->public  final String
     */
    public class RegisterManager {
    }

    /**
     * 获取人脸库
     * <p>
     * 正式地址：http://192.168.1.127:1132/openapi/Attend/GetGroupByCyId/{companyID}
     */
    //		public static final String GET_FACE_DATEBASE_BY_COMPANYID = REGISTER_URL+ REGISTER_FLODER+ "Attend/GetGroupByCyId/";
    public static final String GET_FACE_DATEBASE_BY_COMPANYID =LOGIN_URL + REGISTER_FLODER + "Attend/GetGroupByCyId/";

    /**
     * 获取部门库
     * <p>
     * 正式地址：http://192.168.1.127:1132/openapi/Main/GetDeptByCyId/{companyID}
     */
    //		public static final String GET_DEPARTMENT_DATEBASE_BY_COMPANYID =  REGISTER_URL+ REGISTER_FLODER+ "Main/GetDeptByCyId/";
    public static final String GET_DEPARTMENT_DATEBASE_BY_COMPANYID = LOGIN_URL + REGISTER_FLODER + "Main/GetDeptByCyId/";

    /**
     * 新员工注册接口
     * <p>
     * <p>
     * 地址： http://192.168.1.127:1132/openapi/EmployeeImage/AddEmployeeAndImage
     */
    //		public static final String POST_NEW_EMPLOYEE = REGISTER_URL+ REGISTER_FLODER+ "EmployeeImage/AddEmployeeAndImage";
    public static final String POST_NEW_EMPLOYEE = LOGIN_URL + REGISTER_FLODER + "EmployeeImage/AddEmployeeAndImage";

    /**
     * 老员工注册接口
     * <p>
     * <p>
     * 地址：
     */
    //		public static final String POST_OLD_EMPLOYEE = REGISTER_URL+REGISTER_FLODER+"Employee/AddImageAndType";
    public static final String POST_OLD_EMPLOYEE = LOGIN_URL+ REGISTER_FLODER + "Employee/AddImageAndType";

    /**
     * 获取老员工列表
     */
    //		public static final String GET_OLD_EMPLOYEE_LIST = REGISTER_URL+REGISTER_FLODER+"Employee/GetEmployeeList/";
    public static final String GET_OLD_EMPLOYEE_LIST = LOGIN_URL+ REGISTER_FLODER + "Employee/GetEmployeeList/";

    /**
     * 获取老员工详细信息
     */
    //		public static final String GET_OLD_EMPLOYEE_DETAILS = REGISTER_URL+REGISTER_FLODER+"Employee/GetEmployeeByID/";
    public static final String GET_OLD_EMPLOYEE_DETAILS = LOGIN_URL+ REGISTER_FLODER + "Employee/GetEmployeeByID/";

}
