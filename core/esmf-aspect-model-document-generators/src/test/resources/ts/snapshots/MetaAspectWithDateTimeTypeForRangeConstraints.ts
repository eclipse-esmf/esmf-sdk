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

import { AspectWithDateTimeTypeForRangeConstraints, } from './AspectWithDateTimeTypeForRangeConstraints';
import { Date, } from 'asdas';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithDateTimeTypeForRangeConstraints (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithDateTimeTypeForRangeConstraints).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithDateTimeTypeForRangeConstraints implements StaticMetaClass<AspectWithDateTimeTypeForRangeConstraints>, PropertyContainer<AspectWithDateTimeTypeForRangeConstraints> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithDateTimeTypeForRangeConstraints';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithDateTimeTypeForRangeConstraints();


    public static readonly TEST_PROPERTY_WITH_DATE_TIME =

        new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithDateTimeTypeForRangeConstraints';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testPropertyWithDateTime',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTime',
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
                    urn: this.NAMESPACE + 'MeasurementDateTime',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTime'), Units.fromName('secondUnitOfTime')), new ArrayList<Constraint>() { {
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
           value: _datatypeFactory.newXMLGregorianCalendar

(
    '2000-01-01T14:23:00';
),
    type: new DefaultScalar;
(
    'http://www.w3.org/2001/XMLSchema#dateTime';
),
}

),
Optional.of({
    metaModelBaseAttributes: {},
    value: _datatypeFactory.newXMLGregorianCalendar('2000-01-02T15:23:00'),
    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTime'),
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
'testPropertyWithDateTime',
    isAbstract;
:
false,
})



public static readonly
TEST_PROPERTY_WITH_DATE_TIME_STAMP =

    new (class extends DefaultStaticProperty<AspectWithDateTimeTypeForRangeConstraints, Date> {


        getPropertyType(): string {
            return 'Date';
        }

        getContainingType(): string {
            return 'AspectWithDateTimeTypeForRangeConstraints';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'testPropertyWithDateTimeStamp',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultTrait({
                urn: this.NAMESPACE + 'testWithGregorianCalenderMinGregorianCalenderMaxDateTimeStamp',
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
                urn: this.NAMESPACE + 'MeasurementDateTimeStamp',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTimeStamp'), Units.fromName('secondUnitOfTime')), new ArrayList<Constraint>()
{
    {
        add(new DefaultRangeConstraint({
            isAnonymous: true,
            preferredNames: [],
            descriptions: [],
            see: [],
        }, Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newXMLGregorianCalendar('2000-01-01T14:23:00.66372+14:00'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTimeStamp'),
        }), Optional.of({
            metaModelBaseAttributes: {},
            value: _datatypeFactory.newXMLGregorianCalendar('2000-01-01T15:23:00.66372+14:00'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTimeStamp'),
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
'testPropertyWithDateTimeStamp',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithDateTimeTypeForRangeConstraints';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithDateTimeTypeForRangeConstraints.MODEL_ELEMENT_URN;
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
    return 'AspectWithDateTimeTypeForRangeConstraints';
}

getProperties();
:
Array < StaticProperty < AspectWithDateTimeTypeForRangeConstraints, any >> {
    return [MetaAspectWithDateTimeTypeForRangeConstraints.TEST_PROPERTY_WITH_DATE_TIME, MetaAspectWithDateTimeTypeForRangeConstraints.TEST_PROPERTY_WITH_DATE_TIME_STAMP];
};

getAllProperties();
:
Array < StaticProperty < AspectWithDateTimeTypeForRangeConstraints, any >> {
    return this.getProperties();
};


}


