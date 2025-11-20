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

/**
 * The known versions of the Aspect Meta Model
 */
export enum KnownVersion {
    SAMM_1_0_0 = '1.0.0',
    SAMM_2_0_0 = '2.0.0',
    SAMM_2_1_0 = '2.1.0',
    SAMM_2_2_0 = '2.2.0',
}

export class KnownVersionUtils {
    /**
     * Converts the enum value to its version string.
     *
     * @param version KnownVersion enum value
     * @returns version string representation
     */
    static toVersionString(version: KnownVersion): string {
        return version;
    }

    /**
     * Finds a KnownVersion enum value from a version string.
     *
     * @param version Version string to match
     * @returns KnownVersion enum value or undefined if not found
     */
    static fromVersionString(version: string): KnownVersion | undefined {
        const filtered = Object.keys(KnownVersion)
            .map((key) => KnownVersion[key as keyof typeof KnownVersion])
            .filter((v) => v === version);
        return filtered.length > 0 ? filtered[0] : undefined;
    }

    /**
     * Returns the latest KnownVersion.
     *
     * @returns Latest KnownVersion enum value
     */
    static getLatest(): KnownVersion {
        const versions = Object.keys(KnownVersion).map(
            (key) => KnownVersion[key as keyof typeof KnownVersion]
        );
        return versions[versions.length - 1];
    }

    /**
     * Returns all KnownVersion enum values as a list.
     *
     * @returns List of KnownVersion enum values
     */
    static getVersions(): KnownVersion[] {
        return Object.keys(KnownVersion).map(
            (key) => KnownVersion[key as keyof typeof KnownVersion]
        );
    }

    /**
     * Checks if a version is newer than another version.
     *
     * @param version Current KnownVersion
     * @param otherVersion Other KnownVersion to compare
     * @returns True if the current version is newer, false otherwise
     */
    static isNewerThan(version: KnownVersion, otherVersion: KnownVersion): boolean {
        return version > otherVersion;
    }

    /**
     * Checks if a version is older than another version.
     *
     * @param version Current KnownVersion
     * @param otherVersion Other KnownVersion to compare
     * @returns True if the current version is older, false otherwise
     */
    static isOlderThan(version: KnownVersion, otherVersion: KnownVersion): boolean {
        return version < otherVersion;
    }
}