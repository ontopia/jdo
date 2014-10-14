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
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.PSI;

@PersistenceCapable(table = "TM_VARIANT_NAME")
@Inheritance(strategy=InheritanceStrategy.COMPLETE_TABLE)
@Indices({
	@Index(name = "TM_VARIANT_NAME_IX_ID_TOPICMAP", members = {"id", "topicmap"}), // hash not implemented
	@Index(name = "TM_VARIANT_NAME_IX_NAME", members = {"topicname"})
})
public class VariantName extends Scoped implements VariantNameIF {
	
	@Column(name = "topicname")
	private TopicName topicname;
	
	@Column(name = "datatype", jdbcType = "LONGVARCHAR")
	private String datatype = PSI.XSD_STRING;

	@Column(name = "value", jdbcType = "LONGVARCHAR")
	private String value;

	@Column(name = "length")
	private long length;

	public VariantName(TopicName name) {
		super((TopicMap) name.getTopicMap());
	}
	
	@Override
	protected String getClassIndicator() {
		return "V";
	}

	@Override
	public TopicNameIF getTopicName() {
		return topicname;
	}

	@Override
	public LocatorIF getDataType() {
		return URILocator.create(datatype);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Reader getReader() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setValue(String value) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.value = value;
	}

	@Override
	public LocatorIF getLocator() {
		if (PSI.XSD_URI.equals(datatype)) {
			return URILocator.create(value);
		}
		return null;
	}

	@Override
	public void setLocator(LocatorIF locator) {
		if (isReadOnly()) throw new ReadOnlyException();
		setValue(locator.getAddress(), URILocator.create(PSI.XSD_URI));
	}

	@Override
	public void setValue(String value, LocatorIF datatype) {
		if (isReadOnly()) throw new ReadOnlyException();
		this.value = value;
		this.datatype = datatype.getAddress();
	}

	@Override
	public void setReader(Reader value, long length, LocatorIF datatype) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getLength() {
		return length;
	}

	@Override
	public TopicIF getTopic() {
		return topicname.getTopic();
	}

	@Override
	protected void beforeRemove() {
		// unregister at name
		topicname.getVariants().remove(this);
		
		// let super cleanup
		super.beforeRemove();
	}
}
