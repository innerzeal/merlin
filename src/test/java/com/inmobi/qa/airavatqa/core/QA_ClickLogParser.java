package com.inmobi.qa.airavatqa.core;

import com.inmobi.dp.types.*;
import com.inmobi.grid.io.log.ColumnGroupedRow;
import com.inmobi.grid.io.log.InputFormats;
import com.inmobi.grid.io.meta.FieldMapper;
import com.inmobi.grid.util.SerDeUtil;
import com.inmobi.types.GUID;
import com.inmobi.types.Geo;
import com.inmobi.types.adserving.AdRR;
import com.inmobi.types.postimpression.ClickRequestResponse;
import com.inmobi.types.postimpression.RequestResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;

import javax.servlet.jsp.el.ELException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

/**
 * User:shashank sharma
 * Date: 13/06/2011
 */
@SuppressWarnings("unchecked")
public class QA_ClickLogParser  {


	@Getter DPRequestID clickID;
	@Getter DPClickInfo clickInfo;
	@Getter DPContext clickContext;
	@Getter DPClickHeader clickHeader;
	@Getter DPClickCookies clickCookies;
	@Getter DPAdMeta adInfo;
	@Getter DPClickMisc clickMisc;

	public  void setClickLog(ClickRequestResponse requestResponse)  throws Exception{


		clickID = getClickIdentifier(requestResponse.getRequest_response());
		clickInfo = getClickInfo(requestResponse.getRequest_response());
		clickContext = getClickContext(requestResponse.getRequest_response());
		clickHeader = getClickHeader(requestResponse.getRequest_response());
		clickCookies = getClickCookies(requestResponse.getRequest_response());
		adInfo = getClickAdInfo(requestResponse.getRequest_response());
		clickMisc = getClickMisc(requestResponse.getRequest_response());

		ColumnGroupedRow clickRow = FieldMapper.getRow(InputFormats.CLICK_LOG, "raw", clickID, clickInfo,
				clickContext, clickHeader, adInfo, clickCookies, clickMisc);
		//     rows.add(clickRow);


	}



	public static ClickRequestResponse textToClickRequestResponse(Text record) throws Exception
	{
		ClickRequestResponse requestResponse = new ClickRequestResponse();
		SerDeUtil.DESER_BINARY.get().deserialize(requestResponse,
				Base64.decodeBase64(record.toString().getBytes()));

		return requestResponse;
	}


	public static ArrayList<ClickRequestResponse> thriftToClickRequestResponse(File thriftLocaltion, int numberOfRecords) throws Exception  {
		ArrayList<ClickRequestResponse> requestResponse = new ArrayList<ClickRequestResponse>();
		FileReader r = new FileReader(thriftLocaltion);
		BufferedReader reader = new BufferedReader (r);
		String line = reader.readLine();

		int count = 0 ;

		while(line!=null)
		{
			if( (count == numberOfRecords) && (numberOfRecords!=-1))
				break;

			ClickRequestResponse crr = new ClickRequestResponse();
			Base64 base64 = new Base64();
			TDeserializer ud= new TDeserializer();
			ud.deserialize((TBase) crr,base64.decode(line.getBytes()));
			requestResponse.add(crr);

			count++;
			line = reader.readLine();

		}

		return requestResponse;

	}

	private DPClickMisc getClickMisc(RequestResponse requestResponse) {
		DPClickMisc misc = new DPClickMisc();
		misc.setBuckets(requestResponse.getBucket_ids());
		misc.setRemote_ip(requestResponse.getRequest().getRemote_ip());
		misc.setNormalized_carrier_ip(requestResponse.getNormalized_carrier_ip());
		misc.setRefferer_url(requestResponse.getRequest().getRefferer_url());
		return misc;
	}

	private DPAdMeta getClickAdInfo(RequestResponse requestResponse) {
		DPAdMeta adInfo = new DPAdMeta();
		if(requestResponse.getAd()!=null){
			adInfo.setAd_inc_id(requestResponse.getAd().getId().getAd());
			adInfo.setAdgrp_inc_id(requestResponse.getAd().getId().getGroup());
			adInfo.setCmpgn_inc_id(requestResponse.getAd().getId().getCampaign());
			adInfo.setPricing(requestResponse.getAd().getMeta().getPricing());
			adInfo.setAd_format(requestResponse.getAd().getMeta().getAd_format());
			adInfo.setAdgrp_guid(requestResponse.getAd().getId().getAdgroup_guid());
			adInfo.setCampaign_guid(requestResponse.getAd().getId().getCampaign_guid());
			adInfo.setAdv_guid(requestResponse.getAd().getId().getAdvertiser_guid());
		}
		return adInfo;
	}

