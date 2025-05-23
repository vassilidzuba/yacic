# API


[previous](06-projects.md)
[toc](_toc.md)


*Yacic* is a REST service, accessed through a client, for instance curl or one of the provided clients in development (*yacicctl* and *yacictfe*)

The API can be divided in severalcategories according to the type of object it concerns:

- config
- pipeline
- project
- build
- step

The call of the APIs must be authentified. We will assume here that the user name is *vassili* and the password is *sekret*.

## config

### /yacic/config/reload

This command reloads the configuration files (global configurations, actiuons, pipelines). It ha no parameter:

```bash
curl -u vassili:sekret http://localhost:8080/yacic/config/reload
```

The result should be:

```json
{"ok":true}
```

## pipeline

### /yacic/pipeline/list

This command lists the pipelines. It has no parameter:

```bash
curl -u vassili:sekret http://localhost:8080/yacic/pipeline/list
```

The result could be:

```json
["java-build","java-release","gcc-build","go-build","java-graalvm"]
```

## project

### /yacic/project/list

This copmmand lists the projects.It has no parameter:

```bash
curl -u vassili:sekret http://localhost:8080/yacic/project/list
```

The result could be (after formatting and deleting of a large part):

```json
[
	{
		"projectId": "badexample1",
		"repo": "http://odin.manul.lan:3000/vassili/badexample1.git",
		"branches": [
			{
				"branchId": "main",
				"branchDir": "b0"
			},
			{
				"branchId": "feature/initial",
				"branchDir": "b1"
			}
		]
	},
   ...
]

```

### /yacic/project/run

This command is used to run a project. The parameters are:

- `project`: name of the project (mandatory)
- `branch`: name of the branch (optional, when missing, `main` will be assumed)

For instance:

```xml
curl -u vassili:sekret "http://localhost:8080/yacic/project/run?project=example1&branch=feature/initial"
```

The result should be similar (after formatting) to:

```json
{
	"projectId": "example1",
	"branchId": "feature/initial",
	"timestamp": "20250523165339",
	"status": "ok",
	"duration": 76891,
	"pipeline": "java-build"
}
```

The fields of the reply are:

- `projectId`: name of the project
- `branchId`: nameof the branch
- `timestamp`: timestamp (startt date/time of the build)
- `status`: exit status
- `duration`: duration of the run in millisseconds
- `pipeline`: name of the executed pipeline


### /yacic/projectt/get

This command returns a file from the project directory. It could be uses forijnstance toi return the HTML file with the coverage of a
golang project, as we don't use sonarqube in that case yet. It has two parameters, the project name and the branch name (with a default value of `main`)..

```bash
curl -u vassili:sekret "http://localhost:8080/yacic/project/get?project=hellogo&branch=feature/initial&file=coverage.html"
```

The result, when successfull, is the content of the file.

### /yacic/projectt/getconfig

This command returns the configuration file of the project. It has only a single parameter, the project name.

```bash
curl -u vassili:sekret "http://localhost:8080/yacic/project/getconfig?project=hellogo"
```

## build

### /yacic/build/list

This command lists the build for a given project and branch:

```bash
curl -u vassili:sekret "http://localhost:8080/yacic/build/list?project=hellogo&branch=feature/initial"
```

The result will be similar (after formatting anbd deleting of alarge part):

```json
[
	{
		"projectId": "hellogo",
		"branchId": "feature/initial",
		"timestamp": "20250523152952",
		"status": "ok",
		"duration": 64762,
		"build_id": 1
	},
   ...
]
```

The fields are the same as in the return of the project/run command (without the pipeline name).

### /yacic/build/log

This command returns the log associated with a build.
The parameters are:

- `project`: the name of the project (mandatory)
- `branch`: the name of the branch (optional, the default is `main`)
- `timestamp`: the timestamp of the build (optional, the default is the most recent build)


```bash
curl -u vassili:sekret "http://localhost:8080/yacic/build/log?project=hellogo&branch=feature/initial&timestamp=20250523152952"
```

The result will be the content off the log.


## step

### /yacic/step/list

This command lists the steps of a build.

```bash
curl -u vassili:sekret "http://localhost:8080/yacic/step/list?project=hellogo&branch=feature/initial&timestamp=20250523152952"
```

The result will be similar (after formatting and deleting of a large part) to:

```json
[
	{
		"projectId": "hellogo",
		"branchId": "feature/initial",
		"timestamp": "20250523152952",
		"stepId": "clone",
		"seq": 0,
		"status": "ok",
		"duration": 3824
	},
   ...
]
```

It gives access to the duration of each steps.



[previous](06-projects.md)
[toc](_toc.md)



Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

