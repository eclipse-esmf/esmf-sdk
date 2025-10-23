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


import { AspectWithEnumerationWithMultipleSeeAttributes, } from './AspectWithEnumerationWithMultipleSeeAttributes';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithEnumerationWithMultipleSeeAttributes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEnumerationWithMultipleSeeAttributes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEnumerationWithMultipleSeeAttributes implements StaticMetaClass<AspectWithEnumerationWithMultipleSeeAttributes>, PropertyContainer<AspectWithEnumerationWithMultipleSeeAttributes> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEnumerationWithMultipleSeeAttributes';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEnumerationWithMultipleSeeAttributes();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithEnumerationWithMultipleSeeAttributes, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEnumerationWithMultipleSeeAttributes';
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
                    isAnonymous: true,
                    preferredNames: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Test Enumeration',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'), new ArrayList<Value>() { {
    add({
            metaModelBaseAttributes: {},
            value: 'bar',
            type: new DefaultScalar

(
    'http://www.w3.org/2001/XMLSchema#string';
),
}

)
add({
    metaModelBaseAttributes: {},
    value: 'foo',
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
});
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
    return 'AspectWithEnumerationWithMultipleSeeAttributes';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEnumerationWithMultipleSeeAttributes.MODEL_ELEMENT_URN;
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
    return 'AspectWithEnumerationWithMultipleSeeAttributes';
}

getProperties();
:
Array < StaticProperty < AspectWithEnumerationWithMultipleSeeAttributes, any >> {
    return [MetaAspectWithEnumerationWithMultipleSeeAttributes.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEnumerationWithMultipleSeeAttributes, any >> {
    return this.getProperties();
};


}


