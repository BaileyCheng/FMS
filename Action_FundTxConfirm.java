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
import com.wls.FMS.web.constants.CS;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_FundTxConfirmInf;


public class Action_FundTxConfirm extends DispatchAction{
	public static String myFunctionID = "FundTx.FundTxConfirm";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private int myPageSize = 10;
	
	private Service_FundTxConfirmInf service;
	public void setService(Service_FundTxConfirmInf service) {
		this.service = service;
	}
	private Service_CommonInf service_Common;
	public void setService_Common(Service_CommonInf service_DataManager) {
		this.service_Common = service_DataManager;
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
	
	public ActionForward doForward(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String loginUserID = MyStrutsUtil.getLoginUserID(req);
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 5);
		
		req.setAttribute("p_cmbTX_USER_BRANCHHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchDescLstByUser(loginUserID, ""), ""));
		req.setAttribute("p_cmbTX_DATEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getDataDateLst(CS.TBL_FMS_FUND_TX, ""), ""));
		req.setAttribute("p_cmbTX_STATUSHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundTxStatusLst(""), ""));
		
		pMap.put("@req", req);
		List rowLst = service.getFundTxLst(pMap, 1, myPageSize);
		req.setAttribute("rowLst", rowLst);
		
		int totalPage = service.getRowLstTotalPage(pMap, myPageSize);
		req.setAttribute("cmbPageHtml", MyStrutsUtil.htmlSelectOption(totalPage, 1));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("curPage", 1);
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doQuery(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if(req.getSession().getAttribute("sPreForm") != null) form = (ActionForm) req.getSession().getAttribute("sPreForm");
			
		String loginUserID = MyStrutsUtil.getLoginUserID(req);
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 5);
		int curPage = Integer.parseInt(CMTool.noEmpty(((DynaActionForm)form).getString("curPage"), "1"));
		
		req.setAttribute("p_cmbTX_USER_BRANCHHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchDescLstByUser(loginUserID, ""), pMap.get("TX_USER_BRANCH")));
		req.setAttribute("p_cmbTX_USER_SEQHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchTraderDescLstByUser(req, (String)pMap.get("TX_USER_BRANCH"), ""), pMap.get("TX_USER_SEQ")));
		req.setAttribute("p_cmbTX_DATEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getDataDateLst(CS.TBL_FMS_FUND_TX, ""), pMap.get("TX_DATE")));
		req.setAttribute("p_cmbTX_STATUSHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundTxStatusLst(""), pMap.get("TX_STATUS")));
		
		pMap.put("@req", req);
		List rowLst = service.getFundTxLst(pMap, curPage, myPageSize);
		req.setAttribute("rowLst", rowLst);
		
		int totalPage = service.getRowLstTotalPage(pMap, myPageSize);
		req.setAttribute("cmbPageHtml", MyStrutsUtil.htmlSelectOption(totalPage, curPage));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("curPage", curPage);
		
		req.getSession().removeAttribute("sPreForm");
		req.getSession().removeAttribute("sPreAction");
		return mapping.findForward(myForward);
	}
	
	public ActionForward doChangePage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doSetStatus_OK(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@req", req);
		
		service.doSetStatus_OK(pMap);
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doSetStatus_Q(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@req", req);
		
		service.doSetStatus_Q(pMap);
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String hidVIEW_TX_ID = ((DynaActionForm)form).getString("hidVIEW_TX_ID");
		
		req.setAttribute("rCUR_TX_ID", hidVIEW_TX_ID);
		req.getSession().setAttribute("sPreForm", form);
		req.getSession().setAttribute("sPreAction", "/" + myForward + ".do?act=doQuery");
		return new ActionForward("/FundTxLst_Detail.do?act=doViewFundTx");
	}
	
	public ActionForward doChangeBranch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String p_cmbTX_USER_BRANCH = req.getParameter("p_cmbTX_USER_BRANCH");
		
		String rv = MyStrutsUtil.jsonSelectOption(service_Common.getBranchTraderDescLstByUser(req, p_cmbTX_USER_BRANCH, ""), "");
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(rv);
		resp.getWriter().close();
		
		return null;
	}
}
