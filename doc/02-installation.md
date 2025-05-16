# Installation

[toc](_toc.md)
[previous](01-intro.md)


Running `yacic` requires Java 21 or later. the program is packaged as a fat jar.
It requires a configuration containing the definitions of the pipelines and projects.
A default configuration is available in the repository in `server/config`.

## Deployment

Assuming the deployment directory is DEPLOYMENT, one would find in that directory:

- the jar `server-1.0-SNAPSHOT.jar` (the version may evolve)
- a config directory that would contain
    - the main configuration file (usually `config.json`)
    - a subdirectory with the actions (by default `actiondefinitions`)
    - a subdirectory with the pipelines (by default `pipelines`)
    - a subdirectory with the projects (by default `projects`)

The command to run the service will be :

    java -jar server-1.0-SNAPSHOT.jar server config/yacic.json

## The database

to be completed.

## The configuration file

The sample configuration file is:

```json
{
   "pipelineDirectory": "config/pipelines",
   "actionDefinitionDirectory": "config/actiondefinitions",
   "projectDirectory": "config/projects",
   "logsDirectory": "\\\\192.168.0.19\\yacic\\logs",
   "authenticationFile": "config/authentication.json",
   "maxNbLogs": 5,
      
   "nodes": [
      {"host": "odin", "roles": ["git", "java", "golang", "gcc"]}
   ],
   
   "database": {
       "url": "jdbc:postgresql://odin:5432/yacic",
       "user": "yacic",
       "password": "****!"
   }
}
```

The fields in this configuration are:

- `pipelineDirectory`: directory containing the pipeline definitions
- `actionDefinitionDirectory`: directory containing the action definitions
- `projectDirectory`:  directory containing the projects definitions
- `logsDirectory`: directory containing the logs
- `authenticationFile`: file with the authentication data
- `maxNbLogs`: the maximum number of logs to keep
- `nodes`: the nodes (see below)
- `database`: the connection properties od the database

The nodes are hosts on which the actions are run (here a single one, named `odin`)
To each node is associated a list of roles. A given action has an associated role, and will be run randomly on a host accepting that role.



## The ports

The service is run by default on the port 8080, with the admin port being 8081.
The posts can be changed by adding the following in the configuration file:

```json

"server": {
        "applicationConnectors":
            [
                {
                "type": "http",
                "port": 9090
                }
            ],
        "adminConnectors": [
            {
            "type": "http",
            "port": 9091}
            ]
    }
```


## The authentication

The authentication data contains a list of pairs (user name, password. For instance:

```json
[
	{
		"username": "vassili",
		"password": "sekret"
	},
	{
		"username": "johndoe",
		"password": "seKret"
	},
]

```


[toc](_toc.md)
[previous](01-intro.md)

Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
