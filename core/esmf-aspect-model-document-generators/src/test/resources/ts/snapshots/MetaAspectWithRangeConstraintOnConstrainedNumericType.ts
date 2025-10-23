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

import { AspectWithRangeConstraintOnConstrainedNumericType, } from './AspectWithRangeConstraintOnConstrainedNumericType';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { number, } from 'asdas';


/*
* Generated class MetaAspectWithRangeConstraintOnConstrainedNumericType (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithRangeConstraintOnConstrainedNumericType).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithRangeConstraintOnConstrainedNumericType implements StaticMetaClass<AspectWithRangeConstraintOnConstrainedNumericType>, PropertyContainer<AspectWithRangeConstraintOnConstrainedNumericType> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithRangeConstraintOnConstrainedNumericType';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithRangeConstraintOnConstrainedNumericType();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithRangeConstraintOnConstrainedNumericType, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithRangeConstraintOnConstrainedNumericType';
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
                }, new DefaultMeasurement({
                    urn: this.NAMESPACE + 'Measurement',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'), Units.fromName('metrePerSecond')), new ArrayList<Constraint>() { {
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
           value: Short.parseShort

(
    '5';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#short';
),
}

),
Optional.empty(), BoundDefinition.AT_LEAST, BoundDefinition.OPEN;
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
    return 'AspectWithRangeConstraintOnConstrainedNumericType';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithRangeConstraintOnConstrainedNumericType.MODEL_ELEMENT_URN;
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
    return 'AspectWithRangeConstraintOnConstrainedNumericType';
}

getProperties();
:
Array < StaticProperty < AspectWithRangeConstraintOnConstrainedNumericType, any >> {
    return [MetaAspectWithRangeConstraintOnConstrainedNumericType.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithRangeConstraintOnConstrainedNumericType, any >> {
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


