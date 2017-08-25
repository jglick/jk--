# How to Use Jenkins Less

## abstract

You will hear plenty of advice about how to use more Jenkins features and plugins. For certain goals, that is exactly what you need to do. But often your best bet is to avoid special Jenkins features and use external tools or systems. Hear about how and why you can minimize your Jenkins footprint.

## full description

Every person who wrote a Jenkins plugin, or a feature included in a plugin or Jenkins core, was trying to let users solve some problem more easily. The trouble is that some of these plugins are old and not so well maintained. Even when they are, every feature your CI/CD system relies on has a not-so-hidden cost: unknown or unaddressed bugs; complex UI with sparse documentation; lack of support for very new or exotic tools or protocols; small user community with little collective knowledge; risk of behavioral changes or regressions during upgrades. Therefore a smart administrator needs to understand the difference not only between critical and nice-to-have features, but also which behaviors must be implemented in Jenkins as opposed to those which are mere _conveniences_ that could be handled differently. “Building vs. buying” should not be a reflexive choice.

In this talk we will study some Jenkins capabilities from the perspective of whether and how you could get the same effect without them; in other words, the unique value of Jenkins feature code. Can you send mail/chat notifications without a special plugin for that system—and pick the right recipient(s) and message? Can you “check out” an SCM repository into a workspace as part of a Jenkins-neutral build script—or will you be stuck getting tooling, credentials, and changelogs right? When can and should you write Pipeline libraries instead of using a plugin? What is the difference between build “wrappers”, custom “project types”, and custom build “steps” when it comes to using complex tools? Do you need a report plugin tailored to your reporting tool or will a more generic integration serve as well?

Expect to see demonstrations of different approaches to the same problem which illustrate the trade-offs between tighter integration with the Jenkins UI and other features; vs. transparency, flexibility, and self-reliance.

# Plugins! So many plugins!

## On a good day

* So easy to get started! And inline help shows me how to select the right choices.
* Lots of features ready to use before I even anticipated needing them.
* Integrated smoothly with other plugins and Jenkins features.
* Jenkins update center constantly offers me improvements and fixes. I just “select all”!

## On a not-so-good day

* This configuration form is crazy. Maybe I should edit the wiki and describe how to fill it out.
* This bug is a showstopper! Am I the only one to use this mode? Why did no one fix this yet?
* Who maintains this thing anyway? Hmm, last release was in 2013, and only 350 installations…
* Not working yet. I guess I could start a debugger and figure it out but I do not know Java well.
* Did anyone take a look at my pull request yet? I need it to support the newest protocol.
* Oops, I just updated to 2.27 from 2.3 and I am getting errors! But I do not want to roll back all the way.
* One team using my server is happier with the old version, can I just update the plugin for everyone else?

## The “build vs. buy” questions

* Do you care more about getting started quickly, or long-term maintainability of your CI/CD system?
* Are your feature needs complex & idiosyncratic, or straightforward & common?
* Are the plugins you use maintained well? If not, do you have in-house expertise?
* Do you enjoy copying & pasting from stackoverflow.com?

# Using Builders & Publishers Less

## Steps vs. wrappers vs. project types

* many plugins offer build steps
    * some run unique functionality, others mostly package up options
* “wrappers” set an environment for nested steps
    * details of how to run the actual step left to the job definition
* Pipeline jobs can interleave these in any order
    * not so in freestyle, so many plugin features are like `if` statements with a UI
* steer clear of dedicated project types!
    * CloudBees Jenkins Enterprise has job templates which offer a plugin-like UI

## How do I run Maven in a Jenkins job?

* Pipeline project:
    * `sh 'mvn install'`
    * `withMaven` wrapper
    * colorizing output? custom `settings.xml`? tool, JDK, publishers, snapshot deps?
* freestyle project:
    * shell step `mvn install`
    * or, Maven build step
* Maven project
    * obvious and convenient
    * a terrible choice for many reasons, including:
        * nonstandard execution classpath, breaks some extensions
        * Jenkins classes in build JVM, including Remoting channel

