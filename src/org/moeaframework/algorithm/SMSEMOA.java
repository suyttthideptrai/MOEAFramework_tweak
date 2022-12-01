/* Copyright 2009-2022 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.algorithm;

import java.io.Serializable;
import java.util.Comparator;

import org.moeaframework.core.FastNondominatedSorting;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.FitnessComparator;
import org.moeaframework.core.comparator.NondominatedSortingComparator;
import org.moeaframework.core.comparator.RankComparator;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeContributionFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeFitnessEvaluator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of the S-metric Selection MOEA (SMS-MOEA).  The S metric is
 * also known as the hypervolume indicator.
 * <p>
 * References:
 * <ol>
 *   <li>Emmerich, M., N. Beume, and B. Naujoks (2007).  An EMO Algorithm Using
 *       the Hypervolume Measure as Selection Criterion.  European Journal of
 *       Operational Research, 3:1653-1669.
 * </ol>
 */
public class SMSEMOA extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The fitness evaluator to use (e.g., hypervolume or additive-epsilon indicator).
	 */
	private FitnessEvaluator fitnessEvaluator;
	
	/**
	 * The selection operator.
	 */
	private final Selection selection;
	
	/**
	 * Constructs a new SMS-EMOA instance with default settings.
	 * 
	 * @param problem the problem
	 */
	public SMSEMOA(Problem problem) {
		this(problem,
				new RandomInitialization(problem, Settings.DEFAULT_POPULATION_SIZE),
				OperatorFactory.getInstance().getVariation(problem),
				new HypervolumeContributionFitnessEvaluator(problem));
	}

	/**
	 * Constructs a new SMS-EMOA instance.
	 * 
	 * @param problem the problem
	 * @param initialization the initialization operator
	 * @param variation the variation operator
	 * @param fitnessEvaluator the fitness evaluator
	 */
	public SMSEMOA(Problem problem, Initialization initialization,
			Variation variation, FitnessEvaluator fitnessEvaluator) {
		super(problem, new Population(), null, initialization, variation);
		this.fitnessEvaluator = fitnessEvaluator;
		
		if (fitnessEvaluator ==  null) {
			selection = new TournamentSelection(new NondominatedSortingComparator());
		} else {
			selection = new TournamentSelection(new NondominatedFitnessComparator());
		}
	}
	
	@Override
	@Property("operator")
	public void setVariation(Variation variation) {
		super.setVariation(variation);
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		// rank the solutions
		new FastNondominatedSorting().evaluate(population);
		
		if (fitnessEvaluator != null) {
			fitnessEvaluator.evaluate(population);
		}
	}

	@Override
	protected void iterate() {
		int populationSize = population.size();
		Solution[] parents = selection.select(variation.getArity(), population);
		Solution[] offspring = variation.evolve(parents);
		
		evaluate(offspring[0]);
		population.add(offspring[0]);
		
		// rank the solutions and remove the worst
		new FastNondominatedSorting().evaluate(population);
		
		if (fitnessEvaluator == null) {
			population.truncate(populationSize, new NondominatedSortingComparator());
		} else {
			computeFitnessForLastFront();
			
			population.truncate(populationSize, new NondominatedFitnessComparator());
		}
	}
	
	/**
	 * Computes the fitness for the last front.
	 */
	private void computeFitnessForLastFront() {
		Population front = new Population();
		int rank = 0;
		
		for (Solution solution : population) {
			int solutionRank = (Integer)solution.getAttribute(FastNondominatedSorting.RANK_ATTRIBUTE);
			
			if (solutionRank > rank) {
				front.clear();
				rank = solutionRank;
			}
			
			if (solutionRank == rank) {
				front.add(solution);
			}
			
			solution.setAttribute(FitnessEvaluator.FITNESS_ATTRIBUTE, 0.0);
		}
		
		fitnessEvaluator.evaluate(front);
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("indicator")) {
			String indicator = properties.getString("indicator", null);
			
			if ("hypervolume".equalsIgnoreCase(indicator)) {
				fitnessEvaluator = new HypervolumeFitnessEvaluator(problem);
			} else if ("epsilon".equalsIgnoreCase(indicator)) {
				fitnessEvaluator = new AdditiveEpsilonIndicatorFitnessEvaluator(problem);
			} else {
				throw new ConfigurationException("invalid indicator: " + indicator);
			}
		}
		
		super.applyConfiguration(properties);
		
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		if (fitnessEvaluator instanceof HypervolumeFitnessEvaluator) {
			properties.setString("indicator", "hypervolume");
		} else if (fitnessEvaluator instanceof AdditiveEpsilonIndicatorFitnessEvaluator) {
			properties.setString("indicator", "epsilon");
		}
		
		return properties;
	}
	
	private class NondominatedFitnessComparator extends ChainedComparator implements Comparator<Solution>, Serializable {

		private static final long serialVersionUID = -4088873047790962685L;

		public NondominatedFitnessComparator() {
			super(new RankComparator(), new FitnessComparator(fitnessEvaluator.areLargerValuesPreferred()));
		}

	}

}
