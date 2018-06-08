package com.mandli.ipd;

/**
 * The results of a round of Prisoner's Dilemma.
 *
 * @author egoepfert on 4/14/16.
 */
public class Result {

	private final Reward reward;
	private final Action opponentAction;

	/**
	 * Construct an instance.
	 *
	 * @param reward The earned reward.
	 * @param opponentAction The action taken by the opponent.
	 */
	public Result(Reward reward, Action opponentAction) {
		this.opponentAction = opponentAction;
		this.reward = reward;
	}

	/**
	 * @return The earned reward.
	 */
	public Reward getReward() {
		return reward;
	}

	/**
	 * @return The action taken by the opposing {@link Agent}.
	 */
	public Action getOpponentAction() {
		return opponentAction;
	}

}
