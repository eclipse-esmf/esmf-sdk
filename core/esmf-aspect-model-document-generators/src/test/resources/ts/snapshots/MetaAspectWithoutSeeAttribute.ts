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

import { AspectWithoutSeeAttribute, } from './AspectWithoutSeeAttribute';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithoutSeeAttribute (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithoutSeeAttribute).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithoutSeeAttribute implements StaticMetaClass<AspectWithoutSeeAttribute>, PropertyContainer<AspectWithoutSeeAttribute> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithoutSeeAttribute';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithoutSeeAttribute();


    getModelClass(): string {
        return 'AspectWithoutSeeAttribute';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithoutSeeAttribute.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithoutSeeAttribute';
    }

    getProperties(): Array<StaticProperty<AspectWithoutSeeAttribute, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithoutSeeAttribute, any>> {
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


}


