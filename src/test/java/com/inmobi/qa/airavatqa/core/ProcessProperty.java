package com.inmobi.qa.airavatqa.core;

import lombok.Getter;
import lombok.Setter;

import com.inmobi.qa.airavatqa.generated.process.Property;

public class ProcessProperty {

	@Getter @Setter Property property = new Property();
	public ProcessProperty(String name, String value){
		property.setName(name);
		property.setValue(value);
	}
}
