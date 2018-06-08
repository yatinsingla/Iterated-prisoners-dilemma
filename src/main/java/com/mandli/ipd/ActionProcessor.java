package com.mandli.ipd;

/**
 * A processor accepting two {@link Agent}'s actions and returning the result for each participant.
 *
 * @author egoepfert on 4/14/16.
 */
@FunctionalInterface
public interface ActionProcessor {

	/**
	 * Accept an {@link Agent}'s action and return the {@link Result}.
	 *
	 * @param action The {@link Action#COOPERATE} or {@link Action#DEFECT} action.
	 * @return The {@link Result} for the submitting {@link Agent}.
	 */
	Result submitAction(Action action);

}
