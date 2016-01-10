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

import java.io.Reader;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.UniquenessViolationException;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistenceCapable(table = "TM_TOPIC")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Index(name = "TM_TOPIC_IX_ID_TOPICMAP", members = {"id", "topicmap"})
public class Topic extends TMObject implements TopicIF {
	private static final Logger logger = LoggerFactory.getLogger(Topic.class);
	
	@Persistent(mappedBy = "topic", dependentElement = "true")
	private Set<SubjectLocator> subjectLocators = new HashSet<SubjectLocator>();

	@Persistent(mappedBy = "topic", dependentElement = "true")
	private Set<SubjectIdentifier> subjectIdentifiers = new HashSet<SubjectIdentifier>();

	@Persistent(table = "TM_TOPIC_TYPES")
	@Join(column = "topic")
	@Element(column = "type")
	@Index(name = "TM_TOPIC_TYPE_IX", members = {"topic", "type"})
	private Set<Topic> types = new HashSet<Topic>(1);

	@Persistent(mappedBy = "topic", dependentElement = "true")
	private Set<TopicName> topicNames = new HashSet<TopicName>();

	@Persistent(mappedBy = "topic", dependentElement = "true")
	private Set<Occurrence> occurrences = new HashSet<Occurrence>();
	
	@Persistent(mappedBy = "player", dependentElement = "true")
	private Set<AssociationRole> roles = new HashSet<AssociationRole>();
	
	@Persistent(name = "reified", column = "reified")
	private Reifiable reified = null;

	private static enum TopicQuery {
		NAMES_BY_TYPE(TopicName.class, 
			"topic == t && type == nt && topicmap == tm", 
			"Topic t, Topic nt, TopicMap tm"),
		OCCURRENCES_BY_TYPE(Occurrence.class, 
			"topic == t && topicmap == tm && type == ot", 
			"Topic t, Topic ot, TopicMap tm"),
		ROLES_BY_TYPE1(AssociationRole.class, 
			"player == t && topicmap == tm && type == rt",
			"Topic t, Topic rt, TopicMap tm"),
		ROLES_BY_TYPE2(AssociationRole.class, 
			"player == t && topicmap == tm && type == rt && association.type == at",
			"Topic t, Topic rt, Topic at, TopicMap tm"),
		ASSOCIATIONS(AssociationRole.class, 
			"player == t && topicmap == tm", 
			"Topic t, TopicMap tm") {
			@Override
			protected void extend(Query q) {
				q.setResult("distinct association");
			}
		},
		ASSOCIATIONS_BY_TYPE(AssociationRole.class,
			"player == t && topicmap == tm && association.type == at", 
			"Topic t, Topic at, TopicMap tm") {
			@Override
			protected void extend(Query q) {
				q.setResult("distinct association");
			}
		};

		private final Class<?> klass;
		private final String filter;
		private final String parameters;
		private static final EnumMap<TopicQuery, Query> queryCache = 
				new EnumMap<TopicQuery, Query>(TopicQuery.class);
		
		private TopicQuery(Class<?> klazz, String filter, String parameters) {
			this.filter = filter;
			this.parameters = parameters;
			this.klass = klazz;
		}
		
		Query get(PersistenceManager pm) {
			Query q = queryCache.get(this);
			if (q == null) {
				q = pm.newQuery(klass, filter);
				q.declareParameters(parameters);
				extend(q);
				q.compile();
				queryCache.put(this, q);
				return q;
			} else {
				return pm.newQuery(q);
			}
		}
		
		protected void extend(Query q) { }
	}
	
	Topic(TopicMap topicmap) {
		super(topicmap);
	}
	
	@Override
	protected String getClassIndicator() {
		return "T";
	}

	@Override
	public Collection<LocatorIF> getSubjectLocators() {
		return new HashSet<LocatorIF>(subjectLocators);
	}

