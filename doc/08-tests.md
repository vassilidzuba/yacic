# Tests

yacic has been used to try to build a few open source repo. We describe here the problems we found.

Note that:

- to avoid multiple useless cloning of the repo from github, a flag `NOCLONE` has been introduced
- we don't want to deploy release versions of these projects, so that a flag `NODEPLOY` has been introduced
- we don't want to build docker images for libaries, so that a flag `NODOCKER` has been used

## jackson-core

The branch `2.18` has been built, with tag `jackson-core-2.18.4`.

I didn't found *jackson-base* on gitgub, so its impossible to build snapshots versions.
Release versions are identified by tags so that the checkout of the tag has been dont manually
as yacic is not able to do that yet.

It has been run using java11-build pipeline.

Many sonar issues were found.

## commons-lang

The branch master has been built, with pipeline java11-build.

Many sonar issues were found, with version `3.18.0-SNAPSHOT`.

## jetty.project

doesn't work:

requires call git from maven

## junit5

doesn(t work

requires gradle

