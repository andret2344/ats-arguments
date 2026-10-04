plugins {
	idea
	java
	jacoco
	`maven-publish`
	checkstyle
	id("org.barfuin.gradle.jacocolog") version "3.1.0"
	id("org.jetbrains.changelog") version "2.5.0"
}

sourceSets {
	create("integration") {
		java.srcDir("src/integration/java")
		resources.srcDir("src/integration/resources")
		compileClasspath += sourceSets["main"].output + sourceSets["test"].output
		runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
	}
}

configurations {
	named("integrationImplementation") {
		extendsFrom(configurations.getByName("testImplementation"))
	}
	named("integrationRuntimeOnly") {
		extendsFrom(configurations.getByName("testRuntimeOnly"))
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
	compileOnly(
			group = "org.spigotmc",
			name = "spigot-api",
			version = "${project.properties["spigotVersion"]}-R0.1-SNAPSHOT"
	)
	compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.30")
	implementation(group = "org.jetbrains", name = "annotations", version = "24.1.0")
	annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.30")

	testCompileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.30")
	testImplementation(group = "org.assertj", name = "assertj-core", version = "3.24.2")
	testImplementation(group = "org.mockito", name = "mockito-core", version = "5.8.0")
	testImplementation(group = "org.mockito", name = "mockito-inline", version = "5.2.0")
	testImplementation(group = "org.mockito", name = "mockito-testng", version = "0.5.2")
	testImplementation(
			group = "org.spigotmc",
			name = "spigot-api",
			version = "${project.properties["spigotVersion"]}-R0.1-SNAPSHOT"
	)
	testImplementation(group = "org.testng", name = "testng", version = "7.8.0")
	testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.30")
}

tasks {
	compileJava {
		options.compilerArgs.addAll(
				listOf(
						"-parameters",
						"-g",
						"-Xlint:deprecation",
						"-Xlint:unchecked"
				)
		)
	}

	compileTestJava {
		options.compilerArgs.addAll(
				listOf(
						"-parameters",
						"-g",
						"-Xlint:deprecation",
						"-Xlint:unchecked"
				)
		)
	}

	checkstyleMain {
		exclude("example/**.java")
	}

	checkstyleTest {
		exclude("**")
	}

	named<Checkstyle>("checkstyleIntegration") {
		exclude("**")
	}

	create<Test>("integrationTest") {
		description = "Runs the integration tests."
		group = "verification"
		testClassesDirs = sourceSets["integration"].output.classesDirs
		classpath = sourceSets["integration"].runtimeClasspath
		outputs.upToDateWhen { false }
		useTestNG()
	}

	check {
		dependsOn("integrationTest")
	}

	test {
		jacoco {
			exclude("**/Material")
		}
		useTestNG()
		finalizedBy(jacocoTestCoverageVerification, jacocoAggregatedReport)
	}

	jacocoTestReport {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it).matching {
				exclude("example/**")
			}
		}))
	}

	jacocoTestCoverageVerification {
		violationRules {
			rule {
				classDirectories.setFrom(jacocoTestReport.get().classDirectories)
				limit {
					minimum = "1".toBigDecimal()
				}
			}
		}
	}

	javadoc {
		source = sourceSets["main"].allJava
		classpath = configurations["compileClasspath"]

		options {
			this as StandardJavadocDocletOptions

			memberLevel = JavadocMemberLevel.PROTECTED
			author(true)

			links("https://docs.oracle.com/en/java/javase/11/docs/api/")
		}
	}

	register<Jar>("sourceJar") {
		archiveClassifier.set("sources")
		from(sourceSets["main"].allJava)
	}

	register<Jar>("packageJavadoc") {
		archiveClassifier.set("javadoc")
		from(named("javadoc"))
	}

	withType<Jar> {
		archiveBaseName.set("${project.properties["artifact"]}")
		// Shading copies META-INF into the consumer's jar, which keeps the NOTICE attribution there;
		// the suffix stops other libraries' LICENSE/NOTICE files from replacing ours
		metaInf {
			from("LICENSE", "NOTICE")
			rename { "$it-${project.properties["artifact"]}" }
		}
	}
}

changelog {
	groups.empty()
	// SemVer plus the four-part patch versions (e.g. 0.1.1.2) used by older releases
	headerParserRegex.set("""^((0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:\.(0|[1-9]\d*))?(?:-[0-9A-Za-z.-]+)?)$""".toRegex())
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			artifact(tasks.jar)
			artifact(tasks.named("sourceJar"))
			artifact(tasks.named("packageJavadoc"))
			groupId = project.properties["group"] as String
			version = project.properties["version"] as String
			artifactId = project.properties["artifact"] as String
			pom {
				licenses {
					license {
						name.set("Apache License, Version 2.0")
						url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}
			}
		}
	}
	repositories {
		maven {
			name = "GitHubPackages"

			url = uri("https://maven.pkg.github.com/andret2344/ats-arguments")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
