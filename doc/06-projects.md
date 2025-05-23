# Projects

[previous](05-pipelines.md)
[toc](_toc.md)
[next](07-api.md)


A project is defined in a subdirectory of the projects directory defined in the global configuration.

Assuming the projects directory is `config/projects` and the name of the project is `foo`, the projects configuration will be
in `config/projects/foo/foo.json`.

Here is an example of a project configuration:

```json
{
	"project": "hellojava",
	"repo": "http://odin.manul.lan:3000/vassili/hellojava.git",
	"root": "/mnt/yacic",
	
	"pipeline": "java-build",
	"pipelines": [
		{"branch": "main", "pipeline": "java-release"}
	],
	
	"branches": [
		{"name": "main",            "dir": "b0"},
		{"name": "feature/initial", "dir": "b1"}
	],
	"flags": ["DOCKER"],
	"properties": [
		{"key": "DOCKERTAG", "value": "192.168.0.20:5000/hellojava:1.0"}
	]
}
```

The fields in the configuration are :

- `project`: the name of the project
- `repo`: the URL of the git repository containing the project
- `root`: the directory in which the project will be cloned
- `pipeline`: the name of the pipeline used by default
- `pipelines`: a list of pipeles to be used with specific branches, typically when the release pipeline is different from the development pipeline
- `branches`: the branches. A branch has two properties:its name as used in Git, and the name of the subdirectory in which the repo will be cloned
- `flags`: the flags(here a single flag named `DOCKER`)
- `properties`: the properties,with a key and a value. the keys will be substituted by the values when in the comand.

In the previous example, the repos will be cloned in :

- `/mnt/yacic/hellojava/b0/hellojava` for the main branch
- `/mnt/yacic/hellojava/b1/hellojava` for the feature/initial branch

Of course, the directory containing the clone of the repo must be accessible using the same path for all the actions of the pipeline.



[previous](05-pipelines.md)
[toc](_toc.md)
[next](07-api.md)


Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

