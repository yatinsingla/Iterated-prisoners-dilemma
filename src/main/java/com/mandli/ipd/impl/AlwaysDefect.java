package com.mandli.ipd.impl;

import com.mandli.ipd.Action;
import com.mandli.ipd.ActionProcessor;
import com.mandli.ipd.Agent;

/**
 * An agent using this strategy of simply always defecting.
 *
 * @author egoepfert on 4/14/16.
 */
public class AlwaysDefect implements Agent {

	@Override
	public void performAction(ActionProcessor actionProcessor) {
		actionProcessor.submitAction(Action.DEFECT);
	}

}
