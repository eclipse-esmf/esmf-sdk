package org.eclipse.esmf.metamodel.entity;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
import org.eclipse.esmf.samm.KnownVersion;

import java.util.Optional;

public class TypeEntity implements Type {
    private KnownVersion knownVersion;
    private Optional<AspectModelUrn> urn;

    public TypeEntity(KnownVersion knownVersion, Optional<AspectModelUrn> urn) {
        this.knownVersion = knownVersion;
        this.urn = urn;
    }

    @Override
    public KnownVersion getMetaModelVersion() {
        return this.knownVersion;
    }

    @Override
    public <T, C> T accept(AspectVisitor<T, C> visitor, C context) {
        return null;
    }

    @Override
    public String getUrn() {
        return this.urn.toString();
    }
}
