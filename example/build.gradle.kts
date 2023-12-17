/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

plugins {
	idea
	java
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("kr.entree.spigradle") version "2.4.3"
}

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation(rootProject)
	compileOnly(
			group = "org.spigotmc",
			name = "spigot-api",
			version = "${rootProject.properties["spigotVersion"]}-R0.1-SNAPSHOT"
	)
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
		apiVersion = "1.20"

		commands {
			create("test") {
				description = "The main command."
				usage = "/<command>"
			}
			create("parameters") {
				description = "Parameters command"
				usage = "/<command>"
			}
		}
	}
}
