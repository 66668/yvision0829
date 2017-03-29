package com.yvision.db.entity;


import com.yvision.db.EntityBase;

public class UserEntity extends EntityBase {
    public UserTable TableSchema() {
        return (UserTable) _tableSchema;
    }

    public UserEntity() {
        _tableSchema = UserTable.Current();
    }

    //
    public String getUserName() {
        return (String) GetData(UserTable.C_userName);
    }

    public void setUserName(String value) {
        SetData(UserTable.C_userName, value);
    }


    //公司编号
    public String getAdminUserName() {
        return (String) GetData(UserTable.C_adminUserName);
    }

    public void setAdminUserName(String value) {
        SetData(UserTable.C_adminUserName, value);
    }

    //部门
    public String getDepartmentID() {
        return (String) GetData(UserTable.C_DepartmentID);
    }

    public void setDepartmentID(String value) {
        SetData(UserTable.C_DepartmentID, value);
    }

    /**
     * 员工编号，登录返回值
     *
     * @return
     */
    public String getWorkId() {
        return (String) GetData(UserTable.C_WrokId);
    }

    /**
     * 员工编号，登录返回值
     *
     * @return
     */
    public void setWorkId(String value) {
        SetData(UserTable.C_WrokId, value);
    }

    //设备id
    public String getDeviceId() {
        return (String) GetData(UserTable.C_DeviceId);
    }

    public void setDeviceId(String value) {
        SetData(UserTable.C_DeviceId, value);
    }

    //用户id
    public String getStoreUserId() {
        return (String) GetData(UserTable.C_StoreUserId);
    }

    public void setStoreUserId(String value) {
        SetData(UserTable.C_StoreUserId, value);
    }

    //员工id
    public String getEmployeeId() {
        return (String) GetData(UserTable.C_EmployeeId);
    }

    public void setEmployeeId(String value) {
        SetData(UserTable.C_EmployeeId, value);
    }


    //公司编号 id
    //公司编号 id
    public String getStoreID() {
        return (String) GetData(UserTable.C_StoreID);
    }

    public void setStoreID(String value) {
        SetData(UserTable.C_StoreID, value);
    }

    /**
     * 激光别名
     * @return
     */
    public String getregistRationID() {
        return (String) GetData(UserTable.C_registRationID);
    }

    /**
     * 极光别名
     * @param value
     */

    public void setregistRationID(String value) {
        SetData(UserTable.C_registRationID, value);
    }

    public String getEmployeeName() {
        return (String) GetData(UserTable.C_EmployeeName);
    }

    public void setEmployeeName(String value) {
        SetData(UserTable.C_EmployeeName, value);
    }

    public String getEmployeeGender() {
        return (String) GetData(UserTable.C_EmployeeGender);
    }

    public void setEmployeeGender(String value) {
        SetData(UserTable.C_EmployeeGender, value);
    }


    public String getAccount() {
        return (String) GetData(UserTable.C_Account);
    }

    public void setAccount(String value) {
        SetData(UserTable.C_Account, value);
    }

    public String getPassword() {
        return (String) GetData(UserTable.C_Password);
    }

    public void setPassword(String value) {
        SetData(UserTable.C_Password, value);
    }

    public String getUserPicture() {
        return (String) GetData(UserTable.C_UserPicture);
    }

    public void setUserPicture(String value) {
        SetData(UserTable.C_UserPicture, value);
    }

}
