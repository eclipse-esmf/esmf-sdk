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

package io.openmanufacturing.sds.substitution;

import java.lang.reflect.InvocationTargetException;

import org.apache.velocity.Template;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.IntrospectionCacheData;
import org.apache.velocity.util.introspection.VelMethod;
import org.slf4j.Logger;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * This is a GraalVM substitution class (see https://blog.frankel.ch/coping-incompatible-code-graalvm-compilation/#substitutions)
 * for {@link ASTMethod}.
 * Reason: This class augments the Apache Velocity core class {@link ASTMethod} which is responsible for calling methods from
 * templates. It adds logging (with log level TRACE) to be able to understand which methods in which templates are called.
 */
@TargetClass( ASTMethod.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
} )
public final class Target_org_apache_velocity_runtime_parser_node_ASTMethod {
   @Alias
   protected Logger log;

   @Alias
   private static Class<?>[] EMPTY_CLASS_ARRAY;

   @Alias
   protected RuntimeServices rsvc;

   @Alias
   protected Info uberInfo;

   @Alias
   private int paramCount;

   @Alias
   private String methodName;

   @Alias
   protected boolean strictRef;

   @Alias
   private boolean logOnInvalid;

   @Alias
   protected Node[] children;

   @Alias
   protected Template template;

   @Alias
   protected int line;

   @Alias
   protected int column;

   @Alias
   private native Object handleInvocationException( final Object o, final InternalContextAdapter context, final Throwable t );

   /**
    * This method is largely adopted from {@link ASTMethod#execute(Object, InternalContextAdapter)} and just augmented with
    * additional logging. Several calls to methods from ASTMethod's super class are replaced with direct access to the @Alias'ed
    * fields, because only methods from the same class can be @Alias'ed.
    */
   @Substitute
   public Object execute( final Object o, final InternalContextAdapter context ) throws MethodInvocationException {
      try {
         rsvc.getLogContext().pushLogContext( (SimpleNode) (Object) this, uberInfo );
         final Object[] params = new Object[paramCount];
         final Class<?>[] paramClasses = paramCount > 0 ? new Class[paramCount] : EMPTY_CLASS_ARRAY;

         for ( int j = 0; j < paramCount; j++ ) {
            params[j] = children[j + 1].value( context );
            if ( params[j] != null ) {
               paramClasses[j] = params[j].getClass();
            }
         }

         final VelMethod method = ClassUtils.getMethod( methodName, params, paramClasses, o, context, (SimpleNode) (Object) this, strictRef );

         if ( o != null && method == null && logOnInvalid ) {
            final StringBuilder plist = new StringBuilder();
            for ( int i = 0; i < params.length; i++ ) {
               final Class<?> param = paramClasses[i];
               plist.append( param == null ? "null" : param.getName() );
               if ( i < params.length - 1 ) {
                  plist.append( ", " );
               }
            }
            log.debug( "Object '{}' does not contain method {}({}) (or several ambiguous methods) at {}[line {}, column {}]",
                  o.getClass().getName(), methodName, plist, template.getName(), line, column );
         }

         final IntrospectionCacheData prevICD = context.icacheGet( this );
         if ( method == null ) {
            if ( prevICD != null ) {
               context.icachePut( this, null );
            }
            return null;
         } else if ( prevICD == null ) {
            context.icachePut( this, new IntrospectionCacheData() );
         }

         try {
            final Object obj = method.invoke( o, params );
            // Logging statement added to comprehend which template methods are called
            log.trace( "Invoking template method: template:{} method:{} returntype:{}", template.getName(), method.getMethodName(),
                  method.getReturnType().getName() );

            if ( obj == null ) {
               if ( method.getReturnType() == Void.TYPE ) {
                  return "";
               }
            }

            return obj;
         } catch ( final InvocationTargetException ite ) {
            return handleInvocationException( o, context, ite.getTargetException() );
         } catch ( final IllegalArgumentException t ) {
            return handleInvocationException( o, context, t );
         } catch ( final RuntimeException e ) {
            throw e;
         } catch ( final Exception e ) {
            final String msg = "ASTMethod.execute() : exception invoking method '" + methodName + "' in " + o.getClass();
            log.error( msg, e );
            throw new VelocityException( msg, e, rsvc.getLogContext().getStackTrace() );
         }
      } finally {
         rsvc.getLogContext().popLogContext();
      }
   }
}
