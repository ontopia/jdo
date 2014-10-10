
package net.ontopia.topicmaps.impl.jdo;

import java.io.Reader;
import java.util.Collection;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.OntopiaRuntimeException;

public class JDOTopicMapBuilder implements TopicMapBuilderIF {
	private final TopicMap topicmap;

	public JDOTopicMapBuilder(TopicMap topicmap) {
		if (topicmap.isReadOnly()) throw new ReadOnlyException();
		this.topicmap = topicmap;
	}
	
	public TopicMapIF getTopicMap() {
		return topicmap;
	}

	public TopicIF makeTopic() {
		return topicmap.makeTopic();
	}

	public TopicIF makeTopic(TopicIF topic_type) {
		return topicmap.makeTopic(topic_type);
	}

	public TopicIF makeTopic(Collection<TopicIF> topic_types) {
		return topicmap.makeTopic(topic_types);
	}

	public TopicNameIF makeTopicName(TopicIF topic, String value) {
		return makeTopicName(topic, getDefaultNameType(), value);
	}
	
	public TopicNameIF makeTopicName(TopicIF topic, TopicIF bntype, String value) {
		return checkAndCast(topic, "Topic", Topic.class).makeTopicName(bntype, value);
	}

	private Topic getDefaultNameType() {
		return null; // todo
	}

	public VariantNameIF makeVariantName(TopicNameIF name, String value, Collection<TopicIF> scope) {
		return checkAndCast(name, "Name", TopicName.class).makeVariantName(value, scope);
	}

	public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator, Collection<TopicIF> scope) {
		return checkAndCast(name, "Name", TopicName.class).makeVariantName(locator, scope);
	}

	public VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype, Collection<TopicIF> scope) {
		return checkAndCast(name, "Name", TopicName.class).makeVariantName(value, datatype, scope);
	}

	public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype, Collection<TopicIF> scope) {
		return checkAndCast(name, "Name", TopicName.class).makeVariantName(value, length, datatype, scope);
	}

	@Deprecated
	public VariantNameIF makeVariantName(TopicNameIF name, String value) {
		throw new OntopiaRuntimeException("JDOTopicMapBuilder.makeVariantName(TopicNameIF, String) is not implemented");
	}
	@Deprecated
	public VariantNameIF makeVariantName(TopicNameIF name, LocatorIF locator) {
		throw new OntopiaRuntimeException("JDOTopicMapBuilder.makeVariantName(TopicNameIF, LocatorIF) is not implemented");
	}
	@Deprecated
	public VariantNameIF makeVariantName(TopicNameIF name, String value, LocatorIF datatype) {
		throw new OntopiaRuntimeException("JDOTopicMapBuilder.makeVariantName(TopicNameIF, String, LocatorIF) is not implemented");
	}
	@Deprecated
	public VariantNameIF makeVariantName(TopicNameIF name, Reader value, long length, LocatorIF datatype) {
		throw new OntopiaRuntimeException("JDOTopicMapBuilder.makeVariantName(TopicNameIF, Reader, long, LocatorIF) is not implemented");
	}

	public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value) {
		return checkAndCast(topic, "Topic", Topic.class).makeOccurrence(occurs_type, value);
	}

	public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, String value, LocatorIF datatype) {
		return checkAndCast(topic, "Topic", Topic.class).makeOccurrence(occurs_type, value, datatype);
	}

	public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, Reader value, long length, LocatorIF datatype) {
		return checkAndCast(topic, "Topic", Topic.class).makeOccurrence(occurs_type, value, length, datatype);
	}

	public OccurrenceIF makeOccurrence(TopicIF topic, TopicIF occurs_type, LocatorIF locator) {
		return checkAndCast(topic, "Topic", Topic.class).makeOccurrence(occurs_type, locator);
	}

	public AssociationIF makeAssociation(TopicIF assoc_type) {
		return topicmap.makeAssociation(assoc_type);
	}

	public AssociationIF makeAssociation(TopicIF assoc_type, TopicIF role_type, TopicIF player) {
		AssociationIF association = makeAssociation(assoc_type);
		makeAssociationRole(association, role_type, player);
		return association;
	}

	public AssociationRoleIF makeAssociationRole(AssociationIF assoc, TopicIF role_type, TopicIF player) {
		return checkAndCast(assoc, "Association", Association.class).makeAssociationRole(role_type, player);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TMObject> T checkAndCast(TMObjectIF object, String name, Class<T> klass) {
		if (object == null) throw new NullPointerException(name + " cannot be null");
		if (klass.isAssignableFrom(object.getClass())) {
			return (T) object;
		} else{
			throw new OntopiaRuntimeException("Incorrect object for " + name + ", expect " + klass.getName() + ", found " + object.getClass().getName());
		}
	}
}
