package com.inmobi.qa.airavatqa.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.testng.Assert;

import com.inmobi.dp.types.ImpressionData;
import com.inmobi.grid.util.SerDeUtil;
import com.inmobi.types.adserving.AdRR;
import com.inmobi.types.adserving.Impression;

public class hbaseUtil {


	public static String hbaseLocation="10.14.110.46";
	public static String hbasePort = "2181";

	
	public static boolean doesTableExistHBase(String hbaseLocation, String clientPort,String tableName) throws Exception{

		Configuration conf = setHBaseConfiguration(hbaseLocation,clientPort);
        HBaseAdmin admin = new HBaseAdmin(conf);

		return admin.tableExists(tableName);
	}


	public static Configuration getDefaultHBaseConfiguration(){
		return setHBaseConfiguration(hbaseLocation,hbasePort);
	}

	public static String getValueFromHBase(String tableName, String impId,
			String colFamily, String subCol) throws Exception {
		HTable table = new HTable(tableName);
		Get g = new Get(Bytes.toBytes(impId));
		Result r = table.get(g);
		byte [] value = r.getValue(Bytes.toBytes(colFamily),Bytes.toBytes(subCol));
		
		String valueStr = Bytes.toString(value);
		return valueStr;
		
	}
	

	public static Configuration setHBaseConfiguration(String hbaseLocation, String clientPort){
		Configuration conf = new Configuration();
		conf.set("hbase.zookeeper.quorum",hbaseLocation);
		conf.set("hbase.zookeeper.property.clientPort", clientPort);

		Configuration hBaseConfiguration = HBaseConfiguration.create(conf);

		return hBaseConfiguration;
	}



	public static void verifyHBase(ArrayList<AdRR> adrrList)throws Exception {
		
		for(AdRR adrr : adrrList)
		{
			Assert.assertTrue(verifyHBase(adrr));
		}
	}



	private static boolean verifyHBase(AdRR adrr) throws Exception {

		Util.print("verification for AdRR: "+adrr.toString());
		
		if(adrr.getImpressions()!=null){
			for(int i = 0 ; i < adrr.getImpressions().size();i++){
				Impression imp =adrr.getImpressions().get(i);
				Assert.assertTrue(hbaseUtil.doesTableExistHBase(hbaseLocation, hbasePort, localDCUtil.getHBaseTableName(imp)));
			
			HTable table = new HTable(getDefaultHBaseConfiguration(),localDCUtil.getHBaseTableName(imp));
			/*
			System.out.println("scanning full table:");
			ResultScanner scanner = table.getScanner(new Scan());
			for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
			  System.out.println(Bytes.toString(rr.getRow())); 

			}*/
			
			//imp id for imp is
			String impID = new UUID(imp.getId().getId_high(), imp.getId().getId_low()).toString();
			
			//get data from hbase
			Get g = new Get(Bytes.toBytes(impID));
			Result r = table.get(g);
			byte [] value = r.getValue(Bytes.toBytes("DEFAULT"),Bytes.toBytes("DEFAULT"));
			ImpressionData obj = new ImpressionData();
			SerDeUtil.DESERIALIZER.get().deserialize(obj, value);
			localDCUtil.compareImpressionAndImpressonData(adrr,i,obj);
			return true;
			}
			
		}
		else if(adrr.getImpressions()==null)
			return true;
		return false;
	}

	public  static void  droptable(String tableName) throws Exception{

		HBaseAdmin admin = new HBaseAdmin(hbaseUtil.getDefaultHBaseConfiguration());

		Util.print("Checking tableName: " + tableName);
		if (admin.tableExists(tableName)) {
			Util.print("Dropping tableName: " + tableName);
			admin.disableTable(tableName);
			admin.deleteTable(tableName);

		}
	}
	
	public static void dropAllTables() throws Exception{
		HBaseAdmin admin = new HBaseAdmin(hbaseUtil.getDefaultHBaseConfiguration());
	
		//get all tables 
		HTableDescriptor[] tables = admin.listTables();
	
		for (HTableDescriptor t : Arrays.asList(tables)) {
			System.out.println("Dropping tableName " + t.getNameAsString());
			
			admin.disableTable(t.getNameAsString());
			admin.deleteTable(t.getNameAsString());
			}

		Util.print("HBase is not clear for: "+admin.getConfiguration());
		
		
		
		
	}
	
	
}
