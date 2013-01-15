package com.inmobi.qa.airavatqa.core;

public enum GetBundle {

	BillingFeedReplicationBundle("src/test/resources/LocalDC_feedReplicaltion_BillingRC"), RegularBundle("src/test/resources/ELbundle");

	private final String value;

	private GetBundle(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
