/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa;

import com.inmobi.qa.airavatqa.core.APIResult;
import com.inmobi.qa.airavatqa.core.ENTITY_TYPE;
import com.inmobi.qa.airavatqa.core.EntityHelperFactory;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.interfaces.entity.IEntityManagerHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.testng.TestNGException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author rishu.mehrotra
 */

/*
public class EntityManagerValidateTest {
    
    
    final String VALIDATE_URL="http://10.14.110.46:8082/ivory-webapp-0.1/api/entities/validate";
    
    @Test(groups={"submitValidData"},dataProvider="validDP")
    public void validateValidData(String data,APIResult.Status expectedResult,String filename)
    {
        try {
            System.out.println("The testcase is being run for "+filename);

            commonAction(ENTITY_TYPE.DATA,filename, data, VALIDATE_URL, expectedResult);

        }
        catch(Exception e)
        {
            throw new TestNGException(e.getMessage());
            
        }
    }
    
    
    @Test(groups={"submitValidProcess"},dataProvider="validDP")
    public void validateValidProcess(String data,APIResult.Status expectedResult,String filename)
    {
        try {
            System.out.println("The testcase is being run for "+filename);
            
            commonAction(ENTITY_TYPE.PROCESS,filename, data, VALIDATE_URL, expectedResult);

        }
        catch(Exception e)
        {
            throw new TestNGException(e.getMessage());
            
        }
    }
    
    
    @Test(groups={"DataWrongURL"},dataProvider="validDP")
    public void validateValidDataRequestToWrongURL(String data,APIResult.Status expectedResult,String filename) throws Exception
    {
       try {
            System.out.println("The testcase is being run for "+filename);

            String url=VALIDATE_URL+"/process";
            
            commonAction(ENTITY_TYPE.PROCESS,filename, data, url, expectedResult);

        }
        catch(Exception e)
        {
            throw new TestNGException(e.getMessage());
            
        } 
    }
    
    @Test(groups={"ProcessWrongURL"},dataProvider="validDP")
    public void validateValidProcessRequestToWrongURL(String data,APIResult.Status expectedResult,String filename) throws Exception
    {
       try {
            System.out.println("The testcase is being run for "+filename);

            String url=VALIDATE_URL+"/feed";
            
            commonAction(ENTITY_TYPE.PROCESS,filename, data, url, expectedResult);

        }
        catch(Exception e)
        {
            throw new TestNGException(e.getMessage());
            
        } 
    }
    
    @Test(groups={"invalidProcess"},dataProvider="validDP")
    public void validateInvalidProcess(String data,APIResult.Status expectedResult,String filename)
    {
        try {
            System.out.println("The testcase is being run for "+filename);
            
            commonAction(ENTITY_TYPE.PROCESS,filename, data,VALIDATE_URL, expectedResult);

        }
        catch(Exception e)
        {
            throw new TestNGException(e.getMessage());
            
        }
    }
    
    
    private void commonAction(ENTITY_TYPE type,String filename,String data,String url,APIResult.Status expectedResult) throws Exception
    {
            IEntityManagerHelper helperEntity=EntityHelperFactory.getEntityHelper(type);

            String result=helperEntity.validateEntity(url, data);

            helperEntity.validateResponse(result, expectedResult,filename);
    }
    
    
    
    @DataProvider(name="validDP")
    public Object[][] getData(Method m) throws Exception
    {
        
    	String testDataFolderPath = Util.getTestDataFolderPath(m.getName());

		System.out.println("testDataFolderPath: "+testDataFolderPath);
		
		return Util.getDataFromFolder(testDataFolderPath,m.getName());

    }
    
    
    
       
}
*/