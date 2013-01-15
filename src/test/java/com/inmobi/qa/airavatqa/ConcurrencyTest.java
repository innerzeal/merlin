/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import com.inmobi.qa.airavatqa.core.APIResult;
import com.inmobi.qa.airavatqa.core.Brother;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.testng.Assert;
import org.testng.TestNGException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author rishu.mehrotra
 */
/*

public class ConcurrencyTest {
    
     String VALIDATE_URL="http://10.14.110.46:8082/ivory-webapp-0.1-SNAPSHOT/api/entities/validate";
     String SUBMIT_URL="http://10.14.110.46:8082/ivory-webapp-0.1-SNAPSHOT/api/entities/submit";
     String GET_ENTITY_DEFINITION="http://10.14.110.46:8082/ivory-webapp-0.1-SNAPSHOT/api/entities/definition";
     String DELETE_URL="http://10.14.110.46:8082/ivory-webapp-0.1-SNAPSHOT/api/entities/delete";
     String FAIL_DELETE="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><result><status>FAILED</status><message>Entity: sample does not exists</message></result>";
    
    
    
    @Test(groups = { "0.1","0.2"})(groups={"concurrentGetOnSameProcessTest"},dataProvider="sameResource")
    public void concurrentGetOnSameProcessTest(String data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
            
            //lets submit some file
            
            if(Util.parseResponse(entityManagerHelper.submitEntity(SUBMIT_URL,data)).getStatus().equals(APIResult.Status.SUCCEEDED))
            {
               //save initial state
               ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
                
               Brother[] brothers=commonActions(GET_ENTITY_DEFINITION,"get",data);
               
               //now to compare that each member got the same output
               Set<String> outputs=new LinkedHashSet<String>();
               for(Brother brother:brothers)
               {
                   outputs.add(brother.getOutput());
               }
               
               Assert.assertEquals(outputs.size(),1,"Output size is not 1. Something is wrong");
               //put the content comparison once the comments thing is fixed.
               
               //get the final state of the stores
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //both should be same
               Collections.sort(archiveStoreFinalState);Collections.sort(archiveStoreInitialState);
               Assert.assertTrue(archiveStoreFinalState.equals(archiveStoreInitialState),"archive store state has changed for no reason!");
               Collections.sort(processStoreInitialState);Collections.sort(processStoreFinalState);
               Assert.assertTrue(processStoreFinalState.equals(processStoreInitialState),"process store state has changed for no reason!");
            }
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }
        finally {
            
            //make sure the entity is deleted
            try {entityManagerHelper.delete(DELETE_URL,data);}catch(Exception e){e.printStackTrace();}
        }
    }
    
    @Test(groups = { "0.1","0.2"})(groups={"concurrentDeleteOnSameProcessTest"},dataProvider="sameResource")
    public void concurrentDeleteOnSameProcessTest(String data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
              
        	
            //lets submit some file
            
            if(Util.parseResponse(entityManagerHelper.submitEntity(SUBMIT_URL,data)).getStatus().equals(APIResult.Status.SUCCEEDED))
            {
                
               ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
               
               Brother[] brothers=commonActions(DELETE_URL,"delete",data);
               
               //now to compare that each member got the same output
               HashMap<APIResult.Status,Integer> outputMap = new HashMap<APIResult.Status, Integer>();
               for(Brother brother:brothers)
               {
                   APIResult parsedOutput=Util.parseResponse(brother.getOutput());
                   
                   if(outputMap.containsKey(parsedOutput.getStatus()))
                   {
                       outputMap.put(parsedOutput.getStatus(),new Integer(outputMap.get(parsedOutput.getStatus())+1));
                   }
                   else
                   {
                       outputMap.put(parsedOutput.getStatus(),1);
                   }
                   
               }
               
               Assert.assertEquals(outputMap.size(),2,"Output size is not 2. Something is wrong");
               
               
               //now to parse each output
               Assert.assertEquals(outputMap.get(APIResult.Status.SUCCEEDED),new Integer(1),"There were more than 1 successful concurrent deletes!");
               Assert.assertEquals(outputMap.get(APIResult.Status.FAILED),new Integer(9),"There were more than 1 successful concurrent deletes!");
               
               //get the final state of the stores
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //make sure that the process store state changes by only 1 file
               processStoreInitialState.removeAll(processStoreFinalState);
               Assert.assertEquals(processStoreInitialState.size(),1,"there are more than 1 file added in process store!!!");
               Assert.assertEquals(processStoreInitialState.get(0),(Util.readEntityName(data)+".xml"),"the file that was added looks different");
               
               
               
               //make sure that archive store has only 1 file
               archiveStoreFinalState.removeAll(archiveStoreInitialState);
               Assert.assertEquals(archiveStoreFinalState.size(),1,"archive store had more than 1 files created!");
               System.out.println("**********"+archiveStoreFinalState.get(0));
               Assert.assertEquals(archiveStoreFinalState.get(0).split("\\.")[0],Util.readEntityName(data),"file present seems different than the one that was deleted!");
            }
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        }  
    }
    
    @Test(groups={"concurrentGetAndDeleteOnSameProcessTest"},dataProvider="sameResource")
    public void concurrentGetAndDeleteOnSameProcessTest(String data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
            if(Util.parseResponse(entityManagerHelper.submitEntity(SUBMIT_URL,data)).getStatus().equals(APIResult.Status.SUCCEEDED))
            {
                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
                ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
               
                //Brother[] brothers=commonActions(SUBMIT_URL,"submit",data);
                Brother[] brothers=commonActions(data,new String[]{"delete","get"});
               
               //now to compare that each member got the same output. This is a small problem since right now get on a non-existing resource does not return a APIResult object, but a string
               HashMap<APIResult.Status,Integer> outputMap = new HashMap<APIResult.Status, Integer>();
               
               for(Brother brother:brothers)
               {
                   APIResult parsedOutput=Util.parseResponse(brother.getOutput());
                   
                   if(outputMap.containsKey(parsedOutput.getStatus()))
                   {
                       outputMap.put(parsedOutput.getStatus(),new Integer(outputMap.get(parsedOutput.getStatus())+1));
                   }
                   else
                   {
                       outputMap.put(parsedOutput.getStatus(),1);
                   }
                   
               }
               
               //how it should be? there should only be 1 success. Rest all should fail.
               Assert.assertEquals(outputMap.get(APIResult.Status.SUCCEEDED),new Integer(1),"There were more than 1 successful concurrent delete/read!");
               Assert.assertEquals(outputMap.get(APIResult.Status.FAILED),new Integer(9),"There were more than 1 successful concurrent delete/read!");
               
               
               
               //checking for storage state
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //make sure that the process store state changes by only 1 file
               processStoreInitialState.removeAll(processStoreFinalState);
               Assert.assertEquals(processStoreInitialState.size(),1,"there are more than 1 file added in process store!!!");
               Assert.assertEquals(processStoreInitialState.get(0),(Util.readEntityName(data)+".xml"),"the file that was added looks different");
               
               //make sure that archive store has only 1 file
               archiveStoreFinalState.removeAll(archiveStoreInitialState);
               Assert.assertEquals(archiveStoreFinalState.size(),1,"archive store had more than 1 files created!");
               System.out.println("**********"+archiveStoreFinalState.get(0));
               Assert.assertEquals(archiveStoreFinalState.get(0).split("\\.")[0],Util.readEntityName(data),"file present seems different than the one that was deleted!");
               
               
            }
               
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        } 
        finally {
            
            //make sure the entity is deleted
            try {entityManagerHelper.delete(DELETE_URL,data);}catch(Exception e){e.printStackTrace();}
        }
    }
    
    
    @Test(groups={"concurrentSubmitOnSameProcessTest"},dataProvider="sameResource")
    public void concurrentSubmitOnSameProcessTest(String data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
                ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
               
                Brother[] brothers=commonActions(SUBMIT_URL,"submit",data);
               
               //now to compare that each member got the same output
               HashMap<APIResult.Status,Integer> outputMap = new HashMap<APIResult.Status, Integer>();
               for(Brother brother:brothers)
               {
                   APIResult parsedOutput=Util.parseResponse(brother.getOutput());
                   
                   if(outputMap.containsKey(parsedOutput.getStatus()))
                   {
                       outputMap.put(parsedOutput.getStatus(),new Integer(outputMap.get(parsedOutput.getStatus())+1));
                   }
                   else
                   {
                       outputMap.put(parsedOutput.getStatus(),1);
                   }
                   
               }
               
               Assert.assertEquals(outputMap.size(),2,"Output size is not 2. Something is wrong");
               
               
               //now to parse each output
               Assert.assertEquals(outputMap.get(APIResult.Status.SUCCEEDED),new Integer(1),"There were more than 1 successful concurrent submissions!");
               Assert.assertEquals(outputMap.get(APIResult.Status.FAILED),new Integer(9),"There were more than 1 successful concurrent submissions!");
               
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //see if only 1 file has been added
               Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
               Assert.assertEquals(processStoreFinalState, processStoreInitialState,"process store seems different!");
               
               //see if only 1 file has been archived and not more
               archiveStoreFinalState.removeAll(archiveStoreInitialState);
               Assert.assertEquals(archiveStoreFinalState.size(),1,"More than 1 files were archived!");
               Assert.assertTrue(archiveStoreFinalState.get(0).contains(Util.readEntityName(data)),"some other file was archived!");
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        } 
        finally {
            
            //make sure the entity is deleted
            try {entityManagerHelper.delete(DELETE_URL,data);}catch(Exception e){e.printStackTrace();}
        }
    }
    
    @Test(groups={"concurrentSubmitOnDifferentProcessTest"},dataProvider="differentResources")
    public void concurrentSubmitOnDifferentProcessTest(String [] data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
                ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
               
                //writing common actions here separately now. Will need to merge this later in a better way.
                
               ThreadGroup brothersGrimm=new ThreadGroup("submitGroup");
               
               //now just add 10 instances
               
               Brother[] brothers=new Brother[data.length];
               
               for(int i=1;i<=brothers.length;i++)
               {
                   brothers[i-1]=new Brother("brother"+i,"submit", ENTITY_TYPE.PROCESS,brothersGrimm,data[i-1],SUBMIT_URL);
               }
               
               for(Brother brother:brothers)
               {
                   brother.start();
                  
               }
               
               Thread.sleep(10000);
               
               //now to compare that each member got the same output
               HashMap<APIResult.Status,Integer> outputMap = new HashMap<APIResult.Status, Integer>();
               for(Brother brother:brothers)
               {
                   APIResult parsedOutput=Util.parseResponse(brother.getOutput());
                   
                   if(outputMap.containsKey(parsedOutput.getStatus()))
                   {
                       outputMap.put(parsedOutput.getStatus(),new Integer(outputMap.get(parsedOutput.getStatus())+1));
                   }
                   else
                   {
                       outputMap.put(parsedOutput.getStatus(),1);
                   }
                   
               }
               
               Assert.assertEquals(outputMap.size(),1,"Output size is not 1. Something is wrong");
               
               
               //now to parse each output
               Assert.assertEquals(outputMap.get(APIResult.Status.SUCCEEDED),new Integer(brothers.length),"There weren't 10 successful concurrent submissions!");
               Assert.assertEquals(outputMap.get(APIResult.Status.FAILED),null,"There were 0 successful concurrent submissions!");
               
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //see if the required number of files has been added
               Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
               processStoreFinalState.removeAll(processStoreInitialState);
               Assert.assertEquals(processStoreFinalState.size(),data.length,"Not all files were added/more files were added!!");
               
               for(String dataLine:data)
               {
                   Assert.assertTrue(processStoreFinalState.contains(Util.readEntityName(dataLine)+".xml"),"It seems the file "+Util.readEntityName(dataLine)+" was not submitted correctly!!!");
                   
               }
               
               //check to see if nothing has been archived.
               Collections.sort(archiveStoreFinalState);Collections.sort(archiveStoreInitialState);
               Assert.assertEquals(archiveStoreFinalState, archiveStoreInitialState,"Archive store looks different!");
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        } 
        finally {
            
            //make sure the entity is deleted
            for(String dataLine:data)
            { try {entityManagerHelper.delete(DELETE_URL,dataLine);}catch(Exception e){e.printStackTrace();} }
        }
    }
    
    @Test(groups={"concurrentDeleteOnDifferentProcessTest"},dataProvider="differentResources")
    public void concurrentDeleteOnDifferentProcessTest(String [] data) throws Exception
    {
        IEntityManagerHelper entityManagerHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);
        
        
        try {
            
                //first submit all data
                for(String dataLine:data)
                {
                    entityManagerHelper.submitEntity(SUBMIT_URL, dataLine);
                }
            
                ArrayList<String> processStoreInitialState=Util.getProcessStoreInfo();
                ArrayList<String> archiveStoreInitialState=Util.getArchiveStoreInfo();
               
                //writing common actions here separately now. Will need to merge this later in a better way.
                
               ThreadGroup brothersGrimm=new ThreadGroup("submitGroup");
               
               //now just add 10 instances
               
               Brother[] brothers=new Brother[data.length];
               
               for(int i=1;i<=brothers.length;i++)
               {
                   brothers[i-1]=new Brother("brother"+i,"delete", ENTITY_TYPE.PROCESS,brothersGrimm,data[i-1],DELETE_URL);
               }
               
               for(Brother brother:brothers)
               {
                   brother.start();
                  
               }
               
               Thread.sleep(10000);
               
               //now to compare that each member got the same output
               HashMap<APIResult.Status,Integer> outputMap = new HashMap<APIResult.Status, Integer>();
               for(Brother brother:brothers)
               {
                   APIResult parsedOutput=Util.parseResponse(brother.getOutput());
                   
                   if(outputMap.containsKey(parsedOutput.getStatus()))
                   {
                       outputMap.put(parsedOutput.getStatus(),new Integer(outputMap.get(parsedOutput.getStatus())+1));
                   }
                   else
                   {
                       outputMap.put(parsedOutput.getStatus(),1);
                   }
                   
               }
               
               Assert.assertEquals(outputMap.size(),1,"Output size is not 1. Something is wrong");
               
               
               //now to parse each output
               Assert.assertEquals(outputMap.get(APIResult.Status.SUCCEEDED),new Integer(brothers.length),"There weren't 10 successful concurrent deletions!");
               Assert.assertEquals(outputMap.get(APIResult.Status.FAILED),null,"There were some failures in successful concurrent deletions!");
               
               ArrayList<String> processStoreFinalState=Util.getProcessStoreInfo();
               ArrayList<String> archiveStoreFinalState=Util.getArchiveStoreInfo();
               
               //see if the required number of files has been added
               Collections.sort(processStoreFinalState);Collections.sort(processStoreInitialState);
               Assert.assertEquals(processStoreInitialState.size(),data.length,"Not all files were deleted/more files were deleted!!");
               
               
               
               //check to see if nothing has been archived.
               Collections.sort(archiveStoreFinalState);Collections.sort(archiveStoreInitialState);
               archiveStoreFinalState.removeAll(archiveStoreInitialState);
               Assert.assertEquals(archiveStoreFinalState.size(),data.length,"Not all files were deleted correctly! Archive differs!");
               
               for(String dataLine:data)
               {
                   boolean found=false;
                   for(String archiveEntry:archiveStoreFinalState)
                   {
                       if(archiveEntry.contains(Util.readEntityName(dataLine)))
                           
                       {
                           found=true;
                           break;
                       }
                   }
                   
                   Assert.assertTrue(found,"The file "+dataLine+" was not deleted correctly! Archive store differs!");
                   
                   
               }
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TestNGException(e.getMessage());
        } 
        finally {
            
            //make sure the entity is deleted
            for(String dataLine:data)
            { try {entityManagerHelper.delete(DELETE_URL,dataLine);}catch(Exception e){e.printStackTrace();} }
        }
    }
    
    
    private Brother[] commonActions(String url,String operation,String data) throws Exception
    {
               ThreadGroup brothersGrimm=new ThreadGroup("submitGroup");
               
               //now just add 10 instances
               
               Brother[] brothers=new Brother[10];
               
               for(int i=1;i<=brothers.length;i++)
               {
                   brothers[i-1]=new Brother("brother"+i,operation, ENTITY_TYPE.PROCESS,brothersGrimm,data,url);
               }
               
               for(Brother brother:brothers)
               {
                   brother.start();
                  
               }
               
               Thread.sleep(10000);
               
               return brothers;
    }
    
    private Brother[] commonActions(String data,String ... operation) throws Exception
    {
               ThreadGroup brothersGrimm=new ThreadGroup("mixGroup");
               
               //now just add 10 instances
               
               Brother[] brothers=new Brother[10];
               
               for(int i=1;i<=brothers.length;i++)
               {
                   if(i%2==0)
                   {
                        brothers[i-1]=new Brother("brother"+i,operation[i%2],ENTITY_TYPE.PROCESS,brothersGrimm,data,DELETE_URL);
                   }
                   else
                   {
                        brothers[i-1]=new Brother("brother"+i,operation[i%2],ENTITY_TYPE.PROCESS,brothersGrimm,data,GET_ENTITY_DEFINITION);
                   }
               }
               
               for(Brother brother:brothers)
               {
                   brother.start();
                  
               }
               
               Thread.sleep(10000);
               
               return brothers;
    }
    
    
    
    
    @DataProvider(name="sameResource")
    public Object[][] getDataFromSameResource(Method m) throws Exception
    {
        Object[][] testData=new Object[1][1];
        testData[0][0]=Util.fileToString(new File("src/test/resources/process/submit/valid/valid01.xml"));
        return testData;
    }
    
    @DataProvider(name="differentResources")
    public Object[][] getVariedDataFromSameResource(Method m) throws Exception
    {
        
        String data=Util.fileToString(new File("src/test/resources/process/submit/valid/valid01.xml"));
        
        Object[][] testData=new Object[1][1];
        String[] dataSet=new String[10];
        
        for(int i=0;i<10;i++)
        {
           dataSet[i]=Util.generateUniqueProcessEntity(data); 
        }
        
        testData[0][0]=dataSet;
        return testData;
    }
    
    
    
}
*/