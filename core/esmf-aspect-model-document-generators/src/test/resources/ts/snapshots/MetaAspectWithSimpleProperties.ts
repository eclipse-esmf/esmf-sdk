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

import { AspectWithSimpleProperties, } from './AspectWithSimpleProperties';
import { Date, number, string, } from 'asdas';
import { DefaultStaticProperty, } from './core/staticConstraintProperty';


/*
* Generated class MetaAspectWithSimpleProperties (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleProperties).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimpleProperties implements StaticMetaClass<AspectWithSimpleProperties>, PropertyContainer<AspectWithSimpleProperties> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimpleProperties';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithSimpleProperties();


    public static readonly TEST_STRING =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testString',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Text',
                        preferredNames: [{
                            value: 'Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: 'Example Value Test',
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#string'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testString',
                isAbstract: false,
            });


    public static readonly TEST_INT =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testInt',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'Int',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: Integer.valueOf('3'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#int'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testInt',
                isAbstract: false,
            });


    public static readonly TEST_FLOAT =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testFloat',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'Float',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: Float.valueOf('2.25'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#float'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testFloat',
                isAbstract: false,
            });


    public static readonly TEST_LOCAL_DATE_TIME =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testLocalDateTime',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'LocalDateTime',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {
                    metaModelBaseAttributes: {},
                    value: _datatypeFactory.newXMLGregorianCalendar('2018-02-28T14:23:32.918'),
                    type: new DefaultScalar('http://www.w3.org/2001/XMLSchema#dateTime'),
                },
                optional: false,
                notInPayload: false,
                payloadName: 'testLocalDateTime',
                isAbstract: false,
            });


    public static readonly TEST_LOCAL_DATE_TIME_WITHOUT_EXAMPLE =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testLocalDateTimeWithoutExample',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'LocalDateTime',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testLocalDateTimeWithoutExample',
                isAbstract: false,
            });


    public static readonly TEST_DURATION_WITHOUT_EXAMPLE =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'testDurationWithoutExample',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.NAMESPACE + 'Duration',
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'testDurationWithoutExample',
                isAbstract: false,
            });


    public static readonly RANDOM_VALUE =

        new (class extends DefaultStaticProperty<AspectWithSimpleProperties, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleProperties';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'randomValue',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Text',
                        preferredNames: [{
                            value: 'Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
                            languageTag: 'en',
                        },
                        ],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'randomValue',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithSimpleProperties';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithSimpleProperties.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithSimpleProperties';
    }

    getProperties(): Array<StaticProperty<AspectWithSimpleProperties, any>> {
        return [MetaAspectWithSimpleProperties.TEST_STRING, MetaAspectWithSimpleProperties.TEST_INT, MetaAspectWithSimpleProperties.TEST_FLOAT, MetaAspectWithSimpleProperties.TEST_LOCAL_DATE_TIME, MetaAspectWithSimpleProperties.TEST_LOCAL_DATE_TIME_WITHOUT_EXAMPLE, MetaAspectWithSimpleProperties.TEST_DURATION_WITHOUT_EXAMPLE, MetaAspectWithSimpleProperties.RANDOM_VALUE];
    }

    getAllProperties(): Array<StaticProperty<AspectWithSimpleProperties, any>> {
        return this.getProperties();
    }


}


