# atsArguments

## License

Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.

## Dependency setup

To set up the library in your project, you have to do following steps:

- Add `https://gitlab.com/api/v4/projects/12063927/packages/maven` as a repository

`build.gradle`:

```groovy
repositories {
	mavenCentral()
	maven { url 'https://gitlab.com/api/v4/projects/12063927/packages/maven' }
	// other repositories
}
```

`pom.xml`:

```xml

<repositories>
	<repository>
		<url>https://gitlab.com/api/v4/projects/12063927/packages/maven</url>
	</repository>
	<!-- other repositories -->
</repositories>
```

- Add the dependency.

`build.gradle`:

```groovy
dependencies {
	implementation group: 'eu.andret', name: 'ats-arguments', version: '0.1.3'
	// other dependencies
}
```

`pom.xml`:

```xml

<dependencies>
	<dependency>
		<groupId>eu.andret</groupId>
		<artifactId>ats-arguments</artifactId>
		<version>0.1.3</version>
	</dependency>
	<!-- other dependencies -->
</dependencies>
```

- At the end you have to shadow the dependency to not have collision in case two plugins uses the same classes

`build.gradle`:

```groovy
plugins {
	id 'com.github.johnrengelman.shadow' version '5.2.0'
}

//...

shadowJar {
	relocate 'eu.andret.arguments', 'YOUR_PLUGIN_PACKAGE.arguments'
	configurations = [project.configurations.implementation]
}

build.dependsOn(shadowJar)
```

`pom.xml`:

```xml

<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-shade-plugin</artifactId>
			<version>3.2.1</version>
			<executions>
				<execution>
					<phase>package</phase>
					<goals>
						<goal>shade</goal>
					</goals>
					<configuration>
						<relocations>
							<relocation>
								<pattern>eu.andret.arguments</pattern>
								<shadedPattern>eu.andret.YOUR_PLUGIN_NAME.arguments</shadedPattern>
							</relocation>
						</relocations>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
	<!-- ... -->
</build>
```

> **Note**: It not necessarily have to be `eu.andret.YOUR_PLUGIN_NAME`, it can be any package, that will be unique on
> the server (like: `com.example.test.arguments`).

## Basic code setup

To be able to use this library, there has to be a class extending `AnnotatedCommandExecutor` and calling its
constructor. This class also needs to be annotated with `@BaseCommand`.

```java

@BaseCommand("test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}
}
```

By doing this, we have just filled the class with everything that is necessary. Now, let's tell the manager to take care
of this class:

```java
public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		AnnotatedCommand<TestPlugin> annotatedCommand = CommandManager.registerCommand(TestCommand.class, this);
		// other initial setup logic
	}
}
```

And that's it, we have done the basic setup. However, you have to remember to register the command you put
into  `@BaseCommand` inside your `plugin.yml` file!

Now, to use this library in proper way, simply write any non-static method in your `@BaseCommand`
-annotated class, annotating it with `@Argument`:

```java

@BaseCommand("test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}

	@Argument
	public void testing() {
		System.out.print("I'm testing!");
	}
}
```

This one will be parsed into `/test testing` command, which executing will result in displaying the text on the console.

It's also possible to pass additional arguments that will be used as AnnotatedCommandExecutor constructor arguments.

```java
public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		AnnotatedCommand<TestPlugin> annotatedCommand = CommandManager.registerCommand(TestCommand.class, this, getServer().getWorld("world"));
		// other initial setup logic
	}
}

@BaseCommand("params")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	private final World world;

	public TestCommand(CommandSender sender, TestPlugin plugin, World world) {
		super(sender, plugin);
		this.world = world;
	}

	@Argument
	public void getName() {
		System.out.print(world.getName());
	}
}
```

## How should I use it?

First, you have to know, that **only** annotated classes and methods are important. You can write any amount of "
typical" methods in `@BaseCommand` class and if they aren't annotated with `@Argument`, you don't have to worry about
them.

Ok, but what exactly can you do?

### Rules

The most meaningful part of `atsArguments` is the `@Argument` annotation. It has plenty of settings you can use, but
first, look at rules that apply:

