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

import { AspectWithMultiLineDescription, } from './AspectWithMultiLineDescription';
import { LangString, } from './core/langString';


/*
* Generated class MetaAspectWithMultiLineDescription (urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithMultiLineDescription).
* Generated "esmf-sdk DEV-SNAPSHOT", date = "replaced"
*/
import { StaticMetaClass, PropertyContainer, StaticProperty } from './core/staticConstraintProperty';
import { KnownVersion, KnownVersionUtils } from './core/knownVersion';

export class MetaAspectWithMultiLineDescription implements StaticMetaClass<AspectWithMultiLineDescription>, PropertyContainer<AspectWithMultiLineDescription> {
    public static readonly NAMESPACE = 'urn:samm:org.eclipse.esmf.test:1.0.0#';
    public static readonly MODEL_ELEMENT_URN = this.NAMESPACE + 'AspectWithMultiLineDescription';

    private static readonly CHARACTERISTIC_NAMESPACE = 'urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0';

    public static readonly INSTANCE = new MetaAspectWithMultiLineDescription();


    getModelClass(): string {
        return 'AspectWithMultiLineDescription';
    }

    getAspectModelUrn(): string {
        return MetaAspectWithMultiLineDescription.MODEL_ELEMENT_URN;
    }

    getMetaModelVersion(): KnownVersion {
        return KnownVersionUtils.getLatest();
    }

    getName(): string {
        return 'AspectWithMultiLineDescription';
    }

    getProperties(): Array<StaticProperty<AspectWithMultiLineDescription, any>> {
        return [];
    }

    getAllProperties(): Array<StaticProperty<AspectWithMultiLineDescription, any>> {
        return this.getProperties();
    }


    getDescriptions(): Array<LangString> {
        return [
            {value: 'This\nis\na\ntest', languageTag: 'en'},
        ];
    }


}


