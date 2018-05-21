package cn.appsys.service.developer;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;

@Service
public class AppInfoServiceImpl implements AppInfoService {
	@Resource
	private AppInfoMapper mapper;
	@Resource
	private AppVersionMapper appVersionMapper;
	
	@Override
	public boolean add(AppInfo appInfo) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.add(appInfo) > 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean modify(AppInfo appInfo) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.modify(appInfo) > 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean deleteAppInfoById(Integer delId) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.deleteAppInfoById(delId) > 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public AppInfo getAppInfo(Integer id,String APKName) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfo(id,APKName);
	}

	@Override
	public List<AppInfo> getAppInfoList(String querySoftwareName,
									Integer queryStatus, Integer queryCategoryLevel1,
									Integer queryCategoryLevel2, Integer queryCategoryLevel3,
									Integer queryFlatformId, Integer devId, Integer currentPageNo,
									Integer pageSize) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
	}

	@Override
	public int getAppInfoCount(String querySoftwareName, Integer queryStatus,
			Integer queryCategoryLevel1, Integer queryCategoryLevel2,
			Integer queryCategoryLevel3, Integer queryFlatformId, Integer devId)
			throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId,devId);
	}

	@Override
	public boolean deleteAppLogo(Integer id) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.deleteAppLogo(id) > 0){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 涓氬姟锛氭牴鎹產ppId鍒犻櫎APP淇℃伅
	 * 1銆侀�氳繃appId锛屾煡璇pp_verion琛ㄤ腑鏄惁鏈夋暟鎹�
	 * 2銆佽嫢鐗堟湰琛ㄤ腑鏈夎app搴旂敤瀵瑰簲鐨勭増鏈俊鎭紝鍒欒繘琛岀骇鑱斿垹闄わ紝鍏堝垹鐗堟湰淇℃伅锛坅pp_version锛夛紝鍚庡垹app鍩烘湰淇℃伅锛坅pp_info锛�
	 * 3銆佽嫢鐗堟湰琛ㄤ腑鏃犺app搴旂敤瀵瑰簲鐨勭増鏈俊鎭紝鍒欑洿鎺ュ垹闄pp鍩烘湰淇℃伅锛坅pp_info锛夈��
	 * 娉ㄦ剰锛氫簨鍔℃帶鍒讹紝涓婁紶鏂囦欢鐨勫垹闄�
	 */
	@Override
	public boolean appsysdeleteAppById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		int versionCount = appVersionMapper.getVersionCountByAppId(id);
		List<AppVersion> appVersionList = null;
		if(versionCount > 0){//1 鍏堝垹鐗堟湰淇℃伅
			//<1> 鍒犻櫎涓婁紶鐨刟pk鏂囦欢
			appVersionList = appVersionMapper.getAppVersionList(id);
			for(AppVersion appVersion:appVersionList){
				if(appVersion.getApkLocPath() != null && !appVersion.getApkLocPath().equals("")){
					File file = new File(appVersion.getApkLocPath());
					if(file.exists()){
						if(!file.delete())
							throw new Exception();
					}
				}
			}			
			//<2> 鍒犻櫎app_version琛ㄦ暟鎹�
			appVersionMapper.deleteVersionByAppId(id);
		}
		//2 鍐嶅垹app鍩虹淇℃伅
		//<1> 鍒犻櫎涓婁紶鐨刲ogo鍥剧墖
		AppInfo appInfo = mapper.getAppInfo(id, null);
		if(appInfo.getLogoLocPath() != null && !appInfo.getLogoLocPath().equals("")){
			File file = new File(appInfo.getLogoLocPath());
			if(file.exists()){
				if(!file.delete())
					throw new Exception();
			}
		}
		//<2> 鍒犻櫎app_info琛ㄦ暟鎹�
		if(mapper.deleteAppInfoById(id) > 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean appsysUpdateSaleStatusByAppId(AppInfo appInfoObj) throws Exception {
		/*
		 * 涓婃灦锛� 
			1 鏇存敼status鐢便��2 or 5銆� to 4 锛� 涓婃灦鏃堕棿
			2 鏍规嵁versionid 鏇存柊 publishStauts 涓� 2 
			
			涓嬫灦锛�
			鏇存敼status 鐢�4缁欎负5
		 */
		
		Integer operator = appInfoObj.getModifyBy();
		
		//get appinfo by appid
		AppInfo appInfo = mapper.getAppInfo(appInfoObj.getId(), null);
		if(null == appInfo){
			return false;
		}else{
			switch (appInfo.getStatus()) {
				case 2: //褰撶姸鎬佷负瀹℃牳閫氳繃鏃讹紝鍙互杩涜涓婃灦鎿嶄綔
					onSale(appInfo,operator,4,2);
					break;
				case 5://褰撶姸鎬佷负涓嬫灦鏃讹紝鍙互杩涜涓婃灦鎿嶄綔
					onSale(appInfo,operator,4,2);
					break;
				case 4://褰撶姸鎬佷负涓婃灦鏃讹紝鍙互杩涜涓嬫灦鎿嶄綔
					offSale(appInfo,operator,5);
					break;

			default:
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * on Sale
	 * @param appInfo
	 * @param operator
	 * @param appInfStatus
	 * @param versionStatus
	 * @throws Exception
	 */
	private void onSale(AppInfo appInfo,Integer operator,Integer appInfStatus,Integer versionStatus) throws Exception{
		offSale(appInfo,operator,appInfStatus);
		setSaleSwitchToAppVersion(appInfo,operator,versionStatus);
	}
	
	
	/**
	 * offSale
	 * @param appInfo
	 * @param operator
	 * @param appInfStatus
	 * @return
	 * @throws Exception
	 */
	private boolean offSale(AppInfo appInfo,Integer operator,Integer appInfStatus) throws Exception{
		AppInfo _appInfo = new AppInfo();
		_appInfo.setId(appInfo.getId());
		_appInfo.setStatus(appInfStatus);
		_appInfo.setModifyBy(operator);
		_appInfo.setOffSaleDate(new Date(System.currentTimeMillis()));
		mapper.modify(_appInfo);
		return true;
	}
	
	/**
	 * set sale method to on or off
	 * @param appInfo
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	private boolean setSaleSwitchToAppVersion(AppInfo appInfo,Integer operator,Integer saleStatus) throws Exception{
		AppVersion appVersion = new AppVersion();
		appVersion.setId(appInfo.getVersionId());
		appVersion.setPublishStatus(saleStatus);
		appVersion.setModifyBy(operator);
		appVersion.setModifyDate(new Date(System.currentTimeMillis()));
		appVersionMapper.modify(appVersion);
		return false;
	}
	
}
