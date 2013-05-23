package com.inmobi.qa.falcon;

import com.inmobi.qa.ivory.interfaces.EntityHelperFactory;
import com.inmobi.qa.ivory.interfaces.IEntityManagerHelper;
import com.inmobi.qa.ivory.supportClasses.ENTITY_TYPE;



public class Demo {

	IEntityManagerHelper dataHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.DATA);
	IEntityManagerHelper processHelper=EntityHelperFactory.getEntityHelper(ENTITY_TYPE.PROCESS);

/*

	@Test(dataProvider="demo-DP") 
	public void demoBundle(Bundle bundle) throws Exception {

		try{
			bundle.submitAndScheduleBundle();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new TestNGException(e.getMessage());
		}

		finally
		{
			bundle.deleteBundle();
		}
	}
	
	@DataProvider(name="demo-DP")
	public static Object[][] getTestData(Method m) throws Exception
	{

		return Util.readDemoBundle();
	}*/
}
