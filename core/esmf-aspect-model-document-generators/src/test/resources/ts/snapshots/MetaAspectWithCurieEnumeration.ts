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


import { AspectWithCurieEnumeration, } from './AspectWithCurieEnumeration';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';

import { any, } from 'asdas';


/*
* Generated class MetaAspectWithCurieEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCurieEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCurieEnumeration implements StaticMetaClass<AspectWithCurieEnumeration>, PropertyContainer<AspectWithCurieEnumeration> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCurieEnumeration';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCurieEnumeration();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithCurieEnumeration, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithCurieEnumeration';
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
                    preferredNames: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test for enumeration.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, new DefaultScalar('urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {},
            value: new Curie

(
    'unit:gram';
),
    type: new DefaultScalar;
(
    'urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: new Curie('unit:hectopascal'),
    type: new DefaultScalar('urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie'),
});
}
})
,
{
    {
    }
,
    new Curie('unit:hectopascal'),
        type;
:
    new DefaultScalar('urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#curie'),
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
    return 'AspectWithCurieEnumeration';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithCurieEnumeration.MODEL_ELEMENT_URN;
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
    return 'AspectWithCurieEnumeration';
}

getProperties();
:
Array < StaticProperty < AspectWithCurieEnumeration, any >> {
    return [MetaAspectWithCurieEnumeration.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithCurieEnumeration, any >> {
    return this.getProperties();
};


}


