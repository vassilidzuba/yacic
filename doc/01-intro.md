# Introduction

[toc](_toc.md)
[next](02-installation.md)

YACIC is a program running CI/CI pipelines.
It's a REST service, that should be called by a dedicated client or even cURL.

The concepts are the following:

- a *project* is accessed in a Git repository
- the *project configuration* is stored in the global configuration outside of the project repository)
- the project configuration references a *pipeline*
- the pipeline, when run, executes *actions*. Each execution is acalled a *step*
- the actions are run on an executor host, usually via podman
- a run of a pipeline is called a *build*


## TODO

- add commands to manage the configuration without needing access to the installation file syste.
- allow to checkout a specific tag
- allow to build java programs when the pom is not at the root of the repo
- support gradle

[toc](_toc.md)
[next](02-installation.md)

Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)