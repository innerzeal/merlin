package com.inmobi.qa.airavatqa.core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.jsp.el.ELException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;

import lombok.Getter;
import lombok.Setter;

import com.inmobi.dp.types.DPAdMeta;
import com.inmobi.dp.types.DPAdSelection;
import com.inmobi.dp.types.DPContext;
import com.inmobi.dp.types.DPHandsetExtras;
import com.inmobi.dp.types.DPImpression;
import com.inmobi.dp.types.DPImpressionCounters;
import com.inmobi.dp.types.DPRequestCounters;
import com.inmobi.dp.types.DPRequestHeader;
import com.inmobi.dp.types.DPRequestID;
import com.inmobi.dp.types.DPRequestInfo;
import com.inmobi.dp.types.DPRequestMisc;
import com.inmobi.dp.types.DPRequestSource;
import com.inmobi.dp.types.DPUser;
import com.inmobi.grid.io.log.ColumnGroupedRow;
import com.inmobi.grid.io.log.InputFormats;
import com.inmobi.grid.io.meta.FieldMapper;
import com.inmobi.grid.util.SerDeUtil;
import com.inmobi.types.Geo;
import com.inmobi.types.Site;
import com.inmobi.types.adserving.AdRR;
import com.inmobi.types.adserving.Impression;
import com.inmobi.types.adserving.Request;

public class QA_RRLogLineParser {

	@Getter @Setter AdRR requestPlusImpression = new AdRR();
	List<ColumnGroupedRow> rows = new ArrayList<ColumnGroupedRow>();


	
	@Getter DPRequestID requestID ;
	@Getter DPRequestInfo requestInfo ;
	@Getter DPContext requestContext;
	@Getter DPUser user;
	@Getter DPHandsetExtras handsetExtras;
	@Getter DPRequestHeader headers;
	@Getter DPRequestCounters requestCounters;
	@Getter DPRequestSource source ;
	@Getter DPRequestMisc misc;
	@Getter ArrayList<DPImpression> dpImpression = new ArrayList<DPImpression>();
	@Getter ArrayList<DPAdMeta> admeta =  new ArrayList<DPAdMeta>();
	@Getter ArrayList<DPAdSelection> adSelection = new ArrayList<DPAdSelection>();
	@Getter ArrayList<DPImpressionCounters> adCounter = new ArrayList<DPImpressionCounters>();
	
	
	

	private Set<String> uniqueImpressions = new HashSet<String>();


	public QA_RRLogLineParser(AdRR requestResponse) throws IOException {

		try {
			
			requestID = getRequestID(requestResponse);
			requestInfo = getRequestInfo(requestResponse);
			requestContext = getRequestContext(requestResponse);
			user = getRequestUser(requestResponse);
			handsetExtras = getHandsetExtras(requestResponse);
			headers = getRequestHeaders(requestResponse);
			requestCounters = getRequestCounters(requestResponse);
			source = getRequestSource(requestResponse);
            misc = getRequestOtherInfo(requestResponse);

			if (!requestResponse.isIs_terminated() && 
			        requestResponse.getImpressions() != null && !requestResponse.getImpressions().isEmpty()) {
				uniqueImpressions.clear();
                boolean isFirst = true;
				for (Impression impression : requestResponse.getImpressions()) {
					DPImpression dpImpressionTemp = getImpressionFor(impression, requestResponse);
					dpImpressionTemp.setUnique(!uniqueImpressions.contains(dpImpressionTemp.getId()));
					uniqueImpressions.add(dpImpressionTemp.getId());
					
					dpImpression.add(dpImpressionTemp);
					
					admeta.add(getAdMetaFor(impression));
					adSelection.add(getAdSelectionFor(impression, requestResponse));
					adCounter.add(getAdCountersFor(isFirst));
					
                    isFirst = false;
				}
			}

		} catch (Exception e) {
            System.out.println(requestResponse);
            e.printStackTrace();
		}
	}

	
	public ColumnGroupedRow getRCRequest() throws Exception{
		
		DPRequestID requestID = getRequestID(requestPlusImpression);
		DPRequestInfo requestInfo = getRequestInfo(requestPlusImpression);
		DPContext requestContext = getRequestContext(requestPlusImpression);
		DPUser user = getRequestUser(requestPlusImpression);
		DPHandsetExtras handsetExtras = getHandsetExtras(requestPlusImpression);
		DPRequestHeader headers = getRequestHeaders(requestPlusImpression);
		DPRequestCounters requestCounters = getRequestCounters(requestPlusImpression);
		DPRequestSource source = getRequestSource(requestPlusImpression);
        DPRequestMisc misc = getRequestOtherInfo(requestPlusImpression);

		ColumnGroupedRow requestRow = FieldMapper.getRow(InputFormats.RR_LOG, "raw", requestID,
				requestInfo, requestContext, user, handsetExtras, headers, requestCounters, source, misc);
	
		return requestRow;
	}
	
public ArrayList<ColumnGroupedRow> getRCImpression() throws Exception{
	
	ArrayList<ColumnGroupedRow> rcImpressionList = null;
	
	if (!requestPlusImpression.isIs_terminated() && !requestPlusImpression.getImpressions().isEmpty()) {
		uniqueImpressions.clear();
        boolean isFirst = true;
        rcImpressionList = new ArrayList<ColumnGroupedRow>();
		for (Impression impression : requestPlusImpression.getImpressions()) {
			DPImpression dpImpression = getImpressionFor(impression, requestPlusImpression);
			dpImpression.setUnique(!uniqueImpressions.contains(dpImpression.getId()));
			DPRequestID requestID = getRequestID(requestPlusImpression);
			uniqueImpressions.add(dpImpression.getId());
			DPAdMeta admeta = getAdMetaFor(impression);
			DPContext requestContext = getRequestContext(requestPlusImpression);
			DPUser user = getRequestUser(requestPlusImpression);
			DPHandsetExtras handsetExtras = getHandsetExtras(requestPlusImpression);
			DPAdSelection adSelection = getAdSelectionFor(impression, requestPlusImpression);
			DPRequestSource source = getRequestSource(requestPlusImpression);
			DPImpressionCounters adCounter = getAdCountersFor(isFirst);
			ColumnGroupedRow adRow = FieldMapper.getRow(InputFormats.AD_LOG, "raw", requestID,
					requestContext, user, handsetExtras, dpImpression, admeta, adCounter, adSelection, source);
			
			rcImpressionList.add(adRow);
		}
		}
	return rcImpressionList;
	}
	
