/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa.coordinatorCore;

import com.inmobi.qa.airavatqa.core.Bundle;
import com.inmobi.qa.airavatqa.core.Util;
import com.inmobi.qa.airavatqa.generated.process.Input;
import com.inmobi.qa.airavatqa.generated.process.Output;
import com.inmobi.qa.airavatqa.generated.process.Process;
import com.inmobi.qa.airavatqa.generated.process.Property;
import com.inmobi.qa.airavatqa.generated.cluster.Cluster;
import com.inmobi.qa.airavatqa.generated.cluster.Interfacetype;
import com.inmobi.qa.airavatqa.generated.coordinator.ACTION;
import com.inmobi.qa.airavatqa.generated.coordinator.CONFIGURATION;
import com.inmobi.qa.airavatqa.generated.coordinator.CONTROLS;
import com.inmobi.qa.airavatqa.generated.coordinator.COORDINATORAPP;
import com.inmobi.qa.airavatqa.generated.coordinator.DATAIN;
import com.inmobi.qa.airavatqa.generated.coordinator.DATAOUT;
import com.inmobi.qa.airavatqa.generated.coordinator.DATASETS;
import com.inmobi.qa.airavatqa.generated.coordinator.INPUTEVENTS;
import com.inmobi.qa.airavatqa.generated.coordinator.OUTPUTEVENTS;
import com.inmobi.qa.airavatqa.generated.coordinator.SYNCDATASET;
import com.inmobi.qa.airavatqa.generated.coordinator.WORKFLOW;
import com.inmobi.qa.airavatqa.generated.feed.Feed;
import com.inmobi.qa.airavatqa.generated.feed.Location;
import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

/**
 *
 * @author rishu.mehrotra
 */
