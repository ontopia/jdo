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

package net.ontopia.topicmaps.impl.jdo;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import net.ontopia.infoset.core.LocatorIF;

@PersistenceCapable(table = "TM_SUBJECT_IDENTIFER")
@Inheritance(strategy = InheritanceStrategy.COMPLETE_TABLE)
public class SubjectIdentifier extends TopicLocator {
	private static final long serialVersionUID = 1L;

	public SubjectIdentifier() {
	}

	public SubjectIdentifier(LocatorIF locator, Topic topic) {
		super(locator, topic);
	}
}
