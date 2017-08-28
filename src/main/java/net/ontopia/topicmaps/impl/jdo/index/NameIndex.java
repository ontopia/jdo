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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

public class NameIndex extends AbstractIndex implements NameIndexIF {

	public NameIndex(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public Collection<TopicNameIF> getTopicNames(String value) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.NAMEINDEX_TOPIC_NAMES), topicmap, value);
	}

	@Override
	public Collection<VariantNameIF> getVariants(String value) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.NAMEINDEX_VARIANT_NAMES), topicmap, value);
	}

	@Override
	public Collection<VariantNameIF> getVariants(String value, LocatorIF datatype) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		if (datatype == null) throw new NullPointerException("Datatype cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.NAMEINDEX_VARIANT_NAMES_DATATYPE), topicmap, value, datatype.getAddress());
	}

	@Override
	public Collection<TopicNameIF> getTopicNames(String value, TopicIF topicNameType) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		if (topicNameType == null) throw new NullPointerException("TopicNameType cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.NAMEINDEX_TOPIC_NAMES_TYPE), topicmap, value, topicNameType);
	}
}
