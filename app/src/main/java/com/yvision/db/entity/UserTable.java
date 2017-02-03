package com.yvision.db.entity;

import com.yvision.db.ColumnInfo;
import com.yvision.db.TableInfo;

public class UserTable extends TableInfo {
    public static String C_TableName = "user";

    public static String C_workId = "workId";//
    public static String C_storeId = "storeId";//
    public static String C_StoreID = "Store_ID";////公司编号
    public static String C_ClientID = "ClientID";//
    public static String C_URL = "url";//
    public static String C_StoreUserId = "StoreUserId";//
    public static String C_employeeID = "employeeID";//

    public static String C_UserId = "user_id";
    public static String C_Fullname = "fullname";
    public static String C_StoreName = "store_name";
    public static String C_Account = "account";
    public static String C_Password = "password";
    public static String C_UserPicture = "userPicture";

    public UserTable() {
        _tableName = "user";
    }

    protected static UserTable _current;

    public static UserTable Current() {
        if (_current == null) {
            Initial();
        }
        return _current;
    }

    private static void Initial() {
        _current = new UserTable();

        _current.Add(C_workId, new ColumnInfo(C_workId, "workId", false, "String"));
        _current.Add(C_storeId, new ColumnInfo(C_storeId, "storeId", false, "String"));
        _current.Add(C_ClientID, new ColumnInfo(C_ClientID, "ClientID", false, "String"));
        _current.Add(C_StoreID, new ColumnInfo(C_StoreID, "Store_ID", false, "String"));
        _current.Add(C_URL, new ColumnInfo(C_URL, "url", false, "String"));
        _current.Add(C_StoreUserId, new ColumnInfo(C_StoreUserId, "StoreUserId", false, "String"));
        _current.Add(C_employeeID, new ColumnInfo(C_employeeID, "employeeID", false, "String"));


        _current.Add(C_UserId, new ColumnInfo(C_UserId, "UserId", true, "String"));
        _current.Add(C_Fullname, new ColumnInfo(C_Fullname, "Fullname", false, "String"));
        _current.Add(C_StoreName, new ColumnInfo(C_StoreName, "StoreName", false, "String"));

        _current.Add(C_Account, new ColumnInfo(C_Account, "Account", false, "String"));
        _current.Add(C_Password, new ColumnInfo(C_Password, "Password", false, "String"));
        _current.Add(C_UserPicture, new ColumnInfo(C_UserPicture, "UserPicture", false, "String"));
    }

    //
    public ColumnInfo workId() {
        return GetColumnInfoByName(C_workId);
    }

    //
    public ColumnInfo storeId() {
        return GetColumnInfoByName(C_storeId);
    }

    //
    public ColumnInfo StoreID() {
        return GetColumnInfoByName(C_StoreID);
    }

    //
    public ColumnInfo ClientID() {
        return GetColumnInfoByName(C_ClientID);
    }

    //
    public ColumnInfo url() {
        return GetColumnInfoByName(C_URL);
    }

    //
    public ColumnInfo employeeID() {
        return GetColumnInfoByName(C_employeeID);
    }

    //
    public ColumnInfo StoreUserId() {
        return GetColumnInfoByName(C_StoreUserId);
    }

    public ColumnInfo UserId() {
        return GetColumnInfoByName(C_UserId);
    }

    public ColumnInfo Fullname() {
        return GetColumnInfoByName(C_Fullname);
    }

    public ColumnInfo StoreName() {
        return GetColumnInfoByName(C_StoreName);
    }

    public ColumnInfo Account() {
        return GetColumnInfoByName(C_Account);
    }

    public ColumnInfo Password() {
        return GetColumnInfoByName(C_Password);
    }

    public ColumnInfo UserPicture() {
        return GetColumnInfoByName(C_UserPicture);
    }

}