	private static  DPRequestID getClickIdentifier(RequestResponse requestResponse)
	throws ELException, ParseException {
		DPRequestID clickID = new DPRequestID();
		clickID.setRequestId(requestResponse.getRequest().getRequest_id());
		clickID.setHost(requestResponse.getHost_name());
		return clickID;
	}

	private DPClickInfo getClickInfo(RequestResponse requestResponse) throws ELException {
		DPClickInfo clickInfo = new DPClickInfo();
		clickInfo.setClickTime(requestResponse.getReq_ts());
		GUID impression_id = requestResponse.getRequest().getImpression_id();
		//null in case of fraud?
				if(impression_id!=null){
					clickInfo.setImpression_id(new UUID(impression_id.getId_high(), impression_id.getId_low()).toString());
				}
		clickInfo.setTerminated(requestResponse.isIs_terminated());
		clickInfo.setFraud(requestResponse.isIs_fraudulent());

		if (requestResponse.isIs_terminated()) {
			clickInfo.setTermination_reason(requestResponse.getTermination_reason());
		}

		if (requestResponse.isIs_fraudulent()) {
			clickInfo.setFraud_reasons(requestResponse.getFraud_reasons());
		}
		return clickInfo;
	}

	private DPContext getClickContext(RequestResponse requestResponse) throws ELException {
		DPContext requestContext = new DPContext();
		Geo location = requestResponse.getRequest().getGeo();

		requestContext.setHandset(requestResponse.getRequest().getHandset().getId());
		requestContext.setLocation(location);
		requestContext.unsetLoc_src();

		//null in case of fraud?
		if(requestResponse.getRequest().getRequest_params()!=null){
			String siteId = requestResponse.getRequest().getRequest_params().get("rq-site-inc-id");
			try {
				requestContext.setSite_inc_id(Integer.parseInt(siteId));
			} catch (Exception ignore) { }
		}
		requestContext.setCustom_slot_id(-1);
		return requestContext;
	}

	private DPClickHeader getClickHeader(RequestResponse requestResponse) throws ELException {
		DPClickHeader clickheader = new DPClickHeader();
		clickheader.setRq_params(requestResponse.getRequest().getRequest_params());
		clickheader.setRq_headers(requestResponse.getRequest().getRequest_headers());
		return clickheader;
	}

	private DPClickCookies getClickCookies(RequestResponse requestResponse) throws ELException {
		DPClickCookies cookies = new DPClickCookies();
		cookies.setCookies(requestResponse.getRequest().getCookies());
		return cookies;
	}



	public static ArrayList<ClickRequestResponse> getClickRequestResponseFromInput(
			ColoHelper ivoryqa1, String inputHDFSLocation,int numberOfRecords) throws Exception {

		ArrayList<Path> inputFilePaths = hadoopUtil.getAllFilesHDFSLocation(ivoryqa1, inputHDFSLocation);


		for(Path p : inputFilePaths)
		{
			File f = hadoopUtil.getFileFromHDFSFolder(ivoryqa1, p.toString());

			//unzip 
			File unzipped = new File(ZipUtil.unzipFileToAnotherFile(f.getAbsolutePath(),"unzippedClick"));

			//delete .gz file
			f.delete();

			ArrayList<ClickRequestResponse> returnObject = QA_ClickLogParser.thriftToClickRequestResponse(unzipped, numberOfRecords);

			//delete unzipped
			unzipped.delete();

			return returnObject;


		}

		ArrayList<File> getInputFilesList = hadoopUtil.getAllFilesFromHDFSFolder(ivoryqa1, inputHDFSLocation, "clickData/");
		return null;

	}

	public static int getNumberOfCRRInHDFSFile(ColoHelper coloHelper,Path path) throws Exception{
		File f = hadoopUtil.getFileFromHDFSFolder(coloHelper,path.toUri().toString(),"");
		FileReader r = new FileReader(f);
		BufferedReader reader = new BufferedReader (r);
		String line = reader.readLine();

		int count = 0 ;

		while(line!=null)
		{	
			count++;
			line = reader.readLine();
		}

		f.delete();
		return count;
	}

}
