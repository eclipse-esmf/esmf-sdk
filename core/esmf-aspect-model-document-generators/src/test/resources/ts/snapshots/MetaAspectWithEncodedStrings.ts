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

import { AspectWithEncodedStrings, } from './AspectWithEncodedStrings';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithEncodedStrings (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEncodedStrings).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithEncodedStrings implements StaticMetaClass<AspectWithEncodedStrings>, PropertyContainer<AspectWithEncodedStrings> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithEncodedStrings';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithEncodedStrings();


    getModelClass(): string {
        return 'AspectWithEncodedStrings';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithEncodedStrings.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithEncodedStrings';
    }

    getProperties(): Array<StaticProperty<AspectWithEncodedStrings, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithEncodedStrings, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'VGhpcyBpcyBhbiBBc3BlY3Qgd2l0aCBlbmNvZGVkIHRleHQu', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'Aspect With encoded text', languageTag: 'en'},
        ];
    }


}


