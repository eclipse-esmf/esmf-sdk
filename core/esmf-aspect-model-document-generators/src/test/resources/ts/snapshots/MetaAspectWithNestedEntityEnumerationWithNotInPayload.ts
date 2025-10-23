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


import { AspectWithNestedEntityEnumerationWithNotInPayload, } from './AspectWithNestedEntityEnumerationWithNotInPayload';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaNestedEntity, } from './MetaNestedEntity';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';

import { string, } from 'asdas';


/*
* Generated class MetaAspectWithNestedEntityEnumerationWithNotInPayload (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNestedEntityEnumerationWithNotInPayload).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNestedEntityEnumerationWithNotInPayload implements StaticMetaClass<AspectWithNestedEntityEnumerationWithNotInPayload>, PropertyContainer<AspectWithNestedEntityEnumerationWithNotInPayload> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNestedEntityEnumerationWithNotInPayload';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithNestedEntityEnumerationWithNotInPayload();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithNestedEntityEnumerationWithNotInPayload, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithNestedEntityEnumerationWithNotInPayload';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
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
                    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
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
        put(MetaReplacedAspectArtifact.ENTITY_PROPERTY, {
            metaModelBaseAttributes: {},
            value: 'This is a test.',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
        put(MetaReplacedAspectArtifact.NESTED_ENTITY_PROPERTY, new DefaultEntityInstance({
            urn: this.NAMESPACE + 'NestedEntityInstance',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, new HashMap<Property, Value>()
        {
            {
                put(MetaNestedEntity.NOT_IN_PAYLOAD_PROPERTY, {
                    metaModelBaseAttributes: {},
                    value: 'foo',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                });
            }
        }
    ,
        DefaultEntity.createDefaultEntity({
            urn: this.NAMESPACE + 'NestedEntity',
            preferredNames: [],
            descriptions: [],
            see: [],
        }, MetaNestedEntity.INSTANCE.getProperties(), Optional.empty());
    ))

    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'ReplacedAspectArtifact',
    preferredNames: [],
    descriptions: [],
    see: [],
}, MetaReplacedAspectArtifact.INSTANCE.getProperties(), Optional.empty());
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
'testProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithNestedEntityEnumerationWithNotInPayload';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithNestedEntityEnumerationWithNotInPayload.MODEL_ELEMENT_URN;
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
    return 'AspectWithNestedEntityEnumerationWithNotInPayload';
}

getProperties();
:
Array < StaticProperty < AspectWithNestedEntityEnumerationWithNotInPayload, any >> {
    return [MetaAspectWithNestedEntityEnumerationWithNotInPayload.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithNestedEntityEnumerationWithNotInPayload, any >> {
    return this.getProperties();
};


}


