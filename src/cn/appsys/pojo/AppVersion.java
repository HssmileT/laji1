package cn.appsys.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class AppVersion {
	private Integer id;//涓婚敭id
	private Integer appId;//appId
	private String versionNo;//鐗堟湰鍙�
	private String versionInfo;//鐗堟湰鎻忚堪
	private Integer publishStatus;//鍙戝竷鐘舵�乮d
	private String downloadLink;//apk鏂囦欢涓嬭浇閾炬帴
	private BigDecimal versionSize;//鐗堟湰澶у皬
	private Integer createdBy;//鍒涘缓鑰�
	private Date creationDate;//鍒涘缓鏃堕棿
	private Integer modifyBy;//鏇存柊鑰�
	private Date modifyDate;//鏇存柊鏃堕棿
	private String apkLocPath;//apk鏂囦欢鐨勬湇鍔″櫒瀛樺偍璺緞
	private String valueName;
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	private int valueId;
	public int getValueId() {
		return valueId;
	}
	public void setValueId(int valueId) {
		this.valueId = valueId;
	}
	private String appName;//APP杞欢鍚嶇О
	private String publishStatusName;//鍙戝竷鐘舵�佸悕绉�
	private String apkFileName;//涓婁紶鐨刟pk鏂囦欢鍚嶇О
	
	public String getApkFileName() {
		return apkFileName;
	}
	public void setApkFileName(String apkFileName) {
		this.apkFileName = apkFileName;
	}
	public String getPublishStatusName() {
		return publishStatusName;
	}
	public void setPublishStatusName(String publishStatusName) {
		this.publishStatusName = publishStatusName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getApkLocPath() {
		return apkLocPath;
	}
	public void setApkLocPath(String apkLocPath) {
		this.apkLocPath = apkLocPath;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getVersionInfo() {
		return versionInfo;
	}
	public void setVersionInfo(String versionInfo) {
		this.versionInfo = versionInfo;
	}
	public Integer getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public BigDecimal getVersionSize() {
		return versionSize;
	}
	public void setVersionSize(BigDecimal versionSize) {
		this.versionSize = versionSize;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Integer getModifyBy() {
		return modifyBy;
	}
	public void setModifyBy(Integer modifyBy) {
		this.modifyBy = modifyBy;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
}
