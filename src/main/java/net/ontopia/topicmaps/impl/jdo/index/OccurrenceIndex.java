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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

public class OccurrenceIndex extends AbstractIndex implements OccurrenceIndexIF {

	public OccurrenceIndex(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(String value) {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES), topicmap, value);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype) {
		if (datatype == null) return Collections.emptySet();
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES_DATATYPE), topicmap, value, datatype.getAddress());
	}

	@Override
	public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix) {
		if (prefix == null) return Collections.emptySet();
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES_PREFIX), topicmap, prefix);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype) {
		if (datatype == null) return Collections.emptySet();
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES_PREFIX_DATATYPE), topicmap, prefix, datatype.getAddress());
	}

	@Override
	public Iterator<String> getValuesGreaterThanOrEqual(String value) {
		if (value == null) return Collections.<String>emptySet().iterator();
		return JDOQueryUtils.<String>queryToWrappedList(
				getQuery(Queries.OCCURRENCEINDEX_VALUES_GREATER_EQUAL), topicmap, value)
				.iterator();
	}

	@Override
	public Iterator<String> getValuesSmallerThanOrEqual(String value) {
		if (value == null) return Collections.<String>emptySet().iterator();
		return JDOQueryUtils.<String>queryToWrappedList(
				getQuery(Queries.OCCURRENCEINDEX_VALUES_SMALLER_EQUAL), topicmap, value)
				.iterator();
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(String value, TopicIF occurrenceType) {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES_TYPE), topicmap, value, occurrenceType);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype, TopicIF occurrenceType) {
		if (datatype == null) return Collections.emptySet();
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.OCCURRENCEINDEX_OCCURRENCES_DATATYPE_TYPE), topicmap, value, datatype.getAddress(), occurrenceType);
		
	}
}