	public Request getRequest()
	{
		return requestPlusImpression.getRequest();
	}
	
	 public static DPRequestMisc getRequestOtherInfo(AdRR requestResponse) {
	        DPRequestMisc misc = new DPRequestMisc();
	        misc.setRq_page_url(requestResponse.getRequest().getRq_page_url());
	        misc.setRq_carrier_ip(requestResponse.getRequest().getMk_carrier());
	        misc.setNormailzed_carrier_ip(requestResponse.getNormalized_carrier());
	        misc.setRq_lat_lon(requestResponse.getRequest().getLat_lon());
	        misc.setRq_response_format(requestResponse.getRequest().getRq_response_format());
	        misc.setBuckets(requestResponse.getBucket_ids());
	        return misc;
	    }

	    static DPHandsetExtras getHandsetExtras(AdRR requestResponse) {
	        DPHandsetExtras handset = new DPHandsetExtras();

	        if(requestResponse.getRequest().getOrig_handset() != null) {
	            if (requestResponse.getRequest().getOrig_handset().isSetUser_agent()) {
	                handset.setOriginal_user_agent(requestResponse.getRequest().getOrig_handset().getUser_agent());
	            }
	            if (requestResponse.getRequest().getOrig_handset().isSetId()) {
	                handset.setOriginal_handset(requestResponse.getRequest().getOrig_handset().getId());
	            }
	            if (requestResponse.getRequest().getHandset().isSetUser_agent()) {
	                handset.setUser_agent(requestResponse.getRequest().getHandset().getUser_agent());
	            }
	        }
	        return handset;
	    }

	    public static DPRequestID getRequestID(AdRR requestResponse) throws ELException, ParseException {
			DPRequestID requestID = new DPRequestID();
			UUID uuid = new UUID(requestResponse.getRequest().getId().getId_high(),requestResponse.getRequest().getId().getId_low());
			requestID.setRequestId(uuid.toString());
			requestID.setHost(requestResponse.getHost());
			return requestID;
		}

		public static DPRequestInfo getRequestInfo(AdRR requestResponse) throws ELException {
			DPRequestInfo requestInfo = new DPRequestInfo();
	        requestInfo.setEpoch_ms(requestResponse.getUnix_ts());
	        requestInfo.setFill(requestResponse.getRequest().getN_ads_served() > 0);
	        requestInfo.setTerminated(requestResponse.isIs_terminated());
	        if (requestResponse.isIs_terminated()) {
	            requestInfo.setTermination_reason(requestResponse.getTermination_reason());
	        }
			return requestInfo;
		}

		static DPContext getRequestContext(AdRR requestResponse) throws ELException {
			DPContext requestContext = new DPContext();
			Geo location = requestResponse.getRequest().getIP();

			requestContext.setHandset(requestResponse.getRequest().getHandset().getId());
			requestContext.setLocation(location);
	        requestContext.setLoc_src(requestResponse.getRequest().getLoc_src());

	        requestContext.setSite_guid(requestResponse.getRequest().getRq_site_id());
	        if (requestResponse.getRequest().getInventory() != null) {
	            requestContext.setInventory_type(requestResponse.getRequest().getInventory());
	        }
	        if (requestResponse.getRequest().isSetCustom_slot_id()) {
	            requestContext.setCustom_slot_id(requestResponse.getRequest().getCustom_slot_id());
	        }
	        Site detected_site = requestResponse.getRequest().getDetected_site();
	        if (detected_site != null) {
	            requestContext.setSite_inc_id(detected_site.getSite_inc_id());
	        }
			String reftag = requestResponse.getRequest().getReftag();
	        if (!StringUtils.isEmpty(reftag)) {
	            requestContext.setReftag(reftag);
	        }

			Short rq_mk_ad_slot = requestResponse.getRequest().getSlot_requested();
	        if (rq_mk_ad_slot != null) {
	            requestContext.setRequested_slot_size(rq_mk_ad_slot);
	        }

			String request_ad_type = requestResponse.getRequest().getRq_ad_interaction_type();
	        if (request_ad_type != null) {
	            requestContext.setRq_ad_interaction_type(request_ad_type);
	        }

			return requestContext;
		}

