# atsArguments changelog

## Unreleased

### Added

- `@ExceptionFallback` methods, which handle exceptions thrown by `@Argument` methods. A fallback method takes either
  no parameters or the exception.

### Changed

- The license changed from MIT to the Apache License 2.0. Redistributions, including plugins with the library shaded
  in, have to keep the `NOTICE` file, which every jar now carries as `META-INF/NOTICE-ats-arguments`.
- The library is published to GitHub Packages instead of the GitLab package registry. See the README for the
  repository and token setup.
- Requires Java 25 (was 11) and is built against Spigot API 26.3 (was 1.18.1). Servers running on a Java older than 25,
  which covers most 1.20 and 1.21 servers, cannot load it.
- Completers get the already typed arguments as `List<String>` first and the `CommandSender` second. The
  single-argument variants take `Function<List<String>, Collection<String>>` instead of
  `Function<CommandSender, Collection<String>>`.
- Mappers registered without a fallback condition now fall back when they return `null`. Before, they never fell back.
- Enum mappers match names case-insensitively, and unknown names trigger the fallback instead of throwing an exception.
- With `autoTranslateColors` enabled, color codes in the help messages are translated too. Messages sent directly
  through the `CommandSender`, from command methods or from listeners, are still not translated.
- A new command class instance is created for every command execution. Before, one instance was kept per sender for
  the whole server uptime, so its fields carried state between executions and every sender that ever ran the command,
  including players who had left, stayed in memory.
- An exception thrown by the command class constructor reaches Bukkit wrapped in `IllegalStateException` instead of
  as a raw `InvocationTargetException`.

### Removed

- `AnnotatedCommand.getCommandExecutor(CommandSender)`. No command class instance outlives a single execution.
- `FallbackConstants`. Pass a predicate such as `Objects::isNull` instead.
- The Lombok-generated `equals`, `hashCode` and `toString` of `AnnotatedCommand`, `AnnotatedCommand.Options`,
  `AnnotatedCommandExecutor` (and so of every command class) and `FallbackException`. They now use identity, so two
  command class instances with the same sender and plugin are no longer equal.

## 0.1.3

### Fixed

- Now pages are back, you can check current JavaDoc here.

### Added

- Fallbacks now has more sense as they use id or type, not the method name, so we have the greater control.
- Response mappers, that works the opposite way than mappers.
- Completers now know what arguments have already been typed.
- You can get the Command Class for specified sender to execute the methods manually.
- Created basic integration tests, that will be successively written.

### Removed

- `ResponseType` was not used, always it was set to `SENDER`.

## 0.1.2

### Fixed

- An exception could occur in common with wrong parameters count.
- Fallback method ignored method name.

### Added

- Type mappers allowing to use, e.g. boolean mapper with no `@Mapper` annotation. Completers work as well.
- It's now possible to pass additional constructor arguments to `AnnotatedCommandExecutor` subclass.
- Main command behavior can be overloaded with custom tasks.
- Returning a string with `\r\n` (etc.) in it, makes it sends multiple messages.
- Returning array or collection does it as well.
- Added enum mappers and completers, no additional logic must be applied.
- Now it's possible to decide if commands should be case-sensitive or case-insensitive.

## 0.1.1.2 (patch)

- Fixed fallbacks

## 0.1.1.1 (patch)

- Fixed critical incident with parameters count mismatch

## 0.1.1

- Changed not working `showIfNoPerms` into `displayType` in `@Argument` annotation.
- Added possibility to automatically translate color codes.
- Added `@Fallback` method allowing accessing non-parsed via `@Param` annotation values.
- Added `@Completer` annotation to improve tab completion.
- Added `@Ignore` annotation to skip configured tab completion.

## 0.1.0

- Added `@Param` annotation working with `annotatedCommand.addArgumentMapper()` method.
- Split code into parts.

## 0.0.5-alpha2

- Updated javadoc.

## 0.0.5-alpha1

- Made listener interfaces public.

## 0.0.5-alpha

- Redesigned way of usage.
- Added TabCompleter.

## 0.0.4-alpha

- Added `@BaseCommand` annotation.

## 0.0.3-alpha

- Removed obsolete debugger.

## 0.0.2-alpha

- Fixed errors.

## 0.0.1-alpha

- Initial version.
