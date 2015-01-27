package com.wls.FMS.web.action;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
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
import com.wls.FMS.web.service.inf.Service_BulletinDetailInf;


public class Action_BulletinDetail extends DispatchAction{
	public static String myFunctionID = "Bulletin.BulletinDetail";
	private String myForward = this.getClass().getName().substring(this.getClass().getName().indexOf("_")+1);
	
	private Service_BulletinDetailInf service;
	public void setService(Object service) {
		this.service = (Service_BulletinDetailInf) service;
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
	
	public ActionForward doViewBulletin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String rViewBulletinID = (String) req.getAttribute("rViewBulletinID");
		
		Map map_Bulletin = service.getBulletinMap(rViewBulletinID);
		req.setAttribute("map_Bulletin", map_Bulletin);
		req.setAttribute("lst_Attachment", service.getBulletinAttachLst((String)map_Bulletin.get("BUL_ATTACHMENT"), req));
//		service.setPermission(req);
		
		return mapping.findForward(myForward);
	}
	
	public ActionForward doReturnBulletinView(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return new ActionForward("/BulletinView.do?act=doForward");
	}
	
	public ActionForward doGetFile(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		FileTool_Properties p = MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req);
		String hidFilePath = ((DynaActionForm)form).getString("hidFilePath");
		String webapps = p.getString("WebApp.webapps");
		
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(service.getAttachName(hidFilePath), "UTF-8") + "\"");
		OutputStream out = resp.getOutputStream();
		InputStream in = new FileInputStream(webapps + hidFilePath);
        byte[] b = new byte[2048];
        int len;
        while((len = in.read(b))>0){
        	out.write(b, 0, len);
        }
        in.close(); out.flush(); out.close();
		
		return null;
	}
	
	public ActionForward doEditBulletin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String hidCurBulID = ((DynaActionForm)form).getString("hidCurBulID");
		
		req.setAttribute("rCurBulID", hidCurBulID);
		return new ActionForward("/BulletinEdit.do?act=doForward");
	}
	
	public ActionForward doDeleteBulletin(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map pMap = MyStrutsUtil.getDbFieldMap(form, 3);
		pMap.put("@req", req);
		pMap.put("@p", MyStrutsUtil.getPropertyPlugin(CS.PROPERTY_NAME, req));
		service.doDeleteBulletin(pMap);
		
		return new ActionForward("/BulletinView.do?act=doForward");
	}
}
