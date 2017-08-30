/*
 * #!
 * Ontopia JDO
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.GenericDocument;
import net.ontopia.infoset.fulltext.core.SearchResultIF;
import net.ontopia.infoset.fulltext.core.SearcherIF;
import net.ontopia.topicmaps.impl.jdo.TMObject;
import net.ontopia.topicmaps.impl.jdo.TopicMap;
import net.ontopia.topicmaps.impl.jdo.TopicName;
import net.ontopia.topicmaps.impl.jdo.utils.Queries;

public class BasicSearcher extends AbstractIndex implements SearcherIF {

	public BasicSearcher(TopicMap topicmap) {
		super(topicmap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SearchResultIF search(String query) throws IOException {
		if (query == null) return new JDOSearchResult(Collections.<TMObject>emptyList());
		List<TMObject> results = (List<TMObject>) getQuery(Queries.BASICSEARCHER_SEARCH).executeWithArray(topicmap, query);
		return new JDOSearchResult(results);
	}

	@Override
	public void close() throws IOException {
		// no-op
	}
	
	class JDOSearchResult implements SearchResultIF {

		private final List<TMObject> results;

		private JDOSearchResult(List<TMObject> results) {
			this.results = results;
		}

		@Override
		public DocumentIF getDocument(int hit) throws IOException {
			if ((hit < 0) || (hit >= results.size())) return null;
			TMObject object = results.get(hit);
			GenericDocument d = new GenericDocument();
			if (object instanceof TopicName) {
				d.addField(new JDOField("class", "B"));
			} else {
				d.addField(new JDOField("class", TopicMap.className(object.getClass())));
			}
			d.addField(new JDOField("object_id", object.getObjectId()));
			return d;
		}

		@Override
		public float getScore(int hit) throws IOException {
			return 0;
		}

		@Override
		public int hits() throws IOException {
			return results.size();
		}
	}
	
	class JDOField implements FieldIF {

		private final String name;
		private final String value;

		public JDOField(String name, String value) {
			this.name = name;
			this.value = value;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public Reader getReader() {
			return null;
		}

		@Override
		public boolean isStored() {
			return true;
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isTokenized() {
			return false;
		}
	}
}
