/*******************************************************************************
 * Copyright (c) 2014-2015 TELECOM ParisTech and AdaCore.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Elie Richa - initial implementation
 *******************************************************************************/
package fr.tpt.atlanalyser.post2pre.ncvalidators;

import org.eclipse.emf.henshin.model.NestedCondition;

public interface NestedConditionValidator {

    public static NestedConditionValidator TRUE = new NestedConditionValidator() {

                                                    @Override
                                                    public boolean isValid(
                                                            NestedCondition nc) {
                                                        return true;
                                                    }

                                                    @Override
                                                    public String toString() {
                                                        return "TRUE";
                                                    };
                                                };

    public boolean isValid(NestedCondition nc);

}
