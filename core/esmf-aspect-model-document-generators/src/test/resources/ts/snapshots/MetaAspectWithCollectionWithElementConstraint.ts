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

import { AspectWithCollectionWithElementConstraint, } from './AspectWithCollectionWithElementConstraint';
import { LangString, } from './core/langString';
import { StaticContainerProperty, } from './core/staticConstraintProperty';
import { number, } from 'asdas';


/*
* Generated class MetaAspectWithCollectionWithElementConstraint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionWithElementConstraint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithCollectionWithElementConstraint implements StaticMetaClass<AspectWithCollectionWithElementConstraint>, PropertyContainer<AspectWithCollectionWithElementConstraint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithCollectionWithElementConstraint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithCollectionWithElementConstraint();


    public static readonly TEST_PROPERTY =

        new (class extends StaticContainerProperty<AspectWithCollectionWithElementConstraint, number, number[]> {


            getPropertyType(): string {
                return '${codeGenerationConfig.importTracker().getRawContainerType( $propertyType )}';
            }

            getContainingType(): string {
                return 'AspectWithCollectionWithElementConstraint';
            }

            getContainedType(): AspectWithCollectionWithElementConstraint {
                return 'AspectWithCollectionWithElementConstraint';
            }

        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [{
                        value: 'Test Property',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test property.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                        'http://example.com/me',
                    ],
                },
                characteristic: new DefaultCollection({
                    urn: this.NAMESPACE + 'TestCollection',
                    preferredNames: [{
                        value: 'Test Collection',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'This is a test collection.',
                        languageTag: 'en',
                    },
                    ],
                    see: ['http://example.com/',
                    ],
                }, Optional.of(new DefaultScalar('http://www.w3.org/2001/XMLSchema#float')), Optional.of(new DefaultTrait({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultMeasurement({
                    urn: this.NAMESPACE + 'Measurement',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'), Units.fromName('metrePerSecond')), new ArrayList<Constraint>() { {
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
           value: Float.valueOf

(
    '2.3';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#float';
),
}

),
Optional.of({
    metaModelBaseAttributes: {},
    value: Float.valueOf('10.5'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'),
}), BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST;
))

}
})))
,
{
    {
    }
,
    Float.valueOf('5.0'),
        type;
:
    new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'),
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
    return 'AspectWithCollectionWithElementConstraint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithCollectionWithElementConstraint.MODEL_ELEMENT_URN;
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
    return 'AspectWithCollectionWithElementConstraint';
}

getProperties();
:
Array < StaticProperty < AspectWithCollectionWithElementConstraint, any >> {
    return [MetaAspectWithCollectionWithElementConstraint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithCollectionWithElementConstraint, any >> {
    return this.getProperties();
};


getPreferredNames();
:
Array < LangString > {
    return [
        {value: 'Test Aspect', languageTag: 'en'},
    ];
};


getDescriptions();
:
Array < LangString > {
    return [
        {value: 'This is a test description', languageTag: 'en'},
    ];
};

getSee();
:
Array < String > {
    return [
        'http://example.com/',
    ];
};

}


