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

package net.ontopia.topicmaps.impl.jdo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.jdo.Query;

public class JDOQueryUtils {
	
	// does not close the query result, make sure the caller does!
	@SuppressWarnings("unchecked")
	private static <T> Collection<T> queryToCollection(Query query, Object... parameters) {
		return (Collection<T>) query.executeWithArray(parameters);
	}

	public static <T> Collection<T> queryToWrappedSet(Query query, Object... parameters) {
		try {
			return new HashSet<T>(JDOQueryUtils.<T>queryToCollection(query, parameters));
		} finally {
			query.closeAll();
		}
	}

	public static <T> List<T> queryToWrappedList(Query query, Object... parameters) {
		try {
			return new ArrayList<T>(JDOQueryUtils.<T>queryToCollection(query, parameters));
		} finally {
			query.closeAll();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T singularResultQuery(Query query, Object... variables) {
		try {
			Object queryResult = query.executeWithArray(variables);
			if (queryResult instanceof Collection) {
				Collection<T> result = (Collection<T>) queryResult;
				if (result.isEmpty()) {
					return null;
				}
				return result.iterator().next();
			} else {
				return (T) queryResult;
			}
		} finally {
			query.closeAll();
		}
	}
}
