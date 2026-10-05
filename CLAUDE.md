# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Keeping this file current

This file must always match the repository. Whenever you change anything it describes (commands, build setup,
architecture, conventions, release flow), update it in the same change. Whenever you notice that the repository
already differs from what is written here (someone else changed it), fix the affected section right away and tell the
user what you corrected.

## What this is

`ats-arguments` (`eu.andret:ats-arguments`) is a Spigot/Bukkit library: plugin authors write a class annotated with
`@BaseCommand`, annotate its methods with `@Argument`, and the library turns `/command method args...` into reflective
calls of those methods, including argument parsing, permissions and tab completion. Published to GitHub Packages
(`https://maven.pkg.github.com/andret2344/ats-arguments`); consumers shade and relocate it into their own plugin.

## Build and test

Gradle 9.8 (Kotlin DSL), Java 25 through a toolchain set in both build scripts from `libs.versions.java`, so the
bytecode is Java 25 whichever JDK runs Gradle. Gradle has to find a JDK 25 locally (CI uses Zulu 25); no toolchain
download is configured.

```shell
./gradlew assemble            # build library and the example plugin
./gradlew test                # unit tests (src/test), then jacoco coverage verification
./gradlew integrationTest     # integration tests (src/integration)
./gradlew checkstyleMain      # checkstyle, main sources only
./gradlew check               # all of the above
./gradlew test --tests eu.andret.arguments.UtilTest -x jacocoTestCoverageVerification   # single test class
```

- Tests use **TestNG** + Mockito + AssertJ, written as `// given` / `// when` / `// then`. Every `Test` task loads
  Mockito as a `-javaagent` (the `mockitoAgent` configuration) instead of letting it self-attach.
- `test` is finalized by `jacocoTestCoverageVerification` with a **100% instruction coverage** minimum, and by
  `jacocoLogTestCoverage`, which prints the coverage summary. Any new main code without full unit-test coverage fails
  the build. A filtered run fails that check too, hence the `-x` above.
- Integration tests drive the real pipeline through `LocalCommandExecutor.onCommand(...)` with mocked
  `CommandSender`/`PluginCommand` and fixture command classes in `src/integration/.../executor`.
- Checkstyle (`config/checkstyle/checkstyle.xml`, tool version from the catalog) requires javadoc with a summary
  sentence (`SummaryJavadoc`) on public types and methods, forbids catching generic `Exception` (`IllegalCatch`) and
  star imports. Test and integration sources are excluded.
- Javadoc must always match the code. Whenever a change makes a javadoc wrong (a parameter's type or role, a return
  value, a thrown exception, what a method or class does), fix that javadoc in the same change. The same applies to
  an outdated javadoc you notice in code you touch, even if your change did not cause it.
- Source files carry no copyright header; do not add one. The project is Apache 2.0, and attribution lives only in
  `NOTICE` (copyright andret2344 plus a link to the repository). Every jar task copies them into `META-INF` as
  `LICENSE-ats-arguments` and `NOTICE-ats-arguments` (suffixed so other shaded libraries cannot overwrite them), so
  they end up in consumers' shaded jars. The POM declares the license.
- All text files use LF line endings, enforced by `.gitattributes` (`* text=auto eol=lf`), except `*.bat`, which
  stays CRLF because `cmd.exe` misparses LF batch files.
- All versions live in the version catalog `gradle/libs.versions.toml`: Java, libraries, Gradle plugins
  (`alias(libs.plugins.*)`) and the jacoco and checkstyle tool versions, including the Spigot API version used by
  the library and the example plugin.
- No Lombok: constructors, getters and setters are written by hand, and so they count toward the 100% coverage.
  Classes have no `equals`/`hashCode`/`toString` unless something needs them. Reflective calls declare
  `ReflectiveOperationException` on the `I*` interfaces instead of hiding it.

## Architecture

Entry point `CommandManager.registerCommand(commandClass, plugin, extraCtorArgs...)` looks up the `PluginCommand` from
`plugin.yml` by the `@BaseCommand` value, creates an `AnnotatedCommand` (the public configuration facade returned to
the user) and installs two package-private Bukkit hooks on the command: `LocalCommandExecutor` and
`LocalTabCompleter`. `AnnotatedCommand` only forwards configuration (mappers, completers, response mappers,
listeners, `Options`) to those two objects, which it reaches via `command.getExecutor()` / `getTabCompleter()`.

