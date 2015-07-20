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
package fr.tpt.atlanalyser;

public class ATLAnalyserRuntimeException extends RuntimeException {

    public ATLAnalyserRuntimeException(String msg) {
        super(msg);
    }

    public ATLAnalyserRuntimeException(String fmt, Object... objs) {
        super(String.format(fmt, objs));
    }

    public ATLAnalyserRuntimeException(Throwable cause) {
        super(cause);
    }

    public ATLAnalyserRuntimeException() {
        super();
    }

    private static final long serialVersionUID = 1L;

}
