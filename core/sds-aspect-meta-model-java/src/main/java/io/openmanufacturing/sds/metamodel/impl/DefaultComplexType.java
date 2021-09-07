package io.openmanufacturing.sds.metamodel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultComplexType extends BaseImpl implements ComplexType {

   /**
    * Used to keep track of all {@link ComplexType} instances regardles of whether they are directly or indirectly
    * referenced in the {@link io.openmanufacturing.sds.metamodel.Aspect}.
    */
   protected static WeakHashMap<AspectModelUrn, ComplexType> instances = new WeakHashMap<>();

   private final List<Property> properties;
   private final Optional<ComplexType> _extends;

   protected static DefaultComplexType createDefaultComplexType( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends ) {
      final DefaultComplexType defaultComplexType = new DefaultComplexType( metaModelBaseAttributes, properties,
            _extends );
      instances.put( metaModelBaseAttributes.getUrn().get(), defaultComplexType );
      return defaultComplexType;
   }

   protected DefaultComplexType( final MetaModelBaseAttributes metaModelBaseAttributes,
         final List<? extends Property> properties,
         final Optional<ComplexType> _extends ) {
      super( metaModelBaseAttributes );
      this.properties = new ArrayList<>( properties );
      this._extends = _extends;
   }

   /**
    * A list of properties defined in the scope of the Complex Type.
    *
    * @return the properties.
    */
   @Override
   public List<Property> getProperties() {
      return List.copyOf( properties );
   }

   @Override
   public Optional<ComplexType> getExtends() {
      return _extends;
   }

   public static Map<AspectModelUrn, ComplexType> getInstances() {
      return Collections.unmodifiableMap( instances );
   }

   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitComplexType( this, context );
   }
}
