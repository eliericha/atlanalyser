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
package fr.tpt.atlanalyser.utils;

import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.ModelElement;

public class HenshinUtils {

    public static void annotate(ModelElement obj, String key, String value) {
        Annotation annotation = HenshinFactory.eINSTANCE.createAnnotation();
        annotation.setKey(key);
        annotation.setValue(value);
        obj.getAnnotations().add(annotation);
    }

}
