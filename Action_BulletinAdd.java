package com.wls.FMS.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.wls.FMS.web.MyStrutsUtil;
import com.wls.FMS.web.constants.CS;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_BulletinAddInf;


public class Action_BulletinAdd extends DispatchAction{
	private String myFunctionID = "Bulletin.BulletinAdd";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private Service_BulletinAddInf service;
	public void setService(Object service) {
		this.service = (Service_BulletinAddInf) service;
	}
	private Service_CommonInf service_Common;
	public void setService_Common(Service_CommonInf service_Common) {
		this.service_Common = service_Common;
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setAttribute("functionName", service_Common.getFunctionName(myFunctionID));
		
		if(!MyStrutsUtil.doCheckRight(service.getJdbc(), myFunctionID, req))
			return mapping.findForward("NoRight");
		else
			return super.execute(mapping, form, req, resp);
	}
	
	public ActionForward doForward(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setAttribute("cmbBUL_TYPEHtml", MyStrutsUtil.htmlSelectOption(service.getBulletinTypeLst(""), ""));
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doAdd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldReqMap(req, form, 3);
		pMap.put("@p", MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req));
		service.doAdd(pMap);
		
		MyStrutsUtil.setStatusMsg(req, getResources(req),  "OK", new Object[]{"資料新增"});
		return doForward(mapping, form, req, resp);
	}
}
