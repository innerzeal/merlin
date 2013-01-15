package com.inmobi.qa.airavatqa.core;

import org.apache.hadoop.fs.Path;
import org.testng.Assert;

import com.inmobi.types.DemandSource;

import lombok.Getter;
import lombok.Setter;

public class DSTCounts {

	@Getter private int network=0;
	@Getter private int house=0;
	@Getter private int hosted=0;
	@Getter private int ifd=0;
	@Getter private int channelPartnership=0;
	@Getter private int total=0;
	@Getter @Setter private int ua2_ua2_Network = 0;
	@Getter @Setter private int lhr1_ua2_Network = 0;
	@Getter @Setter private int uj1_ua2_Network = 0;
	@Getter @Setter private int ua2_ua2_nonNetwork = 0;
	@Getter @Setter private int lhr1_ua2_nonNetwork = 0;
	@Getter @Setter private int uj1_ua2_nonNetwork = 0;

	public void incrementNetwork(Path p){
		network++;
		total++;
		increaseColoClickCount(p,DemandSource.NETWORK);
	}

	public void incrementHouse(Path p){
		house++;
		total++;
		increaseColoClickCount(p,DemandSource.HOUSE);

	}

	public void incrementHosted(Path p){
		hosted++;
		total++;
		increaseColoClickCount(p,DemandSource.HOSTED);

	}

	public void incrementIFD(Path p){
		ifd++;
		total++;
		increaseColoClickCount(p,DemandSource.IFD);

	}

	public void incrementChannelPartnership(Path p){
		channelPartnership++;
		total++;
		increaseColoClickCount(p,DemandSource.CHANNEL_PARTNERSHIP);

	}

	private void increaseColoClickCount(Path p, DemandSource dst) {

		//set count for click from individual colos
		if(p.toUri().toString().contains("ua2/ua2")){

			if(dst.equals(DemandSource.NETWORK))
				ua2_ua2_Network++;
			else
				ua2_ua2_nonNetwork++;

		}

		else if(p.toUri().toString().contains("lhr1/ua2"))
		{
			if(dst.equals(DemandSource.NETWORK))
				lhr1_ua2_Network++;
			else
				lhr1_ua2_nonNetwork++;

		}
		else if(p.toUri().toString().contains("uj1/ua2"))
		{
			if(dst.equals(DemandSource.NETWORK))
				uj1_ua2_Network++;
			else
				uj1_ua2_nonNetwork++;

		}

	}

	public void add(DSTCounts toBeAdded) {

		this.channelPartnership = this.channelPartnership + toBeAdded.getChannelPartnership();
		this.hosted = this.hosted + toBeAdded.getHosted();
		this.house = this.house + toBeAdded.getHouse();
		this.ifd = this.ifd + toBeAdded.getIfd();
		this.network = this.network + toBeAdded.getNetwork();
		this.total = this.total + toBeAdded.getTotal();
	}

	public void checkEquals(DSTCounts toBeComapred){

		System.out.println("user written equals function");
		Assert.assertEquals(this.channelPartnership, toBeComapred.channelPartnership,"channelPartnership not matched");
		Assert.assertEquals(this.hosted, toBeComapred.hosted,"hosted not matched");
		Assert.assertEquals(this.house, toBeComapred.house,"house not matched");
		Assert.assertEquals(this.ifd, toBeComapred.ifd,"ifd not matched");
		Assert.assertEquals(this.network, toBeComapred.network,"network not matched");
		Assert.assertEquals(this.total, toBeComapred.total,"total not matched");

	}
}
