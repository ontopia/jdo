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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.AbstractLocator;
import net.ontopia.infoset.impl.basic.URILocator;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Unique(members = {"address", "topicmap"})
public abstract class JDOLocator extends AbstractLocator implements Externalizable {
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(name = "id", valueStrategy=IdGeneratorStrategy.NATIVE)
	private long id;
	
	@Persistent(name = "address", column = "address")
	@Column(jdbcType = "LONGVARCHAR")
	protected String address;
	
	@Persistent(name = "topicmap", column = "topicmap")
	protected TopicMap topicmap;
	
	@Persistent(name = "object", column = "object")
	protected TMObject object;

	public JDOLocator() {
	}

	public JDOLocator(LocatorIF locator, TMObject object) {
		this(locator.getAddress(), object);
	}

	private JDOLocator(String address, TMObject object) {
		if (address == null) {
			throw new NullPointerException("The locator address cannot be null.");
		}
		this.address = address;
		this.object = object;
		this.topicmap = (TopicMap) object.getTopicMap();
	}

	public String getNotation() {
		return "URI";
	}

	public String getAddress() {
		return address;
	}

	TMObject getObject() {
		return object;
	}

	public LocatorIF resolveAbsolute(String address) {
		// FIXME: should use static method instead of creating URILocator instance
		try {
			return new URILocator(this.address).resolveAbsolute(address);
		} catch (java.net.MalformedURLException e) {
			// use RDBMS locator
		}
	    // Since this locator is general we cannot make the address
		// absolute, so we'll just return a new locator with the same
		// address instead.
		return this;
	}

	public String getExternalForm() {
		// FIXME: should use static method instead of creating URILocator instance
		try {
			return new URILocator(this.address).getExternalForm();
		} catch (java.net.MalformedURLException e) {
			// use existing address
		}
		// this locator is general so we don't know how to do this
		return address;
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LocatorIF)) return false;
		try {
			LocatorIF locator = (LocatorIF) object;
			return address.equals(locator.getAddress());
		} catch (ClassCastException e) {
			return false; // In case the object is not a locator
		} catch (NullPointerException e) {
			return false; // In case the object is null
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(address);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		address = in.readUTF();
	}
}
