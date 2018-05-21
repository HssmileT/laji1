package cn.appsys.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.catalina.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.alibaba.fastjson.JSON;
import com.sun.accessibility.internal.resources.accessibility;

import cn.appsys.dao.devuser.DevUserMapper;
import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.service.developer.DevUserService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/dev")
public class UserController {
	//服务类接口
	@Resource
	DevUserService devUserService;

	@Resource
	AppCategoryService appCategoryService;

	@Resource
	AppInfoService appInfoService;

	@Resource
	AppVersionService appVersionService;

	@Resource
	DataDictionaryService dataDictionaryService;

	
	//登陆方法
	@RequestMapping(value="/login")
	public String login() {
		//跳转至后台登陆页面
		return "devlogin";
	}
	
	//登陆验证方法
	@RequestMapping(value="/dologin")
	public String dologin(HttpServletRequest request) throws Exception {
		//获取用户名和密码
		String devCode=request.getParameter("devCode");
		String devPassword=request.getParameter("devPassword");
		DevUser devUser;
		/*调用登陆方法根据返回值确定是否登陆成功*/
		devUser=devUserService.login(devCode, devPassword);
		if(devUser!=null) {
			request.getSession().setAttribute("devUserSession", devUser);
			return "developer/main";
		}
		return "redirect:dev/devlogin";
	}
	/*注销方法*/
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request) throws Exception {
		/*移除会话中的用户并重定向至首页*/
		request.getSession().removeAttribute("devUserSession");
		return "redirect:../index.jsp";
	}
	/*获取所有APP方法*/
	@RequestMapping(value="/list")
	public String applist(HttpServletRequest request,Model model) throws Exception {
		Integer queryStatus=null;
		/*逐个判断值是否为空*/
		if(request.getParameter("queryStatus")!=null&& request.getParameter("queryStatus")!="") {
			queryStatus=Integer.parseInt(request.getParameter("queryStatus"));
		}
		Integer queryFlatformId=null;
		if(request.getParameter("queryFlatformId")!=null&& request.getParameter("queryFlatformId")!="") {
			queryFlatformId=Integer.parseInt(request.getParameter("queryFlatformId"));
		}
		Integer queryCategoryLevel1=null;
		if(request.getParameter("queryCategoryLevel1")!=null && request.getParameter("queryCategoryLevel1")!="") {
			queryCategoryLevel1=Integer.parseInt(request.getParameter("queryCategoryLevel1"));
		}
		Integer queryCategoryLevel2=null;
		if(request.getParameter("queryCategoryLevel2")!=null && request.getParameter("queryCategoryLevel2")!="") {
			queryCategoryLevel2=Integer.parseInt(request.getParameter("queryCategoryLevel2"));
		}
		Integer queryCategoryLevel3=null;
		if(request.getParameter("queryCategoryLevel3")!=null && request.getParameter("queryCategoryLevel3")!="") {
			queryCategoryLevel3=Integer.parseInt(request.getParameter("queryCategoryLevel3"));
		}
		Integer devId=null;
		if(request.getParameter("devId")!=null && request.getParameter("devId")!="") {
			devId=Integer.parseInt(request.getParameter("devId"));
		}
		int pageSize = Constants.pageSize;
		String querySoftwareName=null;
		if(request.getParameter("querySoftwareName")!=null&& request.getParameter("querySoftwareName")!="") {
			querySoftwareName=request.getParameter("querySoftwareName");
		}
		Integer appId=null;
		if(request.getParameter("appId")!=null&& request.getParameter("appId")!="") {
			appId=Integer.parseInt(request.getParameter("appId"));
		}
		//初始化当前页为1
		Integer currentPageNo=1;
		String pageIndex=request.getParameter("pageIndex");
		//如果有传进来当前页页标则赋值给当前页变量
		if(pageIndex != null){
			try{
				currentPageNo = Integer.valueOf(pageIndex);
			}catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		//声明总记录数为0
		int totalCount = 0;
		try {
			//获取总APP数量
			totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//实例化一个页数实体类并赋值
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount = pages.getTotalPageCount();
		//如果当前页小于1则赋值为1 从第一页开始显示
		if(currentPageNo < 1){
			currentPageNo = 1;
		}else if(currentPageNo > totalPageCount){
			currentPageNo = totalPageCount;
		}
		//将获取到的所有APP信息和页面数量等数据传入会话
		String typeCode=request.getParameter("typeCode");
		List<AppInfo> appInfos=appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
		request.getSession().setAttribute("appInfoList", appInfos);
		List<AppCategory> list=appCategoryService.getAppCategoryListByParentId(null);
		List<DataDictionary> list2=dataDictionaryService.getDataDictionaryList("APP_STATUS");
		request.setAttribute("statusList", list2);
		List<DataDictionary> list3=dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
		request.setAttribute("dataDictionary", list3);
		List<DataDictionary> list4=dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
		request.setAttribute("flatFormList", list4);
		request.setAttribute("categoryLevel1List", list);
		model.addAttribute("pages", pages);
		//跳转至APP列表页面
		return "developer/appinfolist";
	}

	//获取二级分类方法
	@RequestMapping(value="/queryCategoryLevel2")
	@ResponseBody
	public Object queryCategoryLevel2(HttpServletRequest request) throws Exception {
		Integer parentId=Integer.parseInt(request.getParameter("pid"));
		List<AppCategory> list=appCategoryService.getAppCategoryListByParentId(parentId);
		return JSON.toJSONString(list);
	}
	//获取三级分类方法
	@RequestMapping(value="/queryCategoryLevel3")
	@ResponseBody
	public Object queryCategoryLevel3(HttpServletRequest request) throws Exception {
		Integer parentId=Integer.parseInt(request.getParameter("pid"));
		List<AppCategory> list=appCategoryService.getAppCategoryListByParentId(parentId);
		return JSON.toJSONString(list);
	}
	//获取一级分类方法
	@RequestMapping(value="/queryCategoryLevel1")
	@ResponseBody
	public Object queryCategoryLevel1(HttpServletRequest request) throws Exception {
		List<AppCategory> list=appCategoryService.getAppCategoryListByParentId(null);
		return JSON.toJSONString(list);
	}
	//删除APP方法
	@RequestMapping(value="/delapp")
	@ResponseBody
	public Object delapp(HttpServletRequest request) throws Exception {
		//获取APPID
		Integer delId=Integer.parseInt(request.getParameter("id"));
		//判断APP是否存在
		if(appInfoService.getAppInfo(delId, null)==null) {
			return "notexist";
		}
		//调用删除方法并判断是否删除成功
		if(appInfoService.deleteAppInfoById(delId)) {
			return "true";
		}
		return "false";
	}
	//添加APP方法
	@RequestMapping(value="/appinfoadd")
	public String appinfoadd(HttpServletRequest request) throws Exception {
		//跳转至添加APP页面
		return "developer/appinfoadd";
	}

	//获取所属平台列表
	@RequestMapping(value="/getfaltform")
	@ResponseBody
	public Object getfaltform(HttpServletRequest request) throws Exception {
		List<DataDictionary> list=dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
		return JSON.toJSONString(list);
	}

	//获取APP的名称
	@RequestMapping(value="/getApkName")
	@ResponseBody
	public Object getApkName(HttpServletRequest request) throws Exception {
		//获取传过来的APP名称
		String APKName=request.getParameter("APKName");
		//如果为空则返回状态为空
		if(APKName==null||APKName=="") {
			return "empty";
		}
		//判断此APP名称是否已经存在
		AppInfo app=appInfoService.getAppInfo(null, APKName);
		//不存在则返回不存在，存在则返回存在
		if(app!=null) {
			return "exist";
		}
		return "noexist";
	}

	//保存APP添加信息
	@RequestMapping(value="/appinfoaddsave",method=RequestMethod.POST)
	public String appinfoaddsave(HttpServletRequest request,
			@RequestParam(value = "a_logoPicPath") MultipartFile uploadFile) throws Exception {
		//获取APP地址
		String logoPicPath =  null;
		String logoLocPath =  null;
		//获取APP名称
		String APKName = request.getParameter("APKName");
		//判断文件上传控件里是否有文件
		if(!uploadFile.isEmpty()){
			//获取文件路径
			String path = request.getSession().getServletContext().getRealPath("statics"+java.io.File.separator+"uploadfiles");
			String oldFileName = uploadFile.getOriginalFilename();
			String prefix = FilenameUtils.getExtension(oldFileName);
			//如果是后缀为jpg或Png等图片格式的文件则将文件名和路径拼起来
			if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
					||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){
				String fileName = APKName + ".jpg";
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					uploadFile.transferTo(targetFile);
				} catch (Exception e) {
					// 如果报错则返回添加APP页面
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				}
				//将路劲拼起来
				logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		//获取要添加的APP的各个属性
		String softwareName=request.getParameter("softwareName");
		String supportROM=request.getParameter("supportROM");
		String interfaceLanguage=request.getParameter("interfaceLanguage");
		String softwareSize=request.getParameter("softwareSize");
		String downloads=request.getParameter("downloads");
		String flatformId=request.getParameter("flatformId");
		String categoryLevel1=request.getParameter("categoryLevel1");
		String categoryLevel2=request.getParameter("categoryLevel2");
		String categoryLevel3=request.getParameter("categoryLevel3");
		String status=request.getParameter("status");
		String appInfo=request.getParameter("appInfo");
		String a_logoPicPath=request.getParameter("a_logoPicPath");
		Integer devId=((DevUser)request.getSession().getAttribute("devUserSession")).getId();
		//将获取的属性存入实体类
		AppInfo appInfo2=new AppInfo();
		appInfo2.setLogoLocPath(logoPicPath);
		appInfo2.setLogoPicPath(logoPicPath);
		appInfo2.setAPKName(APKName);
		appInfo2.setDevId(devId);
		appInfo2.setModifyBy(devId);
		Date date=new Date();
		appInfo2.setCreationDate(date);
		appInfo2.setAppInfo(appInfo);
		appInfo2.setCategoryLevel1(Integer.parseInt(categoryLevel1));
		appInfo2.setCategoryLevel2(Integer.parseInt(categoryLevel2));
		appInfo2.setCategoryLevel3(Integer.parseInt(categoryLevel3));
		appInfo2.setSoftwareName(softwareName);
		appInfo2.setSupportROM(supportROM);
		appInfo2.setCreatedBy(devId);
		appInfo2.setInterfaceLanguage(interfaceLanguage);
		BigDecimal size=new BigDecimal(softwareSize);
		appInfo2.setSoftwareSize(size);
		appInfo2.setDownloads(Integer.parseInt(downloads));
		appInfo2.setFlatformId(Integer.parseInt(flatformId));
		appInfo2.setStatus(Integer.parseInt(status));
		//调用添加方法并重定向至LIST方法
		appInfoService.add(appInfo2);
		return "redirect:list";
	}

	//修改APP信息方法
	@RequestMapping(value="/appinfomodify")
	public String appinfomodify(HttpServletRequest request) throws Exception {
		//获取APPid之后查询到APP的详细信息并存入会话中 再跳转至修改页面
		Integer id=Integer.parseInt(request.getParameter("id"));
		AppInfo appInfo=appInfoService.getAppInfo(id, null);
		request.getSession().setAttribute("appInfo",appInfo);
		return "developer/appinfomodify";
	}

	//删除APP方法
	@RequestMapping(value="/delfile")
	@ResponseBody
	public String delfile(HttpServletRequest request) throws Exception {
		//获取APPID
		Integer id=Integer.parseInt(request.getParameter("id"));
		//根据删除成功返回相应的字符串
		if(appInfoService.deleteAppLogo(id)) {
			return "success";
		}
		return "failed";
	}

	//保存修改APP信息方法
	@RequestMapping(value="/appinfomodifysave",method=RequestMethod.POST)
	public String modifySave(AppInfo appInfo,javax.servlet.http.HttpSession session,HttpServletRequest request,
			@RequestParam(value="attach",required= false) MultipartFile attach){
		//获取APP名称
		String logoPicPath =  null;
		String logoLocPath =  null;
		String APKName = appInfo.getAPKName();
		//判断文件上传控件里是否有文件
		if(!attach.isEmpty()){
			String path = request.getSession().getServletContext().getRealPath("statics"+File.separator+"uploadfiles");
			String oldFileName = attach.getOriginalFilename();
			String prefix = FilenameUtils.getExtension(oldFileName);
			//如果是后缀为jpg或Png等图片格式的文件则将文件名和路径拼起来
			if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
					||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){
				String fileName = APKName + ".jpg";
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					attach.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//如果报错则返回添加APP页面
					return "redirect:/dev/appinfomodify?id="+appInfo.getId()
					+"&error=error2";
				}
				//将路径拼起来
				logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoLocPath = path+File.separator+fileName;
			}else{
				return "redirect:/dev/appinfomodify?id="+appInfo.getId()
				+"&error=error3";
			}
		}
		//将获取的各个属性写入实体类中
		appInfo.setModifyBy(((DevUser)session.getAttribute(Constants.DEV_USER_SESSION)).getId());
		appInfo.setModifyDate(new Date());
		appInfo.setLogoLocPath(logoLocPath);
		appInfo.setLogoPicPath(logoPicPath);
		try {
			//如果修改成功则跳转回LIST页面
			if(appInfoService.modify(appInfo)){
				return "redirect:/dev/list";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:list";
	}


	//添加版本信息方法
	@RequestMapping(value="/appversionadd")
	public String appversionadd(HttpServletRequest request) throws Exception {
		//获取要添加版本信息的APP的ID
		Integer id=Integer.parseInt(request.getParameter("id"));
		AppVersion appVersion=new AppVersion();
		appVersion.setAppId(id);
		//将APP的历史版本查询出来并存入会话中在跳转至添加版本信息页面
		request.getSession().setAttribute("appVersion", appVersion);
		request.getSession().setAttribute("appVersionList", appVersionService.getAppVersionList(id));
		return "developer/appversionadd";
	}

	//保存添加版本信息方法
	@RequestMapping(value="/addversionsave")
	public String addversionsave(HttpServletRequest request,@RequestParam(value = "a_downloadLink") MultipartFile uploadFile) throws Exception {
		//获取各个属性并写入实体类
		Integer appId=Integer.parseInt(request.getParameter("appId"));
		String versionNo=request.getParameter("versionNo");
		BigDecimal versionSize=new BigDecimal(request.getParameter("versionSize"));
		Integer publishStatus=Integer.parseInt(request.getParameter("publishStatus"));
		String versionInfo=request.getParameter("versionInfo");
		AppVersion appVersion=new AppVersion();
		appVersion.setAppId(appId);
		appVersion.setVersionNo(versionNo);
		appVersion.setVersionSize(versionSize);
		appVersion.setPublishStatus(publishStatus);
		appVersion.setVersionInfo(versionInfo);
		Date date=new Date();
		appVersion.setCreationDate(date);
		appVersion.setCreatedBy(((DevUser)request.getSession().getAttribute("devUserSession")).getId());
		//获取APP地址
		String logoPicPath =  null;
		String logoLocPath =  null;
		//获取APP名称
		String APKName = request.getParameter("APKName");
		//判断文件是否存在
		if(!uploadFile.isEmpty()){
			//获取路径信息
			String path = request.getSession().getServletContext().getRealPath("statics"+java.io.File.separator+"uploadfiles");
			String oldFileName = uploadFile.getOriginalFilename();
			String prefix = FilenameUtils.getExtension(oldFileName);
			//判断是否是图片格式
			if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
					||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){
				//将文件名拼起来并存入实体类
				String fileName = oldFileName + ".jpg";
				appVersion.setApkFileName(fileName);
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					uploadFile.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appinfoadd";
				} 
				logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appinfoadd";
			}
		}
		//将路径写入实体类
		appVersion.setDownloadLink(logoPicPath);
		appVersion.setApkLocPath(logoLocPath);
		//如果添加成功则跳转至LIST页面否则跳转至添加页面
		if(appVersionService.appsysadd(appVersion)) {
			return "redirect:list";
		}
		else {
			return "developer/appversionadd";
		}
	}

	//修改APP版本信息方法
	@RequestMapping(value="/appversionmodify")
	public String appversionmodify(HttpServletRequest request) throws Exception {
		//获取版本ID和APPID并查询详情信息再跳转至修改页面
		Integer versionId=Integer.parseInt(request.getParameter("vid"));
		Integer appId=Integer.parseInt(request.getParameter("aid"));
		AppVersion appVersion=appVersionService.getAppVersionById(versionId);
		request.getSession().setAttribute("appVersionList", appVersionService.getAppVersionList(appId));
		request.getSession().setAttribute("appVersion",appVersion);
		return "developer/appversionmodify";
	}
	//保存修改APP版本信息方法
	@RequestMapping(value="/appversionmodifysave")
	public String appversionmodifysave(HttpServletRequest request,@RequestParam(value = "attach") MultipartFile uploadFile) throws Exception {
		//获取各个属性并写入实体类
		Integer id=Integer.parseInt(request.getParameter("id"));
		Integer appId=Integer.parseInt(request.getParameter("appId"));
		String versionNo=request.getParameter("versionNo");
		BigDecimal versionSize=new BigDecimal(request.getParameter("versionSize"));
		Integer publishStatus=Integer.parseInt(request.getParameter("publishStatus"));
		String versionInfo=request.getParameter("versionInfo");
		AppVersion appVersion=new AppVersion();
		appVersion.setId(id);
		appVersion.setAppId(appId);
		appVersion.setVersionNo(versionNo);
		appVersion.setVersionInfo(versionInfo);
		appVersion.setVersionSize(versionSize);
		appVersion.setPublishStatus(publishStatus);
		//获取APP地址
		String logoPicPath =  null;
		String logoLocPath =  null;
		//获取APP名称
		String APKName = request.getParameter("APKName");
		//判断文件是否存在
		if(!uploadFile.isEmpty()){
			//获取路径信息
			String path = request.getSession().getServletContext().getRealPath("statics"+java.io.File.separator+"uploadfiles");
			String oldFileName = uploadFile.getOriginalFilename();
			String prefix = FilenameUtils.getExtension(oldFileName);
			//判断是否是图片格式
			if(prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png") 
					||prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")){
				//将文件名拼起来并存入实体类
				String fileName = oldFileName + ".jpg";
				appVersion.setApkFileName(fileName);
				File targetFile = new File(path,fileName);
				if(!targetFile.exists()){
					targetFile.mkdirs();
				}
				try {
					uploadFile.transferTo(targetFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_2);
					return "developer/appversionmodify";
				} 
				logoPicPath = request.getContextPath()+"/statics/uploadfiles/"+fileName;
				logoLocPath = path+File.separator+fileName;
			}else{
				request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
				return "developer/appversionmodify";
			}
		}
		//将路径写入实体类
		appVersion.setDownloadLink(logoPicPath);
		appVersion.setApkLocPath(logoLocPath);
		//调用修改版本信息方法并重定向至LIST页面
		appVersionService.modify(appVersion);
		return "redirect:list";
	}
	
	//删除版本信息方法
	@RequestMapping(value="/delfileByVersion")
	@ResponseBody
	public String delfileByVersion(HttpServletRequest request) throws Exception {
		//获取ID并调用删除修改信息方法 根据方法返回值返回是否成功的字符串
		Integer id=Integer.parseInt(request.getParameter("id"));
		if(appVersionService.deleteApkFile(id)) {
			return "success";
		}
		return "failed";
	}
	
	//APP详情查看方法
	@RequestMapping(value="/appview")
	public String appview(HttpServletRequest request) throws Exception {
		//获取APPID
		Integer id=Integer.parseInt(request.getParameter("id"));
		//将详情APP实体类写入会话中并跳转至详情页面
		request.getSession().setAttribute("appInfo", appInfoService.getAppInfo(id, null));
		request.getSession().setAttribute("appVersionList", appVersionService.getAppVersionList(id));
		return "developer/appinfoview";
	}
	
	//上下架方法
	@RequestMapping(value="/sale")
	@ResponseBody
	public Object sale(HttpServletRequest request) throws Exception {
		//获取要上下架的APPID
		Integer id=Integer.parseInt(request.getParameter("id"));
		AppInfo appInfo=new AppInfo();
		appInfo.setId(id);
		//判断JS传来的参数是需要上架还是下架
		if(request.getParameter("status").equals("close")) {
			//写入状态
			appInfo.setStatus(4);
		}
		else {
			appInfo.setStatus(5);
		}
		//更新状态并根据返回值判断是否成功
		if(appInfoService.appsysUpdateSaleStatusByAppId(appInfo)) {
			return "success";
		}
		return "failed";
	}
}
