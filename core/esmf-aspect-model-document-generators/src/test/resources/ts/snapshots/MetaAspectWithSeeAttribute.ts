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

import { AspectWithSeeAttribute, } from './AspectWithSeeAttribute';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithSeeAttribute implements StaticMetaClass<AspectWithSeeAttribute>, PropertyContainer<AspectWithSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithSeeAttribute();


    getModelClass(): string {
        return 'AspectWithSeeAttribute';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithSeeAttribute.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithSeeAttribute';
    }

    getProperties(): Array<StaticProperty<AspectWithSeeAttribute, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithSeeAttribute, any>> {
        return this.getProperties();
    }


    getPreferredNames(): Array<LangString> {
        return [
            {value: 'Test Aspect', languageTag: 'en'},
        ];
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'This is a test Aspect.', languageTag: 'en'},
        ];
    }

    getSee(): Array<String> {
        return [
            'http://example.com/',
        ];
    }

}


