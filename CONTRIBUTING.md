# How to contribute?

I'm really glad you're reading this. Every volunteer developer whom want to help this project to come to fruition is
valuable.

## Basics

Bugs and feature requests are in our [issue board](https://github.com/andret2344/ats-arguments/issues).

## Testing

CI executes tests Automatic due
to [GitHub Actions](https://github.com/andret2344/ats-arguments/actions). The current
configuration checks everything that can be checked.

To test code when developing, just build `example` subproject and use it as classical Bukkit/Spigot plugin.

## Environment

After cloning repo and building it as gradle project, code should be ready for development.

## Contribution

If you want to contribute, **do not** change the version on your own. It'll be done when deploying.
To get your work deployed, simply create Merge Request and assign me (`Andret2344`) to it.
By the time of week, you'll get feedback (or merge).

## Bug reporting

If you face any bugs when using the library, submit it
on [issue board](https://github.com/andret2344/ats-arguments/issues).

### Bug report template

```
As a user, I want to <your goal>.
I try to do it this way:
- Detailed
- Steps
- To
- Reproduce
I expect <precise expectation>. 
Instead of this, <what happens?>
```

## Enhancement request

The way of requesting it is analogical to reporting bugs. Create an issue
on [issue board](https://github.com/andret2344/ats-arguments/issues).

## Code convention

There is checkstyle rules file stored in the repository. CI pipeline checks all the checkstyle rules on every commit.

This is the way it has to be all the time.
