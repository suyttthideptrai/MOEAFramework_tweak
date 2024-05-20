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
package org.moeaframework.core.comparator;

import org.moeaframework.core.FitnessEvaluator;

/**
 * Compares two solutions based on their fitness value.
 * 
 * @see FitnessEvaluator
 */
public class FitnessComparator extends AbstractNumericComparator<Double> {

	/**
	 * Constructs a dominance comparator for comparing solutions based on their fitness value.
	 * 
	 * @param largerValuesPreferred {@code true} if larger fitness values are preferred; otherwise smaller fitness
	 *        values are preferred
	 */
	public FitnessComparator(boolean largerValuesPreferred) {
		super(largerValuesPreferred, FitnessEvaluator::getFitness);
	}

}
