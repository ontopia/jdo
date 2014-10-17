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
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Reifiable extends TMObject implements ReifiableIF {

	Reifiable(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public TopicIF getReifier() {
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.REIFIABLE_GET_REIFIER), topicmap, this);
	}

	@Override
	public void setReifier(TopicIF reifier) throws DuplicateReificationException {
		if (isReadOnly()) throw new ReadOnlyException();
		
		DuplicateReificationException.check(this, reifier);
		
		Topic current = (Topic) getReifier();
		
		if (current != null) {
			current.setReified(null);
		}

		if (reifier != null) {
			((Topic) reifier).setReified(this);
		}
	}

	@Override
	protected void beforeRemove() {
		DeletionUtils.removeDependencies(this);		
		super.beforeRemove();
	}
}
