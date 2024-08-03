plugins {
    id("net.neoforged.gradle.userdev")
    id("com.github.minecraftschurlimods.helperplugin")
}

helper.withTestSourceSet()

dependencies {
    implementation(helper.neoforge())
    compileOnly("org.jetbrains:annotations:23.0.0")
}

helper.withCommonRuns()
runs.configureEach {
    programArguments("--fml.mixin", "betterhudlib.mixins.json")
}

helper.publication.pom {
    organization {
        name = "Minecraftschurli Mods"
        url = "https://github.com/MinecraftschurliMods"
    }
    developers {
        developer {
            id = "minecraftschurli"
            name = "Minecraftschurli"
            email = "minecraftschurli@gmail.com"
            organization = "Minecraftschurli Mods"
            organizationUrl = "https://github.com/Minecraftschurli"
            timezone = "Europe/Vienna"
        }
    }
}
