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

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;

@PersistenceCapable(table = "TM_IDENTIFIERS", identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.COMPLETE_TABLE)
public class IdentityLocator extends JDOLocator {
	private static final long serialVersionUID = 1L;
	
	public static final int ITEM_IDENTIFIER = 0;
	public static final int SUBJECT_IDENTIFIER = 1;
	
	@Persistent
	private int type;

	public IdentityLocator() {
	}

	public IdentityLocator(LocatorIF locator, TMObject object, int type) {
		super(locator, object);
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public boolean isItemIdentifier() {
		return type == ITEM_IDENTIFIER;
	}
	
	public boolean isSubjectIdentifier() {
		return type == SUBJECT_IDENTIFIER;
	}
}
