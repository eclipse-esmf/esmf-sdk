/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.aspectmodel.generator;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableMap;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Uses the Velocity engine to load templates and apply context maps to them
 */
public class TemplateEngine implements UnaryOperator<String> {
   private final VelocityEngine engine;
   private final Map<String, Object> context;

   /**
    * Creates a new instance for the given context
    *
    * @param context the context, i.e. keys and values to inject into the templates
    * @param engineConfiguration the configuration properties used to setup the {@link VelocityEngine}
    */
   public TemplateEngine( final Map<String, Object> context, final Properties engineConfiguration ) {
      this( new VelocityEngine(), context );
      engine.setProperty( RuntimeConstants.RESOURCE_LOADERS, "classpath, file" );
      engine.setProperty( "resource.loader.classpath.class", ClasspathResourceLoader.class.getName() );
      engine.setProperties( engineConfiguration );
      engine.init();
   }

   private TemplateEngine( final VelocityEngine engine, final Map<String, Object> context ) {
      this.engine = engine;
      this.context = context;
   }

   /**
    * Evaluates the given template name which must be available in the class path
    *
    * @param templateName the name of the template to apply, without .vm extension
    * @return the result of the application of the context to the template
    */
   @Override
   public String apply( final String templateName ) {
      final Template template = engine.getTemplate( templateName + ".vm", StandardCharsets.UTF_8.name() );
      final StringWriter stringWriter = new StringWriter();
      final Map<String, Object> mutableContext = new HashMap<>( context );
      final VelocityContext velocityContext = new VelocityContext( mutableContext );
      template.merge( velocityContext, stringWriter );
      return stringWriter.toString();
   }

   /**
    * Inserts a new key and value to the context
    *
    * @param key the key
    * @param value the value
    * @return the template engine with the updated context
    */
   public TemplateEngine with( final String key, final Object value ) {
      return new TemplateEngine( engine,
            ImmutableMap.<String, Object> builder().putAll( context ).put( key, value ).build() );
   }
}
