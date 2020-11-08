# atsArguments

## License

Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.

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
    implementation group: 'eu.andret', name: 'ats-arguments', version: '0.1.1'
    // other dependencies
}
```
`pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>eu.andret</groupId>
        <artifactId>ats-arguments</artifactId>
        <version>0.1.1</version>
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
    relocate 'eu.andret.arguments', 'eu.andret.YOURPLUGINNAME.arguments'
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
                                <shadedPattern>eu.andret.YOURPLUGINNAME.arguments</shadedPattern>
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

> **Note**: It not necessarily have to be `eu.andret.YOURPLUGINNAME`, it can be any package, that will be unique on the server (like: `com.example.test.arguments`).

## Basic code setup
To be able to use this library, there has to be a class extending `AnnotatedCommandExecutor` and calling it's constructor. This class also needs to be annotated with `@BaseCommand`.
```java
@BaseCommand("test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
    public TestCommand(CommandSender sender, TestPlugin plugin) {
        super(sender, plugin);
    }
}
```
This class now is filled with everything necessary. Now let's tell the manager to take care of this class:
```java
public class TestPlugin extends JavaPlugin {
    @Override 
    public void onEnable() {
        CommandManager.registerCommand(TestCommand.class, this);
        // other initial setup logic
    }
}
```

That's it, the basic setup is done. However, you have to remember to register the command you put into  `@BaseCommand` inside your `plugin.yml` file!

Now, to use this library in proper way, simply write any non-static method in your `@BaseCommand`-annotated class, annotating it with `@Argument`:
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

## How should I use it?
First of all, you have to know, that **only** annotated classes and methods are important. You can write any amount of "typical" methods in `@BaseCommand` class and if they aren't annotated with `@Argument`, you don't have to worry about them.

Ok, but what exactly can you do?

The most meaningful part of `atsArguments` is the `@Argument` annotation. It has plenty of settings you can use, but first, look at rules that apply:

- The name of the method (case-insensitive) is a command argument.
- Return value will be sent automatically, unless changed (`void` return type or `null` return value don't send anything)
- Method can have multiple arguments of any primitive type or String. Library will be trying to parse command arguments into method ones.
  - Exception is to create a `Mapper` and use the `@Param` annotation.
  - When using `@Param` and parsing failed, you can access the raw value using `@Fallback` annotation.
- Method cannot have an array, only vararg is possible, rules as the point above.
- There can be multiple methods with the same name, missing arguments are treated as obsolete.
- Library automatically uses tab completion.
- In case of mismatching argument (method's name) or length of others, it'll result in error sent to sender.
- No argument after base command will produce simple syntax of available arguments.

Now, let's see what can we set up using `@Argument`:

| setting | type | values | default | description |
| ------- | ---- | ------ | ------- | ----------- |
| permission | `String` | Any string. | `""` | Permission whether sender can perform the command. |
| executorType | `ExecutorType` | `ALL`, `PLAYER` or `CONSOLE`. | `ALL` | Executor type that is allowed to execute the command. |
| responseType | `ResponseType` | `NONE`, `SENDER`, `CONSOLE` or `BROADCAST`. | `SENDER` | Who should get the returned value from method. |
| description | `String` | Any String. | `""` | The description of command that will show up in help. |
| aliases | `String[]` | Array of any non-colliding strings. | `{}` | Aliases to argument, eg. "cmd" as alias for "command", and so on. |
| position | `int` | Any non-negative int lower or equal to methods arguments count. | `0` | which argument should be the method's name. For 1, it'll be `/test methodArg methodName`. |
| displayType | `DisplayType` | `ALWAYS`, `IF_PERMS`, `NONE` | `ALWAYS` | Describes when argument in help message should be visible. |

To be formal, here's the table for `@BaseCommand`:

| setting | type | values | default | description |
| ------- | ---- | ------ | ------- | ----------- |
| value | `String` | any string representing command | None. | The command all methods will be arguments for. |
| aliases | `String[]` | Array od any Strings. | `{}` | Aliases to command. |

Another annotation `@Param` has following table:

| setting | type | values |  description |
| ------- | ---- | ------ |  ----------- |
| value | `String` | any string | The mapper id to find exact registered mapper. |

There is also `@Fallback` annotation available that have has no elements.

At the end, you can use a few listeners to indicate certain behavior. All listeners need to be set up on `AnnotatedCommand`.
```java
public class TestPlugin extends JavaPlugin {
    @Override 
    public void onEnable() {
        AnnotatedCommand command = CommandManager.registerCommand(TestCommand.class, this);
        // other initial setup logic
    }
}
```
Possible listeners are:
- `OnInsufficientPermissionsListener`
- `OnUnknownSubCommandExecutionListener`

Command can also have mappers. Mappers are used to automate changes from String to any other type. The method `addArgumentMapper` requires 3 arguments:
- The unique id of mapper
- The target return type
- The `Function<String, E>` where the second argument is the `E` type.

Then you can use `@Param(value = "id")` as an `@Argument` method parameter's annotation. If found and executed command, the function created in here will run.

In case of mapping fail, there is possibility to catch the `@Fallback` annotated method with same name as "error handler". Wrong value is described when adding mapper, as fallback condition (never called by default)

There is also possible to sets simple things up.
* `annotatedCommand.setAutoTranslateColors(boolean)` - whether plugin should automatically translate colors from `'&'` to `'§'`.

## Example usage
`TestPlugin.java`:
```java
public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        AnnotatedCommand command = CommandManager.registerCommand(TestCommand.class, this);
        command.setOnInsufficientPermissionsListener(sender -> sender.sendMessage("You don't have permissions"));
        command.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
        command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer, Fallback.ON_NULL);
        command.setAutoTranslateColors(true);
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
        // "/test testing", requires permission "me.testing", sender gets "I'm testing"
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
        // "/test spleef start" (instead of "/test start spleef") 
        GameManager.getGame(gameName).start();
        return gameName + " started"; 
    }

    @Fallback
    public String player(String player) {
        // "/test player Andret2344", sender gets: "Who do you mean?"
        return "Who do you mean?";
    }
    
    @Argument
    public String player(@Param("basicPlayerMapper") Player player) {
        // "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844"
        return "Hello " + player.getName() + ", your UUID is: " + player.getUniqueId();
    }
    
    @Argument(executorType = ExecutorType.PLAYER)
    public String distance(@Param("basicPlayerMapper") Player... players) {
        OptionalDouble min = Arrays.stream(players)
                .filter(Objects::nonNull)
                .mapToDouble(player -> player.getLocation().distance(((Player) sender).getLocation()))
                .min();
        if (min.isPresent()) {
            // "/test player Andret2344 deyanix", sender gets: "The shortest distance is 53.23634. Guess whom it is!"
            return "The shortest distance is " + min.getAsDouble() + ". Guess whom it is!";
        } 
        // "/test player Andret2344 deyanix", sender gets: "No min distance could be found :("
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
	public String colored() {
		// Response will be automatically coloured due to "&4" and "&b"
        return "&4Nothing to look at here. &bBye!"; 
	}
}
```
