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

import { AspectWithTwoStructuredValues, } from './AspectWithTwoStructuredValues';
import { Date, } from 'asdas';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithTwoStructuredValues (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithTwoStructuredValues).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithTwoStructuredValues implements StaticMetaClass<AspectWithTwoStructuredValues>, PropertyContainer<AspectWithTwoStructuredValues> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithTwoStructuredValues';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithTwoStructuredValues();


    public static readonly START_DATE =

        new (class extends DefaultStaticProperty<AspectWithTwoStructuredValues, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithTwoStructuredValues';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'startDate',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultStructuredValue({
                    urn: this.NAMESPACE + 'StructuredDate',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#date'), '(\\d{4})-(\\d{2})-(\\d{2})', new ArrayList<Object>() { {
    add(START_DATE_YEAR);
    add(

    '-';
);

    add(START_DATE_MONTH);
    add(

    '-';
);

    add(START_DATE_DAY);
}
})
,
{
    {
    }
,
    _datatypeFactory.newXMLGregorianCalendar('2019-09-27'),
        type;
:
    new DefaultScalar('http://www.w3.org/2001/XMLSchema#date'),
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'startDate',
    isAbstract;
:
false,
})



public static readonly
END_DATE =

    new (class extends DefaultStaticProperty<AspectWithTwoStructuredValues, Date> {


        getPropertyType(): string {
            return 'Date';
        }

        getContainingType(): string {
            return 'AspectWithTwoStructuredValues';
        }


    })(
        {
            metaModelBaseAttributes: {
                urn: this.NAMESPACE + 'endDate',
                preferredNames: [],
                descriptions: [],
                see: [],
            },
            characteristic: new DefaultStructuredValue({
                urn: this.NAMESPACE + 'StructuredDate',
                preferredNames: [],
                descriptions: [],
                see: [],
            }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#date'), '(\\d{4})-(\\d{2})-(\\d{2})', new ArrayList<Object>()
{
    {
        add(END_DATE_YEAR);
        add('-');
        add(END_DATE_MONTH);
        add('-');
        add(END_DATE_DAY);
    }
}
)
,
{
    {
    }
,
    _datatypeFactory.newXMLGregorianCalendar('2019-09-27'),
        type;
:
    new DefaultScalar('http://www.w3.org/2001/XMLSchema#date'),
}
,
false,
    notInPayload;
:
false,
    payloadName;
:
'endDate',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithTwoStructuredValues';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithTwoStructuredValues.MODEL_ELEMENT_URN;
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
    return 'AspectWithTwoStructuredValues';
}

getProperties();
:
Array < StaticProperty < AspectWithTwoStructuredValues, any >> {
    return [MetaAspectWithTwoStructuredValues.START_DATE, MetaAspectWithTwoStructuredValues.END_DATE];
};

getAllProperties();
:
Array < StaticProperty < AspectWithTwoStructuredValues, any >> {
    return this.getProperties();
};


}


