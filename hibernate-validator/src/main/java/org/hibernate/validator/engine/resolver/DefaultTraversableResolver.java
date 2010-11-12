/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.validator.engine.resolver;

import java.lang.annotation.ElementType;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.ValidationException;

import org.slf4j.Logger;

import org.hibernate.validator.util.LoggerFactory;
import org.hibernate.validator.util.ReflectionHelper;

/**
 * A JPA 2 aware {@code TraversableResolver}.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class DefaultTraversableResolver implements TraversableResolver {

	private static final Logger log = LoggerFactory.make();

	/**
	 * Class to load to check whether JPA is on the classpath.
	 */
	private static final String PERSISTENCE_CLASS_NAME = "javax.persistence.Persistence";

	/**
	 * Method to check whether the found {@code Persistence} class is of the version 2
	 */
	private static final String PERSISTENCE_UTIL_METHOD = "getPersistenceUtil";

	/**
	 * Class to instantiate in case JPA 2 is on the classpath.
	 */
	private static final String JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME = "org.hibernate.validator.engine.resolver.JPATraversableResolver";

	/**
	 * A JPA 2 aware traversable resolver.
	 */
	private TraversableResolver jpaTraversableResolver;


	public DefaultTraversableResolver() {
		detectJPA();
	}

	/**
	 * Tries to load detect and load JPA.
	 */
	private void detectJPA() {
		// check whether we have Persistence on the classpath - 1 or 2
		Class<?> persistenceClass;
		try {
			persistenceClass = ReflectionHelper.loadClass( PERSISTENCE_CLASS_NAME, this.getClass() );
		}
		catch ( ValidationException e ) {
			log.debug(
					"Cannot find {} on classpath. Assuming non JPA 2 environment. All properties will per default be traversable.",
					PERSISTENCE_CLASS_NAME
			);
			return;
		}

		if ( !ReflectionHelper.containsMethod( persistenceClass, PERSISTENCE_UTIL_METHOD ) ) {
			log.debug(
					"Found {} on classpath, but no method '{}'. Assuming JPA 1 environment. All properties will per default be traversable.",
					PERSISTENCE_CLASS_NAME,
					PERSISTENCE_UTIL_METHOD
			);
			return;
		}

		log.debug(
				"Found {} on classpath containing '{}'. Assuming JPA 2 environment. Trying to instantiate JPA aware TraversableResolver",
				PERSISTENCE_CLASS_NAME,
				PERSISTENCE_UTIL_METHOD
		);

		try {
			@SuppressWarnings("unchecked")
			Class<? extends TraversableResolver> jpaAwareResolverClass = (Class<? extends TraversableResolver>)
					ReflectionHelper.loadClass( JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME, this.getClass() );
			jpaTraversableResolver = ReflectionHelper.newInstance( jpaAwareResolverClass, "" );
			log.debug(
					"Instantiated JPA aware TraversableResolver of type {}.", JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME
			);
		}
		catch ( ValidationException e ) {
			log.debug(
					"Unable to load or instantiate JPA aware resolver {}. All properties will per default be traversable.",
					JPA_AWARE_TRAVERSABLE_RESOLVER_CLASS_NAME
			);
		}
	}

	public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
		return jpaTraversableResolver == null || jpaTraversableResolver.isReachable(
				traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType
		);
	}

	public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
		return jpaTraversableResolver == null || jpaTraversableResolver.isCascadable(
				traversableObject, traversableProperty, rootBeanType, pathToTraversableObject, elementType
		);
	}
}
