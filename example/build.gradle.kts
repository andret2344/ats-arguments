plugins {
	idea
	java
	alias(libs.plugins.shadow)
	alias(libs.plugins.spigradle)
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
	}
}

repositories {
	mavenCentral()
	mavenLocal()
	spigotRepos {
		spigotmc()
	}
}

dependencies {
	implementation(project(":"))
	compileOnly(libs.spigot.api)
}

tasks {
	compileJava {
		options.compilerArgs.addAll(listOf("-parameters", "-g", "-Xlint:deprecation", "-Xlint:unchecked"))
	}

	shadowJar {
		dependencies {
			rootProject
		}
	}

	build {
		dependsOn("shadowJar")
	}

	spigot {
		authors = listOf("Andret")
		apiVersion = "26.1"

		commands {
			register("test") {
				description = "The main command."
				usage = "/<command>"
			}
			register("parameters") {
				description = "Parameters command"
				usage = "/<command>"
			}
		}
	}
}
