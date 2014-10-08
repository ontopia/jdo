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
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;

@PersistenceCapable(table = "TM_TOPIC")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Index(name = "TM_TOPIC_IX_ID_TOPICMAP", members = {"id", "topicmap"})
public class Topic extends TMObject implements TopicIF {
	@NotPersistent // todo
	private Collection<LocatorIF> subjectLocators;
	@NotPersistent // todo
	private Collection<LocatorIF> subjectIdentifiers;

	@Persistent(table = "TM_TOPIC_TYPES")
	@Join(column = "topic")
	@Element(column = "type")
	@Index(name = "TM_TOPIC_TYPE_IX", members = {"topic", "type"})
	private Collection<Topic> types;

	@Persistent(mappedBy = "topic")
	private Collection<TopicName> topicNames;

	@Persistent(mappedBy = "topic")
	private Collection<Occurrence> occurrences;
	
	@Persistent(mappedBy = "player")
	private Collection<AssociationRole> roles;
	
	@Persistent(mappedBy = "reifier")
	private ReifiableIF reified = null;

	Topic(TopicMap topicmap) {
		super(topicmap);
	}
	
	@Override
	protected String getClassIndicator() {
		return "T";
	}

	public Collection<LocatorIF> getSubjectLocators() {
		return subjectLocators;
	}

	public void addSubjectLocator(LocatorIF lif) throws ConstraintViolationException {
		if (isReadOnly()) throw new ReadOnlyException();
		subjectLocators.add(lif);
	}

	public void removeSubjectLocator(LocatorIF lif) {
		if (isReadOnly()) throw new ReadOnlyException();
		subjectLocators.remove(lif);
	}

	public Collection<LocatorIF> getSubjectIdentifiers() {
		return subjectIdentifiers;
	}

	public void addSubjectIdentifier(LocatorIF lif) throws ConstraintViolationException {
		if (isReadOnly()) throw new ReadOnlyException();
		subjectIdentifiers.add(lif);
	}

	public void removeSubjectIdentifier(LocatorIF lif) {
		if (isReadOnly()) throw new ReadOnlyException();
		subjectIdentifiers.remove(lif);
	}

	public Collection<TopicIF> getTypes() {
		return new HashSet<TopicIF>(types);
	}

	public void addType(TopicIF tif) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo: class check
		types.add((Topic) tif);
	}

	public void removeType(TopicIF tif) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo: class check
		types.remove((Topic) tif);
	}

	public Collection<TopicNameIF> getTopicNames() {
		return new HashSet<TopicNameIF>(topicNames);
	}

	public Collection<TopicNameIF> getTopicNamesByType(TopicIF tif) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<OccurrenceIF> getOccurrences() {
		return new HashSet<OccurrenceIF>(occurrences);
	}

	public Collection<OccurrenceIF> getOccurrencesByType(TopicIF tif) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<AssociationRoleIF> getRoles() {
		return new HashSet<AssociationRoleIF>(roles);
	}

	public Collection<AssociationRoleIF> getRolesByType(TopicIF tif) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<AssociationRoleIF> getRolesByType(TopicIF tif, TopicIF tif1) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<AssociationIF> getAssociations() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public Collection<AssociationIF> getAssociationsByType(TopicIF tif) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void merge(TopicIF tif) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo
	}

	public ReifiableIF getReified() {
		return reified;
	}
}
