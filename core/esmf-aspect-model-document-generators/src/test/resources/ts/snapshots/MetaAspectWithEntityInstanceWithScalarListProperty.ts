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


import { AspectWithEntityInstanceWithScalarListProperty, } from './AspectWithEntityInstanceWithScalarListProperty';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { MetaReplacedAspectArtifact, } from './MetaReplacedAspectArtifact';

import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithEntityInstanceWithScalarListProperty (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityInstanceWithScalarListProperty).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityInstanceWithScalarListProperty implements StaticMetaClass<AspectWithEntityInstanceWithScalarListProperty>, PropertyContainer<AspectWithEntityInstanceWithScalarListProperty> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityInstanceWithScalarListProperty';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEntityInstanceWithScalarListProperty();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEntityInstanceWithScalarListProperty, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEntityInstanceWithScalarListProperty';
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
    'ReplacedAspectArtifactInstance';
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
        put(MetaReplacedAspectArtifact.CODE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('3'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaReplacedAspectArtifact.TEST_LIST, new DefaultCollectionValue(new ArrayList<>()
        {
            {
                add({
                    metaModelBaseAttributes: {},
                    value: new BigInteger('1'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
                add({
                    metaModelBaseAttributes: {},
                    value: new BigInteger('2'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
                add({
                    metaModelBaseAttributes: {},
                    value: new BigInteger('3'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
                });
            }
        }
    ,
        org.eclipse.esmf.metamodel.CollectionValue.CollectionType.LIST, new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer');
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
    return 'AspectWithEntityInstanceWithScalarListProperty';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEntityInstanceWithScalarListProperty.MODEL_ELEMENT_URN;
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
    return 'AspectWithEntityInstanceWithScalarListProperty';
}

getProperties();
:
Array < StaticProperty < AspectWithEntityInstanceWithScalarListProperty, any >> {
    return [MetaAspectWithEntityInstanceWithScalarListProperty.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEntityInstanceWithScalarListProperty, any >> {
    return this.getProperties();
};


}


