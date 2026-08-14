package com.neojou.humanformationcalculator

/**
 * Application product version — **single source of truth** for UI / About.
 *
 * When bumping a release, update these constants first, then follow
 * [docs/VERSIONING.md](../../../../../docs/VERSIONING.md) (repo root).
 *
 * Scheme (product-facing, not forced SemVer):
 * - [NAME]: `MAJOR.MINOR` (e.g. `"0.1"`) or `MAJOR.MINOR.PATCH` when needed
 * - [DISPLAY]: shown in About, typically `"v" + NAME`
 */
object AppVersion {
    /** Product name (Traditional Chinese). */
    const val APP_NAME: String = "人陣計算機"

    /** English / package short name. */
    const val APP_NAME_EN: String = "Human-Formation Calculator"

    /**
     * Marketing / product version string (no leading `v`).
     * Current release: **0.4**
     */
    const val NAME: String = "0.4"

    /** User-visible label, e.g. `v0.4`. */
    const val DISPLAY: String = "v$NAME"

    /** One-line blurb for About. */
    const val SUMMARY: String = "Q 版人列士兵舉旗加法"
}
