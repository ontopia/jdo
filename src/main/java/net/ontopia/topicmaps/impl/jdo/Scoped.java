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

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Scoped extends Typed implements ScopedIF {
	
	@Persistent(table = "TM_SCOPES")
	@Join(column = "scoped")
	@Element(column = "scope")
	@Index(name = "TM_SCOPE_IX", members = {"scoped", "scope"})
	private Set<Topic> scope = new HashSet<Topic>();

	Scoped(Topic type) {
		super(type);
	}

	public Collection<TopicIF> getScope() {
		return new HashSet<TopicIF>(scope);
	}

	public void addTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		Topic themeTopic = (Topic) theme;
		if (!scope.contains(themeTopic)) {
			scope.add(themeTopic);
		}
	}

	public void removeTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		scope.remove((Topic) theme);
	}

	@Override
	protected void beforeRemove() {
		
		// disconnect scoping topics
		scope.clear();
		
		// let super cleanuo
		super.beforeRemove();
	}
}
