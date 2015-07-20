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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import fr.tpt.atlanalyser.atl.ATL.LocatedElement;

public class ErrorReporting {

    public static void error(Logger logger, LocatedElement elem, String fmt,
            Object... args) {
        if (elem != null && elem.getLocation() != null) {
            fmt = elem.getLocation() + ": " + fmt;
        }

        Message logMessage = logger.getMessageFactory().newMessage(fmt, args);
        String msg = logMessage.getFormattedMessage();
        logger.error(logMessage);

        throw new UnsupportedOperationException(msg);
    }

}
