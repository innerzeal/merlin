package com.inmobi.qa.airavatqa.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.testng.log4testng.Logger;

import com.inmobi.types.DemandSource;
import com.inmobi.types.adserving.AdRR;
import com.inmobi.types.postimpression.ClickRequestResponse;

public class hadoopUtil {

	static Logger logger=Logger.getLogger(Util.class);

	public static File getFileFromHDFSFolder(PrismHelper prismHelper,
			String hdfsLocation,String localLocation) throws Exception {

		System.setProperty("java.security.krb5.realm", "");
		System.setProperty("java.security.krb5.kdc", "");

		//	logger.info("getting file: "+ hdfsLocation);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		UserGroupInformation user = UserGroupInformation.createRemoteUser("hdfs");

		Path srcPath = Util.stringToPath(hdfsLocation);
		Path destPath = Util.stringToPath(localLocation+srcPath.getName());

		File f = new File(destPath.toString());
		if(f.exists())
			f.delete();

		fs.copyToLocalFile(srcPath,destPath);

		return new File(destPath.toString());

	}






	public static File getFileFromHDFSFolder(PrismHelper prismHelper,
			String hdfsLocation) throws Exception {
		System.setProperty("java.security.krb5.realm", "");
		System.setProperty("java.security.krb5.kdc", "");
		return getFileFromHDFSFolder(prismHelper, hdfsLocation,"dataFromHDFS/");
	}



	public static ArrayList<File> getAllFilesFromHDFSFolder(
			PrismHelper prismHelper, String hdfsLocation,String localBaseFolder) throws Exception{

		setSystemPropertyHDFS();

		ArrayList<File> returnList = new ArrayList<File>();

		logger.info("getting file from folder: "+ hdfsLocation);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(Util.stringToPath(hdfsLocation)); 


		for(FileStatus stat : stats)
		{
			String currentPath = stat.getPath().toUri().getPath(); // gives directory name
			if(!stat.isDir())
			{
				returnList.add(getFileFromHDFSFolder(prismHelper, currentPath,localBaseFolder));
			}


		}


		return returnList;

	}




	public static void setSystemPropertyHDFS() {
		System.setProperty("java.security.krb5.realm", "");
		System.setProperty("java.security.krb5.kdc", "");

	}



	public static ArrayList<String> getHDFSSubFolders(ColoHelper prismHelper,
			String baseDir) throws IOException {
		setSystemPropertyHDFS();

		ArrayList<String> returnList = new ArrayList<String>();

		logger.info("getting folder list from: "+ baseDir);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(Util.stringToPath(baseDir)); 


		for(FileStatus stat : stats)
		{
			if(stat.isDir())
				returnList.add(stat.getPath().toUri().getPath());

		}


		return returnList;


	}



	public static String defaultFileName ="/Users/samarth.gupta/testData/testFolder/zip/part-m-00000.gz.1299112731669475part-m-00000.gz";


	public static ArrayList<AdRR> putGZAdRRInHdfs(String fileName, int numberOfImpressions, int numberOfRequest,
			ColoHelper ivoryqa1, List<String> folderForHDFS) throws Exception{

		File file ;

		if(fileName.equals("default"))
			file=new File(ZipUtil.unzipFileToAnotherFile(defaultFileName,"unzippedRRThrift"));
		else 
			file=new File(ZipUtil.unzipFileToAnotherFile(fileName,"unzippedRRThrift"));


		ArrayList<AdRR> adrrList = new ArrayList<AdRR>();
		for(int requestCount=0; requestCount <numberOfRequest; requestCount++)
		{
			adrrList.add(new ThriftParser<AdRR>().parseThrift(file,numberOfImpressions,requestCount));
		}

		String outputFileName = "testData"+new Random().nextInt(100000);
		Util.WriteToFileAdRR(outputFileName,adrrList);

		ZipUtil.zipFile(outputFileName);

		Util.copyDataToFolders(ivoryqa1, "", folderForHDFS, outputFileName+".gz");

		File delete = new File(outputFileName+".gz");
		delete.delete();

		return adrrList;

	}        


	public static Configuration getHadoopConfiguration(ColoHelper prismHelper)throws Exception{
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");
		return conf;
	}



	public static ArrayList<Path> getAllFilesHDFSLocation(
			ColoHelper prismHelper, String hdfsLocation)throws Exception {
		setSystemPropertyHDFS();

		ArrayList<Path> returnList = new ArrayList<Path>();

		//logger.info("getting file from folder: "+ hdfsLocation);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(Util.stringToPath(hdfsLocation)); 


		for(FileStatus stat : stats)
		{
			String currentPath = stat.getPath().toUri().getPath(); // gives directory name
			if(!stat.isDir())
			{
				returnList.add(stat.getPath());
			}

		}

		return returnList;

	}

