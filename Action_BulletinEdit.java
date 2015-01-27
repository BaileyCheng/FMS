package com.wls.FMS.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;

import will.io.FileTool_Properties;

import com.wls.FMS.web.MyStrutsUtil;
import com.wls.FMS.web.constants.CS;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_BulletinEditInf;


public class Action_BulletinEdit extends DispatchAction{
	public static String myFunctionID = "Bulletin.BulletinEdit";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private Service_BulletinEditInf service;
	public void setService(Object service){
		this.service = (Service_BulletinEditInf) service;
	}
	private Service_CommonInf service_Common;
	public void setService_Common(Service_CommonInf service_Common){
		this.service_Common = service_Common;
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setAttribute("functionName", service_Common.getFunctionName(myFunctionID));

		if(!MyStrutsUtil.doCheckRight(service.getJdbc(), myFunctionID, req)){
			return mapping.findForward("NoRight");
		}
		else{
			service_Common.setPermission(req, myFunctionID);
			return super.execute(mapping, form, req, resp);
		}
	}
	
	public ActionForward doReturn(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String hidProcBUL_ID = ((DynaActionForm)form).getString("hidProcBUL_ID");
		req.setAttribute("rCurBulID", hidProcBUL_ID);
		
		return new ActionForward("/BulletinView.do?act=doDetail");
	}
	
	public ActionForward doForward(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String rCurBulID = (String) req.getAttribute("rCurBulID");
		
		Map map_Bulletin = service.getBulletinMap(rCurBulID);
		List lst_Attachment = service.getBulletinAttachLst((String)map_Bulletin.get("BUL_ATTACHMENT"));
		req.setAttribute("map_Bulletin", map_Bulletin);
		req.setAttribute("cmbBUL_TYPEHtml", MyStrutsUtil.htmlSelectOption(service.getBulletinTypeLst(""), map_Bulletin.get("BUL_TYPE")));
		req.setAttribute("lst_Attachment", lst_Attachment);
		req.setAttribute("lstSize", lst_Attachment.size());
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doAddAttach(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		FileTool_Properties p = MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req);
		String hidProcBUL_ID = ((DynaActionForm)form).getString("hidProcBUL_ID");
		
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 10);
		pMap.put("@BulID", hidProcBUL_ID);
		pMap.put("@WebappsPath", p.getString("WebApp.webapps"));
		pMap.put("@UploadPath", p.getString("WebApp.UploadPath.Bulletin"));
		service.doAddAttach(pMap);
		
		MyStrutsUtil.setStatusMsg(req, getResources(req), "OK", "附件新增");
		req.setAttribute("rCurBulID", hidProcBUL_ID);
		return doForward(mapping, form, req, resp);
	}
	
	public ActionForward doDelAttach(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		FileTool_Properties p = MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req);
		String hidProcBUL_ID = ((DynaActionForm)form).getString("hidProcBUL_ID");
		
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@BulID", hidProcBUL_ID);
		pMap.put("@WebappsPath", p.getString("WebApp.webapps"));
		service.doDelAttach(pMap);
		
		MyStrutsUtil.setStatusMsg(req, getResources(req), "OK", "附件刪除");
		req.setAttribute("rCurBulID", hidProcBUL_ID);
		return doForward(mapping, form, req, resp);
	}
	
	public ActionForward doUpdateBulletin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		FileTool_Properties p = MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req);
		String hidProcBUL_ID = ((DynaActionForm)form).getString("hidProcBUL_ID");
		
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@BulID", hidProcBUL_ID);
		service.doUpdateBulletin(pMap);
		
		MyStrutsUtil.setStatusMsg(req, getResources(req), "OK", "更新");
		req.setAttribute("rCurBulID", hidProcBUL_ID);
		return doForward(mapping, form, req, resp);
	}
}
