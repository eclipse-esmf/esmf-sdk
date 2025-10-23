/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import { ReplacedAspectArtifact, } from './ReplacedAspectArtifact';


import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { EntityInstanceTest3, } from './EntityInstanceTest3';
import { MetaTheEntity, } from './MetaTheEntity';

import { string, } from 'asdas';


/*
* Generated class MetaEntityInstanceTest3 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest3).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaEntityInstanceTest3 implements StaticMetaClass<EntityInstanceTest3>, PropertyContainer<EntityInstanceTest3> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest3';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaEntityInstanceTest3();


    public static readonly ASPECT_PROPERTY =

        new (class extends DefaultStaticProperty<EntityInstanceTest3, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'EntityInstanceTest3';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'aspectProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'TheEntity',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaTheEntity.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'entityInstance';
,
    preferredNames: [];
,
    descriptions: [];
,
    see: [];
,
}

,
new HashMap<Property, Value>();
{
    {
        put(MetaTheEntity.ENTITY_PROPERTY, {
            metaModelBaseAttributes: {},
            value: 'Test',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'TheEntity',
    preferredNames: [],
    descriptions: [],
    see: [],
}, MetaTheEntity.INSTANCE.getProperties(), Optional.empty());
))

}
})
,
{
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'aspectProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'EntityInstanceTest3';
}

getAspectModelUrn();
:
string;
{
    return MetaEntityInstanceTest3.MODEL_ELEMENT_URN;
}

getMetaModelVersion();
:
KnownVersion;
{
    return KnownVersionUtils.getLatest();
}

getName();
:
string;
{
    return 'EntityInstanceTest3';
}

getProperties();
:
Array < StaticProperty < EntityInstanceTest3, any >> {
    return [MetaEntityInstanceTest3.ASPECT_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < EntityInstanceTest3, any >> {
    return this.getProperties();
};


}


