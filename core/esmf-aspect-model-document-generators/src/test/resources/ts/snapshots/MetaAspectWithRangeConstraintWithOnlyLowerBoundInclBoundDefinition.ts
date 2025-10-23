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

import { AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition, } from './AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { number, } from 'asdas';


/*
* Generated class MetaAspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition implements StaticMetaClass<AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition>, PropertyContainer<AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition';
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
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'TestRangeConstraint',
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
                               preferredNames: [{
                                   value: 'Test Range Constraint',
                                   languageTag: 'en',
                               },
                               ],
                               descriptions: [{
                                   value: 'This is a test range constraint.',
                                   languageTag: 'en',
                               },
                               ],
                               see: ['http://example.com/',
                               ],
                           }, Optional

.

    of({
           metaModelBaseAttributes: {
               isAnonymous: true,
               preferredNames: [{
                   value: 'Test Range Constraint',
                   languageTag: 'en',
               },
               ],
               descriptions: [{
                   value: 'This is a test range constraint.',
                   languageTag: 'en',
               },
               ],
               see: ['http://example.com/',
               ],
           },
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
Optional.empty(), BoundDefinition.GREATER_THAN, BoundDefinition.OPEN;
))

}
})
,
{
    {
    }
,
    Float.valueOf('5.7'),
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
    return 'AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition.MODEL_ELEMENT_URN;
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
    return 'AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition';
}

getProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition, any >> {
    return [MetaAspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithRangeConstraintWithOnlyLowerBoundInclBoundDefinition, any >> {
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


