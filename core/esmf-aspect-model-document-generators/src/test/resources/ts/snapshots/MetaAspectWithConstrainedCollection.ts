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

import { AspectWithConstrainedCollection, } from './AspectWithConstrainedCollection';
import { StaticContainerProperty, } from './core/staticConstraintProperty';
import { string, } from 'asdas';


/*
* Generated class MetaAspectWithConstrainedCollection (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithConstrainedCollection).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithConstrainedCollection implements StaticMetaClass<AspectWithConstrainedCollection>, PropertyContainer<AspectWithConstrainedCollection> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithConstrainedCollection';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithConstrainedCollection();


    public static readonly TEST_COLLECTION =

        new (class extends StaticContainerProperty<AspectWithConstrainedCollection, string, string[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithConstrainedCollection';
            }

            getContainedType(): AspectWithConstrainedCollection {
                return 'AspectWithConstrainedCollection';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testCollection',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'IntegerRange',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultList({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer')), Optional.empty()), new ArrayList<Constraint>() { {
    add(

    new;

    DefaultRangeConstraint({
                               isAnonymous: true,
                               preferredNames: [],
                               descriptions: [],
                               see: [],
                           }, Optional

.

    of({
           metaModelBaseAttributes: {},
           value: new BigInteger

(
    '2';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#integer';
),
}

),
Optional.of({
    metaModelBaseAttributes: {},
    value: new BigInteger('10'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#integer'),
}), BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST;
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
'testCollection',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithConstrainedCollection';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithConstrainedCollection.MODEL_ELEMENT_URN;
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
    return 'AspectWithConstrainedCollection';
}

getProperties();
:
Array < StaticProperty < AspectWithConstrainedCollection, any >> {
    return [MetaAspectWithConstrainedCollection.TEST_COLLECTION];
};

getAllProperties();
:
Array < StaticProperty < AspectWithConstrainedCollection, any >> {
    return this.getProperties();
};


}


