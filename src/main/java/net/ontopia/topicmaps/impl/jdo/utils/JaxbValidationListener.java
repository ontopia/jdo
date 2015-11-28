/*
 * #!
 * Ontopia JDO
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package net.ontopia.topicmaps.impl.jdo.utils;

import java.net.URL;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import org.slf4j.Logger;
import org.w3c.dom.Node;

/**
 * Jaxb ValidationEventHandler that logs to the provided slf4j logger. The default jaxb event 
 * handler logs to the System.out, the code of this class is based on that default behavior, 
 * except that it now logs to slf4j. Note that errors (normal and fatal) are not logged by 
 * default because this handler is fail-fast and causes exceptions with the same messages. The 
 * calling class can decide to log the error or not.
 */
public class JaxbValidationListener implements ValidationEventHandler {

	private final Logger logger;

	private boolean logErrors = false;
	private boolean logWarnings = true;

	public JaxbValidationListener(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean handleEvent(ValidationEvent event) {

		String location = getLocation(event);

		// calculate the severity prefix and return value
		switch (event.getSeverity()) {
			case ValidationEvent.WARNING:
				if (logWarnings) {
					logger.warn(event.getMessage() + " " + location);
				}
				return true;
			case ValidationEvent.ERROR:
				if (logErrors) {
					logger.error(event.getMessage() + " " + location);
				}
				return false;
			case ValidationEvent.FATAL_ERROR:
				if (logErrors) {
					logger.error("Fatal: " + event.getMessage() + " " + location);
				}
				return false;
			default:
				throw new IllegalStateException();
		}
	}

	private String getLocation(ValidationEvent event) {
		StringBuilder msg = new StringBuilder();

		ValidationEventLocator locator = event.getLocator();
		if (locator != null) {
			URL url = locator.getURL();
			Object obj = locator.getObject();
			Node node = locator.getNode();
			int line = locator.getLineNumber();

			if (url != null || line != -1) {
				msg.append("line ").append(line);
				if (url != null) {
					msg.append(" of ").append(url);
				}
			} else if (obj != null) {
				msg.append(" obj: ").append(obj.toString());
			} else if (node != null) {
				msg.append(" node: ").append(node.toString());
			}
		} else {
			msg.append("unknown location");
		}

		return msg.toString();
	}

	public void setLogErrors(boolean logErrors) {
		this.logErrors = logErrors;
	}

	public void setLogWarnings(boolean logWarnings) {
		this.logWarnings = logWarnings;
	}
}
