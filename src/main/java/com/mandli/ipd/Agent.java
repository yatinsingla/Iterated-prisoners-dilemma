package com.mandli.ipd;

/**
 * An agent or competitor in an Iterated Prisoner's Dilemma match, with a lifecycle of a single match.
 * <p/>
 * A match is a repeated sequence of rounds between two {@link Agent} instances, A and B. Each round requires the participating agents to submit one of two possible actions: {@link
 * Action#COOPERATE} or {@link Action#DEFECT}. Both agents submit their action prior to knowing what the opposing agent has submitted. A round is then scored as follows:
 * <pre>
 * ----------------------------------------------------
 * |                |  A Cooperates  |   A Defects    |
 * |----------------|----------------|----------------|
 * |  B Cooperates  |      A: 3      |      A: 5      |
 * |                |      B: 3      |      B: 0      |
 * |----------------|----------------|----------------|
 * |  B Defects     |      A: 0      |      A: 1      |
 * |                |      B: 5      |      B: 1      |
 * ----------------------------------------------------
 * </pre>
 * <p/>
 * Each agent type will then compete in a round-robin tournament of Iterated Prisoner’s Dilemma. The tournament will consist of multiple matches pitting each agent once against an
 * instance of itself and against every other available agent implementation.
 * <p/>
 * Good luck!
 *
 * @author egoepfert on 4/14/16.
 * @see <a href="http://en.wikipedia.org/wiki/Prisoner's_dilemma">Prisoner's dilemma (Wikipedia)</a>
 */
public interface Agent {

	/**
	 * @param actionProcessor A processor to accept an {@link Agent}'s action and return a {@link Result}.
	 */
	void performAction(ActionProcessor actionProcessor);

}
