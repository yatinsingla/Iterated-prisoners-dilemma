package com.mandli.ipd.impl;

import com.mandli.ipd.Action;
import com.mandli.ipd.ActionProcessor;
import com.mandli.ipd.Agent;

/**
 * An agent using this strategy of making a random choice between cooperate and defect.
 *
 * @author egoepfert on 4/14/16.
 */
public class Random implements Agent {

	@Override
	public void performAction(ActionProcessor actionProcessor) {
		actionProcessor.submitAction(Math.random() < 0.5 ? Action.COOPERATE : Action.DEFECT);
	}

}
