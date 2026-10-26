/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.aspectmodel.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.esmf.annotations.InterfaceVersion;
import org.eclipse.esmf.annotations.RequiredInterfaceVersion;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.validation.RdfBasedValidator;

import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension Point for SAMM. Currently supports loading of custom ResolutionStrategies for
 * resolving Aspect Models from custom locations.
 * <p>
 * Custom Implementations need to be annotated with {@link RequiredInterfaceVersion}, which needs to
 * match the {@link InterfaceVersion} of the implemented interface.
 * The custom implementation will not be loaded if there is a version mismatch of the
 * implementations {@link RequiredInterfaceVersion} and the interfaces {@link InterfaceVersion}.
 */
public class ExtensionService {
   private static final Logger LOG = LoggerFactory.getLogger( ExtensionService.class );
   private static final Path PLUGIN_DIRECTORY =
         Path.of( AppDirsFactory.getInstance().getUserDataDir( "esmf", "1", "esmf" ) ).resolve( "plugins" );
   private static final ClassLoader PLUGIN_CLASS_LOADER = createPluginClassLoader( PLUGIN_DIRECTORY );
   private static final List<ResolutionStrategy> RESOLUTION_STRATEGIES = loadServiceProviders( ResolutionStrategy.class );
   private static final List<RdfBasedValidator> RDF_BASED_VALIDATORS = loadServiceProviders( RdfBasedValidator.class );

   private ExtensionService() {}

   public static List<ResolutionStrategy> getResolutionStrategies() {
      return RESOLUTION_STRATEGIES;
   }

   public static List<RdfBasedValidator> getRdfBasedValidators() {
      return RDF_BASED_VALIDATORS;
   }

   /**
    * Discovers and loads all plugin-provided implementations of the given extension point interface,
    * skipping (and logging) any
    * implementation that is missing, broken, or built against an incompatible interface version.
    * The interface itself must be annotated with {@link InterfaceVersion} and implementations are
    * expected to be annotated with {@link RequiredInterfaceVersion}.
    */
   private static <T> List<T> loadServiceProviders( final Class<T> serviceInterface ) {
      final int currentInterfaceVersion = currentInterfaceVersion( serviceInterface );
      final List<T> providers = new ArrayList<>();
      final Iterator<T> iterator = ServiceLoader.load( serviceInterface, PLUGIN_CLASS_LOADER ).iterator();
      boolean hasMore = hasNextSafely( iterator, serviceInterface );
      while ( hasMore ) {
         nextSafely( iterator, serviceInterface ).ifPresent( provider -> addIfCompatible( provider, currentInterfaceVersion, providers ) );
         hasMore = hasNextSafely( iterator, serviceInterface );
      }
      return List.copyOf( providers );
   }

   private static int currentInterfaceVersion( final Class<?> serviceInterface ) {
      final InterfaceVersion interfaceVersion = serviceInterface.getAnnotation( InterfaceVersion.class );
      if ( interfaceVersion == null ) {
         throw new IllegalStateException(
               "Extension point interface " + serviceInterface.getName() + " is missing the required @InterfaceVersion annotation" );
      }
      return interfaceVersion.version();
   }

   /**
    * A single broken provider (e.g. missing class, incompatible bytecode) must not prevent the
    * remaining,
    * well-behaved providers from being loaded, so failures while advancing the iterator are logged and
    * treated
    * as "no (more) provider found" instead of propagating.
    */
   private static <T> boolean hasNextSafely( final Iterator<T> providers, final Class<T> serviceInterface ) {
      try {
         return providers.hasNext();
      } catch ( final ServiceConfigurationError | LinkageError exception ) {
         LOG.warn( "[extension-service] Stopped looking for further {} providers: {}", serviceInterface.getSimpleName(),
               exception.getMessage() );
         return false;
      }
   }

   private static <T> Optional<T> nextSafely( final Iterator<T> providers, final Class<T> serviceInterface ) {
      try {
         return Optional.of( providers.next() );
      } catch ( final ServiceConfigurationError | LinkageError exception ) {
         LOG.warn( "[extension-service] Skipping a {} provider that could not be instantiated: {}",
               serviceInterface.getSimpleName(), exception.getMessage() );
         return Optional.empty();
      }
   }

   private static <T> void addIfCompatible( final T provider, final int currentInterfaceVersion, final List<T> providers ) {
      try {
         if ( !isCompatible( provider.getClass(), currentInterfaceVersion ) ) {
            LOG.warn( "[extension-service] Ignoring {} with incompatible interface version", provider.getClass().getName() );
            return;
         }
         providers.add( provider );
         LOG.debug( "[extension-service] Loaded custom extension: {}", provider.getClass().getName() );
      } catch ( final RuntimeException | LinkageError exception ) {
         LOG.warn( "[extension-service] Ignoring {} that could not be validated: {}",
               provider.getClass().getName(), exception.getMessage() );
      }
   }

   private static boolean isCompatible( final Class<?> interfaceImplementation, final int currentInterfaceVersion ) {
      final RequiredInterfaceVersion requiredVersion = interfaceImplementation.getAnnotation( RequiredInterfaceVersion.class );
      if ( requiredVersion == null ) {
         LOG.warn( "[extension-service] {} is missing the required @RequiredInterfaceVersion annotation",
               interfaceImplementation.getName() );
         return false;
      }
      return requiredVersion.version() == currentInterfaceVersion;
   }

   private static Optional<URL> toUrl( final Path path ) {
      try {
         return Optional.of( path.toUri().toURL() );
      } catch ( final MalformedURLException exception ) {
         LOG.warn( "[extension-service] Could not read plugin jar {}: {}", path, exception.getMessage() );
         return Optional.empty();
      }
   }

   private static ClassLoader createPluginClassLoader( final Path pluginDirectory ) {
      try {
         ensurePluginDirectoryExists( pluginDirectory );
         try ( Stream<Path> files = Files.list( pluginDirectory ) ) {
            final URL[] jarUrls = files
                  .filter( path -> path.toString().endsWith( ".jar" ) )
                  .map( ExtensionService::toUrl )
                  .filter( Optional::isPresent )
                  .map( Optional::get )
                  .toArray( URL[]::new );
            LOG.debug( "[extension-service] Found {} plugin jar(s) in {}", jarUrls.length, pluginDirectory );
            return new URLClassLoader( jarUrls, ExtensionService.class.getClassLoader() );
         }
      } catch ( final RuntimeException | IOException exception ) {
         // Never let a plugin-loading problem take down the whole class (and with it every subsequent call
         // to getResolutionStrategies(), via ExceptionInInitializerError/NoClassDefFoundError); fall back to
         // "no extensions" instead.
         LOG.warn( "[extension-service] Could not set up plugin classloader for {}, extensions are disabled: {}",
               pluginDirectory, exception.getMessage() );
         return ExtensionService.class.getClassLoader();
      }
   }

   private static void ensurePluginDirectoryExists( final Path pluginDirectory ) {
      if ( !Files.exists( pluginDirectory ) ) {
         try {
            Files.createDirectories( pluginDirectory );
         } catch ( final IOException exception ) {
            throw new ModelResolutionException( "Unable to create plugins directory at " + pluginDirectory, exception );
         }
      }
   }
}
