package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.impl.DefaultComplexType;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.loader.ModelElementFactory;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityEntity extends DefaultComplexType implements Entity {

    public EntityEntity(
            MetaModelBaseAttributes metaModelBaseAttributes,
            List<? extends Property> properties ) {
        this( metaModelBaseAttributes, properties, Optional.empty(), Collections.emptyList(), null );
    }

    public EntityEntity(
            MetaModelBaseAttributes metaModelBaseAttributes,
            List<? extends Property> properties,
            Optional<ComplexType> _extends,
            List<AspectModelUrn> extendingElements,
            ModelElementFactory loadedElements ) {
        super( metaModelBaseAttributes, properties, _extends, extendingElements, loadedElements );
    }

    /**
     * Accepts an Aspect visitor
     *
     * @param visitor The visitor to accept
     * @param <T> The result type of the traversal operation
     * @param <C> The context of the visitor traversal
     */
    @Override
    public <T, C> T accept(final AspectVisitor<T, C> visitor, final C context ) {
        return visitor.visitEntity( this, context );
    }
}
