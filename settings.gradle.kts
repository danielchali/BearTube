/*
 * SPDX-FileCopyrightText: 2025 BearTube contributors <>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "BearTube"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://repo.clojars.org")
    }
}
include(":app") // androidApp
include(":desktopApp")
include("shared")

// Use a local copy of the media extractor by uncommenting the lines below.
// We assume that the app and media extractor have the same parent directory.
// If this is not the case, please change the path in includeBuild().

//    includeBuild("../NewPipeExtractor") {
//        dependencySubstitution {
//            substitute(module("com.github.TeamNewPipe:NewPipeExtractor"))
//                .using(project(":extractor"))
//        }
//    }
