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
package org.moeaframework.core.operator;

import org.moeaframework.core.Solution;

/**
 * Versions of {@link CompoundVariation} that only supports mutation operators.
 */
public class CompoundMutation extends AbstractCompoundVariation<Mutation> implements Mutation {

	/**
	 * Constructs a compound mutation operator with the specified operators.
	 * 
	 * @param operators the mutation operators in the order they are applied
	 */
	public CompoundMutation(Mutation... operators) {
		super();

		for (Mutation operator : operators) {
			appendOperator(operator);
		}
	}
	
	@Override
	public Solution mutate(Solution parent) {
		Solution result = parent.copy();

		for (Mutation operator : operators) {
			result = operator.mutate(result);
		}

		return result;
	}

}