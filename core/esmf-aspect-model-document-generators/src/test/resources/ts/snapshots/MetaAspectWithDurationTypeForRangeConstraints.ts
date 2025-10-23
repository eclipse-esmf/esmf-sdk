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

import { AspectWithDurationTypeForRangeConstraints, } from './AspectWithDurationTypeForRangeConstraints';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { any, } from 'asdas';


/*
* Generated class MetaAspectWithDurationTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDurationTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDurationTypeForRangeConstraints implements StaticMetaClass<AspectWithDurationTypeForRangeConstraints>, PropertyContainer<AspectWithDurationTypeForRangeConstraints> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDurationTypeForRangeConstraints';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithDurationTypeForRangeConstraints();


    public static readonly TEST_PROPERTY_WITH_DAY_TIME_DURATION =

        new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithDurationTypeForRangeConstraints';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testPropertyWithDayTimeDuration',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'testWithDurationMinDurationMaxDayTimeDuration',
                    preferredNames: [{
                        value: 'Test Range',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Test Range',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, new DefaultMeasurement({
                    urn: this.NAMESPACE + 'MeasurementDayTimeDuration',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#dayTimeDuration'), Units.fromName('hour')), new ArrayList<Constraint>() { {
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
           value: _datatypeFactory.newDuration

(
    'P1DT5H';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#dayTimeDuration';
),
}

),
Optional.of({
    metaModelBaseAttributes: {},
    value: _datatypeFactory.newDuration('P1DT8H'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dayTimeDuration'),
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
'testPropertyWithDayTimeDuration',
    isAbstract;
:
false,
})



public static readonly
TEST_PROPERTY_WITH_DURATION =

    new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any> {


        getPropertyType(): string {
            return 'any';
        }

        getContainingType(): string {
            return 'AspectWithDurationTypeForRangeConstraints';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'testPropertyWithDuration',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultTrait({
                urn: this.NAMESPACE + 'testWithDurationMinDurationMaxDuration',
                preferredNames: [{
                    value: 'Test Range',
                    languageTag: 'en',
                },
                ],
                descriptions: [{
                    value: 'Test Range',
                    languageTag: 'en',
                },
                ],
                see: [],
            }, new DefaultMeasurement({
                urn: this.NAMESPACE + 'MeasurementDuration',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#duration'), Units.fromName('hour')), new ArrayList<Constraint>()
{
    {
        add(new DefaultRangeConstraint({
            isAnonymous: true,
            preferredNames: [],
            descriptions: [],
            see: [],
        }, Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newDuration('PT1H5M0S'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#duration'),
        }), Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newDuration('PT1H5M3S'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#duration'),
        }), BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST));
    }
}
)
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
'testPropertyWithDuration',
    isAbstract;
:
false,
})



public static readonly
TEST_PROPERTY_WITH_YEAR_MONTH_DURATION =

    new (class extends DefaultStaticProperty<AspectWithDurationTypeForRangeConstraints, any> {


        getPropertyType(): string {
            return 'any';
        }

        getContainingType(): string {
            return 'AspectWithDurationTypeForRangeConstraints';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'testPropertyWithYearMonthDuration',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultTrait({
                urn: this.NAMESPACE + 'testWithDurationMinDurationMaxYearMonthDuration',
                preferredNames: [{
                    value: 'Test Range',
                    languageTag: 'en',
                },
                ],
                descriptions: [{
                    value: 'Test Range',
                    languageTag: 'en',
                },
                ],
                see: [],
            }, new DefaultMeasurement({
                urn: this.NAMESPACE + 'MeasurementYearMonthDuration',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#yearMonthDuration'), Units.fromName('hour')), new ArrayList<Constraint>()
{
    {
        add(new DefaultRangeConstraint({
            isAnonymous: true,
            preferredNames: [],
            descriptions: [],
            see: [],
        }, Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newDuration('P5Y2M'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#yearMonthDuration'),
        }), Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newDuration('P5Y3M'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#yearMonthDuration'),
        }), BoundDefinition.AT_LEAST, BoundDefinition.AT_MOST));
    }
}
)
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
'testPropertyWithYearMonthDuration',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithDurationTypeForRangeConstraints';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithDurationTypeForRangeConstraints.MODEL_ELEMENT_URN;
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
    return 'AspectWithDurationTypeForRangeConstraints';
}

getProperties();
:
Array < StaticProperty < AspectWithDurationTypeForRangeConstraints, any >> {
    return [MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_DAY_TIME_DURATION, MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_DURATION, MetaAspectWithDurationTypeForRangeConstraints.TEST_PROPERTY_WITH_YEAR_MONTH_DURATION];
};

getAllProperties();
:
Array < StaticProperty < AspectWithDurationTypeForRangeConstraints, any >> {
    return this.getProperties();
};


}


