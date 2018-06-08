# Iterated-prisoners-dilemma

The Prisoner’s Dilemma Coding Challenge
---------------------------------------

Overview:
---------

Task is to implement a competitor, or agent, to compete in a round-robin tournament of the Iterated Prisoner’s Dilemma.

Details:
--------

Entry will be evaluated on the following criteria:
- Code quality
- Clarity
- Ingenuity
- Your agent’s total score in the tournament

The tournament will consist of multiple matches that pit each agent against an instance of itself, and against all other available competitors. The four other agents that you will be competing against have already been created: AlwaysCooperate, AlwaysDefect, Random, and TitForTat.

A match is a repeated sequence of rounds between two Agent instances, A and B. Each round requires the participating agents to submit one of two possible actions: COOPERATE or DEFECT. Both agents submit their action prior to knowing what the opposing agent has submitted. A round is then scored as follows:
----------------------------------------------------
|                |  A Cooperates  |   A Defects    |
|----------------|----------------|----------------|
|  B Cooperates  |      A: 3      |      A: 5      |
|                |      B: 3      |      B: 0      |
|----------------|----------------|----------------|
|  B Defects     |      A: 0      |      A: 1      |
|                |      B: 5      |      B: 1      |
----------------------------------------------------

Getting Started:
----------------

Before you begin, you'll need Java SE Development Kit 8 (available here http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

You'll first need to create an implementation of com.mandli.ipd.Agent. This will involve implementing the Agent.performAction(ActionProcessor) method. The method accepts an ActionProcessor, which will take the agent's action and return a result in the form of com.mandli.ipd.Result. This result provides two pieces of information: The agent's reward or score for that round, and the opposing agent's action.

Once you have an Agent implementation that you'd like to try out, add a reference to its constructor in the AGENT_FACTORIES list at the top of com.mandli.ipd.IPD. The four agents you'll be competing against have already been added.

The tournament can be run via the main method in com.mandli.ipd.IPD. Results are written to standard out.