- The name of the method (case sensitivity is a setting) is a command argument.
- Return value will be sent automatically, unless changed (`void` return type or `null` return value don't send
  anything).
    - Primitive types and String will be sent "as is".
    - Complex types will be parsed to `String` via `toString` method.
        - The behavior can be overwritten using `@ArgumentResponse` or by configuring type response (analogically
          to `@Mapper` behavior, but the opposite way).
- Method can have multiple arguments of any primitive type or String. Library will be trying to parse command arguments
  into method ones. In order to use other types, there is a possibility to configure mappers.
    - Type Mapper will instruct how to parse text into certain class independently
    - Argument Mapper will be used only with explicit `@Mapper` annotation.
    - When using `@Mapper` and parsing failed, you can access the raw value using `@Fallback`
      annotation.
- Method cannot have an array parameter, but only a VarArg is possible, rules as the point above.
- There can be multiple methods with the same name, api will treat missing arguments as obsolete.
- Library automatically uses tab completion for method names.
    - You can configure more precise completers for methods parameters with `@Completer` annotation.
- In case of mismatching argument (method's name) or length of others, it'll result in error sent to sender.
- No argument after base command will produce simple syntax of available arguments.
    - The behavior can be overwritten.

### Annotations

API provides a few quite useful annotations.

- `@Argument` - Basic annotation for command configuration.

| setting      | type           | values                                                          | default  | description                                                                               |
|--------------|----------------|-----------------------------------------------------------------|----------|-------------------------------------------------------------------------------------------|
| permission   | `String`       | Any string.                                                     | `""`     | Permission whether sender can perform the command.                                        |
| executorType | `ExecutorType` | `ALL`, `PLAYER` or `CONSOLE`.                                   | `ALL`    | Executor type that is allowed to execute the command.                                     |
| description  | `String`       | Any String.                                                     | `""`     | The description of command that will show up in help.                                     |
| aliases      | `String[]`     | Array of any non-colliding strings.                             | `{}`     | Aliases to argument, eg. "cmd" as alias for "command", and so on.                         |
| position     | `int`          | Any non-negative int lower or equal to methods arguments count. | `0`      | which argument should be the method's name. For 1, it'll be `/test methodArg methodName`. |
| displayType  | `DisplayType`  | `ALWAYS`, `IF_PERMS`, `NONE`                                    | `ALWAYS` | Describes when argument in help message should be visible.                                |

- `@BaseCommand` - Obligatory annotation for command class configuration.

| setting | type       | values                          | default | description                                    |
|---------|------------|---------------------------------|---------|------------------------------------------------|
| value   | `String`   | any string representing command | None.   | The command all methods will be arguments for. |
| aliases | `String[]` | Array od any Strings.           | `{}`    | Aliases to command.                            |

- `@Mapper` - Allowing connecting argument with a configured mapper.

| setting | type     | values     | description                                    |
|---------|----------|------------|------------------------------------------------|
| value   | `String` | any string | The mapper id to find exact registered mapper. |

- `@ArgumentResponse` - Allows connection return value with "to `String`" mapper.

| setting | type     | values     | description                                             |
|---------|----------|------------|---------------------------------------------------------|
| value   | `String` | any string | The mapper id to find exact registered response mapper. |

- `@ArgumentFallback` - Annotation allowing catching not mapped correctly with argument mapper values.

| setting | type       | values      | description                                                              |
|---------|------------|-------------|--------------------------------------------------------------------------|
| value   | `String[]` | any strings | The mapper ids that in case of failure should call the annotated method. |

- `@TypeFallback` - Annotation allowing catching not mapped correctly with type mapper values.

| setting | type         | values      | description                                                                  | 
|---------|--------------|-------------|------------------------------------------------------------------------------|
| value   | `Class<?>[]` | any classes | The mapper classes that in case of failure should call the annotated method. |

- `@Completer` - Annotation that connects argument with configured argument completer.

| setting | type     | values     | description                                          |
|---------|----------|------------|------------------------------------------------------|
| value   | `String` | any string | The completer id to find exact registered completer. |

- `@Ignore` - Annotation for ignoring completions from argument or type completer for a certain argument.

### Listeners

You can use a few listeners to indicate certain behavior. All listeners need to be set up on `AnnotatedCommand`.

Possible listeners are:

- `OnInsufficientPermissionsListener`
- `OnUnknownSubCommandExecutionListener`
- `OnMainCommandExecutionListener`

Not configuring listeners don't result in any default. Nothing will happen.

### Mappers

Command can also have mappers. Plugin uses mappers to automate conversion from String to any other type. Every Mapper
can have its own fallback condition.

It's allowed to configure argument mappers and type mappers. In case of collision, argument mappers has the priority as
more precise.

In case of mapping fail, there is possibility to catch the fallback annotated method with matching value identifying
mapper. The fallback condition (`NEVER` called by default) describes what "wrong value" is.

#### Type mappers

Type mappers allow specifying how to convert string into provided class. Use this mapper if always mapping to certain
type is the same. The method `addTypeMapper` requires following data:

- The target return type (e.g. `Player.class`)
- The `Function<String, E>` where the first method's argument is the `E` type.
- (optionally) The fallback condition determining when fallback method will be executed (by default it's not called).

#### Argument mappers

Argument mappers allow specifying how to map string into other type using string key as an identifier. It's a perfect
solution if the same type requires different mappings in dependency on method's name. The method `addArgumentMapper`
requires following data:

- The unique id of mapper.
- The target return type (e.g. `Player.class`).
- The `Function<String, E>` where the second method's argument is the `E` type.
- (optionally) The fallback condition determining when fallback method will be executed (by default it's not called).

Then you can use `@Mapper("someId")` as an `@Argument` method parameter's annotation. If found and executed command, the
function created in here will run.

#### Type response mappers

Type response mappers allow specifying how to convert provided class object into `String`. Use this mapper if always
mapping from certain type is the same. The method `addTypeResponseMapper` requires following data:

- The target return type (e.g. `World.class`)
- The `Function<E, String>` where the first method's argument is the `E` type.

#### Argument response mappers

Argument response mappers allow specifying how to map returned complex type value into `String`. It's a perfect solution
when you want to return an object and always be converting it to string in the same way. The
method `addArgumentResponseMapper` takes 3 arguments:

- The unique id of the mapper.
- The source return type (i.e. `World.class`).
- The `Function<E, String>` where the second method's argument is the `E` type.

You can use `@ArgumentResponse("someId")` along with `@Argument` annotation on method. If the method executed properly,
the function will be called on the returned value to convert it to `String` just before sending the response.

### Completers

Completer is something that will display suggestions when trying to execute command in-game. You can configure type
completer or argument one.

#### Type completers

Type completes are easier to configure, because they rely only on the argument type. The method `addTypeCompleter`
requires 2 arguments:

- The type that should be completed (e.g. `boolean.class`)
- One of:
    - `Collection<String>` containing suggestions,
    - `Supplier<Collection<String>>` as instruction how to produce suggestions,
    - `Function<List<String>. Collection<String>>` as instruction how to produce suggestion based on currently typed
      words.
    - `BiFunction<List<String>, CommandSender, Collection<String>>` as instruction how to produce suggestions based on
      currently typed words and the typist.

That's it. Just call the described method and completers will start suggesting values based on parameter type.

#### Argument completers

Argument completers are a bit more complex, as they require the id as string. You should use them if not always same
type will have the same suggestions. The method `addArgumentCompleter` requires 2 arguments:

- Unique completer id.
- One of:
    - `Collection<String>` containing suggestions,
    - `Supplier<Collection<String>>` as instruction how to produce suggestions,
    - `Function<List<String>, Collection<String>>` as instruction how to produce suggestion based on currently typed
      words.
    - `BiFunction<List<String>, CommandSender, Collection<String>>` as instruction how to produce suggestions based on
      currently typed words and the typist.

To have it working, we need to put `@Completer("someId")` before the parameter to get suggestions. Without this
annotation, nothing will happen.

### Other configuration

There is also possibility to access `AnnotatedCommand.Options` object via `annotatedCommand.getOptions()`. This object
that allows the simple configuration.

- `options.setAutoTranslateColors(boolean)` - whether plugin should automatically translate colors from `'&'` matching
  chat color.
- `options.setCaseSensitive(boolean)` - whether arguments should be case-sensitive or case-insensitive.

## Full example

Full example can be found here:

[Full example](https://gitlab.com/andret-tools-system/ats-arguments/-/tree/master/example/src/main/java/eu/andret/arguments/example)
