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
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

public class ScopeIndex extends AbstractIndex implements ScopeIndexIF {

	public ScopeIndex(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	public Collection<TopicNameIF> getTopicNames(TopicIF scope) {
		if (scope == null) throw new NullPointerException("Scope cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_SCOPED_TOPIC_NAMES), topicmap, scope);
	}

	@Override
	public Collection<VariantNameIF> getVariants(TopicIF scope) {
		if (scope == null) throw new NullPointerException("Scope cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_SCOPED_VARIANT_NAMES), topicmap, scope);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(TopicIF scope) {
		if (scope == null) throw new NullPointerException("Scope cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_SCOPED_OCCURRENCES), topicmap, scope);
	}

	@Override
	public Collection<AssociationIF> getAssociations(TopicIF scope) {
		if (scope == null) throw new NullPointerException("Scope cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_SCOPED_ASSOCIATIONS), topicmap, scope);
	}

	@Override
	public Collection<TopicIF> getTopicNameThemes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_TOPIC_NAME_SCOPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getVariantThemes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_VARIANT_NAME_SCOPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getOccurrenceThemes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_OCCURRENCE_SCOPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getAssociationThemes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.SCOPEINDEX_ASSOCIATION_SCOPES), topicmap);
	}

	@Override
	public boolean usedAsTopicNameTheme(TopicIF topic) {
		if (topic == null) return false;
		return getTopicNameThemes().contains(topic); // todo: or query?
	}

	@Override
	public boolean usedAsVariantTheme(TopicIF topic) {
		if (topic == null) return false;
		return getVariantThemes().contains(topic); // todo: or query?
	}

	@Override
	public boolean usedAsOccurrenceTheme(TopicIF topic) {
		if (topic == null) return false;
		return getOccurrenceThemes().contains(topic); // todo: or query?
	}

	@Override
	public boolean usedAsAssociationTheme(TopicIF topic) {
		if (topic == null) return false;
		return getAssociationThemes().contains(topic); // todo: or query?
	}

	@Override
	public boolean usedAsTheme(TopicIF topic) {
		if (topic == null) return false;
		return usedAsTopicNameTheme(topic) ||
				usedAsVariantTheme(topic) ||
				usedAsOccurrenceTheme(topic) ||
				usedAsAssociationTheme(topic);
		// todo: or query?
	}
}
