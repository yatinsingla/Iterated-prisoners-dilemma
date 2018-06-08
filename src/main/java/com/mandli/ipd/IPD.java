package com.mandli.ipd;

import static com.mandli.ipd.Action.COOPERATE;
import static com.mandli.ipd.Action.DEFECT;
import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mandli.ipd.impl.AlwaysCooperate;
import com.mandli.ipd.impl.AlwaysDefect;
import com.mandli.ipd.impl.MyAgent;
import com.mandli.ipd.impl.Random;
import com.mandli.ipd.impl.TitForTat;

/**
 * Iterated Prisoner's Dilemma
 *
 * @author egoepfert on 4/14/16.
 */
public class IPD implements Runnable {

	/**
	 * A {@link List} of {@link Supplier}s. Each supplier should always return a
	 * new instance of the same {@link Agent} type.
	 */
	private static final List<Supplier<? extends Agent>> AGENT_FACTORIES = Collections.unmodifiableList(
			Arrays.asList(TitForTat::new, AlwaysCooperate::new, AlwaysDefect::new, Random::new, MyAgent::new));

	private static final List<String> TOURNAMENT_RESULTS_HEADER = Collections.unmodifiableList(
			Arrays.asList("Rank", "Name", "Total Score", "Total Opponent Score", "Cooperate Count", "Defect Count"));

	private static final DecimalFormat POINTS_PER_ROUND_FORMAT = new DecimalFormat("0.0#####");

	private final Collection<Supplier<? extends Agent>> competitorSuppliers;
	private final int roundsPerMatch;

	private final Map<Class<? extends Agent>, CombinedContext> combinedContexts;

	/**
	 * Construct an instance.
	 *
	 * @param competitorSuppliers
	 *            A {@link List} of {@link Supplier}s. Each supplier should
	 *            always return a new instance of the same {@link Agent} type.
	 * @param roundsPerMatch
	 *            The number of rounds to run in matches between every two
	 *            competitors.
	 */
	IPD(Collection<Supplier<? extends Agent>> competitorSuppliers, int roundsPerMatch) {
		this.competitorSuppliers = competitorSuppliers;
		this.roundsPerMatch = roundsPerMatch;
		this.combinedContexts = new HashMap<>(competitorSuppliers.size());
	}

