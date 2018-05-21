package cn.appsys.service.developer;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.appsys.dao.appinfo.AppInfoMapper;
import cn.appsys.dao.appversion.AppVersionMapper;
import cn.appsys.pojo.AppVersion;
@Service
public class AppVersionServiceImpl implements AppVersionService {
	@Resource
	private  AppVersionMapper mapper;
	@Resource
	private AppInfoMapper appInfoMapper;
	
	@Override
	public List<AppVersion> getAppVersionList(Integer appId) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppVersionList(appId);
	}
	/**
	 * 涓氬姟锛氭柊澧瀉pp鐨勭増鏈俊鎭�
	 * 1銆乤pp_verion琛ㄦ彃鍏ユ暟鎹�
	 * 2銆佹洿鏂癮pp_info琛ㄥ搴攁pp鐨剉ersionId瀛楁锛堣褰曟渶鏂扮増鏈琲d锛�
	 * 娉ㄦ剰锛氫簨鍔℃帶鍒�
	 */
	@Override
	public boolean appsysadd(AppVersion appVersion) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		Integer versionId = null;
		if(mapper.add(appVersion) > 0){
			versionId = appVersion.getId();
			flag = true;
		}
		if(appInfoMapper.updateVersionId(versionId, appVersion.getAppId()) > 0 && flag){
			flag = true;
		}
		return flag;
	}
	@Override
	public AppVersion getAppVersionById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getAppVersionById(id);
	}
	@Override
	public boolean modify(AppVersion appVersion) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.modify(appVersion) > 0){
			flag = true;
		}
		return flag;
	}
	@Override
	public boolean deleteApkFile(Integer id) throws Exception {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(mapper.deleteApkFile(id) > 0){
			flag = true;
		}
		return flag;
	}
	@Override
	public boolean deleteVersionByAppId(Integer id) throws Exception {
		boolean flag = false;
		if(mapper.deleteVersionByAppId(id) > 0){
			flag = true;
		}
		return flag;
	}

}
