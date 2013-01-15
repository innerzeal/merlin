/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa.interfaces.process;

/**
 *
 * @author rishu.mehrotra
 */
public interface IProcessManagerHelper {
    
    public String getRunningInstances(String url,String name) throws Exception;
    public String getStatus(String url,String name) throws Exception;
    public String killProcessInstance(String url,String name) throws Exception;
    public String suspendProcessInstance(String url,String name) throws Exception;
    public String resumeProcessInstance(String url,String name) throws Exception;
    public String reRunInstance(String url,String name) throws Exception;
    
}