	public static ArrayList<Path> getAllFilesRecursivelyHDFS(
			ColoHelper colcoHelper, Path location)  throws Exception{

		setSystemPropertyHDFS();
		ArrayList<Path> returnList = new ArrayList<Path>();

		Configuration conf =hadoopUtil.getHadoopConfiguration(colcoHelper);

		final FileSystem fs=FileSystem.get(conf);

		if(location.toString().contains("*"))
			location = Util.stringToPath(location.toString().substring(0, location.toString().indexOf("*")-1));

		FileStatus[] stats = fs.listStatus(location); 

		for(FileStatus stat : stats)
		{
			//Util.print("crrentPath: " +stat.getPath().toUri().getPath()); // gives directory name
			if(!stat.isDir())
			{
				if(!stat.getPath().getName().contains("_SUCCESS"))
					returnList.add(stat.getPath());
			}
			else 
				returnList.addAll(getAllFilesRecursivelyHDFS(colcoHelper,stat.getPath()));


		}

		return returnList ;
	}

	public static ArrayList<Path> getAllFilesRecursivelyHDFS(
			ColoHelper coloHelper, Path location, String ...ignoreFolders) throws Exception{

		setSystemPropertyHDFS();
		ArrayList<Path> returnList = new ArrayList<Path>();

		Configuration conf =hadoopUtil.getHadoopConfiguration(coloHelper);

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(location); 

		//Util.print("getAllFilesRecursivelyHDFS: "+location);

		if(stats == null)
			return returnList;
		for(FileStatus stat : stats)
		{

			//Util.print("checking in DIR: "+stat.getPath());

			if(!stat.isDir())
			{
				if(!checkIfIsIgnored(stat.getPath().toUri().toString(),ignoreFolders)){
					//	Util.print("adding File: " +stat.getPath().toUri().getPath()); // gives file name

					returnList.add(stat.getPath());
				}
			}
			else {
				//	Util.print("recursing for DIR: " +stat.getPath().toUri().getPath()); // gives directory name

				returnList.addAll(getAllFilesRecursivelyHDFS(coloHelper,stat.getPath(),ignoreFolders));
			}
		}

		return returnList ;

	}






	private static boolean checkIfIsIgnored(String folder,
			String[] ignoreFolders) {

		for(int i = 0 ; i <ignoreFolders.length ; i++){

			if(folder.contains(ignoreFolders[i])){
				//	Util.print("ignored Folder found: "+ignoreFolders[i]);
				return true;
			}
		}
		return false;
	}

	public static void deleteFile(ColoHelper coloHelper, Path fileHDFSLocaltion) throws Exception{
		setSystemPropertyHDFS();
		Configuration conf =hadoopUtil.getHadoopConfiguration(coloHelper);

		final FileSystem fs=FileSystem.get(conf);

		fs.delete(fileHDFSLocaltion,false);
	}

	public static void copyDataToFolders(PrismHelper prismHelper,
			final String folderPrefix, final Path folder,
			final String... fileLocations) throws Exception {
		Configuration conf = new Configuration();
		conf.set("fs.default.name", "hdfs://"
				+ prismHelper.getProcessHelper().getHadoopURL());

		final FileSystem fs = FileSystem.get(conf);

		UserGroupInformation user = UserGroupInformation
		.createRemoteUser("hdfs");


		for(final String file : fileLocations)
		{
			user.doAs(new PrivilegedExceptionAction<Boolean>() {

				@Override
				public Boolean run() throws Exception {
					//	logger.info("copying  "+file+" to "+folderPrefix+folder);
					fs.copyFromLocalFile(new Path(file), new Path(
							folderPrefix + folder));
					return true;

				}
			});
		}


	}



