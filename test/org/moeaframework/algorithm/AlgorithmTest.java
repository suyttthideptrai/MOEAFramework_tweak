/* Copyright 2009-2024 David Hadka
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

import org.moeaframework.Analyzer;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.Executor;
import org.moeaframework.core.Settings;
import org.moeaframework.core.indicator.StandardIndicator;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Methods for comparing two algorithm implementations statistically.
 */
public abstract class AlgorithmTest {
	
	/**
	 * The number of seeds to run each algorithm.
	 */
	public static final int SEEDS = 10;
	
	/**
	 * The number of quality indicators that must have similar or better performance in order to pass the test.
	 */
	public static final int THRESHOLD = 5;
	
	/**
	 * Call from any test to skip if JMetal does not exist.
	 */
	public void assumeJMetalExists() {
		Assume.assumeTrue("JMetal-Plugin required to run test", 
				AlgorithmFactory.getInstance().hasProvider("org.moeaframework.algorithm.jmetal.JMetalAlgorithms"));
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default {@link AlgorithmFactory} is used to create
	 * instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 */
	public void test(String problem, String algorithm1, String algorithm2) {
		test(problem, algorithm1, algorithm2, false);
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.  The default {@link AlgorithmFactory} is used to create
	 * instances.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework algorithm exceeds the performance
	 */
	public void test(String problem, String algorithm1, String algorithm2, boolean allowBetterPerformance) {
		test(problem, algorithm1, new TypedProperties(), algorithm2, new TypedProperties(), allowBetterPerformance,
				AlgorithmFactory.getInstance());
	}
	
	/**
	 * Tests if two algorithms are statistically indifferent.
	 * 
	 * @param problem the name of the problem to test
	 * @param algorithm1 the name of the first algorithm to test
	 * @param properties1 the properties used by the first algorithm to test
	 * @param algorithm2 the name of the second algorithm to test
	 * @param properties2 the properties used by the second algorithm to test
	 * @param allowBetterPerformance do not fail if the MOEA Framework algorithm exceeds the performance
	 * @param factory the factory used to construct the algorithms
	 */
	public void test(String problem, String algorithm1, TypedProperties properties1, String algorithm2,
			TypedProperties properties2, boolean allowBetterPerformance, AlgorithmFactory factory) {
		String algorithm1Name = algorithm1;
		String algorithm2Name = algorithm2;
		
		// if running the same algorithm with different settings, differentiate the names
		if (algorithm1Name.equalsIgnoreCase(algorithm2Name)) {
			algorithm1Name += "-" + properties1.hashCode();
			algorithm2Name += "-" + properties2.hashCode();
		}
		
		Analyzer analyzer = new Analyzer()
				.withProblem(problem)
				.includeAllMetrics()
				.showAggregate()
				.showStatisticalSignificance();
		
		Executor executor = new Executor()
				.withProblem(problem)
				.usingAlgorithmFactory(factory)
				.distributeOnAllCores();
		
		analyzer.addAll(algorithm1Name, 
				executor.withAlgorithm(algorithm1)
						.withProperties(properties1)
						.withMaxEvaluations(10000)
						.runSeeds(SEEDS));
		analyzer.addAll(algorithm2Name, 
				executor.withAlgorithm(algorithm2)
						.withProperties(properties2)
						.withMaxEvaluations(10000)
						.runSeeds(SEEDS));
		
		Analyzer.AnalyzerResults analyzerResults = analyzer.getAnalysis();
		
		if (Settings.isVerbose()) {
			analyzerResults.display();
		}
		
		int indifferences = 0;
		
		for (StandardIndicator indicator : analyzerResults.getIndicators()) {
			indifferences += analyzerResults.getSimilarAlgorithms(algorithm1Name, indicator).size();
		}
		
		if (indifferences < THRESHOLD) {
			if (allowBetterPerformance) {
				int outperformance = 0;
				
				for (StandardIndicator indicator : analyzerResults.getIndicators()) {
					double value1 = analyzerResults.getStatistics(algorithm1Name, indicator).getPercentile(50);
					double value2 = analyzerResults.getStatistics(algorithm2Name, indicator).getPercentile(50);
					
					if (indicator.areLargerValuesPreferred()) {
						if (value1 >= value2) {
							outperformance++;
						}
					} else {
						if (value1 <= value2) {
							outperformance++;
						}
					}
				}

				if (outperformance < THRESHOLD) {
					Assert.fail("algorithms show different performance");
				}
			} else {
				Assert.fail("algorithms show statistical difference");
			}
		}
	}

}
