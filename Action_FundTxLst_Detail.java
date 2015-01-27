package com.wls.FMS.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.wls.FMS.web.MyStrutsUtil;
import com.wls.FMS.web.service.common.inf.Service_CommonInf;
import com.wls.FMS.web.service.inf.Service_FundTxLst_DetailInf;


public class Action_FundTxLst_Detail extends DispatchAction{
	public static final String myFunctionID = "FundTx.FundTxLst_Detail";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	private Service_FundTxLst_DetailInf service;
	public void setService(Object service) {
		this.service = (Service_FundTxLst_DetailInf) service;
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
		return MyStrutsUtil.getFunctionForward(Action_FundTxLst.myFunctionID, "doQuery");
	}
	
	public ActionForward doDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String rCUR_TX_ID = (String) req.getAttribute(Action_FundTxLst.R_CUR_TX_ID);
		
		Map pMap = new HashMap();
		pMap.put("TX_ID", rCUR_TX_ID);
		pMap.put("@req", req);
		Map map_TX = service.getFundTxMap(pMap); req.setAttribute("map_TX", map_TX);
		List lst_TxD = service.getFundTxDLst(pMap); req.setAttribute("lst_TxD", lst_TxD);
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doEdit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@req", req);
		service.doEdit(pMap);
		
		MyStrutsUtil.setHidJSMsg(req, "修改完成");
		req.setAttribute(Action_FundTxLst.R_CUR_TX_ID, MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		return doDetail(mapping, form, req, resp);
	}
	
	public ActionForward doDel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		service.doDel(pMap);
		
		MyStrutsUtil.setHidJSMsg(req, "刪除成功");
		return mapping.findForward(myForward);
	}
	
	public ActionForward doAddTxD(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldReqMap(req, form, 10);
		pMap.put("TX_ID", MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		pMap.put("TXD_PAY_TYPE", MyStrutsUtil.getDynaFormString(form, "cmbTX_PAY_TYPE"));
		pMap.put("TXD_PAY_TYPE_DESC", MyStrutsUtil.getDynaFormString(form, "txtTX_PAY_TYPE_DESC"));
		service.doAddTxD(pMap);
		
		req.setAttribute(Action_FundTxLst.R_CUR_TX_ID, MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		return doDetail(mapping, form, req, resp);
	}
	
	public ActionForward doDelTxD(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldReqMap(req, form, 3);
		pMap.put("TX_ID", MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		service.doDelTxD(pMap);
		
		req.setAttribute(Action_FundTxLst.R_CUR_TX_ID, MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		return doDetail(mapping, form, req, resp);
	}
	
	public ActionForward doEditTxD(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.setAttribute(Action_FundTxLst.R_CUR_TX_ID, MyStrutsUtil.getDynaFormString(form, "hidCUR_TX_ID"));
		return MyStrutsUtil.getFunctionForward(Action_FundTxLst_TxDEdit.myFunctionID);
	}
}
