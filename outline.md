# How to Use Jenkins Less

## abstract

You will hear plenty of advice about how to use more Jenkins features and plugins. For certain goals, that is exactly what you need to do. But often your best bet is to avoid special Jenkins features and use external tools or systems. Hear about how and why you can minimize your Jenkins footprint.

## full description

Every person who wrote a Jenkins plugin, or a feature included in a plugin or Jenkins core, was trying to let users solve some problem more easily. The trouble is that some of these plugins are old and not so well maintained. Even when they are, every feature your CI/CD system relies on has a not-so-hidden cost: unknown or unaddressed bugs; complex UI with sparse documentation; lack of support for very new or exotic tools or protocols; small user community with little collective knowledge; risk of behavioral changes or regressions during upgrades. Therefore a smart administrator needs to understand the difference not only between critical and nice-to-have- features, but also which behaviors must be implemented in Jenkins as opposed to those which are mere _conveniences_ that could be handled differently. “Building vs. buying” should not be a reflexive choice.

In this talk we will study some Jenkins capabilities from the perspective of whether and how you could get the same effect without them; in other words, the unique value of Jenkins feature code. Can you send mail/chat notifications without a special plugin for that system—and pick the right recipient(s) and message? Can you “check out” an SCM repository into a workspace as part of a Jenkins-neutral build script—or will you be stuck getting tooling, credentials, and changelogs right? When can and should you write Pipeline libraries instead of using a plugin? What is the difference between build “wrappers”, custom “project types”, and custom build “steps” when it comes to using complex tools? Do you need a report plugin tailored to your reporting tool or will a more generic integration serve as well?

Expect to see demonstrations of different approaches to the same problem which illustrate the trade-offs between tighter integration with the Jenkins UI and other features; vs. transparency, flexibility, and self-reliance.

# Plugins! So many plugins!

## On a good day

* full UI, often easy to get started
  * inline help is ideal for documentation
* lots of features anticipated for you
* detailed integrations with other plugins and Jenkins features
* Jenkins update center constantly offering improvements and fixes, just “select all”

## On a not-so-good day

* Who maintains this thing anyway? Hmm, last release was in 2013…
* This bug is so awful! I am the only one to use this mode? Why did no one fix this yet?
* Did you take a look at my pull request yet? I need it to support the newest protocol.
* This configuration form is crazy. Maybe I should edit the wiki and describe how to fill it out.
* Not working yet, and only a thousand installations. I guess I will have to open a Java debugger and figure it out.
* Oops, I just updated to 2.2.7 from 2.2.3 and I am getting errors!
* One team using my server is happier with the old version, can I just update the plugin for everyone else?

# Using Builders & Publishers Less

## Steps vs. wrappers vs. project types

**TODO**

## Generic vs. specific publishers

**TODO**

## Running Maven

* `pipeline-maven`
* `maven-plugin`
* freestyle Maven builder

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

## Demo: userspace vs. dedicated Mercurial SCM

# Using Pipeline Libraries

**TODO**
