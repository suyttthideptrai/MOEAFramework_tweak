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
package org.moeaframework.examples.extensions;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.continuation.AdaptiveTimeContinuationExtension;
import org.moeaframework.algorithm.extension.LoggingExtension;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates adding adaptive time continuation, whereby the population is periodically resized and injected with
 * random solutions to avoid getting stuck at local optima.  Note that the algorithm must have an archive set to use
 * adaptive time continuation.
 */
public class AdaptiveTimeContinuationExample {
	
	public static void main(String[] args) throws IOException {
		NSGAII algorithm = new NSGAII(new DTLZ2(2));
		algorithm.setArchive(new EpsilonBoxDominanceArchive(0.01));
		
		algorithm.addExtension(new LoggingExtension());
		algorithm.addExtension(new AdaptiveTimeContinuationExtension());
		
		algorithm.run(100000);
		algorithm.getResult().display();
	}

}
