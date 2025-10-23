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

import { AspectWithFixedPoint, } from './AspectWithFixedPoint';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithFixedPoint (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithFixedPoint).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithFixedPoint implements StaticMetaClass<AspectWithFixedPoint>, PropertyContainer<AspectWithFixedPoint> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithFixedPoint';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithFixedPoint();


    public static readonly TEST_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithFixedPoint, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithFixedPoint';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultTrait({
                    urn: this.NAMESPACE + 'TestFixedPoint',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultMeasurement({
                    urn: this.NAMESPACE + 'Measurement',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#decimal'), Units.fromName('metrePerSecond')), new ArrayList<Constraint>() { {
    add(

    new;

    DefaultFixedPointConstraint({
                                    isAnonymous: true,
                                    preferredNames: [{
                                        value: 'Test Fixed Point',
                                        languageTag: 'en',
                                    },
                                    ],
                                    descriptions: [{
                                        value: 'This is a test fixed point constraint.',
                                        languageTag: 'en',
                                    },
                                    ],
                                    see: ['http://example.com/',
                                    ],
                                },

    5;
,
    3;
));
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
    return 'AspectWithFixedPoint';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithFixedPoint.MODEL_ELEMENT_URN;
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
    return 'AspectWithFixedPoint';
}

getProperties();
:
Array < StaticProperty < AspectWithFixedPoint, any >> {
    return [MetaAspectWithFixedPoint.TEST_PROPERTY];
};

getAllProperties();
:
Array < StaticProperty < AspectWithFixedPoint, any >> {
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


