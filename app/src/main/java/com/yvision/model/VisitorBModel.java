package com.yvision.model;

import java.io.Serializable;

/**
 * 返回的 VisitorBModel 里面的字段不是所有都有值，只返回一些基本的信息：
 * VisitorID、VisitorName、RespondentID、Aim、Affilication、
 * WelcomeWord、Remark、isVip
 *
 * @author JackSong
 */
public class VisitorBModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String RecordID;//记录ID
    private String VisitorID;//访客employeeID
    private String VisitorName;//访客姓名
    private String RespondentID;//受访者ID
    private String RespondentName;//受访者ID

    private String Aim;//来访目的
    private String Affilication;//访客单位
    private String ArrivalTimePlan;//到访时间
    private String LeaveTimePlan;//离开时间
    private String WelcomeWord;//欢迎语

    private String Remark;//备注
    private boolean isVip;//vip
    private boolean isReceived;//来访状态
    private String ImagePath;//图片
    private String StoreID;//公司编号
    private String OperatorName;//
    private String GroupId;//

    private String PhoneNumber;//手机号
    private String iLastUpdateTime;//用于记录访客登记的时间

    public String getOperatorName() {
        return OperatorName;
    }

    public void setOperatorName(String operatorName) {
        OperatorName = operatorName;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getRespondentName() {
        return RespondentName;
    }

    public void setRespondentName(String respondentName) {
        RespondentName = respondentName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getRecordID() {
        return RecordID;
    }

    public void setRecordID(String recordID) {
        RecordID = recordID;
    }

    public String getVisitorID() {
        return VisitorID;
    }

    public void setVisitorID(String visitorID) {
        VisitorID = visitorID;
    }

    public String getVisitorName() {
        return VisitorName;
    }

    public void setVisitorName(String visitorName) {
        VisitorName = visitorName;
    }

    public String getRespondentID() {
        return RespondentID;
    }

    public void setRespondentID(String RespindentId) {
        this.RespondentID = RespindentId;
    }

    public String getAim() {
        return Aim;
    }

    public void setAim(String aim) {
        Aim = aim;
    }

    public String getAffilication() {
        return Affilication;
    }

    public void setAffilication(String affilication) {
        Affilication = affilication;
    }

    public String getArrivalTimePlan() {
        return ArrivalTimePlan;
    }

    public void setArrivalTimePlan(String arrivalTimePlan) {
        ArrivalTimePlan = arrivalTimePlan;
    }

    public String getLeaveTimePlan() {
        return LeaveTimePlan;
    }

    public void setLeaveTimePlan(String leaveTimePlan) {
        LeaveTimePlan = leaveTimePlan;
    }

    public String getWelcomeWord() {
        return WelcomeWord;
    }

    public void setWelcomeWord(String welcomeWord) {
        WelcomeWord = welcomeWord;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        this.Remark = remark;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean isVip) {
        this.isVip = isVip;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getStoreID() {
        return StoreID;
    }

    public void setStoreID(String storeID) {
        StoreID = storeID;
    }

    public String getiLastUpdateTime() {
        return iLastUpdateTime;
    }

    public void setiLastUpdateTime(String iLastUpdateTime) {
        this.iLastUpdateTime = iLastUpdateTime;
    }
}
