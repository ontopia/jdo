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
import java.util.HashSet;
import java.util.Set;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Scoped extends Reifiable implements ScopedIF {
	private static final Logger logger = LoggerFactory.getLogger(Scoped.class);
	
	@Persistent(table = "TM_SCOPES")
	@Join(column = "scoped")
	@Element(column = "scope")
	@Index(name = "TM_SCOPE_IX", members = {"scoped", "scope"})
	private Set<Topic> scope = new HashSet<>();

	Scoped(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public Collection<TopicIF> getScope() {
		return new HashSet<TopicIF>(scope);
	}

	@Override
	public void addTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (theme == null) throw new OntopiaNullPointerException("Scope cannot be null");
		Topic themeTopic = (Topic) theme;
		if (!scope.contains(themeTopic)) {
			scope.add(themeTopic);
			logger.trace("{} +scope {}", this, theme);
		}
	}

	@Override
	public void removeTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		if (theme == null) throw new OntopiaNullPointerException("Scope cannot be null");
		scope.remove((Topic) theme);
		logger.trace("{} -scope {}", this, theme);
	}

	@Override
	protected void beforeRemove() {
		super.beforeRemove();
		
		// disconnect scoping topics
		if (!isDeleted()) scope.clear();
	}
}
