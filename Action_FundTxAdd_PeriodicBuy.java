package com.wls.FMS.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.wls.FMS.web.MyStrutsUtil;
import com.wls.FMS.web.constants.CS_SYS_DATA;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_FundTxAdd_PeriodicBuyInf;


import java.util.List;


public class Action_FundTxAdd_PeriodicBuy extends DispatchAction{
	public static String myFunctionID = "FundTx.FundTxAdd";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private Service_FundTxAdd_PeriodicBuyInf service;
	public void setService(Object service) {
		this.service = (Service_FundTxAdd_PeriodicBuyInf) service;
	}
	private Service_CommonInf service_Common;
	public void setService_Common(Service_CommonInf service_Common) {
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
		return MyStrutsUtil.getFunctionForward(Action_FundTxLst.myFunctionID);
	}
	
	public ActionForward doForward(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		//設定交易類別的下拉選項
		//原始會去寫出多個選項
		//req.setAttribute("cmbTX_METHODHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxMethod), "S"));
		//現在改成只顯示單一選項:  定期定額
		String realValue ="R";
		String viewValue ="定期定額";
		String cmbTX_METHODHtml = "<Option value=\""+ realValue + "\" selected>" + viewValue + "</Option>";
		req.setAttribute("cmbTX_METHODHtml", cmbTX_METHODHtml);

		req.setAttribute("cmbTX_FUND_SITCHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundSITCLst(null), "PAM"));
		req.setAttribute("cmbTX_FUND_IDHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundLst("PAM", ""), ""));
		//付款方式 disable掉
		//req.setAttribute("cmbTX_PAY_TYPEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxDPayType, ""), ""));
		//設定 扣款日的下拉選項
		//List investmentDate;   //扣款日下拉選項的List
		//String sql = "Select INVESTMENT_DATE from FMS_SITC_INVESTMENTDATE";
		//List investmentDate = jdbc.getArrLst(sql);   //扣款日下拉選項的List
		
		//設定 扣款日的下拉選項   Bailey Cheng
		//req.setAttribute("cmbTX_PAY_TimeHtml", MyStrutsUtil.htmlSelectOption(service_Common.getInvestmenDate(null), ""));
		
		//設定扣款日動態checkbox list
		String loginUserID = MyStrutsUtil.getLoginUserID(req);
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		
		List lst_PaymentDate = service.getPaymentDateLst(pMap); req.setAttribute("lst_PaymentDate", lst_PaymentDate);
		
		
		return mapping.findForward(myForward);
	}

	public ActionForward doAdd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldReqMap(req, form, 3);
		service.doAdd(pMap);
		
		req.setAttribute("cmbTX_METHODHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxMethod), pMap.get("TX_METHOD")));
		req.setAttribute("cmbTX_FUND_SITCHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundSITCLst(null), pMap.get("TX_FUND_SITC")));
		req.setAttribute("cmbTX_FUND_IDHtml", MyStrutsUtil.htmlSelectOption(service_Common.getFundLst((String)pMap.get("TX_FUND_SITC"), ""), pMap.get("TX_FUND_ID")));
		//付款方式disable掉
		//req.setAttribute("cmbTX_PAY_TYPEHtml", MyStrutsUtil.htmlSelectOption(service_Common.getSysDataLst(CS_SYS_DATA.FundTx_TxDPayType, ""), pMap.get("TX_PAY_TYPE")));
		
		MyStrutsUtil.setHidJSMsg(req, "新增成功");
		return mapping.findForward(myForward);
	}
	
	public ActionForward doGetCustInfo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String txtTX_CUST_ID = req.getParameter("txtTX_CUST_ID");
		
		return MyStrutsUtil.jsonResponse(resp, service.getCustInfo(txtTX_CUST_ID));
	}
	
	public ActionForward doGetFundInfo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String cmbTX_TYPE = req.getParameter("cmbTX_TYPE");
		String cmbTX_FUND_ID = req.getParameter("cmbTX_FUND_ID");
		String txtTX_AMT = req.getParameter("txtTX_AMT");
		
		String rv = service.getFundInfo(cmbTX_TYPE, cmbTX_FUND_ID, Long.parseLong(txtTX_AMT));
		return MyStrutsUtil.jsonResponse(resp, rv);
	}
	
	public ActionForward doGetTraderInfo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String txtTX_USER_ID = req.getParameter("txtTX_USER_ID");
		
		String rv = service.getTraderInfo(txtTX_USER_ID);
		return MyStrutsUtil.jsonResponse(resp, rv);
	}
	
	public ActionForward doCountTxData(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = new HashMap();
		pMap.put("@req", req);
		
		String rv = service.getCountTxData(pMap).toString();
		return MyStrutsUtil.jsonResponse(resp, rv);
	}
}