public class CoordinatorHelper {
    
            
//    public static COORDINATORAPP createCoordinator(Bundle bundle) throws Exception
//    {
//        COORDINATORAPP result=new COORDINATORAPP();
//        
//        //lets get relevant data first
//        Process process=getProcessObject(bundle.getProcessData());
//        
//        //set coordinator name
//        if(null!=process.getName())
//        {
//            result.setName("IVORY_PROCESS_DEFAULT_"+process.getName());
//        }
//        
//        //set coordinator frequency:
//        if(null!=process.getFrequency() && null!=process.getPeriodicity())
//        {
//            result.setFrequency("${coord:"+process.getFrequency().value()+"("+process.getPeriodicity()+")}");
//        }
//        
//        
//        
//        //set time limits
//        if(null!=process.getValidity())
//        {
//            if(null!=process.getValidity().getStart())
//            {
//                result.setStart(process.getValidity().getStart());
//            }
//            
//            if(null!=process.getValidity().getEnd())
//            {
//                result.setEnd(process.getValidity().getEnd());
//            }
//            if(null!=process.getValidity().getTimezone())
//            {
//                result.setTimezone(process.getValidity().getTimezone().name());
//                //process.getValidity().getTimezone();
//            }
//        }
//        
//        
//        //create CONTROLS and set the same
//        com.inmobi.qa.airavatqa.generated.coordinator.CONTROLS coordControl=new com.inmobi.qa.airavatqa.generated.coordinator.CONTROLS();
//        
//        if(null!=process.getConcurrency())
//        {
//            coordControl.setConcurrency(String.valueOf(process.getConcurrency().intValue()));
//        }
//        if(null!=process.getExecution())
//        {
//            coordControl.setExecution(process.getExecution().name());
//        }
//      
//        // yet to set throttle and timeout values here 
//        
//        result.setControls(coordControl);
//        
//        
//        result.setDatasets(defineCoordinatorDataSets(bundle));
//        
//        
//        result.setInputEvents(defineCoordinatorInputEvents(bundle,process));
//        
//        result.setOutputEvents(defineCoordinatorOutputEvents(bundle,process));
//        
//        result.setAction(defineFeedActions(bundle));
//        
//        return result;
//        
//    }
//    
//    private static ACTION defineFeedActions(Bundle bundle) throws Exception
//    {
//        ACTION resultAction=new ACTION();
//        resultAction.setWorkflow(defineWorkFlow(bundle));
//        return resultAction;
//    }
//    
//    private static WORKFLOW defineWorkFlow(Bundle bundle) throws Exception
//    {
//       WORKFLOW workflow=new WORKFLOW();
//       
//       Process process=bundle.getProcessObject();
//       
//       if(null!=process.getWorkflow() && null!=process.getWorkflow().getPath())
//       {
//            workflow.setAppPath("${nameNode}/projects/ivory/staging/ivory/workflows/process/"+Util.readEntityName(bundle.getProcessData()));
//       }
//       
//       //need to set other configs too
//       CONFIGURATION configuration=new CONFIGURATION();
//       
//       if(null!=process.getProperties() && null!=process.getProperties().getProperty())
//       {
//           for(Property property:process.getProperties().getProperty())
//           {
//               CONFIGURATION.Property confProperty=new CONFIGURATION.Property();
//               
//               if(null!=property.getName())
//               {
//                    confProperty.setName(property.getName());
//               }
//               if(null!=property.getValueAttribute())
//               {
//                    confProperty.setValue(property.getValueAttribute());
//               }
//               configuration.addProperty(confProperty);
//           }
//       }
//       
//       //now to add default properties that get added:
//       
//       //libpath:
//       CONFIGURATION.Property libPathProperty=new CONFIGURATION.Property();
//       libPathProperty.setName("oozie.libpath");
//       //libPathProperty.setValue("${nameNode}"+process.get);
//       libPathProperty.setValue("${nameNode}"+process.getWorkflow().getPath()+"/lib");
//       configuration.addProperty(libPathProperty);
//       
//       //namenode
////       CONFIGURATION.Property nameNode=new CONFIGURATION.Property();
////       nameNode.setName("nameNode");
////       nameNode.setValue("${nameNode}");
////       configuration.addProperty(nameNode);
//       
//       //jobtracker. Not required anymore
////       CONFIGURATION.Property jobTracker=new CONFIGURATION.Property();
////       jobTracker.setName("jobTracker");
////       jobTracker.setValue("${jobTracker}");
////       configuration.addProperty(jobTracker);
//       
//       //broker-URL
//       CONFIGURATION.Property brokerUrl=new CONFIGURATION.Property();
//       brokerUrl.setName("brokerUrl");
//       brokerUrl.setValue(Util.readQueueLocationFromCluster(bundle.getClusterData()));
//       configuration.addProperty(brokerUrl);        
//       
//       //adding external id
//       CONFIGURATION.Property externalId=new CONFIGURATION.Property();
//       externalId.setName("oozie.wf.external.id");
//       externalId.setValue(Util.readEntityName(bundle.getProcessData())+"/DEFAULT/${coord:nominalTime()}");
//       configuration.addProperty(externalId);
//       
//       String outputFeedNames="";
//       
//       //now to add all data sets; input and output
//       for(Input input:process.getInputs().getInput())
//       {
//           CONFIGURATION.Property inputProperty=new CONFIGURATION.Property();
//           inputProperty.setName(input.getName());
//           //inputProperty.setName("ivoryInPaths");
//           String value="'"+input.getName()+"'";
//           if(null!=input.getPartition()) {value+=", '"+input.getPartition()+"'";}
//           inputProperty.setValue("${coord:dataIn("+value+")}");
//           
//           if(null!=input.getPartition())
//           {
//               inputProperty.setValue(inputProperty.getValue().replace("coord","elext"));
//           }
//           
//           configuration.addProperty(inputProperty);
//       }
//       
//              //now to add all data sets; input and output
//       for(Input input:process.getInputs().getInput())
//       {
//           CONFIGURATION.Property inputPaths=new CONFIGURATION.Property();
//           inputPaths.setName("ivoryInPaths");
//           //inputProperty.setName("ivoryInPaths");
//           String value="'"+input.getName()+"'";
//           if(null!=input.getPartition()) {value+=", '"+input.getPartition()+"'";}
//           inputPaths.setValue("${coord:dataIn("+value+")}");
//           
//           if(null!=input.getPartition())
//           {
//               inputPaths.setValue(inputPaths.getValue().replace("coord","elext"));
//           }
//           
//           configuration.addProperty(inputPaths);
//       }
//       
//       
//       String feedInstancePathString="";
//       
//       for(Output output:process.getOutputs().getOutput())
//       {
//           CONFIGURATION.Property outputProperty=new CONFIGURATION.Property();
//           outputProperty.setName(output.getName());
//           outputProperty.setValue("${coord:dataOut('"+output.getName()+"')}");
//           
//           configuration.addProperty(outputProperty);
//           outputFeedNames+=output.getName()+",";
//           feedInstancePathString+="${coord:dataOut('"+output.getName()+"')}"+",";
//       }
//       
//       
//       
//       //adding feednames
//       CONFIGURATION.Property feedNames=new CONFIGURATION.Property();
//       feedNames.setName("feedNames");
//       feedNames.setValue(outputFeedNames.substring(0,outputFeedNames.length()-1));
//       configuration.addProperty(feedNames);
//       
//       //adding process topic name.Not used anymore
////       CONFIGURATION.Property entityTopicName=new CONFIGURATION.Property();
////       entityTopicName.setName("entityTopicName");
////       entityTopicName.setValue(Util.readEntityName(bundle.getProcessData()));
////       configuration.addProperty(entityTopicName);
//       
////       CONFIGURATION.Property entityType=new CONFIGURATION.Property();
////       entityType.setName("entityType");
////       entityType.setValue("PROCESS");
////       configuration.addProperty(entityType);
//       
//       CONFIGURATION.Property entityType2=new CONFIGURATION.Property();
//       entityType2.setName("entityType");
//       entityType2.setValue("PROCESS");
//       configuration.addProperty(entityType2);
//       
//       // add nominal time
//       CONFIGURATION.Property nominalTime=new CONFIGURATION.Property();
//       nominalTime.setName("nominalTime");
//       nominalTime.setValue("${coord:formatTime(coord:nominalTime(), 'yyyy-MM-dd-HH-mm')}");
//       configuration.addProperty(nominalTime);
//       
//       //timestamp
//       CONFIGURATION.Property timeStamp=new CONFIGURATION.Property();
//       timeStamp.setName("timeStamp");
//       timeStamp.setValue("${coord:formatTime(coord:actualTime(), 'yyyy-MM-dd-HH-mm')}");
//       configuration.addProperty(timeStamp);
//       
//       //adding feedInstancePaths
//       CONFIGURATION.Property feedInstancePaths=new CONFIGURATION.Property();
//       feedInstancePaths.setName("feedInstancePaths");
//       feedInstancePaths.setValue(feedInstancePathString.substring(0,feedInstancePathString.length()-1));
//       configuration.addProperty(feedInstancePaths);
//       
//       //proprty called GENERATE
//       CONFIGURATION.Property operation=new CONFIGURATION.Property();
//       operation.setName("operation");
//       operation.setValue("GENERATE");
//       configuration.addProperty(operation);
//       
//       //logDir
//       CONFIGURATION.Property logdir=new CONFIGURATION.Property();
//       logdir.setName("logDir");
//       logdir.setValue("${nameNode}/projects/ivory/staging/ivory/workflows/process/"+Util.readEntityName(bundle.getProcessData()));
//       configuration.addProperty(logdir);
//       
//       //entityname
//       CONFIGURATION.Property entityName=new CONFIGURATION.Property();
//       entityName.setName("entityName");
//       entityName.setValue(Util.readEntityName(bundle.getProcessData()));
//       configuration.addProperty(entityName);
//       
//       
//       LinkedHashSet<CONFIGURATION.Property> propertiesSet=new LinkedHashSet<CONFIGURATION.Property>();
//               
//       // add all properties present in all feeds also. Not happening now.
////       for(String feed:bundle.getDataSets())
////       {
////           Feed feedObject=bundle.getFeedObject(Util.readDatasetName(feed));
////           for(com.inmobi.qa.airavatqa.generated.feed.Property property:feedObject.getProperties().getProperty())
////           {
////               CONFIGURATION.Property feedProperty=new CONFIGURATION.Property();
////               feedProperty.setName(property.getName());
////               feedProperty.setValue(property.getValueAttribute());
////               propertiesSet.add(feedProperty);
////           }
////       }
//       
//       //add all non-final cluster properties to the process also. Not valid anymore
////       Cluster cluster=bundle.getClusterObject();
////       for(com.inmobi.qa.airavatqa.generated.cluster.Property property:cluster.getProperties().getProperty())
////       {
////           // add logic to exclude final properties
////           CONFIGURATION.Property clusterProperty=new CONFIGURATION.Property();
////           clusterProperty.setName(property.getName());
////           clusterProperty.setValue(property.getValueAttribute());
////           propertiesSet.add(clusterProperty);
////       }
//       
//       //add set elements to configuration
//       for(CONFIGURATION.Property property:propertiesSet)
//       {
//           configuration.addProperty(property);
//       }
//       
//       //currently default class for mq. 
//       boolean isBrokerClassSpecified=false;
//       for(CONFIGURATION.Property property:configuration.getProperty())
//       {
//          if(property.getName().equalsIgnoreCase("brokerImplClass"))
//          {
//              isBrokerClassSpecified=true;
//              break;
//          }
//       }
//       
//       if(!isBrokerClassSpecified || isBrokerClassEmpty(configuration))
//       {
//        CONFIGURATION.Property brokerImpl=new CONFIGURATION.Property();
//        brokerImpl.setName("brokerImplClass");
//        brokerImpl.setValue("org.apache.activemq.ActiveMQConnectionFactory");
//        configuration.addProperty(brokerImpl);
//       }
//       
//       workflow.setConfiguration(configuration);
//       return workflow;
//    }
//    
//    private static boolean isBrokerClassEmpty(CONFIGURATION configuration) throws Exception
//    {
//       for(CONFIGURATION.Property property:configuration.getProperty()) 
//       {
//           if(property.getName().equalsIgnoreCase("brokerImplClass"))
//           {
//               if(property.getValue().equals(""))
//                   return true;
//           }
//       }
//       return false;
//    }
//    
//    private static OUTPUTEVENTS defineCoordinatorOutputEvents(Bundle bundle,Process process) throws Exception
//    {
//        OUTPUTEVENTS outputs=new OUTPUTEVENTS();
//        
//        if(null!=process.getOutputs() && null!=process.getOutputs().getOutput())
//        {
//            for(Output output:process.getOutputs().getOutput())
//            {
//               DATAOUT outputData=new DATAOUT();
//
//               if(null!=output.getName())
//               {
//                outputData.setName(output.getName());
//                outputData.setDataset(output.getName());
//               }
////               if(null!=output.getFeed())
////               {
////                outputData.setDataset(output.getFeed());
////               }
//               if(null!=output.getInstance())
//               {
//                if(output.getInstance().contains("latest"))
//                {
//                    outputData.setInstance("${coord:"+output.getInstance()+"}");
//                }
//                else
//                {
//                    outputData.setInstance("${elext:"+output.getInstance()+"}");
//                }
//               }
//
//               outputs.addDataOut(outputData);
//
//            }
//        }
//        
//        return outputs;
//    }
//    
//    private static INPUTEVENTS defineCoordinatorInputEvents(Bundle bundle,Process process) throws Exception
//    {
//        INPUTEVENTS inputs=new INPUTEVENTS();
//        
//        if(null!=process.getInputs() && null!=process.getInputs().getInput())
//        {
//            for(Input input:process.getInputs().getInput())
//            {
//               DATAIN inputData=new DATAIN();
//
//               if(null!=input.getName())
//               {
//                inputData.setName(input.getName());
//                inputData.setDataset(input.getName());
//               }
////               if(null!=input.getFeed())
////               {
////                //inputData.setDataset(input.getFeed());
////                inputData.setDataset(input.getName());
////               }
//               if(null!=input.getStartInstance())
//               {
//                if(input.getStartInstance().contains("latest"))
//                {
//                    inputData.setStartInstance("${coord:"+input.getStartInstance()+"}");
//                }
//                else
//                {
//                    inputData.setStartInstance("${elext:"+input.getStartInstance()+"}");
//                }
//               }
//               if(null!=input.getEndInstance())
//               {
//                if(input.getEndInstance().contains("latest"))
//                {
//                    inputData.setEndInstance("${coord:"+input.getEndInstance()+"}");
//                }
//                else
//                {
//                    inputData.setEndInstance("${elext:"+input.getEndInstance()+"}");
//                }
//               }
//
//               inputs.addDataIn(inputData);
//            }
//        }
//        
//        return inputs;
//    }
//    
//    
//    
//    private static DATASETS defineCoordinatorDataSets(Bundle bundle) throws Exception
//    {
//       DATASETS datasets=new DATASETS();
//       for(String dataset:bundle.getDataSets())
//        {
//           Feed feed=getFeedObject(dataset);
//           
//           //now set this feed value
//           SYNCDATASET data=new SYNCDATASET();
//           
//           //get essential values:
//           
//           if(null!=feed.getName())
//           {
//            //data.setName(feed.getName());
//            data.setName(getFeedNameInProcess(feed.getName(),bundle.getProcessObject()));
//           }
//           if(null!=feed.getFrequency() && null!=feed.getPeriodicity())
//           {
//            data.setFrequency("${coord:"+feed.getFrequency()+"("+feed.getPeriodicity()+")}");
//           }
//           if(null!=feed.getClusters() && null!=feed.getClusters().getCluster().get(0) && null!=feed.getClusters().getCluster().get(0).getValidity())
//           {
//               if(null!=feed.getClusters().getCluster().get(0).getValidity().getStart())
//               {
//                data.setInitialInstance(feed.getClusters().getCluster().get(0).getValidity().getStart());
//               }
//               if(null!=feed.getClusters().getCluster().get(0).getValidity().getTimezone())
//               {
//                data.setTimezone(feed.getClusters().getCluster().get(0).getValidity().getTimezone().value());
//               }
//           }
//           
//           if(null!=feed.getLocations())
//           {
//               for(Location location:feed.getLocations().getLocation())
//               {
//                   if(null!=location.getType() && location.getType().equalsIgnoreCase("data"))
//                   {
//                     data.setUriTemplate("${namenode}"+location.getPath());
//                     break;
//                   }
//               }
//           }
//           
//           data.setDoneFlag("");
//           
//           //now to add this to the list
//           datasets.addDatasetOrAsyncDataset(data);
//           
//        } 
//       
//       return datasets;
//    }
//    
//    private static Process getProcessObject(String processString) throws Exception
//    {
//        JAXBContext jc=JAXBContext.newInstance(Process.class); 
//       
//        Unmarshaller u=jc.createUnmarshaller();
//        
//        return (Process)u.unmarshal((new StringReader(processString)));
//        
//    }
//    
//    private static Feed getFeedObject(String feedData) throws Exception
//    {
//        JAXBContext jc=JAXBContext.newInstance(Feed.class);
//    	Unmarshaller u=jc.createUnmarshaller();
//        
//    	return (Feed)u.unmarshal((new StringReader(feedData)));
//    }
//    
//    private static void compareCoordinatorInputEvents(COORDINATORAPP actual,COORDINATORAPP expected) throws Exception
//    {
//        INPUTEVENTS actualInputEvents=actual.getInputEvents();
//        INPUTEVENTS expectedInputevents=expected.getInputEvents();
//        
//        //first check the length
//        Assert.assertEquals(actualInputEvents.getDataIn().size(),expectedInputevents.getDataIn().size(),"inputevent size differs!");
//        
//        if(!(actualInputEvents.getDataIn().isEmpty() && expectedInputevents.getDataIn().isEmpty()))
//        {
//            //now just check if inputevents exist in the other or not!
//            for(DATAIN actualData:actualInputEvents.getDataIn())
//            {
//                boolean found=false;
//                for(DATAIN expectedData:expectedInputevents.getDataIn())
//                {
//                    if(actualData.getDataset().equalsIgnoreCase(expectedData.getDataset()) && actualData.getEndInstance().equalsIgnoreCase(expectedData.getEndInstance()) && actualData.getName().equalsIgnoreCase(expectedData.getName()) && actualData.getStartInstance().equalsIgnoreCase(expectedData.getStartInstance()))
//                    {
//                        //now compare instances
//                        if(compareInstances(actualData.getInstance(),expectedData.getInstance()))
//                        {
//                            found=true;
//                            break;
//                        }
//                    }
//                }
//
//                Assert.assertTrue((found),"input event "+actualData.getName()+" was not found in the expected coordinator!");
//            }
//        }
//    }
//    
//    private static void compareCoordinatorOutputEvents(COORDINATORAPP actual,COORDINATORAPP expected) throws Exception
//    {
//        OUTPUTEVENTS actualOutputEvents=actual.getOutputEvents();
//        OUTPUTEVENTS expectedOutputevents=expected.getOutputEvents();
//        
//        //first check the length
//        Assert.assertEquals(actualOutputEvents.getDataOut().size(),expectedOutputevents.getDataOut().size(),"outputevent size differs!");
//        
//        if(!(actualOutputEvents.getDataOut().isEmpty() && expectedOutputevents.getDataOut().isEmpty()))
//        {
//            //now just check if inputevents exist in the other or not!
//            for(DATAOUT actualData:actualOutputEvents.getDataOut())
//            {
//                boolean found=false;
//                for(DATAOUT expectedData:expectedOutputevents.getDataOut())
//                {
//                    if(actualData.getDataset().equalsIgnoreCase(expectedData.getDataset()) && actualData.getInstance().equalsIgnoreCase(expectedData.getInstance()) && actualData.getName().equalsIgnoreCase(expectedData.getName()))
//                    {
//                        
//                        
//                        {
//                            found=true;
//                            break;
//                        }
//                    }
//                }
//
//                Assert.assertTrue((found),"output event "+actualData.getName()+" was not found in the expected coordinator!");
//            }
//        }
//    }
//    
//    
//    
//    private static boolean compareInstances(List<String> actualInstance,List<String> expectedInstance) throws Exception
//    {
//        int initialSize=actualInstance.size();
//        List<String> newCollection=(List<String>)CollectionUtils.retainAll(actualInstance,expectedInstance);
//        if(initialSize==newCollection.size())
//        {
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
//    
//    public static void compareCoordinators(COORDINATORAPP actual,COORDINATORAPP expected) throws Exception
//    {
//        //compare names
//        Assert.assertEquals(actual.getName(),expected.getName(),"coordinator names dont match!");
//        //compare frequency
//        Assert.assertEquals(actual.getFrequency(),expected.getFrequency(),"coordinator frequency does not match");
//        //compare start
//        Assert.assertEquals(actual.getStart(),expected.getStart(),"coordinator start does not match");
//        //compare end
//        Assert.assertEquals(actual.getEnd(),expected.getEnd(),"coordinator end does not match");
//        //compare timezone
//        Assert.assertEquals(actual.getTimezone(),expected.getTimezone(),"coordinator timezone does not match");
//        //compareControls
//        compareCoordinatorControls(actual,expected);
//        //compare inputevents
//        compareCoordinatorInputEvents(actual, expected);
//        //compare outputevents
//        compareCoordinatorOutputEvents(actual, expected);
//        //compare datasets
//        compareCoordinatorDataSets(actual.getDatasets(),expected.getDatasets());
//        //compare actions
//        compareCoordinatorActions(actual.getAction(),expected.getAction());
//        
//    }
//    
//    private static void compareCoordinatorActions(ACTION actual,ACTION expected) throws Exception
//    {
//        compareWorkflows(actual.getWorkflow(),expected.getWorkflow());
//    }
//    
//    private static void compareWorkflows(WORKFLOW actual,WORKFLOW expected) throws Exception
//    {
//        //Assert.assertEquals(actual.getAppPath(),expected.getAppPath(),"app path does not match inside workflow!");
//        Assert.assertTrue(actual.getAppPath().contains(expected.getAppPath()),"The correct app-path does not seem to exist!");
//        compareConfigurations(actual.getConfiguration(),expected.getConfiguration());
//    }
//    
//    private static void compareConfigurations(CONFIGURATION actual,CONFIGURATION expected) throws Exception
//    {
//        //confirm size is same
//        Assert.assertEquals(actual.getProperty().size(),expected.getProperty().size(),"configuration property size is not same!");
//        
//        for(CONFIGURATION.Property actualProperty:actual.getProperty())
//        {
//            boolean found=false;
//            for(CONFIGURATION.Property expectedProperty:expected.getProperty())
//            {
//                if(compareConfigurationProperty(actualProperty, expectedProperty))
//                {
//                    found=true;
//                    break;
//                }
//            }
//            
//            Assert.assertTrue(found,"Property "+actualProperty.getName()+" was found to be missing!");
//        }
//        
//        
//    }
//    
//    private static boolean compareConfigurationProperty(CONFIGURATION.Property actual,CONFIGURATION.Property expected) throws Exception
//    {
//        if(actual.getName().equalsIgnoreCase("logdir") && actual.getName().equalsIgnoreCase(expected.getName()))
//        {
//            return actual.getValue().contains(expected.getValue());
//        }
//        return (actual.getName().equalsIgnoreCase(expected.getName()) && actual.getValue().equalsIgnoreCase(expected.getValue()));
//    }
//    
//    private static void compareCoordinatorDataSets(DATASETS actualDataSets,DATASETS expectedDataSets) throws Exception
//    {
//        Assert.assertEquals(actualDataSets.getDatasetOrAsyncDataset().size(),expectedDataSets.getDatasetOrAsyncDataset().size(),"size differs for the datasets in expected and actual response!");
//        
//        if(!(actualDataSets.getDatasetOrAsyncDataset().isEmpty() && expectedDataSets.getDatasetOrAsyncDataset().isEmpty()))
//        {
//            for(int i=0;i<actualDataSets.getDatasetOrAsyncDataset().size();i++)
//            {
//
//                boolean found=false;
//                SYNCDATASET actualDataSet=(SYNCDATASET)actualDataSets.getDatasetOrAsyncDataset().get(i);
//                for(int j=0;j<expectedDataSets.getDatasetOrAsyncDataset().size();j++)
//                {
//                    SYNCDATASET expectedDataSet=(SYNCDATASET)expectedDataSets.getDatasetOrAsyncDataset().get(j);
//
//                    if(actualDataSet.getDoneFlag().equalsIgnoreCase(expectedDataSet.getDoneFlag()) && actualDataSet.getFrequency().equalsIgnoreCase(expectedDataSet.getFrequency()) && actualDataSet.getInitialInstance().equalsIgnoreCase(expectedDataSet.getInitialInstance()) && actualDataSet.getName().equalsIgnoreCase(expectedDataSet.getName()) &&actualDataSet.getTimezone().equalsIgnoreCase(expectedDataSet.getTimezone()) && actualDataSet.getUriTemplate().equalsIgnoreCase(expectedDataSet.getUriTemplate()) )
//                            {
//                               found=true;
//                               break;
//                            }
//                }
//
//                Assert.assertTrue(found,"Dataset "+actualDataSet.getName()+" was not found in the expected coordinator!");
//            }
//        }
//    }
//    
//    public static String createParentWorkflow(String process) throws Exception
//    {
//        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
//"<workflow-app xmlns=\"uri:oozie:workflow:0.2\" name=\"ivory-parent-workflow\">"+
//    "<start to=\"pre-processing\"/>"+
//    "<action name=\"pre-processing\">"+
//        "<java>"+
//            "<job-tracker>${jobTracker}</job-tracker>"+
//            "<name-node>${nameNode}</name-node>"+
//            "<main-class>org.apache.ivory.workflow.PreProcessing</main-class>"+
//            "<arg>${processTopicName}</arg>"+
//            "<arg>${feedNames}</arg>"+
//            "<arg>${feedInstancePaths}</arg>"+
//            "<arg>${wf:id()}</arg>"+
//            "<arg>${wf:run()}</arg>"+
//            "<arg>${nominalTime}</arg>"+
//            "<arg>${timeStamp}</arg>"+
//            "<arg>${brokerUrl}</arg>"+
//            "<capture-output/>"+
//        "</java>"+
//        "<ok to=\"user-workflow\"/>"+
//        "<error to=\"fail\"/>"+
//    "</action>"+
//    "<action name=\"user-workflow\">"+
//        "<sub-workflow>"+
//            "<app-path>${nameNode}/examples/apps/aggregator</app-path>"+
//            "<configuration>"+
//                "<property>"+
//                    "<name>queueName</name>"+
//                    "<value>default</value>"+
//                "</property>"+
//                "<property>"+
//                    "<name>nameNode</name>"+
//                    "<value>${nameNode}</value>"+
//                "</property>"+
//                "<property>"+
//                    "<name>jobTracker</name>"+
//                    "<value>${jobTracker}</value>"+
//                "</property>"+
//                "<property>"+
//                    "<name>outputData</name>"+
//                    "<value>${outputData}</value>"+
//                "</property>"+
//                "<property>"+
//                    "<name>inputData</name>"+
//                    "<value>${inputData}</value>"+
//                "</property>"+
//            "</configuration>"+
//        "</sub-workflow>"+
//        "<ok to=\"jms-messaging\"/>"+
//        "<error to=\"fail\"/>"+
//    "</action>"+
//    "<action name=\"jms-messaging\">"+
//        "<java>"+
//            "<job-tracker>${jobTracker}</job-tracker>"+
//            "<name-node>${nameNode}</name-node>"+
//            "<main-class>org.apache.ivory.messaging.MessageProducer</main-class>"+
//            "<arg>${processTopicName}</arg>"+
//            "<arg>${feedNames}</arg>"+
//            "<arg>${feedInstancePaths}</arg>"+
//            "<arg>${wf:id()}</arg>"+
//            "<arg>${wf:run()}</arg>"+
//            "<arg>${nominalTime}</arg>"+
//            "<arg>${timeStamp}</arg>"+
//            "<arg>${brokerUrl}</arg>"+
//            "<capture-output/>"+
//        "</java>"+
//        "<ok to=\"end\"/>"+
//        "<error to=\"fail\"/>"+
//    "</action>"+
//    "<kill name=\"fail\">"+
//        "<message>Workflow failed, error"+
//			"message[${wf:errorMessage(wf:lastErrorNode())}]"+
//		"</message>"+
//    "</kill>"+
//    "<end name=\"end\"/>"+
//"</workflow-app>";
//
//    }
//    
//    private static void compareCoordinatorControls(COORDINATORAPP actual,COORDINATORAPP expected) throws Exception
//    {
//        CONTROLS actualControls=actual.getControls();
//        CONTROLS expectedControls=expected.getControls();
//        
//        Assert.assertEquals(actualControls.getConcurrency(),expectedControls.getConcurrency(),"concurrency does not match!");
//        Assert.assertEquals(actualControls.getExecution(),expectedControls.getExecution(),"execution does not match!");
//        Assert.assertEquals(actualControls.getThrottle(),expectedControls.getThrottle(),"throttle does not match!");
//        Assert.assertEquals(actualControls.getTimeout(),expectedControls.getTimeout(),"timeout does not match!");
//    }
//
//	public static COORDINATORAPP stringToXml(String conf) throws JAXBException {
//		JAXBContext jc=JAXBContext.newInstance(COORDINATORAPP.class); 
//	       
//        Unmarshaller u=jc.createUnmarshaller();
//        
//        return (COORDINATORAPP)u.unmarshal((new StringReader(conf)));
//	}
//
//	public static COORDINATORAPP getProcessCoord(String WorkflowActionConf) {
//	//	String coordPath = getCoordPath(WorkflowActionConf);
//	//	String stringCoord = getCoodFromLocation()
//		return null;
//	}
//        
//        private static String getFeedNameInProcess(String feedName,Process process) throws Exception
//        {
//            for(Input input: process.getInputs().getInput())
//            {
//                if(feedName.equals(input.getFeed()))
//                {
//                    return input.getName();
//                }
//            }
//            
//            for(Output output:process.getOutputs().getOutput())
//            {
//                if(feedName.equals(output.getFeed()))
//                {
//                    return output.getName();
//                }
//            }
//            
//            return null;
//        }
}
