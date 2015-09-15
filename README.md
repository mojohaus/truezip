# MojoHaus Truezip Maven Plugin

This is the [rpm-maven-plugin](http://www.mojohaus.org/truezip/).

[![Build Status](https://travis-ci.org/mojohaus/truezip.svg?branch=master)](https://travis-ci.org/mojohaus/truezip)

## Releasing

* Make sure `ssh-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```