///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.inmobi.qa.airavatqa.core;
//
//import com.inmobi.qa.airavatqa.core.Util.URLS;
//import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
//
///**
// *
// * @author rishu.mehrotra
// */
//public class FeedEntityHelperImpl implements IEntityManagerHelper {
//
//    public ServiceResponse delete(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse getEntityDefinition(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse getStatus(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//    
//    public ServiceResponse getStatus(URLS url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse resume(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse schedule(String url, String data) throws Exception {
//        
//        url+="/feed/"+Util.readDatasetName(data);
//	return Util.sendRequest(url);
//    }
//
//    public ServiceResponse submitAndSchedule(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//    
//    public ServiceResponse submitAndSchedule(URLS url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse submitEntity(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse suspend(String url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//    
//    public ServiceResponse suspend(URLS url, String data) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ServiceResponse validateEntity(String url, String data) throws Exception {
//        
//        return Util.sendRequest(url, data);
//    }
//
//    public void validateResponse(String response, APIResult.Status expectedResponse,String filename) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//	@Override
//	public ServiceResponse submitEntity(URLS url, String data) throws Exception {
//		// TODO Auto-generated method stub
//		 throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public ServiceResponse schedule(URLS scheduleUrl, String processData)
//			throws Exception {
//		return schedule(scheduleUrl.getValue(),processData);
//	}
//
//	@Override
//	public ServiceResponse delete(URLS deleteUrl, String data) throws Exception {
//		// TODO Auto-generated method stub
//		throw new UnsupportedOperationException("Not supported yet.");
//	}
//
//	@Override
//	public ServiceResponse resume(URLS url, String data) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ProcessInstancesResult getRunningInstance(
//			URLS processRuningInstance, String name) throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ProcessInstancesResult getProcessInstanceStatus(
//			String readEntityName, String params) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ProcessInstancesResult getProcessInstanceSuspend(
//			String readEntityName, String params) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//        
//        public String writeEntityToFile(String name) throws Exception
//        {
//            return null;
//        }
//
//}
