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

import { AspectWithNumericStructuredValue, } from './AspectWithNumericStructuredValue';
import { Date, } from 'asdas';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithNumericStructuredValue (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithNumericStructuredValue).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithNumericStructuredValue implements StaticMetaClass<AspectWithNumericStructuredValue>, PropertyContainer<AspectWithNumericStructuredValue> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithNumericStructuredValue';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithNumericStructuredValue();


    public static readonly DATE =

        new (class extends DefaultStaticProperty<AspectWithNumericStructuredValue, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithNumericStructuredValue';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'date',
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
    add(YEAR);
    add(

    '-';
);

    add(MONTH);
    add(

    '-';
);

    add(DAY);
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
'date',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithNumericStructuredValue';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithNumericStructuredValue.MODEL_ELEMENT_URN;
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
    return 'AspectWithNumericStructuredValue';
}

getProperties();
:
Array < StaticProperty < AspectWithNumericStructuredValue, any >> {
    return [MetaAspectWithNumericStructuredValue.DATE];
};

getAllProperties();
:
Array < StaticProperty < AspectWithNumericStructuredValue, any >> {
    return this.getProperties();
};


}


