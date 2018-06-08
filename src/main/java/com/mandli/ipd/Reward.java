package com.mandli.ipd;

/**
 * An enum of the possible rewards to be earned by {@link Agent}s participating in a round of Prisoner's Dilemma.
 *
 * @author egoepfert on 4/14/16.
 */
public enum Reward {

	/**
	 * Reward for two cooperating {@link Agent}s.
	 */
	MUTUAL_COOPERATION(3),

	/**
	 * Reward for two defecting {@link Agent}s.
	 */
	MUTUAL_DEFECTION(1),

	/**
	 * Reward for a defecting {@link Agent} when the other {@link Agent} chooses to cooperate.
	 */
	TEMPTATION_PAYOFF(5),

	/**
	 * Reward for a cooperating {@link Agent} when the other {@link Agent} chooses to defect.
	 */
	SUCKERS_PAYOFF(0);

	private final int value;

	Reward(int value) {
		this.value = value;
	}

	/**
	 * @return The score or value associated with this reward.
	 */
	public int getValue() {
		return value;
	}

}