Execution pipeline in `LocalCommandExecutor.onCommand`:

1. Sender may be wrapped in `ChatColorCommandSenderDecorator` (`Options.autoTranslateColors`).
2. No args: the main-command listener runs, or a help list is built from `@Argument` methods (`DisplayTypeFilter`,
   `MethodToDescriptionMapper`).
3. `CommandToMethodMapper` picks the **first** declared method passing `MethodNameFilter` (name/aliases at
   `@Argument.position`, case sensitivity from `Options`), `ExecutorTypeFilter` and `ArgumentsFilter` (argument
   count and type compatibility, varargs). Order comes from `getDeclaredMethods()`, so overload ambiguity is resolved
   by the JVM's method order, not by specificity.
4. `PermissionFilter` checks `@Argument.permission` (the console and operators always pass), otherwise the
   insufficient-permissions listener fires.
5. `MethodSelector` converts the string args to parameter values: explicit `@Mapper("id")` argument mapper, else a
   registered type mapper, else `Util.convert` for primitives/String, and returns the `ExecutionCall` (methods plus
   arguments). A mapper result matching its fallback condition throws `FallbackException` inside `MethodSelector`,
   which catches it and returns the `@ArgumentFallback` / `@TypeFallback` methods (single `String` parameter, ordered
   by `FallbackPriority`) chosen by its `FallbackSelector`, with the failed string as the argument.
6. `InstanceCreator` builds the command class instance. Its single constructor must start with
   `(CommandSender, JavaPlugin, ...)`; extra args come from `registerCommand`. Instances are **cached per sender** in
   `LocalCommandExecutor.executors`, so fields on the command class are per-sender state.
7. `ExceptionHandler` invokes through `MethodInvoker`. An exception thrown by the method (an
   `InvocationTargetException` from reflection) is routed to `@ExceptionFallback` methods matching the cause's exact
   class (taking no args or the exception); with no match, the cause is rethrown wrapped in `RuntimeException`. Other
   reflective failures, including an exception thrown by the command class constructor or by a fallback method, leave
   `onCommand` wrapped in `IllegalStateException`.
8. `ResponseMapper` turns the return value into messages: arrays and collections are split into elements, each element
   is mapped by `@ArgumentResponse("id")` or a type response mapper (falling back to `String.valueOf`), then split on
   line breaks. `null`/`void` sends nothing.

The mapper registries live in `entity/MappingConfig`, one instance per command created by `LocalCommandExecutor`:
argument mappers by id, type mappers by class, and argument and type response mappers. Completers are not in it; they
are two maps in `LocalTabCompleter` (argument completers by id, type completers by class), shared with
`MethodToCompletionMapper`. Collaborators are behind `I*` interfaces in `filter/` and `mapper/`, with implementations
in `impl/`, and are constructed directly as fields of `LocalCommandExecutor` and `LocalTabCompleter`. The interfaces
exist for mocking in unit tests, not for user extension.

`LocalTabCompleter`: the first arg completes method names and aliases; later args go through
`MethodToCompletionMapper`, using `@Completer("id")` argument completers or type completers, with `@Ignore` disabling
completion for a parameter.

`example/` is a runnable Spigot plugin (shadow + spigradle, which generates `plugin.yml`) that depends on the root
project and is the manual testbed. It is not published.

The README's API section partly predates the current annotations (it still mentions `@Fallback`). Trust
`api/annotation/` over the README.

## Release

Versioning lives in `gradle.properties` (`version`), bumped by hand after each release. `CHANGELOG.md` is managed by the
`org.jetbrains.changelog` plugin. New entries go under `## Unreleased`; headers have no brackets, and a custom
`headerParserRegex` accepts the old four-part versions like `0.1.1.2`.

- `.github/workflows/build.yml`: on push to `main` and on PRs, it runs assemble, unit and integration tests and
  checkstyle. On `main` it also recreates the draft release `v<version>`, with notes taken from `Unreleased`.
- `.github/workflows/release.yml`: runs when the draft is published. It patches the changelog, runs `check`, publishes
  to GitHub Packages, attaches the jars, opens a changelog PR and deploys the javadoc to GitHub Pages.

Tags use the `v` prefix (`v0.1.3`). Issues live on GitHub. GitLab issue numbers (in old commit messages like `#41:` and
in branch names like `issue/#19`) do not match the GitHub ones.