		public static DPRequestSource getRequestSource(AdRR requestResponse) throws ELException {
			DPRequestSource requestSource = new DPRequestSource();
			requestSource.setIntegration_method(requestResponse.getRequest().getRequest_source().getIntegration_method());
			requestSource.setVersion(requestResponse.getRequest().getRequest_source().getVersion());
			requestSource.setReq_origin(requestResponse.getRequest().getRequest_source().getReq_origin());
			requestSource.setIntegration_family(requestResponse.getRequest().getRequest_source().getIntegration_family());
			requestSource.setIs_direct_integration(requestResponse.getRequest().getRequest_source().isIs_direct_integration());
	        if (!requestSource.isIs_direct_integration()) {
	            requestSource.setThird_party_name(requestResponse.getRequest().getRequest_source().getThird_party_name());
	        }
			return requestSource;
		}

		public static DPUser getRequestUser(AdRR requestResponse) throws ELException {
			DPUser user = new DPUser();

	        if (requestResponse.isSetSelected_user_id()) {
			    user.setSelected_user_id(requestResponse.getSelected_user_id());
	        }

	        if (requestResponse.isSetNormalized_user_id()) {
			    user.setNormalized_user_id(requestResponse.getNormalized_user_id());
	        }

	        if (requestResponse.getRequest().getUser().isSetAge()) {
	            user.setAge(requestResponse.getRequest().getUser().getAge());
	        }

	        if (requestResponse.getRequest().getUser().isSetGender()) {
	            user.setGender(requestResponse.getRequest().getUser().getGender());
	        }

	        user.setOpted_out(requestResponse.getRequest().getUser().isHas_opted_out());
	        user.setUser_ids(requestResponse.getRequest().getUser().getUids());
	        return user;
		}

		public static DPRequestHeader getRequestHeaders(AdRR requestResponse) throws ELException {
			DPRequestHeader headers = new DPRequestHeader();
			headers.setUparams(requestResponse.getRequest().getU_params());
			headers.setXheaders(requestResponse.getRequest().getX_headers());
			return headers;
		}

		public static DPRequestCounters getRequestCounters(AdRR requestResponse) throws ELException {
			DPRequestCounters requestCounters = new DPRequestCounters();
	        requestCounters.setAds_served(requestResponse.getN_ads_served());
	        requestCounters.setTotal_ad_req(requestResponse.getRequest().getN_ads_requested());
	        requestCounters.setValid_ad_req(requestResponse.getN_ads_resolved());
			return requestCounters;
		}


		public static DPImpression getImpressionFor(Impression impIn, AdRR requestResponse) throws ELException {
			DPImpression impression = new DPImpression();
			UUID id = new UUID(impIn.getId().getId_high(), impIn.getId().getId_low());
			impression.setId(id.toString());
	        impression.setServed_slot_size(requestResponse.getSlot_served());
	        impression.setSelection_bid(impIn.getSelection_bid());
			return impression;
		}

		public static DPAdMeta getAdMetaFor(Impression impression) throws ELException {
			DPAdMeta admeta = new DPAdMeta();
	        admeta.setAd_inc_id(impression.getAd().getId().getAd());
			admeta.setAd_format(impression.getAd().getMeta().getAd_format());
	        admeta.setAdgrp_inc_id(impression.getAd().getId().getGroup());
	        admeta.setCmpgn_inc_id(impression.getAd().getId().getCampaign());
	        admeta.setAdv_guid(impression.getAd().getId().getAdvertiser_guid());
	        admeta.setPricing(impression.getAd().getMeta().getPricing());
			return admeta;
		}

		public static DPAdSelection getAdSelectionFor(Impression impression, AdRR requestResponse) throws ELException {
			DPAdSelection adSelection = new DPAdSelection();
	        adSelection.setBuckets(requestResponse.getBucket_ids());
	        adSelection.setRanks(impression.getRanks());
			return adSelection;
		}

		public static DPImpressionCounters getAdCountersFor(boolean isFirst) {
			DPImpressionCounters adCounter = new DPImpressionCounters();
			adCounter.setPgimp((isFirst ? (short) 1 : 0));
			adCounter.setAdimp((short) 1);
			return adCounter;
		}
}
