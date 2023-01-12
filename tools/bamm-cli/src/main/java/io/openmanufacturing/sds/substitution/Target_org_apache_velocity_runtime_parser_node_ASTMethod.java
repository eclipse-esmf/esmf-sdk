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
import java.util.Arrays;
import java.util.stream.Collectors;

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

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass( ASTMethod.class )
public final class Target_org_apache_velocity_runtime_parser_node_ASTMethod {
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
   public native Node jjtGetChild( final int i );

   @Alias
   private native Object handleInvocationException( final Object o, final InternalContextAdapter context, final Throwable t );

   @Substitute
   public Object execute( final Object o, final InternalContextAdapter context ) throws MethodInvocationException {
      try {
         //         rsvc.getLogContext().pushLogContext( this, uberInfo );

         /*
          *  new strategy (strategery!) for introspection. Since we want
          *  to be thread- as well as context-safe, we *must* do it now,
          *  at execution time.  There can be no in-node caching,
          *  but if we are careful, we can do it in the context.
          */
         final Object[] params = new Object[paramCount];

         /*
          * sadly, we do need recalc the values of the args, as this can
          * change from visit to visit
          */
         final Class<?>[] paramClasses =
               paramCount > 0 ? new Class[paramCount] : EMPTY_CLASS_ARRAY;

         for ( int j = 0; j < paramCount; j++ ) {
            params[j] = jjtGetChild( j + 1 ).value( context );
            if ( params[j] != null ) {
               paramClasses[j] = params[j].getClass();
            }
         }

         final VelMethod method = ClassUtils.getMethod( methodName, params, paramClasses,
               o, context, (SimpleNode) (Object) this, strictRef );

         // warn if method wasn't found (if strictRef is true, then ClassUtils did throw an exception)
         if ( o != null && method == null && logOnInvalid ) {
            final StringBuilder plist = new StringBuilder();
            for ( int i = 0; i < params.length; i++ ) {
               final Class<?> param = paramClasses[i];
               plist.append( param == null ? "null" : param.getName() );
               if ( i < params.length - 1 ) {
                  plist.append( ", " );
               }
            }
            //            log.debug( "Object '{}' does not contain method {}({}) (or several ambiguous methods) at {}[line {}, column {}]", o.getClass()
            //            .getName(),
            //                  methodName, plist, getTemplateName(), getLine(), getColumn() );
         }

         /*
          * The parent class (typically ASTReference) uses the icache entry
          * under 'this' key to distinguish a valid null result from a non-existent method.
          * So update this dummy cache value if necessary.
          */
         final IntrospectionCacheData prevICD = context.icacheGet( this );
         if ( method == null ) {
            if ( prevICD != null ) {
               context.icachePut( this, null );
            }
            return null;
         } else if ( prevICD == null ) {
            context.icachePut( this, new IntrospectionCacheData() ); // no need to fill in its members
         }

         try {
            /*
             *  get the returned object.  It may be null, and that is
             *  valid for something declared with a void return type.
             *  Since the caller is expecting something to be returned,
             *  as long as things are peachy, we can return an empty
             *  String so ASTReference() correctly figures out that
             *  all is well.
             */

            final Object obj = method.invoke( o, params );
            System.err.printf( "=== Invoking velocity method: %s:%s(%s)%n", method.getMethodName(), method.getReturnType().getName(),
                  Arrays.asList( params ).stream().map( Object::toString ).collect( Collectors.joining( "," ) ) );

            if ( obj == null ) {
               if ( method.getReturnType() == Void.TYPE ) {
                  return "";
               }
            }

            return obj;
         } catch ( final InvocationTargetException ite ) {
            return handleInvocationException( o, context, ite.getTargetException() );
         }

         /* Can also be thrown by method invocation */ catch ( final IllegalArgumentException t ) {
            return handleInvocationException( o, context, t );
         }

         /*
          * pass through application level runtime exceptions
          */ catch ( final RuntimeException e ) {
            throw e;
         } catch ( final Exception e ) {
            final String msg = "ASTMethod.execute() : exception invoking method '"
                  + methodName + "' in " + o.getClass();
            //            log.error( msg, e );
            throw new VelocityException( msg, e, rsvc.getLogContext().getStackTrace() );
         }
      } finally {
         rsvc.getLogContext().popLogContext();
      }
   }
}
