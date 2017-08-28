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
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.utils.JDOQueryUtils;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;
import net.ontopia.topicmaps.utils.PSI;

public class ClassInstanceIndex extends AbstractIndex implements ClassInstanceIndexIF {

	public ClassInstanceIndex(TopicMap topicmap) {
		super(topicmap);
	}
	
	@Override
	public Collection<TopicIF> getTopics(TopicIF topic_type) {
		if (topic_type == null) {
			return JDOQueryUtils.queryToWrappedSet(
					getQuery(Queries.CLASSINSTANCEINDEX_UNTYPED_TOPICS), topicmap);
		} else {
			return JDOQueryUtils.queryToWrappedSet(
					getQuery(Queries.CLASSINSTANCEINDEX_TOPICS_BY_TYPE), topicmap, topic_type);
		}
	}

	@Override
	public Collection<TopicNameIF> getTopicNames(TopicIF name_type) {
		if (name_type == null) {
			name_type = topicmap.getTopicBySubjectIdentifier(PSI.getSAMNameType());
		}
		if (name_type == null) throw new NullPointerException("Name type was null and could not find default name type topic");
		
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_NAMES_BY_TYPE), topicmap, name_type);
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences(TopicIF occurrence_type) {
		if (occurrence_type == null) throw new NullPointerException("Occurrence type cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_OCCURRENCES_BY_TYPE), topicmap, occurrence_type);
	}

	@Override
	public Collection<AssociationIF> getAssociations(TopicIF association_type) {
		if (association_type == null) throw new NullPointerException("Association type cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_ASSOCIATIONS_BY_TYPE), topicmap, association_type);
	}

	@Override
	public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type) {
		if (association_role_type == null) throw new NullPointerException("Association role type cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_ROLES_BY_TYPE), topicmap, association_role_type);
	}

	@Override
	public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type, TopicIF association_type) {
		if (association_role_type == null) throw new NullPointerException("Association role type cannot be null");
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_ROLES_BY_TYPE_AND_ROLE_TYPE), topicmap, association_role_type, association_type);
	}

	@Override
	public Collection<TopicIF> getTopicTypes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_TOPIC_TYPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getTopicNameTypes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_TOPIC_NAME_TYPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getOccurrenceTypes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_OCCURRENCE_TYPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getAssociationTypes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_ASSOCIATION_TYPES), topicmap);
	}

	@Override
	public Collection<TopicIF> getAssociationRoleTypes() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_ASSOCIATION_ROLE_TYPES), topicmap);
	}

	@Override
	public boolean usedAsTopicType(TopicIF topic) {
		if (topic == null) throw new NullPointerException("Topic cannot be null");
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.CLASSINSTANCEINDEX_USED_AS_TOPIC_TYPE), topicmap, topic)
				 != null;
	}

	@Override
	public boolean usedAsTopicNameType(TopicIF topic) {
		if (topic == null) throw new NullPointerException("Topic cannot be null");
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.CLASSINSTANCEINDEX_USED_AS_TOPIC_NAME_TYPE), topicmap, topic)
				 != null;
	}

	@Override
	public boolean usedAsOccurrenceType(TopicIF topic) {
		if (topic == null) throw new NullPointerException("Topic cannot be null");
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.CLASSINSTANCEINDEX_USED_AS_OCCURRENCE_TYPE), topicmap, topic)
				 != null;
	}

	@Override
	public boolean usedAsAssociationType(TopicIF topic) {
		if (topic == null) throw new NullPointerException("Topic cannot be null");
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.CLASSINSTANCEINDEX_USED_AS_ASSOCIATION_TYPE), topicmap, topic)
				 != null;
	}

	@Override
	public boolean usedAsAssociationRoleType(TopicIF topic) {
		if (topic == null) throw new NullPointerException("Topic cannot be null");
		return JDOQueryUtils.singularResultQuery(
				getQuery(Queries.CLASSINSTANCEINDEX_USED_AS_ASSOCIATION_ROLE_TYPE), topicmap, topic)
				 != null;
	}

	@Override
	public boolean usedAsType(TopicIF topic) {
		return usedAsTopicType(topic) ||
				usedAsAssociationType(topic) ||
				usedAsOccurrenceType(topic) ||
				usedAsAssociationRoleType(topic) ||
				usedAsTopicNameType(topic);
	}

	@Override
	public Collection<TopicNameIF> getAllTopicNames() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_All_NAMES), topicmap);
	}

	@Override
	public Collection<VariantNameIF> getAllVariantNames() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_All_VARIANTS), topicmap);
	}

	@Override
	public Collection<OccurrenceIF> getAllOccurrences() {
		return JDOQueryUtils.queryToWrappedSet(
				getQuery(Queries.CLASSINSTANCEINDEX_All_OCCURRENCES), topicmap);
	}
}
