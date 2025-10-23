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


import { AspectWithStringEnumeration, } from './AspectWithStringEnumeration';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithStringEnumeration (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithStringEnumeration).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithStringEnumeration implements StaticMetaClass<AspectWithStringEnumeration>, PropertyContainer<AspectWithStringEnumeration> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithStringEnumeration';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithStringEnumeration();


    public static readonly ENUMERATION_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithStringEnumeration, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithStringEnumeration';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'enumerationProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
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
'enumerationProperty',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithStringEnumeration';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithStringEnumeration.MODEL_ELEMENT_URN;
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
    return 'AspectWithStringEnumeration';
}

getProperties();
:
Array < StaticProperty < AspectWithStringEnumeration, any >> {
    return [MetaAspectWithStringEnumeration.ENUMERATION_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithStringEnumeration, any >> {
    return this.getProperties();
};


}


