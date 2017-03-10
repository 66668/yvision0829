package com.yvision.model;

import java.io.Serializable;

/**
 * Created by sjy on 2017/3/10.
 */

public class VipModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /// vip识别记录编号
    public String VipRecordID;
    /// vip名称
    public String EmployeeName;
    /// vip号
    public String EmployeeCardNo;
    ///  采集图像相对路径(小图)
    public String SmallCapImagePath;
    /// 采集时间
    public String CapTime;
    /// 对比分数
    public String Score;

    public String CompanyName;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getVipRecordID() {
        return VipRecordID;
    }

    public void setVipRecordID(String vipRecordID) {
        VipRecordID = vipRecordID;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getEmployeeCardNo() {
        return EmployeeCardNo;
    }

    public void setEmployeeCardNo(String employeeCardNo) {
        EmployeeCardNo = employeeCardNo;
    }

    public String getSmallCapImagePath() {
        return SmallCapImagePath;
    }

    public void setSmallCapImagePath(String smallCapImagePath) {
        SmallCapImagePath = smallCapImagePath;
    }

    public String getCapTime() {
        return CapTime;
    }

    public void setCapTime(String capTime) {
        CapTime = capTime;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }
}