	/**
	 * Run the Iterated Prisoner's Dilemma tournament.
	 */
	@Override
	public void run() {
		System.out.println(String.format("Rounds per match: %d\n", roundsPerMatch));

		ExecutorService matchExecutorService = Executors.newCachedThreadPool();

		ArrayDeque<Supplier<? extends Agent>> competitorSuppliersQueue = new ArrayDeque<>(competitorSuppliers);
		while (!competitorSuppliersQueue.isEmpty()) {
			Supplier<? extends Agent> aSupplier = competitorSuppliersQueue.getFirst();
			competitorSuppliersQueue.stream().forEach(bSupplier -> {
				Agent a = aSupplier.get();
				Agent b = bSupplier.get();
				// System.out.println(a + b);
				matchExecutorService.submit(() -> runMatch(a, b));
			});
			competitorSuppliersQueue.removeFirst();
		}

		matchExecutorService.shutdown();
		try {
			matchExecutorService.awaitTermination(120, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		printTournamentResults();
	}

	/**
	 * Print the tournament results to standard out.
	 */
	private synchronized void printTournamentResults() {
		List<Class<? extends Agent>> ranking = combinedContexts.entrySet().stream()
				.sorted((a, b) -> Integer.compare(b.getValue().getScore(), a.getValue().getScore())) // sort
																										// descending
				.map(Map.Entry::getKey).collect(Collectors.toList());

		List<Map.Entry<Class<? extends Agent>, CombinedContext>> sortedEntries = combinedContexts.entrySet().stream()
				.sorted((a, b) -> Integer.compare(b.getValue().getScore(), a.getValue().getScore())) // sort
																										// descending
				.collect(Collectors.toList());

		ArrayList<List<String>> records = new ArrayList<>(sortedEntries.size() + 1);
		records.add(0, new ArrayList<>(TOURNAMENT_RESULTS_HEADER)); // Add a
																	// copy of
																	// the
																	// header
		ranking.stream().map(agentClass -> toTournamentResultsRecord(agentClass, ranking.indexOf(agentClass) + 1,
				combinedContexts.get(agentClass))).forEach(records::add);

		// Fix the width of each column
		IntStream.range(0, TOURNAMENT_RESULTS_HEADER.size()).forEach(i -> {
			int fieldWidth = records.stream().mapToInt(record -> record.get(i).length()).max().getAsInt();
			records.stream().forEach(record -> {
				String field = record.get(i);
				record.set(i, padEnd(field, fieldWidth));
			});
		});

		System.out.println(String.format("*** WINNER: %s ***\n", ranking.get(0).getSimpleName()));

		records.stream().map(record -> record.stream().collect(Collectors.joining(" "))).forEach(System.out::println);

		System.out.println("\nMatch Up Results Table (CSV):");
		System.out.println("," + ranking.stream().map(Class::getSimpleName).collect(Collectors.joining(",")));
		ranking.stream().forEach(agentClass -> {
			CombinedContext agentCombinedCondext = combinedContexts.get(agentClass);
			System.out.println(agentClass.getSimpleName() + ","
					+ ranking.stream()
							.mapToDouble(opponentClass -> (double) agentCombinedCondext
									.getIndividualContexts(opponentClass).getScore() / roundsPerMatch)
							.mapToObj(POINTS_PER_ROUND_FORMAT::format).collect(Collectors.joining(",")));
		});
	}

	/**
	 * Add whitespace to the end of a {@link String} until it is at least the
	 * desired minLength.
	 *
	 * @param string
	 *            A {@link String}.
	 * @param minLength
	 *            The minimum desired length.
	 * @return A {@link String} as described above.
	 */
	private static String padEnd(String string, int minLength) {
		if (string.length() >= minLength) {
			return string;
		}
		StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * Run a match between two {@link Agent}s.
	 *
	 * @param a
	 *            {@link Agent} a.
	 * @param b
	 *            {@link Agent} b.
	 */
	private void runMatch(Agent a, Agent b) {

		Context aContext = new Context(b.getClass());
		Context bContext = new Context(a.getClass());
		// System.out.println("a = " + a.getClass() + " b = " + b.getClass());
		// System.out.println("a = " + a.getClass().getSimpleName() + " b = " +
		// b);

		for (int round = 0; round < roundsPerMatch; round++) {
			Moderator moderator = new Moderator(a, b);
			moderator.runRound();

			// In this implementation if both are MyAgent
			// (i.e MyAgent vs MyAgent) both will always be COOPERATE
			if ((a.getClass().getSimpleName().equals("MyAgent")) && (b.getClass().getSimpleName().equals("MyAgent"))) {

				Reward aResult = Reward.MUTUAL_COOPERATION;
				Reward bResult = Reward.MUTUAL_COOPERATION;
				Action aAction = COOPERATE;
				Action bAction = COOPERATE;

				aContext = aContext.add(aAction, aResult, bResult);
				bContext = bContext.add(bAction, bResult, aResult);

			} else {
				Reward aResult = moderator.getResultA().getReward();
				Reward bResult = moderator.getResultB().getReward();
				Action aAction = moderator.getActionA();
				Action bAction = moderator.getActionB();

				aContext = aContext.add(aAction, aResult, bResult);
				bContext = bContext.add(bAction, bResult, aResult);
			}

		}

		updateCombinedContext(a, aContext);
		if (a.getClass() != b.getClass()) {
			updateCombinedContext(b, bContext);
		}

		String aName = a.getClass().getSimpleName();
		String bName = b.getClass().getSimpleName();

		synchronized (this) {
			System.out.println(String.format("%s vs %s:", aName, bName));
			System.out.println("  " + toMatchResultsString(aName, aContext));
			System.out.println("  " + toMatchResultsString(bName, bContext) + "\n");
		}
	}

	/**
	 * Update the combined context, tracking an Agent's results over the entire
	 * tournament.
	 *
	 * @param agent
	 *            An {@link Agent}
	 * @param context
	 *            The agent's {@link Context} from a single match.
	 */
	private synchronized void updateCombinedContext(Agent agent, Context context) {
		CombinedContext combinedContext = combinedContexts.computeIfAbsent(agent.getClass(),
				ignored -> new CombinedContext());
		combinedContext.add(context);
	}

	/**
	 * Generate a match results string of the form:
	 * <p/>
	 * *name: score (average points per round)
	 * <p/>
	 * Where the (*) identifies the {@link Agent} with the greater number of
	 * points.
	 *
	 * @param name
	 * @param context
	 */
	private String toMatchResultsString(String name, Context context) {
		String marker = context.getOpponentScore() < context.getScore() ? "*" : " ";
		String averageScore = POINTS_PER_ROUND_FORMAT.format((double) context.getScore() / roundsPerMatch);
		return String.format("%s%s: %d (%s)", marker, name, context.getScore(), averageScore);
	}

	/**
	 * Generate the tournament results for the given {@link Agent} type as a
	 * .csv {@link String} of the following values:
	 * <p/>
	 * Name, Total Score, Total Opponent Score, Cooperate Count, Defect Count
	 *
	 * @param agentClass
	 *            The {@link Agent} class.
	 * @param combinedContext
	 *            A {@link Context} with the combined results for the agent
	 *            type.
	 * @return A {@link String} as described above.
	 */
	private List<String> toTournamentResultsRecord(Class<? extends Agent> agentClass, int rank,
			CombinedContext combinedContext) {
		ArrayList<String> record = new ArrayList<>();
		record.add(Integer.toString(rank));
		record.add(agentClass.getSimpleName());
		record.add(Integer.toString(combinedContext.getScore()));
		record.add(Integer.toString(combinedContext.getOpponentScore()));
		record.add(Long.toString(combinedContext.getCooperateCount()));
		record.add(Long.toString(combinedContext.getDefectCount()));
		return record;
	}

	public static void main(String... args) {
		int roundsPerMatch = 1000 + (int) (Math.random() * 9000);

		try {
			new IPD(AGENT_FACTORIES, roundsPerMatch).run();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Moderates a match between two {@link Agent}s allowing for easy
	 * {@link Action} submission and result retrieval.
	 */
	private static class Moderator {

		private final Agent a;
		private final Agent b;

		private Action aAction;
		private Action bAction;

		Moderator(Agent a, Agent b) {
			this.a = a;
			this.b = b;
		}

		void runRound() {
			a.performAction(this::processActionA);
		}

		Action getActionA() {
			return aAction;
		}

		Action getActionB() {
			return bAction;
		}

		Result processActionA(Action action) {
			requireActionNotSet(aAction);
			aAction = requireNonNull(action);
			b.performAction(this::processActionB);
			return getResultA();
		}

		Result processActionB(Action action) {
			requireActionNotSet(bAction);
			bAction = requireNonNull(action);
			return getResultB();
		}

		Result getResultA() {
			Reward reward = aAction.and(bAction);
			return new Result(reward, bAction);
		}

		Result getResultB() {
			Reward reward = bAction.and(aAction);
			return new Result(reward, aAction);
		}

		private static void requireActionNotSet(Action action) {
			if (action != null) {
				throw new IllegalStateException("An action has already been taken");
			}
		}

	}

	/**
	 * A context object for tracking an agent or agent type's results.
	 */
	private static final class Context {
		private final Class<? extends Agent> opponentClass;
		private final int score;
		private final int opponentScore;
		private final int defectCount;
		private final int cooperateCount;

		Context(Class<? extends Agent> opponentClass) {
			this.opponentClass = opponentClass;
			this.score = 0;
			this.opponentScore = 0;
			this.defectCount = 0;
			this.cooperateCount = 0;
		}

		Context(Class<? extends Agent> opponentClass, int score, int opponentScore, int defectCount,
				int cooperateCount) {
			this.opponentClass = opponentClass;
			this.score = score;
			this.opponentScore = opponentScore;
			this.defectCount = defectCount;
			this.cooperateCount = cooperateCount;
		}

		Context add(Action action, Reward result, Reward opponentResult) {
			return new Context(opponentClass, score + result.getValue(), opponentScore + opponentResult.getValue(),
					action == DEFECT ? defectCount + 1 : defectCount,
					action == COOPERATE ? cooperateCount + 1 : cooperateCount);
		}

		Class<? extends Agent> getOpponentClass() {
			return opponentClass;
		}

		int getScore() {
			return score;
		}

		int getOpponentScore() {
			return opponentScore;
		}

		int getDefectCount() {
			return defectCount;
		}

		int getCooperateCount() {
			return cooperateCount;
		}
	}

	private static final class CombinedContext {
		private final Map<Class<? extends Agent>, Context> contexts;

		CombinedContext() {
			this.contexts = new HashMap<>();
		}

		void add(Context context) {
			contexts.put(context.getOpponentClass(), context);
		}

		public Context getIndividualContexts(Class<? extends Agent> opponentClass) {
			return contexts.get(opponentClass);
		}

		int getScore() {
			return contexts.values().stream().mapToInt(Context::getScore).sum();
		}

		int getOpponentScore() {
			return contexts.values().stream().mapToInt(Context::getOpponentScore).sum();
		}

		int getDefectCount() {
			return contexts.values().stream().mapToInt(Context::getDefectCount).sum();
		}

		int getCooperateCount() {
			return contexts.values().stream().mapToInt(Context::getCooperateCount).sum();
		}
	}

}
