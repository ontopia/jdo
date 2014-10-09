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
import java.util.HashSet;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DuplicateReificationException;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

@PersistenceCapable(table = "TM_VARIANT_NAME")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Indices({
	@Index(name = "TM_VARIANT_NAME_IX_ID_TOPICMAP", members = {"id", "topicmap"}), // hash not implemented
	@Index(name = "TM_VARIANT_NAME_IX_NAME", members = {"topicname"})
})
public class VariantName extends TMObject implements VariantNameIF {
	
	@Column(name = "topicname")
	private TopicName topicname;
	
	private LocatorIF datatype;
	
	@Column(name = "value")
	private String value;

	@Column(name = "length")
	private long length;

	@Column(name = "reifier")
	private Topic reifier;
	
	@Persistent(table = "TM_SCOPES")
	@Join(column = "scoped")
	@Element(column = "scope")
	private Collection<Topic> scope;

	public VariantName(TopicName name) {
		super((TopicMap) name.getTopicMap());
	}
	
	@Override
	protected String getClassIndicator() {
		return "V";
	}

	public TopicNameIF getTopicName() {
		return topicname;
	}

	public LocatorIF getDataType() {
		return datatype;
	}

	public String getValue() {
		return value;
	}

	public Reader getReader() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void setValue(String value) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.value = value;
	}

	public LocatorIF getLocator() {
		if (datatype.getAddress().equals("")) {
			return URILocator.create(value);
		}
		return null;
	}

	public void setLocator(LocatorIF locator) {
		if (isReadOnly()) throw new ReadOnlyException();
		setValue(locator.getAddress(), URILocator.create("uri")); // todo
	}

	public void setValue(String value, LocatorIF datatype) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.value = value;
		this.datatype = datatype;
	}

	public void setReader(Reader value, long length, LocatorIF datatype) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public long getLength() {
		return length;
	}

	public TopicIF getTopic() {
		return topicname.getTopic();
	}

	public Collection<TopicIF> getScope() {
		return new HashSet<TopicIF>(scope);
	}

	public void addTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		scope.add((Topic) theme);
	}

	public void removeTheme(TopicIF theme) {
		if (isReadOnly()) throw new ReadOnlyException();
		scope.remove((Topic) theme);
	}

	public TopicIF getReifier() {
		return reifier;
	}

	public void setReifier(TopicIF reifier) throws DuplicateReificationException {
		if (isReadOnly()) throw new ReadOnlyException();
		this.reifier = (Topic) reifier;
	}

	@Override
	protected void beforeRemove() {
		// unregister at name
		topicname.getVariants().remove(this);
		
		// let super cleanup
		super.beforeRemove();
	}
}
