# atsArguments

## License

Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.

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
	implementation group: 'eu.andret', name: 'ats-arguments', version: '0.1.1.1'
	// other dependencies
}
```

`pom.xml`:

```xml

<dependencies>
    <dependency>
        <groupId>eu.andret</groupId>
        <artifactId>ats-arguments</artifactId>
        <version>0.1.1.1</version>
    </dependency>
    <!-- other dependencies -->
</dependencies>
```

- At the end you have to shadow the dependency to not have collision in case two plugins uses the
  same classes

`build.gradle`:

```groovy
plugins {
	id 'com.github.johnrengelman.shadow' version '5.2.0'
}

//...

shadowJar {
	relocate 'eu.andret.arguments', 'eu.andret.YOUR_PLUGIN_NAME.arguments'
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

> **Note**: It not necessarily have to be `eu.andret.YOUR_PLUGIN_NAME`, it can be any package, that will be unique on the server (like: `com.example.test.arguments`).

## Basic code setup

To be able to use this library, there has to be a class extending `AnnotatedCommandExecutor` and
calling its constructor. This class also needs to be annotated with `@BaseCommand`.

```java

@BaseCommand("test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}
}
```

This we have just filled class with everything necessary. Now let's tell the manager to take care of
this class:

```java
public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		AnnotatedCommand annotatedCommand = CommandManager.registerCommand(TestCommand.class, this);
		// other initial setup logic
	}
}
```

That's it, we have done the basic setup. However, you have to remember to register the command you
put into  `@BaseCommand` inside your `plugin.yml` file!

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

This one will be parsed into `/test testing` command, which executing will result in displaying the
text on the console.

## How should I use it?

First of all, you have to know, that **only** annotated classes and methods are important. You can
write any amount of "typical" methods in `@BaseCommand` class and if they aren't annotated
with `@Argument`, you don't have to worry about them.

Ok, but what exactly can you do?

### Rules

The most meaningful part of `atsArguments` is the `@Argument` annotation. It has plenty of settings
you can use, but first, look at rules that apply:

- The name of the method (case-insensitive) is a command argument.
- Return value will be sent automatically, unless changed (`void` return type or `null` return value
  don't send anything).
- Method can have multiple arguments of any primitive type or String. Library will be trying to
  parse command arguments into method ones.
    - Exception is to create a mapper and use the `@Param` annotation.
    - When using `@Param` and parsing failed, you can access the raw value using `@Fallback`
      annotation.
- Method cannot have an array, only vararg is possible, rules as the point (with sub-points) above.
- There can be multiple methods with the same name, api will treat missing arguments as obsolete.
- Library automatically uses tab completion for method names.
    - You can configure more precise completers for methods parameters with `@Completer` annotation.
- In case of mismatching argument (method's name) or length of others, it'll result in error sent to
  sender.
- No argument after base command will produce simple syntax of available arguments.

### Annotations

API provided a few quite useful annotations.

- `@Argument` - Basic annotation for command configuration.

| setting | type | values | default | description |
| ------- | ---- | ------ | ------- | ----------- |
| permission | `String` | Any string. | `""` | Permission whether sender can perform the command. |
| executorType | `ExecutorType` | `ALL`, `PLAYER` or `CONSOLE`. | `ALL` | Executor type that is allowed to execute the command. |
| responseType | `ResponseType` | `NONE`, `SENDER`, `CONSOLE` or `BROADCAST`. | `SENDER` | Who should get the returned value from method. |
| description | `String` | Any String. | `""` | The description of command that will show up in help. |
| aliases | `String[]` | Array of any non-colliding strings. | `{}` | Aliases to argument, eg. "cmd" as alias for "command", and so on. |
| position | `int` | Any non-negative int lower or equal to methods arguments count. | `0` | which argument should be the method's name. For 1, it'll be `/test methodArg methodName`. |
| displayType | `DisplayType` | `ALWAYS`, `IF_PERMS`, `NONE` | `ALWAYS` | Describes when argument in help message should be visible. |

- `@BaseCommand` - Obligatory annotation for command class configuration.

| setting | type | values | default | description |
| ------- | ---- | ------ | ------- | ----------- |
| value | `String` | any string representing command | None. | The command all methods will be arguments for. |
| aliases | `String[]` | Array od any Strings. | `{}` | Aliases to command. |

- `@Param` - Allowing connecting argument with a configured mapper.

| setting | type | values |  description |
| ------- | ---- | ------ |  ----------- |
| value | `String` | any string | The mapper id to find exact registered mapper. |

- `@Fallback` - Annotation allowing catching not mapped correctly with `@Param` values.

- `@Completer` - Annotation that connects argument with configured argument completer.

| setting | type | values |  description |
| ------- | ---- | ------ |  ----------- |
| value | `String` | any string | The completer id to find exact registered completer. |

- `@Ignore` - Annotation for ignoring completions from argument or type completer for a certain
  argument.

### Listeners

You can use a few listeners to indicate certain behavior. All listeners need to be set up
on `AnnotatedCommand`.

Possible listeners are:

- `OnInsufficientPermissionsListener`
- `OnUnknownSubCommandExecutionListener`

Not configuring listeners don't result in any default. Nothing will happen.

### Mappers

Command can also have mappers. Plugin uses mappers to automate conversion from String to any other
type. The method `addArgumentMapper` requires 3 arguments:

- The unique id of mapper
- The target return type (eg. `Player.class`)
- The `Function<String, E>` where the second argument is the `E` type.

Then you can use `@Param("someId")` as an `@Argument` method parameter's annotation. If found and
executed command, the function created in here will run.

In case of mapping fail, there is possibility to catch the `@Fallback` annotated method with same
name as "error handler". The fallback condition (never called by default) describes what "wrong
value" is.

### Completers

Completer is something that will display suggestions when trying to execute command in-game. You can
configure type completer or argument one.

#### Type completers

Type completes are easier to configure, because they rely only on the argument type. The
method `addTypeCompleter` requires 2 arguments:

- The type that should be completed (eg. `boolean.class`)
- One of:
    - `Collection<String>` containing suggestions,
    - `Supplier<Collection<String>>` with instruction how to produce suggestions,
    - `Function<CommandSender, Collection<String>>` with instruction how to produce suggestion
      basing on `CommandSender`.

That's it. Just call the described method and completers will start suggesting values basing on
parameter type.

#### ArgumentCompleters

Argument completers are a bit more complex, as they require the id as string. You should use them if
not always same type will have the same suggestions. The method `addArgumentCompleter` requires 2
arguments:

- Unique completer id.
- One of:
    - `Collection<String>` containing suggestions,
    - `Supplier<Collection<String>>` with instruction how to produce suggestions,
    - `Function<CommandSender, Collection<String>>` with instruction how to produce suggestion
      basing on `CommandSender`.

To have it working, we need to put `@Completer("someId")` before the parameter to get suggestions.
Without this annotation, nothing will happen.

### Other configuration

There is also possibility to access `AnnotatedCommand.Options` object
via `annotatedCommand.getOptions()`. This object that allows the simple configuration.

- `options.setAutoTranslateColors(boolean)` - whether plugin should automatically translate colors
  from `'&'` to `'§'`.

## Full example

`TestPlugin.java`:

```java
public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		AnnotatedCommand command = CommandManager.registerCommand(TestCommand.class, this);
		command.setOnInsufficientPermissionsListener(sender -> sender.sendMessage("You don't have permissions"));
		command.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
		command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer, Fallback.ON_NULL);
		command.addTypeCompleter(boolean.class, Arrays.asList("true", "false"));
		command.addArgumentCompleter("basicPlayerCompleter", () -> Bukkit.getOnlinePlayers()
				.stream()
				.map(HumanEntity::getName)
				.collect(Collectors.toList()));
		command.addArgumentCompleter("booleanCompleter", Arrays.asList("true", "false"));
		command.getOptions().setAutoTranslateColors(true);
	}

	public boolean isSuperSecretSetting() {
		return false;
	}
}
```

`TestCommand.java`:

```java

@BaseCommand("test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(permission = "me.testing")
	public String testing() {
		// "/test testing", requires permission "me.testing", sender gets "I'm testing" or "I'm secretly testing"
		if (plugin.isSuperSecretSetting()) {
			return "I'm secretly testing!";
		}
		return "I'm testing!";
	}

	@Argument(executorType = ExecutorType.CONSOLE)
	public String administration(int level) {
		// "/test administration 3", only console can perform this command, sender (console) gets "Level set to 3"
		return "Level set to " + level;
	}

	@Argument(responseType = ResponseType.BROADCAST)
	public String broadcast(String[] message) {
		// "/test broadcast Welcome to the new server", everyone on server gets "Welcome to the new server"  
		return String.join(" ", message);
	}

	@Argument(position = 1)
	public String start(String gameName) {
		// "/test game start" (instead of "/test start game") 
		GameManager.getGame(gameName).start();
		return gameName + " started";
	}

	@Fallback
	public String player(String player) {
		// "/test player Andret2344", sender gets: "Who do you mean?"
		return "Who do you mean? I don't know " + player;
	}

	@Argument
	public String player(@Param("basicPlayerMapper") @Completer("basicPlayerCompleter") Player player) {
		// "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844"
		return "Hello " + player.getName() + ", your UUID is: " + player.getUniqueId();
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public String distance(@Param("basicPlayerMapper") @Completer("basicPlayerCompleter") Player... players) {
		OptionalDouble min = Arrays.stream(players)
				.filter(Objects::nonNull)
				.mapToDouble(player -> player.getLocation().distance(((Player) sender).getLocation()))
				.min();
		if (min.isPresent()) {
			// "/test player Andret2344 test", sender gets: "The shortest distance is 53.23634. Guess whom it is!"
			return "The shortest distance is " + min.getAsDouble() + ". Guess whom it is!";
		}
		// "/test player Andret2344 test", sender gets: "No min distance could be found :("
		return "No min distance could be found :(";
	}

	@Argument(displayType = DisplayType.NONE)
	public void notDisplayed() {
		// Argument won't be displayed when "/test" will be executed
	}

	@Argument(displayType = DisplayType.IF_PERMS, permission = "eu.andret.test.conditions")
	public void conditionallyDisplayed() {
		// Argument will be displayed when "/test" will be executed only if sender has permissions
	}

	@Argument(displayType = DisplayType.ALWAYS, permission = "eu.andret.test.conditions")
	public void alwaysDisplayed() {
		// Argument will be displayed when "/test" will be executed under no conditions
	}

	@Argument
	public String colored(boolean value) {
		// automatic suggestions with "true" and "false" will appear.
		// Response will be automatically colored.
		if (value) {
			return "&6You have found something. &dBye!";
		}
		return "&4Nothing to look at here. &bBye!";
	}

	@Argument
	public String ignored(@Ignore boolean value) {
		// No suggestions will appear.
		// Response will be automatically coloured.
		if (value) {
			return "&6I'm ignored.";
		}
		return "&6I'm ignored too.";
	}
}
```