	@Override
	public void addSubjectLocator(LocatorIF lif) throws ConstraintViolationException {
		if (isDeleted()) throw new ConstraintViolationException("Cannot modify subject locator when topic isn't attached to a topic map.");
		if (isReadOnly()) throw new ReadOnlyException();
		if (lif == null) throw new NullPointerException("Subject locator cannot be null");
		
		if (getSubjectLocators().contains(lif)) {
			return;
		}
		
		try {
			SubjectLocator subjectLocator = new SubjectLocator(lif, this);
			subjectLocators.add(subjectLocator);
		} catch (JDOException | IllegalArgumentException re) {
			throw new UniquenessViolationException("Subject locator " + lif + " is already identifying another topic: " 
					+ topicmap.getTopicBySubjectLocator(lif));
		}
	}

	@Override
	public void removeSubjectLocator(LocatorIF lif) {
		if (isReadOnly()) throw new ReadOnlyException();
		logger.trace("{} --SL {} {}", new Object[] {this, lif, lif.getClass().getSimpleName()});
		removeLocator(subjectLocators, lif);
	}

	@Override
	public Collection<LocatorIF> getSubjectIdentifiers() {
		return new HashSet<LocatorIF>(subjectIdentifiers);
	}

	@Override
	public void addSubjectIdentifier(LocatorIF lif) throws ConstraintViolationException {
		if (isDeleted()) throw new ConstraintViolationException("Cannot modify subject identifier when topic isn't attached to a topic map.");
		if (isReadOnly()) throw new ReadOnlyException();
		if (lif == null) throw new NullPointerException("Subject identifier cannot be null");

		// TMDM constraint: SI cannot be II of another object
		TMObjectIF existing = topicmap.getObjectByItemIdentifier(lif);
		if ((existing != null) && (existing != this) && (existing instanceof TopicIF)) {
			throw new UniquenessViolationException("Another topic " + existing + " already has this subject identifier as its item identifier: " + lif + " (" + this + ")");
		}

		if (getSubjectIdentifiers().contains(lif)) {
			return;
		}
		
		logger.trace("{} ++SI {} {}", new Object[] {this, lif, lif.getClass().getSimpleName()});
		try {
			SubjectIdentifier subjectIdentity = new SubjectIdentifier(lif, this);
			subjectIdentifiers.add(subjectIdentity);
		} catch (JDOException | IllegalArgumentException re) {
			throw new UniquenessViolationException("Subject identifier " + lif + " is already identifying another object: " 
					+ topicmap.getTopicBySubjectIdentifier(lif), re);
		}
	}

	@Override
	public void removeSubjectIdentifier(LocatorIF lif) {
		if (isReadOnly()) throw new ReadOnlyException();
		logger.trace("{} --SI {} {}", new Object[] {this, lif, lif.getClass().getSimpleName()});
		removeLocator(subjectIdentifiers, lif);
	}

	@Override
	public Collection<TopicIF> getTypes() {
		return new HashSet<TopicIF>(types);
	}

