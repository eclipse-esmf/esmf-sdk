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
import { EntityInstanceTest2, } from './EntityInstanceTest2';
import { MetaTheEntity, } from './MetaTheEntity';

import { string, } from 'asdas';


/*
* Generated class MetaEntityInstanceTest2 (urn:samm:org.eclipse.esmf.test:1.0.0#EntityInstanceTest2).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaEntityInstanceTest2 implements StaticMetaClass<EntityInstanceTest2>, PropertyContainer<EntityInstanceTest2> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'EntityInstanceTest2';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaEntityInstanceTest2();


    public static readonly ASPECT_PROPERTY =

        new (class extends DefaultStaticProperty<EntityInstanceTest2, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'EntityInstanceTest2';
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
        put(MetaTheEntity.OPTIONAL_ENTITY_PROPERTY, {
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
    return 'EntityInstanceTest2';
}

getAspectModelUrn();
:
string;
{
    return MetaEntityInstanceTest2.MODEL_ELEMENT_URN;
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
    return 'EntityInstanceTest2';
}

getProperties();
:
Array < StaticProperty < EntityInstanceTest2, any >> {
    return [MetaEntityInstanceTest2.ASPECT_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < EntityInstanceTest2, any >> {
    return this.getProperties();
};


}


