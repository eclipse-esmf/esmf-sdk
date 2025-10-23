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


import { AspectWithEntityEnumerationWithNotExistingEnum, } from './AspectWithEntityEnumerationWithNotExistingEnum';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';
import { MetaSystemState, } from './MetaSystemState';
import { number, string, } from 'asdas';


/*
* Generated class MetaAspectWithEntityEnumerationWithNotExistingEnum (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntityEnumerationWithNotExistingEnum).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEntityEnumerationWithNotExistingEnum implements StaticMetaClass<AspectWithEntityEnumerationWithNotExistingEnum>, PropertyContainer<AspectWithEntityEnumerationWithNotExistingEnum> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEntityEnumerationWithNotExistingEnum';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEntityEnumerationWithNotExistingEnum();


    public static readonly SYSTEM_STATE =

        new (class extends DefaultStaticProperty<AspectWithEntityEnumerationWithNotExistingEnum, ReplacedAspectArtifact> {


            getPropertyType(): string {
                return 'ReplacedAspectArtifact';
            }

            getContainingType(): string {
                return 'AspectWithEntityEnumerationWithNotExistingEnum';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'systemState',
                    preferredNames: [{
                        value: 'System State',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'The state the system is currently in, e.g. heat-up.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                },
                characteristic: new DefaultEnumeration({
                    isAnonymous: true,
                    preferredNames: [{
                        value: 'System States',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Defines which states the system may have.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, DefaultEntity.createDefaultEntity({
                    urn: this.NAMESPACE + 'SystemState',
                    preferredNames: [{
                        value: 'System State',
                        languageTag: 'en',
                    },
                    ],
                    descriptions: [{
                        value: 'Represents a specific state the system may have.',
                        languageTag: 'en',
                    },
                    ],
                    see: [],
                }, MetaSystemState.INSTANCE.getProperties(), Optional.empty()), new ArrayList<Value>() { {
    add(

    new;

    DefaultEntityInstance({
                              urn: this.NAMESPACE

+
    'CoolDown';
,
    preferredNames: [];
,
    descriptions: [];
,
    see: [];
,
}

,
new HashMap<Property, Value>();
{
    {
        put(MetaSystemState.STATE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('3'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaSystemState.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'CoolDown',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'SystemState',
    preferredNames: [{
        value: 'System State',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Represents a specific state the system may have.',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaSystemState.INSTANCE.getProperties(), Optional.empty());
))
add(new DefaultEntityInstance({
    urn: this.NAMESPACE + 'HeatUp',
    preferredNames: [],
    descriptions: [],
    see: [],
}, new HashMap<Property, Value>()
{
    {
        put(MetaSystemState.STATE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('4'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaSystemState.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'HeatUp',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'SystemState',
    preferredNames: [{
        value: 'System State',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Represents a specific state the system may have.',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaSystemState.INSTANCE.getProperties(), Optional.empty());
))
add(new DefaultEntityInstance({
    urn: this.NAMESPACE + 'Off',
    preferredNames: [],
    descriptions: [],
    see: [],
}, new HashMap<Property, Value>()
{
    {
        put(MetaSystemState.STATE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('0'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaSystemState.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'Off',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'SystemState',
    preferredNames: [{
        value: 'System State',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Represents a specific state the system may have.',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaSystemState.INSTANCE.getProperties(), Optional.empty());
))
add(new DefaultEntityInstance({
    urn: this.NAMESPACE + 'On',
    preferredNames: [],
    descriptions: [],
    see: [],
}, new HashMap<Property, Value>()
{
    {
        put(MetaSystemState.STATE, {
            metaModelBaseAttributes: {},
            value: Short.parseShort('1'),
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#short'),
        });
        put(MetaSystemState.DESCRIPTION, {
            metaModelBaseAttributes: {},
            value: 'On',
            type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
        });
    }
}
,
DefaultEntity.createDefaultEntity({
    urn: this.NAMESPACE + 'SystemState',
    preferredNames: [{
        value: 'System State',
        languageTag: 'en',
    },
    ],
    descriptions: [{
        value: 'Represents a specific state the system may have.',
        languageTag: 'en',
    },
    ],
    see: [],
}, MetaSystemState.INSTANCE.getProperties(), Optional.empty());
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
'systemState',
    isAbstract;
:
false,
})



getModelClass();
:
string;
{
    return 'AspectWithEntityEnumerationWithNotExistingEnum';
}

getAspectModelUrn();
:
string;
{
    return MetaAspectWithEntityEnumerationWithNotExistingEnum.MODEL_ELEMENT_URN;
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
    return 'AspectWithEntityEnumerationWithNotExistingEnum';
}

getProperties();
:
Array < StaticProperty < AspectWithEntityEnumerationWithNotExistingEnum, any >> {
    return [MetaAspectWithEntityEnumerationWithNotExistingEnum.SYSTEM_STATE];
};

getAllProperties();
:
Array < StaticProperty < AspectWithEntityEnumerationWithNotExistingEnum, any >> {
    return this.getProperties();
};


getPreferredNames();
:
Array < LangString > {
    return [
        {value: 'Aspect with entity enumeration', languageTag: 'en'},
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