	@Override
	public void addType(TopicIF tif) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo: class check
		logger.trace("{} +type {}", this, tif);
		types.add((Topic) tif);
	}

	@Override
	public void removeType(TopicIF tif) {
		if (isReadOnly()) throw new ReadOnlyException();
		// todo: class check
		logger.trace("{} -type {}", this, tif);
		types.remove((Topic) tif);
	}

	@Override
	public Collection<TopicNameIF> getTopicNames() {
		return new HashSet<TopicNameIF>(topicNames);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<TopicNameIF> getTopicNamesByType(TopicIF nt) {
		if (nt == null) throw new NullPointerException("Topic name type cannot be null");
		return (Collection) TopicQuery.NAMES_BY_TYPE.get(getPersistenceManager()).executeWithArray(this, nt, getTopicMap());
	}

	@Override
	public Collection<OccurrenceIF> getOccurrences() {
		return new HashSet<OccurrenceIF>(occurrences);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<OccurrenceIF> getOccurrencesByType(TopicIF ot) {
		if (ot == null) throw new NullPointerException("Occurrence type cannot be null");
		return (Collection) TopicQuery.OCCURRENCES_BY_TYPE.get(getPersistenceManager()).executeWithArray(this, ot, getTopicMap());
	}

	@Override
	public Collection<AssociationRoleIF> getRoles() {
		return new HashSet<AssociationRoleIF>(roles);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<AssociationRoleIF> getRolesByType(TopicIF rt) {
		if (rt == null) throw new NullPointerException("Association role type cannot be null");
		return (Collection) TopicQuery.ROLES_BY_TYPE1.get(getPersistenceManager()).executeWithArray(this, rt, getTopicMap());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<AssociationRoleIF> getRolesByType(TopicIF rt, TopicIF at) {
		if (rt == null) throw new NullPointerException("Association role type cannot be null");
		if (at == null) throw new NullPointerException("Association type cannot be null");
		return (Collection) TopicQuery.ROLES_BY_TYPE2.get(getPersistenceManager()).executeWithArray(this, rt, at, getTopicMap());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<AssociationIF> getAssociations() {
		return (Collection) TopicQuery.ASSOCIATIONS.get(getPersistenceManager()).executeWithArray(this, getTopicMap());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<AssociationIF> getAssociationsByType(TopicIF at) {
		if (at == null) throw new NullPointerException("Association type cannot be null");
		return (Collection) TopicQuery.ASSOCIATIONS_BY_TYPE.get(getPersistenceManager()).executeWithArray(this, at, getTopicMap());
	}

	@Override
	public void merge(TopicIF tif) {
		CrossTopicMapException.check(tif, this);
		logger.trace("{} MERGE {}", this, tif);
		MergeUtils.mergeInto(this, tif);
	}

	@Override
	public ReifiableIF getReified() {
		return reified;
	}
	
	/**
	 * INTERNAL: sets the reified field for bidirectional 1-1 persistence
	 * @param reified The object that this topic is reifying 
	 */
	void setReified(Reifiable reified) {
		logger.trace("{} +reified {}", this, reified);
		this.reified = reified;
	}

	@Override
	protected void beforeRemove() {
		// unset types to avoid indirect circulation
		types.clear();
		
		DeletionUtils.removeDependencies(this);
		topicmap.removeTopic(this);
	}

	// builder methods
	public TopicNameIF makeTopicName(TopicIF bntype, String value) {
		if (bntype == null) throw new NullPointerException("Name type cannot be null");
		if (value == null) throw new NullPointerException("Value cannot be null");
		TopicName name = new TopicName(this, (Topic) bntype, value);
		getPersistenceManager().makePersistent(name);
		topicNames.add(name);
		logger.trace("{} +name {}", this, name);
		return name;
	}

	public OccurrenceIF makeOccurrence(TopicIF occurs_type, Reader value, long length, LocatorIF datatype) {
		Occurrence occurrence = new Occurrence(this,
				JDOTopicMapBuilder.checkAndCast(occurs_type, "Occurrence type", Topic.class));
		occurrence.setReader(value, length, datatype);
		getPersistenceManager().makePersistent(occurrence);
		occurrences.add(occurrence);
		logger.trace("{} +occ {}", this, occurrence);
		return occurrence;
	}

	private OccurrenceIF makeOccurrence(TopicIF occurs_type) {
		Occurrence occurrence = new Occurrence(this, JDOTopicMapBuilder.checkAndCast(occurs_type, "Occurrence type", Topic.class));
		getPersistenceManager().makePersistent(occurrence);
		occurrences.add(occurrence);
		logger.trace("{} +occ {}", this, occurrence);
		return occurrence;
	}
	
	public OccurrenceIF makeOccurrence(TopicIF occurs_type, LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Locator cannot be null");
		OccurrenceIF occurrence = makeOccurrence(occurs_type);
		occurrence.setLocator(locator);
		logger.trace("{} +occ {}", this, occurrence);
		return occurrence;
	}

	public OccurrenceIF makeOccurrence(TopicIF occurs_type, String value) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		OccurrenceIF occurrence = makeOccurrence(occurs_type);
		occurrence.setValue(value);
		logger.trace("{} +occ {}", this, occurrence);
		return occurrence;
	}

	public OccurrenceIF makeOccurrence(TopicIF occurs_type, String value, LocatorIF datatype) {
		if (value == null) throw new NullPointerException("Value cannot be null");
		if (datatype == null) throw new NullPointerException("Datatype cannot be null");
		OccurrenceIF occurrence = makeOccurrence(occurs_type);
		occurrence.setValue(value, datatype);
		logger.trace("{} +occ {}", this, occurrence);
		return occurrence;
	}
	
	void associationRoleCreated(AssociationRole role) {
		logger.trace("{} +role {}", this, role);
		roles.add(role);
	}
}
