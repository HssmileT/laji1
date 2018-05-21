package cn.appsys.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.AppVersion;
import cn.appsys.pojo.BackendUser;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.pojo.DevUser;
import cn.appsys.service.backend.AppService;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.AppInfoService;
import cn.appsys.service.developer.AppVersionService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/manager")
public class ManagerController {
	
	//服务类接口
	@Resource
	AppService appService;
	
	@Resource
	DataDictionaryService dataDictionaryService;
	
	@Resource
	AppVersionService appVersionService;
	
	@Resource
	BackendUserService backendUserService;
	
	@Resource
	AppCategoryService appCategoryService;

	@Resource
	AppInfoService appInfoService;
	
	@RequestMapping(value="/login")
	public String login() {
		return "backendlogin";
	}
	
	/*登陆方法*/
	@RequestMapping(value="/dologin")
	public String dologin(HttpServletRequest request) throws Exception {
		/*获取用户名密码*/
		String userCode=request.getParameter("userCode");
		String userPassword=request.getParameter("userPassword");
		BackendUser backendUser=new BackendUser();
		/*调用登陆方法根据返回值确定是否登陆成功*/
		backendUser=backendUserService.login(userCode, userPassword);
		if(backendUser!=null) {
			request.getSession().setAttribute("userSession", backendUser);
			return "backend/main";
		}
		return "redirect:login";
	}
	
	/*注销方法*/
	@RequestMapping(value="/logout")
	public String loginout(HttpServletRequest request) throws Exception {
		/*移除会话中的用户并重定向至首页*/
		request.getSession().removeAttribute("userSession");
		return "redirect:../index.jsp";
	}
	
	/*获取所有APP方法*/
	@RequestMapping(value="/list")
	public String list(HttpServletRequest request,Model model) throws Exception {
		/*逐个判断值是否为空*/
		Integer queryStatus=null;
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
		return "backend/applist";
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
	
	//获取APP信息和版本信息并跳转至审核页面方法
	@RequestMapping(value="/check")
	public String check(HttpServletRequest request) throws Exception {
		//获取穿过来的APPID和版本ID
		int aid=Integer.parseInt(request.getParameter("aid"));
		int vid=Integer.parseInt(request.getParameter("vid"));
		AppInfo appInfo=new AppInfo();
		appInfo.setId(aid);
		AppVersion appVersion=new AppVersion();
		appVersion.setId(vid);
		//根据APPID和版本ID获取信息
		appInfo=appInfoService.getAppInfo(aid, null);
		appVersion=appVersionService.getAppVersionById(vid);
		//将信息存入会话并跳转至审核页面
		request.getSession().setAttribute("appInfo", appInfo);
		request.getSession().setAttribute("appVersion", appVersion);
		return "backend/appcheck";
	}
	
	//保存审核状态信息
	@RequestMapping(value="/checksave")
	public String checksave(HttpServletRequest request) throws Exception {
		//获取状态ID和APPid并修改状态信息
		Integer status=Integer.parseInt(request.getParameter("status"));
		Integer id=Integer.parseInt(request.getParameter("id"));
		appService.updateSatus(status, id);
		//重定向至APP列表方法
		return "redirect:/manager/list";
	}
}
