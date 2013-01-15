package com.inmobi.qa.airavatqa.core;

import com.inmobi.qa.airavatqa.generated.process.Property;

import lombok.Getter;
import lombok.Setter;

public class ProcessPartition {

	@Getter @Setter Property property ;
	@Getter @Setter String partition ;
	
	ProcessPartition(String name, String value, String partition){
		property.setName(name);
		property.setValue(value);
		this.partition=partition;
	}
}
