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

import java.util.Collection;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class TMObject extends JDOObject implements TMObjectIF {
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.INCREMENT)
	protected long id;

	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	@NotPersistent // todo
	protected Collection<LocatorIF> itemIdentifiers;

	TMObject() {
	}

	TMObject(TopicMap topicmap) {
		this.topicmap = topicmap;
	}
	
	protected abstract String getClassIndicator();
	
	public String getObjectId() {
		return getClassIndicator() + id;
	}

	// todo: actually implement on setters
	public boolean isReadOnly() {
		return getTopicMap().getStore().isReadOnly();
	}

	public TopicMapIF getTopicMap() {
		return topicmap;
	}

	public Collection<LocatorIF> getItemIdentifiers() {
		return itemIdentifiers;
	}

	public void addItemIdentifier(LocatorIF item_identifier) throws ConstraintViolationException {
		if (isReadOnly()) throw new ReadOnlyException();
		itemIdentifiers.add(item_identifier);
	}

	public void removeItemIdentifier(LocatorIF item_identifier) {
		if (isReadOnly()) throw new ReadOnlyException();
		itemIdentifiers.remove(item_identifier);
	}

	public void remove() {
		if (isReadOnly()) throw new ReadOnlyException();
		// getTransaction.deletePersistent(this);
	}
	
	/* JDO specific */
	public long getLongId() {
		return id;
	}
}
