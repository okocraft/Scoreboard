plugins {
    `java-library`
    alias(libs.plugins.jcommon)
    alias(libs.plugins.bundler)
}

jcommon {
    javaVersion = JavaVersion.VERSION_25

    setupPaperRepository()
    commonRepositories {
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    commonDependencies {
        compileOnly(libs.configurate.yaml)
        compileOnly(libs.paper.api)
        compileOnly(libs.placeholder.api)
        implementation(libs.mcmsgdef)
    }
}

bundler {
    replacePluginVersionForPaper(version)
    copyToRootBuildDirectory("Scoreboard-$version.jar")
}
