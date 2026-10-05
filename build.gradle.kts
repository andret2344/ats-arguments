plugins {
	idea
	java
	jacoco
	`maven-publish`
	checkstyle
	alias(libs.plugins.jacocolog)
	alias(libs.plugins.changelog)
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
	}
}

jacoco {
	toolVersion = libs.versions.jacoco.get()
}

checkstyle {
	toolVersion = libs.versions.checkstyle.get()
}

sourceSets {
	create("integration") {
		java.srcDir("src/integration/java")
		resources.srcDir("src/integration/resources")
		compileClasspath += sourceSets["main"].output + sourceSets["test"].output
		runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
	}
}

val mockitoAgent = configurations.create("mockitoAgent")

configurations {
	// Tests run against the same server API that the library compiles against
	testImplementation {
		extendsFrom(configurations.compileOnly.get())
	}
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
	compileOnly(libs.spigot.api)
	implementation(libs.jetbrains.annotations)

	testImplementation(libs.assertj.core)
	testImplementation(libs.mockito.core)
	mockitoAgent(libs.mockito.core) {
		isTransitive = false
	}
	testImplementation(libs.mockito.testng)
	testImplementation(libs.testng)
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

	register<Test>("integrationTest") {
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

	// Mockito self-attaching its agent at runtime will stop working in a future JDK
	withType<Test> {
		jvmArgumentProviders.add(CommandLineArgumentProvider { listOf("-javaagent:${mockitoAgent.singleFile}") })
	}

	test {
		jacoco {
			exclude("**/Material")
		}
		useTestNG()
		finalizedBy(jacocoTestCoverageVerification, jacocoLogTestCoverage)
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

			links("https://docs.oracle.com/en/java/javase/${libs.versions.java.get()}/docs/api/")
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
		archiveBaseName.set(providers.gradleProperty("artifact"))
		// Shading copies META-INF into the consumer's jar, which keeps the NOTICE attribution there;
		// the suffix stops other libraries' LICENSE/NOTICE files from replacing ours
		metaInf {
			from("LICENSE", "NOTICE")
			rename { "$it-${providers.gradleProperty("artifact").get()}" }
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
			groupId = providers.gradleProperty("group").get()
			version = providers.gradleProperty("version").get()
			artifactId = providers.gradleProperty("artifact").get()
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
