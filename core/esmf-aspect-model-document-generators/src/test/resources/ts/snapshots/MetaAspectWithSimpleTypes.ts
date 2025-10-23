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

import { AspectWithSimpleTypes, } from './AspectWithSimpleTypes';
import { DefaultStaticProperty, StaticUnitProperty, Unit, } from './core/staticConstraintProperty';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithSimpleTypes (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSimpleTypes).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSimpleTypes implements StaticMetaClass<AspectWithSimpleTypes>, PropertyContainer<AspectWithSimpleTypes> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSimpleTypes';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithSimpleTypes();


    public static readonly ANY_URI_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'anyUriProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [{
                            value: 'This is an anyURI characteristic.',
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
                payloadName: 'anyUriProperty',
                isAbstract: false,
            });


    public static readonly BASE64_BINARY_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'base64BinaryProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [{
                            value: 'This is a base64Binary characteristic.',
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
                payloadName: 'base64BinaryProperty',
                isAbstract: false,
            });


    public static readonly BOOLEAN_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, boolean> {


            getPropertyType(): string {
                return 'boolean';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'booleanProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Boolean',
                        preferredNames: [{
                            value: 'Boolean',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Represents a boolean value (i.e. a "flag").',
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
                payloadName: 'booleanProperty',
                isAbstract: false,
            });


    public static readonly BYTE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'byteProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [{
                            value: 'This is a byteProperty characteristic.',
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
                payloadName: 'byteProperty',
                isAbstract: false,
            });


    public static readonly CURIE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'curieProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#UnitReference',
                        preferredNames: [{
                            value: 'Unit Reference',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property containing a reference to one of the units in the Unit Catalog.',
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
                payloadName: 'curieProperty',
                isAbstract: false,
            });


    public static readonly DATE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'dateProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'dateProperty',
                isAbstract: false,
            });


    public static readonly DATE_TIME_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'dateTimeProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#Timestamp',
                        preferredNames: [{
                            value: 'Timestamp',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains the date and time with an optional timezone.',
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
                payloadName: 'dateTimeProperty',
                isAbstract: false,
            });


    public static readonly DATE_TIME_STAMP_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'dateTimeStampProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'dateTimeStampProperty',
                isAbstract: false,
            });


    public static readonly DAY_TIME_DURATION =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'dayTimeDuration',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'dayTimeDuration',
                isAbstract: false,
            });


    public static readonly DECIMAL_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'decimalProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'decimalProperty',
                isAbstract: false,
            });


    public static readonly DOUBLE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'doubleProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'doubleProperty',
                isAbstract: false,
            });


    public static readonly DURATION_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'durationProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'durationProperty',
                isAbstract: false,
            });


    public static readonly FLOAT_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'floatProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'floatProperty',
                isAbstract: false,
            });


    public static readonly G_DAY_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'gDayProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'gDayProperty',
                isAbstract: false,
            });


    public static readonly G_MONTH_DAY_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'gMonthDayProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'gMonthDayProperty',
                isAbstract: false,
            });


    public static readonly G_MONTH_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'gMonthProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'gMonthProperty',
                isAbstract: false,
            });


    public static readonly G_YEAR_MONTH_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'gYearMonthProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'gYearMonthProperty',
                isAbstract: false,
            });


    public static readonly G_YEAR_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'gYearProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'gYearProperty',
                isAbstract: false,
            });


    public static readonly HEX_BINARY_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'hexBinaryProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'hexBinaryProperty',
                isAbstract: false,
            });


    public static readonly INT_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'intProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'intProperty',
                isAbstract: false,
            });


    public static readonly INTEGER_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'integerProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'integerProperty',
                isAbstract: false,
            });


    public static readonly LANG_STRING_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, LangString> {


            getPropertyType(): string {
                return 'LangString';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'langStringProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        urn: this.CHARACTERISTIC_NAMESPACE + '#MultiLanguageText',
                        preferredNames: [{
                            value: 'Multi-Language Text',
                            languageTag: 'en',
                        },
                        ],
                        descriptions: [{
                            value: 'Describes a Property which contains plain text in multiple languages. This is intended exclusively for human readable strings, not for identifiers, measurement values, etc.',
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
                payloadName: 'langStringProperty',
                isAbstract: false,
            });


    public static readonly LONG_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'longProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'longProperty',
                isAbstract: false,
            });


    public static readonly NEGATIVE_INTEGER_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'negativeIntegerProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'negativeIntegerProperty',
                isAbstract: false,
            });


    public static readonly NON_NEGATIVE_INTEGER_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'nonNegativeIntegerProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'nonNegativeIntegerProperty',
                isAbstract: false,
            });


    public static readonly NON_POSITIVE_INTEGER =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'nonPositiveInteger',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'nonPositiveInteger',
                isAbstract: false,
            });


    public static readonly POSITIVE_INTEGER_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'positiveIntegerProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'positiveIntegerProperty',
                isAbstract: false,
            });


    public static readonly SHORT_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'shortProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'shortProperty',
                isAbstract: false,
            });


    public static readonly STRING_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'stringProperty',
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
                payloadName: 'stringProperty',
                isAbstract: false,
            });


    public static readonly TIME_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, Date> {


            getPropertyType(): string {
                return 'Date';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'timeProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'timeProperty',
                isAbstract: false,
            });


    public static readonly UNSIGNED_BYTE_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'unsignedByteProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'unsignedByteProperty',
                isAbstract: false,
            });


    public static readonly UNSIGNED_INT_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'unsignedIntProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'unsignedIntProperty',
                isAbstract: false,
            });


    public static readonly UNSIGNED_LONG_PROPERTY =


        new (class extends StaticUnitProperty<AspectWithSimpleTypes, string> {


            getPropertyType(): string {
                return 'string';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'unsignedLongProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: new DefaultQuantifiable({
                    isAnonymous: true,
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                }, new DefaultScalar('http://www.w3.org/2001/XMLSchema#unsignedLong'), Units.fromName('metre'))
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'unsignedLongProperty',
                isAbstract: false,
            });


    public static readonly UNSIGNED_SHORT_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, number> {


            getPropertyType(): string {
                return 'number';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'unsignedShortProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'unsignedShortProperty',
                isAbstract: false,
            });


    public static readonly YEAR_MONTH_DURATION_PROPERTY =

        new (class extends DefaultStaticProperty<AspectWithSimpleTypes, any> {


            getPropertyType(): string {
                return 'any';
            }

            getContainingType(): string {
                return 'AspectWithSimpleTypes';
            }


        })(
            {
                metaModelBaseAttributes: {
                    urn: this.NAMESPACE + 'yearMonthDurationProperty',
                    preferredNames: [],
                    descriptions: [],
                    see: [],
                },
                characteristic: {
                    metaModelBaseAttributes: {
                        isAnonymous: true,
                        preferredNames: [],
                        descriptions: [],
                        see: [],
                    },
                }
                ,
                exampleValue: {},
                optional: false,
                notInPayload: false,
                payloadName: 'yearMonthDurationProperty',
                isAbstract: false,
            });


    getModelClass(): string {
        return 'AspectWithSimpleTypes';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithSimpleTypes.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithSimpleTypes';
    }

    getProperties(): Array<StaticProperty<AspectWithSimpleTypes, any>> {
        return [MetaAspectWithSimpleTypes.ANY_URI_PROPERTY, MetaAspectWithSimpleTypes.BASE64_BINARY_PROPERTY, MetaAspectWithSimpleTypes.BOOLEAN_PROPERTY, MetaAspectWithSimpleTypes.BYTE_PROPERTY, MetaAspectWithSimpleTypes.CURIE_PROPERTY, MetaAspectWithSimpleTypes.DATE_PROPERTY, MetaAspectWithSimpleTypes.DATE_TIME_PROPERTY, MetaAspectWithSimpleTypes.DATE_TIME_STAMP_PROPERTY, MetaAspectWithSimpleTypes.DAY_TIME_DURATION, MetaAspectWithSimpleTypes.DECIMAL_PROPERTY, MetaAspectWithSimpleTypes.DOUBLE_PROPERTY, MetaAspectWithSimpleTypes.DURATION_PROPERTY, MetaAspectWithSimpleTypes.FLOAT_PROPERTY, MetaAspectWithSimpleTypes.G_DAY_PROPERTY, MetaAspectWithSimpleTypes.G_MONTH_DAY_PROPERTY, MetaAspectWithSimpleTypes.G_MONTH_PROPERTY, MetaAspectWithSimpleTypes.G_YEAR_MONTH_PROPERTY, MetaAspectWithSimpleTypes.G_YEAR_PROPERTY, MetaAspectWithSimpleTypes.HEX_BINARY_PROPERTY, MetaAspectWithSimpleTypes.INT_PROPERTY, MetaAspectWithSimpleTypes.INTEGER_PROPERTY, MetaAspectWithSimpleTypes.LANG_STRING_PROPERTY, MetaAspectWithSimpleTypes.LONG_PROPERTY, MetaAspectWithSimpleTypes.NEGATIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.NON_NEGATIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.NON_POSITIVE_INTEGER, MetaAspectWithSimpleTypes.POSITIVE_INTEGER_PROPERTY, MetaAspectWithSimpleTypes.SHORT_PROPERTY, MetaAspectWithSimpleTypes.STRING_PROPERTY, MetaAspectWithSimpleTypes.TIME_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_BYTE_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_INT_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_LONG_PROPERTY, MetaAspectWithSimpleTypes.UNSIGNED_SHORT_PROPERTY, MetaAspectWithSimpleTypes.YEAR_MONTH_DURATION_PROPERTY];
    }

    getAllProperties(): Array<StaticProperty<AspectWithSimpleTypes, any>> {
        return this.getProperties();
    }


}