	public static ArrayList<ClickRequestResponse> getAllnonNetworkRequestFromHDFS(
			ColoHelper coloHelper, Path p) throws Exception {

		File zippedFile = hadoopUtil.getFileFromHDFSFolder(coloHelper,p.toUri().toString());
		File unzippedFile = ZipUtil.unzipFileToAnotherFile(zippedFile);
		zippedFile.delete();

		ArrayList<ClickRequestResponse> crr = new ArrayList<ClickRequestResponse>();

		FileReader r = new FileReader(unzippedFile);
		BufferedReader b = new BufferedReader(r);
		String line = b.readLine();

		while(line!=null)
		{
			ClickRequestResponse crrDefault = new ClickRequestResponse();
			Base64 base64 = new Base64();
			TDeserializer ud= new TDeserializer();
			ud.deserialize((TBase) crrDefault,base64.decode(line.getBytes()));
			if(!crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.NETWORK))
				crr.add(crrDefault);

			line = b.readLine();
		}
		unzippedFile.delete();
		return crr;

	}






	public static ArrayList<ClickRequestResponse> getAllRequestForDst(ColoHelper coloHelper,
			Path fileLocation, DemandSource dst) throws Exception{
		File zippedFile = hadoopUtil.getFileFromHDFSFolder(coloHelper,fileLocation.toUri().toString(),"");

		File unzippedFile = ZipUtil.unzipFileToAnotherFile(zippedFile);

		ArrayList<ClickRequestResponse> crr = new ArrayList<ClickRequestResponse>();

		FileReader r = new FileReader(unzippedFile);
		BufferedReader b = new BufferedReader(r);
		String line = b.readLine();

		while(line!=null)
		{
			ClickRequestResponse crrDefault = new ClickRequestResponse();
			Base64 base64 = new Base64();
			TDeserializer ud= new TDeserializer();
			ud.deserialize((TBase) crrDefault,base64.decode(line.getBytes()));
			if(crrDefault.getRequest_response().getDemand_source_type().equals(dst))
				crr.add(crrDefault);

			line = b.readLine();
		}
		unzippedFile.delete();

		zippedFile.delete();

		//Util.print("file: "+fileLocation+" DST: "+dst+" numberOfRecords: "+crr.size());
		return crr;

	}






	public static Collection<AdRR> getAllRRFromHDFS(ColoHelper coloHelper,
			Path p) throws Exception{
		File zippedFile = hadoopUtil.getFileFromHDFSFolder(coloHelper,p.toUri().toString(),"");

		File unzippedFile = ZipUtil.unzipFileToAnotherFile(zippedFile);

		ArrayList<AdRR> returnObject = new ArrayList<AdRR>();

		FileReader r = new FileReader(unzippedFile);
		BufferedReader b = new BufferedReader(r);
		String line = b.readLine();

		while(line!=null)
		{
			AdRR rr = new AdRR();
			Base64 base64 = new Base64();
			TDeserializer ud= new TDeserializer();
			ud.deserialize((TBase) rr,base64.decode(line.getBytes()));
			returnObject.add(rr);
			line = b.readLine();
		}
		unzippedFile.delete();

		zippedFile.delete();

		//Util.print("file: "+fileLocation+" DST: "+dst+" numberOfRecords: "+crr.size());
		return returnObject;
	}






	public static DSTCounts getDSTCountsForFile(ColoHelper coloHelper, Path p) throws Exception{

		DSTCounts returnObject = new DSTCounts();

		File zippedFile = hadoopUtil.getFileFromHDFSFolder(coloHelper,p.toUri().toString(),"");

		File unzippedFile = ZipUtil.unzipFileToAnotherFile(zippedFile);


		FileReader r = new FileReader(unzippedFile);
		BufferedReader b = new BufferedReader(r);
		String line = b.readLine();

		while(line!=null)
		{
			ClickRequestResponse crrDefault = new ClickRequestResponse();
			Base64 base64 = new Base64();
			TDeserializer ud= new TDeserializer();
			ud.deserialize((TBase) crrDefault,base64.decode(line.getBytes()));
			if(crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.CHANNEL_PARTNERSHIP))
				returnObject.incrementChannelPartnership(p);
			else if(crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.HOSTED))
				returnObject.incrementHosted(p);
			else if(crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.HOUSE))
				returnObject.incrementHouse(p);
			else if(crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.IFD))
				returnObject.incrementIFD(p);
			else if(crrDefault.getRequest_response().getDemand_source_type().equals(DemandSource.NETWORK))
				returnObject.incrementNetwork(p);

			line = b.readLine();
		}
		unzippedFile.delete();

		zippedFile.delete();

		//Util.print("file: "+fileLocation+" DST: "+dst+" numberOfRecords: "+crr.size());
		return returnObject;
	}
	
	public static ArrayList<String> getHDFSSubFoldersName(ColoHelper prismHelper,
			String baseDir) throws IOException {
		setSystemPropertyHDFS();

		ArrayList<String> returnList = new ArrayList<String>();

		logger.info("getHDFSSubFoldersName: "+ baseDir);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(Util.stringToPath(baseDir)); 


		for(FileStatus stat : stats)
		{
			if(stat.isDir())
				
				returnList.add(stat.getPath().getName());

		}


		return returnList;


	}
	
	public static boolean isFilePresentHDFS(ColoHelper prismHelper,
			String hdfsPath, String fileToCheckFor) throws Exception {
		
		ArrayList<String> fileNames = getAllFileNamesFromHDFS(prismHelper,hdfsPath);
		
		for(String filePath : fileNames){
			
			if(filePath.contains(fileToCheckFor))
				return true;
		}
		
		return false;
	}
	
	private static ArrayList<String> getAllFileNamesFromHDFS(
			ColoHelper prismHelper, String hdfsPath) throws Exception {
		setSystemPropertyHDFS();

		ArrayList<String> returnList = new ArrayList<String>();

		logger.info("getting file from folder: "+ hdfsPath);
		Configuration conf=new Configuration();
		conf.set("fs.default.name","hdfs://"+prismHelper.getProcessHelper().getHadoopURL()+"");

		final FileSystem fs=FileSystem.get(conf);

		FileStatus[] stats = fs.listStatus(Util.stringToPath(hdfsPath)); 


		for(FileStatus stat : stats)
		{
			String currentPath = stat.getPath().toUri().getPath(); // gives directory name
			if(!stat.isDir())
			{
				returnList.add(currentPath);
			}


		}
		return returnList;

	}   
	
}
