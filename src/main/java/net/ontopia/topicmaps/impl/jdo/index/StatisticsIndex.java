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

package net.ontopia.topicmaps.impl.jdo.index;

import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.StatisticsIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

public class StatisticsIndex extends AbstractIndex implements StatisticsIndexIF, IndexIF {

	public StatisticsIndex(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public int getTopicCount() {
		return getCount(Queries.STATISTICSINDEX_TOPICS);
	}

	@Override
	public int getTypedTopicCount() {
		return getCount(Queries.STATISTICSINDEX_TOPIC_TYPES);
	}

	@Override
	public int getUntypedTopicCount() {
		return getCount(Queries.STATISTICSINDEX_UNTYPED_TOPICS);
	}

	@Override
	public int getTopicTypeCount() {
		return getCount(Queries.STATISTICSINDEX_TOPIC_TYPES);
	}

	@Override
	public int getAssociationCount() {
		return getCount(Queries.STATISTICSINDEX_ASSOCIATIONS);
	}

	@Override
	public int getAssociationTypeCount() {
		return getCount(Queries.STATISTICSINDEX_ASSOCIATION_TYPES);
	}

	@Override
	public int getRoleCount() {
		return getCount(Queries.STATISTICSINDEX_ROLES);
	}

	@Override
	public int getRoleTypeCount() {
		return getCount(Queries.STATISTICSINDEX_ROLE_TYPES);
	}

	@Override
	public int getOccurrenceCount() {
		return getCount(Queries.STATISTICSINDEX_OCCURRENCES);
	}

	@Override
	public int getOccurrenceTypeCount() {
		return getCount(Queries.STATISTICSINDEX_OCCURRENCE_TYPES);
	}

	@Override
	public int getTopicNameCount() {
		return getCount(Queries.STATISTICSINDEX_TOPIC_NAMES);
	}

	@Override
	public int getNoNameTopicCount() {
		// workaround bug in subquery: nonames = alltopics - namedtopics
		return getTopicCount() - getCount(Queries.STATISTICSINDEX_NO_NAMES);
	}

	@Override
	public int getTopicNameTypeCount() {
		return getCount(Queries.STATISTICSINDEX_TOPIC_NAME_TYPES);
	}

	@Override
	public int getVariantCount() {
		return getCount(Queries.STATISTICSINDEX_VARIANTS);
	}

	@Override
	public int getSubjectIdentifierCount() {
		return getCount(Queries.STATISTICSINDEX_SUBJECT_IDENTIFIERS);
	}

	@Override
	public int getSubjectLocatorCount() {
		return getCount(Queries.STATISTICSINDEX_SUBJECT_LOCATORS);
	}

	@Override
	public int getItemIdentifierCount() {
		return getCount(Queries.STATISTICSINDEX_ITEM_IDENTIFIERS);
	}
	
	private int getCount(String queryName) {
		Number count = JDOQueryUtils.singularResultQuery(getQuery(queryName), topicmap);
		return count.intValue();
	}
}
