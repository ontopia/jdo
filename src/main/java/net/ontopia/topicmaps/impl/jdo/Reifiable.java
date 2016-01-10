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

import javax.jdo.Query;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Reifiable extends TMObject implements ReifiableIF {
	private static final Logger logger = LoggerFactory.getLogger(Reifiable.class);
	
	// bug: compile fails on this query
	private static Query GET_REIFIER = null;

	Reifiable(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public TopicIF getReifier() {
		if (GET_REIFIER == null) {
			GET_REIFIER = getPersistenceManager().newQuery(Topic.class, "topicmap == :tm && reified == :o");
		}
		return JDOQueryUtils.singularResultQuery(getPersistenceManager().newQuery(GET_REIFIER), topicmap, this);
	}

	@Override
	public void setReifier(TopicIF reifier) throws DuplicateReificationException {
		if (isDeleted()) return;
		if (isReadOnly()) throw new ReadOnlyException();
		logger.trace("{} +reifier {}", this, reifier);

		Topic current = (Topic) getReifier();

		// check for no-op case
		if (!ObjectUtils.different(current, reifier)) return;
		
		// unset
		if (current != null) {
			current.setReified(null);
		}
		
		// set
		if (!DuplicateReificationException.check(this, reifier)) {
			if (reifier != null) {
				((Topic) reifier).setReified(this);
			}
		}
	}
	
	/*
		Might no longer be needed:
	
		// Partial copy from DuplicateReificationException.check() due to #490
		// refactor when #409 is resolved
		// base: 3805eff9d0c3d256e367821072238122f06ffb31
		if (reifier != null) {
			ReifiableIF existingReified = reifier.getReified();
			if (existingReified != null && ObjectUtils.different(existingReified, this)) {
				String key1 = KeyGenerator.makeKey(this);
				String key2 = KeyGenerator.makeKey(existingReified);
				if (!key1.equals(key2)) {
					throw new DuplicateReificationException("The topic " + reifier
							+ " cannot reify more than one reifiable object. 1: " + existingReified
							+ " 2: " + this);
				}
				MergeUtils.mergeInto(this, existingReified);
			}
			((Topic) reifier).setReified(this);
		}
	*/
}
