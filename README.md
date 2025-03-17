# YACIC

This project aims at developing yet another java application to run CI pipelines with the following characteristics:

* simple
* customisable
* extensible


warning: this project is in development, not fit for any purpose yet.

## Project organization

We have the following modules:

* model: define the main concepts
* podmanutil: utilities to access podman
* simpleimpl: simple implementation of a minimal pipeline (sequential execution) and an orchestrator
* persistence: allow the persistene of the data in a database
* server: the REST server providing an interface to the application



## License

This project is licensed under Apache License, version 2 (http://www.apache.org/licenses/LICENSE-2.0).
