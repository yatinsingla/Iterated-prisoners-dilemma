package com.mandli.ipd.impl;

import com.mandli.ipd.Action;
import com.mandli.ipd.ActionProcessor;
import com.mandli.ipd.Agent;

/**
 * This agent (MyAgent) using the following two strategies -------------------
 * 1. While comparing with others : Always DEFECT ----------------------------
 * 2. While comparing with itself (MyAgent vs MyAgent) : COOPERATE -----------
 * this has been implemented in com.mandli.ipd.IPD under runMatch() (Please look
 * for additional comments there)
 * 
 * @author yatinsingla on 5/17/18.
 */

public class MyAgent implements Agent {

	@Override
	public void performAction(ActionProcessor actionProcessor) {

		// TODO Auto-generated method stub
		actionProcessor.submitAction(Action.DEFECT);
	}

}
