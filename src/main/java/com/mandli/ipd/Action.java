package com.mandli.ipd;

import static java.util.Objects.requireNonNull;

/**
 * An enum of the two possible actions to be taken during a round of Prisoner's Dilemma.
 *
 * @author egoepfert on 4/14/16.
 */
public enum Action {

	COOPERATE {
		@Override
		public Reward and(Action other) {
			requireNonNull(other);
			return other == COOPERATE ? Reward.MUTUAL_COOPERATION : Reward.SUCKERS_PAYOFF;
		}
	},

	DEFECT {
		@Override
		public Reward and(Action other) {
			requireNonNull(other);
			return other == DEFECT ? Reward.MUTUAL_DEFECTION : Reward.TEMPTATION_PAYOFF;
		}
	};

	/**
	 *
	 * @param other The {@link Action} submitted by the opposing {@link Agent}.
	 * @return The {@link Reward} for an {@link Agent} submitting this action when the opposing {@link Agent}'s submitted the given {@link Action}.
	 */
	public abstract Reward and(Action other);

}
