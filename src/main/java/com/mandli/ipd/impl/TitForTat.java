package com.mandli.ipd.impl;

import com.mandli.ipd.Action;
import com.mandli.ipd.ActionProcessor;
import com.mandli.ipd.Agent;
import com.mandli.ipd.Result;

/**
 * An agent using the strategy of first cooperating, then subsequently replicating an opponent's previous action.
 *
 * @author egoepfert on 4/14/16.
 */
public class TitForTat implements Agent {

	private Action prevOpponentAction;

	public TitForTat() {
		prevOpponentAction = Action.COOPERATE;
	}

	@Override
	public void performAction(ActionProcessor actionProcessor) {
		Result result = actionProcessor.submitAction(prevOpponentAction);
		prevOpponentAction = result.getOpponentAction();
	}

}
