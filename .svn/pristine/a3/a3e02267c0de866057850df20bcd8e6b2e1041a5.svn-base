package com.telappliant.tvoip.asterisk;

import static com.telappliant.util.PropertyLoader.getPropertyValue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.DelayQueue;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.telappliant.callinfo.process.VoipOfficeCallInfoCaptureProcess;
import com.telappliant.domain.Vocal;

/**
 * @author arif.mohammed
 *
 */
public class VoipService implements Asterisk.Listener{

	private static  Logger log = Logger.getLogger(VoipService.class.getName());

	private ConcurrentSkipListMap<String , Vocal> infoMap = new ConcurrentSkipListMap<String , Vocal>();
	private String authToken;
	
	/* (non-Javadoc)
	 * @see com.telappliant.tvoip.asterisk.Asterisk.Listener#handleResponse(com.telappliant.tvoip.asterisk.Asterisk, com.telappliant.tvoip.asterisk.Event)
	 * 
	 * This method triggers when the response event is received.
	 * isAllCallInfoCaptured will check if all the required events are captured 
	 * before making a call to API to update the call information
	 * xcode (caUid) is captured from the response.
	 */
	public void handleResponse(
			Asterisk	asterisk,
			Event		response)
					throws IOException
	{
		log.info("handleResponse: " + response);
		String channel = response.get("actionid");
		Vocal infoResponse = channel != null ? infoMap.get(channel) : null;
		if(infoResponse != null){
			infoResponse.setCaUid(response.get("value"));
			if(isAllCallInfoCaptured(infoResponse)){
				if(infoResponse.isReadyForSetup()) {
					String vocalJson = makeApiCallToUpdateVocal(infoResponse);
					if(vocalJson != null && !vocalJson.isEmpty()){
						JSONObject json = new JSONObject(vocalJson);
						if(json != null){
							int idTobeDeleted = json.get("id") != null ? Integer.valueOf(json.get("id").toString()) : 0;
							infoResponse.setId(idTobeDeleted);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * @param channel
	 * @return Vocal
	 * Initial setting for Vocal object, which will be used in the future events
	 * @throws InterruptedException 
	 * 
	 * This method will be called initially during the handleEvent method execution. 
	 * Vocal object will be initialised and infoMap will be populated with the channel and the Vocal object.
	 * The delete queue is also added during this period because if the "HangUp" event is missed due to some reason, 
	 * then these events will be deleted based on the delay time. (DelayedProcessForDelete(infoNewChannel, 1000*60*60*4) - delays time to 4 hours)
	 */
	public Vocal getOrNew(String channel) throws InterruptedException {
		Vocal infoNewChannel = new Vocal();
		infoNewChannel.setChannel1(channel);
		
		Vocal t = infoMap.putIfAbsent(channel, infoNewChannel);
		infoNewChannel = t==null?infoNewChannel:t;
		
		if (infoNewChannel.getDelayDelete() == null) {
			infoNewChannel.setDelayDelete(new DelayedProcessForDelete(infoNewChannel, 1000*60*60*4 )); //4 hours
			delayQ.put(infoNewChannel.getDelayDelete());
		}
		return infoNewChannel;
	}

	/* (non-Javadoc)
	 * @see com.telappliant.tvoip.asterisk.Asterisk.Listener#handleEvent(com.telappliant.tvoip.asterisk.Asterisk, com.telappliant.tvoip.asterisk.Event)
	 * This method triggers when all the events other than "Response" event is triggered. Event like
	 * "Newchannel","Bridge", "Hangup" events is checked to capture information.
	 * isAllCallInfoCaptured will check if all the required events are captured 
	 * before making a call to API to update the call information
	 * During Hangup, the callinfo object is stored in the Queue thats needs to be deleted in later point of time. 
	 * If the Hangup event is missed due to any reasons, the callinfo object is added during the initial start of the call and will be deleted after some 4 hours.
	 */
	public void handleEvent(
			Asterisk	asterisk,
			Event		event)
					throws IOException
	{
		log.info("handle Event: " + event);
		String	channel = event.get("channel") == null?event.get("channel1"):event.get("channel");
		if(channel == null) return;
		Vocal infoNewChannel=null;
		try {
			infoNewChannel = getOrNew(channel);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (event.equalsType("Newchannel"))
		{
			infoNewChannel.setCreated(new Date());
			infoNewChannel.setCallerIdNum(event.get("calleridnum"));
			/** The result from sendCmd, executed below will return the "Response" event (handleResponse method). This result will return the "caUid" (xcode)
			 which is captured in the method handleResponse and set in the Vocal object attribute "caUid"*/
			asterisk.getManager().sendCmd("GetVar", "ActionId", channel, "Channel", channel, "Variable", "SIP_HEADER(X-AccountCode)");
		}
		else if (event.equalsType("Bridge"))
		{
			log.info("Bridge: " + event);
			String channel1 = event.get("Channel1");
			String channel2 = event.get("Channel2");

			infoNewChannel.setBridged(1);
			infoNewChannel.setChannel1(channel1);
			infoNewChannel.setChannel2(channel2);
		}
		else if (event.equalsType("Hangup"))
		{
			log.info("Hangup: " + event);
			if(infoNewChannel.isReadyForDeleteApiCall()) {
				try {
					delayQ.remove(infoNewChannel.getDelayDelete());
					infoNewChannel.setDelayDelete(new DelayedProcessForDelete(infoNewChannel, 2000));
					delayQ.put(infoNewChannel.getDelayDelete());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if(channel != null){
			if(infoNewChannel != null){
				if(isAllCallInfoCaptured(infoNewChannel)){
					if(infoNewChannel.isReadyForSetup()) {
						String vocalJson = makeApiCallToUpdateVocal(infoNewChannel);
						if(vocalJson != null && !vocalJson.isEmpty()){
							JSONObject json = new JSONObject(vocalJson);
							if(json != null){
								int idTobeDeleted = json.get("id") != null ? Integer.valueOf(json.get("id").toString()) : 0;
								infoNewChannel.setId(idTobeDeleted);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param callInfo
	 * @return boolean
	 * 
	 * This method will check if all the required events attributes are captured before persisting the data (making API call to CardAssureAPI)
	 */
	public boolean isAllCallInfoCaptured(Vocal callInfo){
		//log.info("infoBridge.getChannel1() : "+ callInfo.getChannel1());
		//log.info("infoBridge.getChannel2() : "+ callInfo.getChannel2());
		//log.info("infoBridge.getCaUid() : "+ callInfo.getCaUid());
		//log.info("infoBridge.getCallerIdNum() : "+ callInfo.getCallerIdNum());
		String channel1 = callInfo.getChannel1();
		String channel2 = callInfo.getChannel2();
		String xCode = callInfo.getCaUid();
		String callerIdNum = callInfo.getCallerIdNum();

		if(channel1 != null && !channel1.isEmpty() && channel2 != null && !channel2.isEmpty() && 
				xCode != null && !xCode.isEmpty() && callerIdNum != null && !callerIdNum.isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * @param vocal
	 * @return String
	 * @throws IOException
	 * 
	 * This method makes a call to API to update the call information
	 */
	public String makeApiCallToUpdateVocal(Vocal vocal) throws IOException{
		String returnAfterCreate = null;
		VoipOfficeCallInfoCaptureProcess inApi = new VoipOfficeCallInfoCaptureProcess();
		String jsonInString = new ObjectMapper().writeValueAsString(vocal);
		log.info("jsonInString : "+ jsonInString);
		if(jsonInString != null){
			//log.info("Updating API, in progress............");
			String url = new StringBuilder(getPropertyValue("host-url")).append(getPropertyValue("web-context")).append(getPropertyValue("request-vocal")).toString();
			returnAfterCreate=inApi.makeApiCallToUpdateVocal(authToken, url, jsonInString);
			log.info("Updating API, is now complete");
		}
		return isValid(returnAfterCreate) ? returnAfterCreate : null ;
	}
	
	/**
	 * @param vocal
	 * @return
	 * @throws IOException
	 * 
	 * This method makes a call to API to delete the stored call information
	 */
	public String makeApiCallToDeleteVocal(Vocal vocal) {
		String returnAfterDelete=null;
		try {
			returnAfterDelete = null;
			VoipOfficeCallInfoCaptureProcess inApi = new VoipOfficeCallInfoCaptureProcess();
			String jsonInString = new ObjectMapper().writeValueAsString(vocal);
			log.info("jsonInString : "+ jsonInString);
			if(jsonInString != null){
				//log.info("Deleting API, in progress............");
				String url = new StringBuilder(getPropertyValue("host-url")).append(getPropertyValue("web-context")).append(getPropertyValue("request-vocal")).append("/"+vocal.getId()).toString();
				returnAfterDelete=inApi.makeApiCallToDeleteVocal(authToken, url);
				log.info("Deleting API, is now complete");
			}
		} catch (Exception e) {
			log.info("Severe Exception occured while deleting the vocal : "+e.getMessage());
			return null;
		}
		return returnAfterDelete != null && isValid(returnAfterDelete) ? returnAfterDelete : null ;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public ConcurrentSkipListMap<String, Vocal> getInfoMap() {
		return infoMap;
	}

	public void setInfoMap(ConcurrentSkipListMap<String, Vocal> infoMap) {
		this.infoMap = infoMap;
	}

	BlockingQueue<DelayedProcessForDelete> delayQ = new DelayQueue<DelayedProcessForDelete>();
	public void setDelayQ(BlockingQueue<DelayedProcessForDelete> delayQ) {
		this.delayQ = delayQ;
	}
	public BlockingQueue<DelayedProcessForDelete> getDelayQ() {
		return delayQ;
	}
	
	public boolean isValid(String json) {
		if(json == null || json.isEmpty()) return false;
	    try {
	        new JsonParser().parse(json);
	        return true;
	    } catch (JsonSyntaxException jse) {
	        return false;
	    }
	}
}
