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

import will.common.CMTool;

import com.wls.FMS.web.MyStrutsUtil;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_BulletinViewInf;


public class Action_BulletinView extends DispatchAction{
	private String myFunctionID = "Bulletin.BulletinView";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private int myPageSize = 10;
	
	private Service_BulletinViewInf service;
	public void setService(Object service) {
		this.service = (Service_BulletinViewInf) service;
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
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doQuery(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String p_cmbBUL_TYPE = ((DynaActionForm)form).getString("p_cmbBUL_TYPE");
		String p_cmbBUL_DATE_TIME = ((DynaActionForm)form).getString("p_cmbBUL_DATE_TIME");
		
		req.setAttribute("p_cmbBUL_TYPEHtml", MyStrutsUtil.htmlSelectOption(service.getBulletinTypeLst(""), p_cmbBUL_TYPE));
		req.setAttribute("p_cmbBUL_DATE_TIMEHtml", MyStrutsUtil.htmlSelectOption(service.getBulletinDateLst(""), p_cmbBUL_DATE_TIME));
		
		String p_cmbPage = ((DynaActionForm)form).getString("p_cmbPage"); if(CMTool.isEmpty(p_cmbPage)) p_cmbPage = "1";
		int curPage = Integer.parseInt(p_cmbPage);
		
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 5);
		pMap.put("BUL_TITLE", "%" + pMap.get("BUL_TITLE") + "%");
		List bulLst = service.getBulletinLst(pMap, curPage, myPageSize);
		req.setAttribute("bulLst", bulLst);
		
		int totalPage = service.getRowLstTotalPage(pMap, myPageSize);
		req.setAttribute("p_cmbPageHtml", MyStrutsUtil.htmlSelectOption(totalPage, curPage));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("curPage", curPage);
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doChangePage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String rCurBulID = (String) req.getAttribute("rCurBulID");
		String hidViewBulletinID = (CMTool.isEmpty(rCurBulID)) ? ((DynaActionForm)form).getString("hidViewBulletinID") : rCurBulID;
		service.doDetail(hidViewBulletinID);
		
		req.setAttribute("rViewBulletinID", hidViewBulletinID);
		return new ActionForward("/BulletinDetail.do?act=doViewBulletin");
	}
}