## Demo: different ways of running Maven

## Generic vs. specific publishers

* “notifiers” can be treated much like builders
    * example: mail or chat notification
    * for Pipeline, really there is no difference at all
* “recorders” more often integrate with Jenkins APIs
  * but some are more general than others
* some are hybrids, like Testopia recently put up for adoption
    * partly sends/receives data from an external server
    * partly has a special build result, only slightly different from JUnit/XUnit plugins

## Demo: HTML Publisher vs. Javadoc

note [JENKINS-32619](https://issues.jenkins-ci.org/browse/JENKINS-32619)

# Using SCMs Less

## What does an SCM plugin do?

* check out / update sources (tool selection, credentials)
* generate changelog compared to previous build
* poll remote server for new changes
* scan for branches or repositories
* advanced:
    * identify “change requests” incl. metadata, trust status
    * “lightweight” checkouts
    * check out named revision (e.g., for a library)

## What can you do with no SCM plugin?

* check out / update sources
    * `withCredentials` (or freestyle equivalent) to authenticate
    * `tool` (or freestyle equivalent) to select installation, if any
* that’s all folks!

## What about a meta-plugin?

* Script SCM plugin more or less works
    * polling, changelogs
    * no Pipeline support, only freestyle
    * you need to know Apache Ant (do you?) and Groovy (?)
    * oops, Groovy scripting here is **unsafe** (more later)
* Shell Script SCM plugin works less
    * some polling, but no changelogs
    * also no Pipeline support
    * shell scripts must be portable on all agents

## Demo: userspace vs. dedicated Mercurial SCM

# Using Groovy Less

## What does Jenkins use Groovy for?

* standard Jenkins scripting language since 2006 at the latest
* extends Java syntax; familiar to many Java devs, less so to other users
* `/script` console, freestyle post-build, and many other uses
    * a custom resumable runtime used for Pipeline
* runs inside Jenkins JVM, can access same APIs as plugins use

## The pain of Groovy security

* Jenkins admins can run any script they like, but this does not scale
* so, enter the “sandbox”
    * most workaday scripts just run
    * but less-common API calls have to be approved before use
    * and fidelity to command-line semantics is imperfect
    * and this is a _very_ complex language → regressions are common

## And the pain of in-process scripting

* sandboxing is mandatory but adds runtime overhead
* unterminated loops, big allocations, etc. stop master from scaling
* scripts are tied to the master’s version of Java and Groovy

## Preferring external processes

* can run on an agent, taking load off the master
* container technology can apply quotas, networking rules, …
* each user picks the exact language and runtime they need

# Using DSLs Less

## Pipeline DSL plugins

* some Pipeline integrations expose a DSL beyond step definitions
* prettier `Jenkinsfile` usage, but
    * little or no **Pipeline Syntax** support
    * cannot use basic Pipeline APIs, need to depend on Groovy details
    * generally unusable from Declarative Pipeline

## The saga of Docker Pipeline

* offers DSL rooted at `docker` to build & run Docker images
* `Image.inside`: major feature to run build steps inside container
    * used by Declarative Pipeline’s `agent docker`
    * very convenient when it works
    * but has common, severe, and nonobvious limitations
* some Credentials and “tool” (Docker CLI) integrations
    * also available as plain steps
* rest of DSL is sugar for `sh 'docker …'` commands
    * some Jenkins fingerprint integration, but no consumer
    * subject of endless PRs, like tweaking args to `build`

## Demo: DSL vs. hand-rolled

## Using Pipeline libraries

* alternative to DSLs
    * similar flexibility, but pulled by `Jenkinsfile`, not pushed by plugin
* available directly from SCM, as branch/tag/revision
    * extension point could support artifact repositories, etc.
    * possible to self-publish on GitHub
* “paired PR” tactic for proposing & testing changes

## Demo: `mvn.groovy` and pinned library versions
