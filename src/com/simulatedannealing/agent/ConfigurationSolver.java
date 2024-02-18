package com.simulatedannealing.agent;

import java.util.Random;
import com.simulatedannealing.environment.Environment;

/**
 * Represents a linear assignment problem where N workers must be assigned
 * to N tasks. Each worker/task combination is further associated with some
 * value. The goal of this task is the produce an optimal configuration that
 * maximizes (or minimizes) the sum of the assigned worker/task values.
 */

public class ConfigurationSolver {
	private Environment env;
	private int[] configuration;
	private int[] bestConfiguration;
	private int iterationCounter = 1;
	private static final double INITIAL_TEMPERATURE = 1000.0;
	private static final double COOLING_RATE = 0.95;

	/** Initializes a Configuration Solver for a specific environment. */
	public ConfigurationSolver(Environment env) {
		this.env = env;
		this.configuration = new int[this.env.getNumWorkers()];
		// Initializing by assigning work to an arbitrary task
		for (int i = 0; i < this.configuration.length; i++) {
			this.configuration[i] = i;
		}
		
		this.bestConfiguration = this.configuration.clone();
	}

	/**
	 * For this Problem Set, you will be exploring search
	 * methods for optimal configurations. In this exercise, you are given
	 * a linear assignment problem, where you must determine the appropriate
	 * configuration assignments for persons to tasks. Specifically, you are
	 * seeking to MAXIMIZE the fitness score of this configuration. While brute
	 * forcing your search will provide you with the optimal solution, you run
	 * into the issue that there are N! possible permutations, which can increase
	 * your search space as N increases. Instead, utilize one of the search methods
	 * presented in class to tackle this problem.
	 * 
	 * For the runSearch(), design an algorithm that will iterate through an
	 * iterative optimization algorithm, updating the current configuration as it
	 * traverses its search space. The runSearch() method should also return this
	 * configuration back to the environment. For example, in an N=5 problem space,
	 * runSearch() may return {4,1,2,3,0}, where Worker #4 is assigned Task #3.
	 * 
	 */


	public int[] runSearch() {
		double currentCost = this.env.calcScore(this.bestConfiguration);
		double temperature = schedule(iterationCounter);
		if (temperature == 0) {
			iterationCounter++;
			return this.bestConfiguration;
		}

		int[] nextSolution = generateNeighborSolution(this.bestConfiguration);
		double deltaE = this.env.calcScore(nextSolution) - currentCost;

		if (deltaE > 0 || acceptWithProbability(Math.exp(deltaE / temperature))) {
			this.bestConfiguration = nextSolution.clone();
			currentCost = this.env.calcScore(this.bestConfiguration);
		}
		iterationCounter++;
		return nextSolution;
	}

	private double schedule(int t) {
		return INITIAL_TEMPERATURE * Math.pow(COOLING_RATE, t);
	}

	private boolean acceptWithProbability(double probability) {
		return Math.random() < probability;
	}

	
	public int[] getBestConfiguration() {
		return this.bestConfiguration;
	}

	private int[] generateNeighborSolution(int[] currentSolution) {
		int[] neighborSolution = currentSolution.clone();
		Random rand = new Random();

		// Ensure that two distinct indices are selected for swapping
		int swapIndex1 = rand.nextInt(this.env.getNumWorkers());
		int swapIndex2;
		do {
			swapIndex2 = rand.nextInt(this.env.getNumWorkers());
		} while (swapIndex1 == swapIndex2);

		// Swap the indices to generate a neighbor solution
		int temp = neighborSolution[swapIndex1];
		neighborSolution[swapIndex1] = neighborSolution[swapIndex2];
		neighborSolution[swapIndex2] = temp;

		return neighborSolution;
	}

}