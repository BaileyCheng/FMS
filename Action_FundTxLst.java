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
import com.wls.FMS.web.constants.CS_SYS_DATA;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_FundTxLstInf;


public class Action_FundTxLst extends DispatchAction{
	public static String myFunctionID = "FundTx.FundTxLst";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private int myPageSize = 15;
	
	private Service_FundTxLstInf service;
	public void setService(Service_FundTxLstInf service) {
		this.service = service;
	}
	private Service_CommonInf service_Common;
	public void setService_Common(Service_CommonInf service_DataManager) {
		this.service_Common = service_DataManager;
	}
	
	/**
	 * 目前點選要查看的TX ID
	 */
	public static String R_CUR_TX_ID = "R_CUR_TX_ID";
	
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
		
		req.setAttribute("p_cmbTX_FUND_IDHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundLst("PAM", ""), ""));
		req.setAttribute("p_cmbTX_USER_BRANCHHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchDescLstByUser(loginUserID, ""), ""));
		req.setAttribute("p_cmbTX_DATEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getDataDateLst(CS.TBL_FMS_FUND_TX, ""), ""));
		req.setAttribute("p_cmbTX_CONDITIONHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxCondition, ""), "N"));
		
		pMap.put("@req", req);
		pMap.put("TX_CONDITION", "N");
		List rowLst = service.getFundTxLst(pMap, 1, myPageSize); req.setAttribute("rowLst", rowLst);
		
		int totalPage = service.getRowLstTotalPage(pMap, myPageSize);
		req.setAttribute("cmbPageHtml", MyStrutsUtil.htmlSelectOption(totalPage, 1));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("curPage", 1);
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doQuery(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		MyStrutsUtil.session_FormCheck(req, myForward, form);
		
		String loginUserID = MyStrutsUtil.getLoginUserID(req);
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 5);
		int curPage = Integer.parseInt(CMTool.noEmpty(((DynaActionForm)form).getString("curPage"), "1"));
		
		req.setAttribute("p_cmbTX_FUND_IDHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundLst("PAM", ""), pMap.get("TX_FUND_ID")));
		req.setAttribute("p_cmbTX_USER_BRANCHHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchDescLstByUser(loginUserID, ""), pMap.get("TX_USER_BRANCH")));
		req.setAttribute("p_cmbTX_USER_SEQHtml", MyStrutsUtil.htmlSelectOption(service_Common.getBranchTraderDescLstByUser(req, (String)pMap.get("TX_USER_BRANCH"), ""), pMap.get("TX_USER_SEQ")));
		req.setAttribute("p_cmbTX_DATEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getDataDateLst(CS.TBL_FMS_FUND_TX, ""), pMap.get("TX_DATE")));
		req.setAttribute("p_cmbTX_CONDITIONHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxCondition, ""), pMap.get("TX_CONDITION")));
		
		pMap.put("@req", req);
		List rowLst = service.getFundTxLst(pMap, curPage, myPageSize); req.setAttribute("rowLst", rowLst);
		
		int totalPage = service.getRowLstTotalPage(pMap, myPageSize);
		req.setAttribute("cmbPageHtml", MyStrutsUtil.htmlSelectOption(totalPage, curPage));
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("curPage", curPage);
		
		MyStrutsUtil.session_Remove(req, myForward);
		return mapping.findForward(myForward);
	}
	
	public ActionForward doChangePage(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return doQuery(mapping, form, req, resp);
	}
	
	public ActionForward doDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setAttribute(R_CUR_TX_ID, MyStrutsUtil.getDynaFormString(form, "hidVIEW_TX_ID"));
		
		MyStrutsUtil.setSessionValue(req, myForward, form);
		return MyStrutsUtil.getFunctionForward(Action_FundTxLst_Detail.myFunctionID, "doDetail");
	}
	
	public ActionForward doChangeBranch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String p_cmbTX_USER_BRANCH = req.getParameter("p_cmbTX_USER_BRANCH");
		
		String rv = MyStrutsUtil.jsonSelectOption(service_Common.getBranchTraderDescLstByUser(req, p_cmbTX_USER_BRANCH, ""), "");
		return MyStrutsUtil.jsonResponse(resp, rv);
	}
}
