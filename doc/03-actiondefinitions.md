# Action definitions

[previous](02-installation.md)
[toc](_toc.md)
[next](04-images.md)

The pipelines refer to actions that are defined in the XML files specified in the action definition directory,
as defined in the global configuration

## Structure of an action definition

The DTD of these file is:

```dtd
<!DOCTYPE podmanactiondefinitions [

<!ELEMENT podmanactiondefinitions (podmanactiondefinition+)>

<!ELEMENT podmanactiondefinition (image?, username, command, setup?, cleanup?, role>
<!ATTLIST podmanactiondefinition 
          id ID #REQUIRED
          mode (host) #IMPLIED
          uselocalproperties (true|false) false>

<!ELEMENT image     (#PCDATA)>
<!ELEMENT username  (#PCDATA)>
<!ELEMENT command   (#PCDATA)>
<!ELEMENT setup     (#PCDATA)>
<!ELEMENT cleanup   (#PCDATA)>
<!ELEMENT role      (#PCDATA)>

)>
```

### Element podmanactiondefinitions

This is the top level element, that contains a list of action  definitions


### Element podmanactiondefinition

This is the definition of an action. It contains the various properties of the action definitions, and has three attributes:

- `id` is the identifier of the action definition, that will be used in the pipeline definition
- `mode` can have the value `host'.In that case the command is directly executed on the host, while by defaut it is executed in a container.
- 'uselocalproperties', if true, indicates that one get properties definitions from  a file `yacic-properties.json` in the current repo. 

## Element image

This element contains the tag of the docker image used to run the action.

Exemple:

```xml
<image>docker.io/alpine/git</image>
```

### Element username

This element contains the name of the username used to connect to the execution machine.

```xml
<username>podman</username>
```

### Element command

This the main command to be executed. It will be completed by the subcommand defined in the pipeline.
Note that *podman* is assumes, except if the mode is *host*.

Here is an example of a command in podman mode:

```xml
<command>--name auth-PROJECT -v ${HOME}:/root -v DATAAREA/PROJECT:/git docker.io/alpine/git</command>
```

Here is an example of a command in host mode:

```xml
<command>cp DATAAREA/PROJECT/target/*-javadoc.jar /home/podman/nginx/javadoc; cd /home/podman/nginx; ./launch-nginx.sh; systemctl --user restart nginx</command>
```

### Element setup

This element contains a command to be executed before the main command, on the host.

```xml
<setup>rm -rf DATAAREA/PROJECT; mkdir -p DATAAREA;</setup>
```
		

### Element cleanup

This element contains a command to be executed after the main command, on the host.


### Element role

This element assigns a role to the action, that allow to determine the host on which it will be executed,
using the mapping between roles and host found in the global configuration.

## The default configuration

This section defines the actions definitions of the default configuration.

Note that then run process will substitute the property names by their values.
The syntax of a property reference is;

    @{name}

or, when providing a defaulty value:

    @{name:default}

Several properties are defined by default:

- `DATAAREA`: the directory in which the repo is cloned, derived from the `root` property
- `PROJECT`: the name of the project, asspecified in the run commands
- `REPO`: the url of the repon ass specified in the project file
- `BRANCH`: the name of the branch, as specified in the run command.
- `BUILID`: id of the build, computed incrementally fora given repo/branch using the database.
- `RELEASE`: release number as defined in the project configuration
- `ACTIONID`: id of the action

### Action clone

```xml
<podmanactiondefinition id="clone">
    <image>docker.io/alpine/git</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v ${HOME}:/root -v @{DATAAREA}:/git docker.io/alpine/git</command>
    <setup>rm -rf @{DATAAREA}/@{PROJECT}; mkdir -p @{DATAAREA};</setup>
    <cleanup></cleanup>
    <role>git</role>
</podmanactiondefinition>
```

This action clones a git repo anonymously.
Here is an example of pipeline step that uses it:

```xml
<step id="clone" 
      category='podman'
      type="clone"
      subcommand='clone -b "@{BRANCH}" @{REPO}'>
  	<description>clone repository</description>
</step>
```

Note that the (git command is implied and should not be present in the subcommand.


### Action git

```xml
<podmanactiondefinition id="git">
    <image>docker.io/alpine/git</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v ${HOME}:/root -v @{DATAAREA}/@{PROJECT}:/git docker.io/alpine/git</command>
    <setup></setup>
    <cleanup></cleanup>
    <role>git</role>
</podmanactiondefinition>
```

This action executes a git command. It differs from the `clone` command only by the `setup` command. It assumes that the repo has already been cloned.
Here is an example of step to tag the repo:


```xml
<step id="git_tag" 
      category='podman'
      type="git"
      subcommand="tag v@{RELEASE}.@{BUILDID}">
   <description>tag repo</description>
</step>
```


### Action maven

```xml
<podmanactiondefinition id="maven">
    <image>maven:3.9.9-amazoncorretto-21-alpine</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v "$HOME/.m2:/root/.m2" -v "@{DATAAREA}/PROJECT}":/usr/src/PROJECT -w /usr/src/@{PROJECT} maven:3.9.9-amazoncorretto-21-alpine</command>
    <role>java</role>
</podmanactiondefinition>
```

This action executes a maven command. Here is an example of pipeline step usingh it:

```xml
<step id="build" 
      category='podman'
      type="maven"
      subcommand="mvn clean package">
   <description>build package using maven</description>
</step>
```


###  Actio maven_onar

```xml
<podmanactiondefinition id="maven_sonar">
    <image>192.168.0.20:5000/maven-sonar:java21</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} --secret sonar-token,type=env,target=token -v "$HOME/.m2:/root/.m2" -v "@{DATAAREA}/PROJECT}":/usr/src/@{PROJECT} -w /usr/src/@{PROJECT} 192.168.0.20:5000/maven-sonar:java21</command>
    <role>java</role>
</podmanactiondefinition>
```

This action executes a `maven sonar:sonar` command. It is different from the generic action command as one need to provide the sonar authentication
token through trhe podman secrets. Here is an example of use iun apipeline step:

```xml
<step id="sonar" 
      category='podman'
      type="maven_sonar"
      subcommand="">
    <description>execute sonarqube</description>
  
</step>
```

### Action deploy_javadoc

```xml
<podmanactiondefinition id="deploy_javadoc" mode="host">
    <username>podman</username>
    <command>cp @{DATAAREA}/@{PROJECT}/target/*-javadoc.jar /home/podman/nginx/javadoc; cd /home/podman/nginx; ./launch-nginx.sh; systemctl --user restart nginx</command>
    <role>java</role>
</podmanactiondefinition>
```

This action is used to copy the javadoic jar files to nginx directory. It is executed on the host, not podman, and an example of step is:

```xml
<step id="deploy_javadoc" 
      category='podman'
      type="deploy_javadoc"
      subcommand="">
    <description>deploy javadoc to nginx</description>
  
</step>
```

### Action build_image

```xml
<podmanactiondefinition id="build_image" mode="host" uselocalproperties="true">
    <username>podman</username>
    <command>cd @{DATAAREA}/@{PROJECT;  podman build -t @{DOCKERTAG} -f Dockerfile</command>
    <role>java</role>
</podmanactiondefinition>
```

This actions build a Docker image using Podman and a Dockerfile. It is executed on the host and uses the property DOCKERTAG defined in the project configuration
or the local properties.
Here is an example of step using it, witich is executed only if the flag NODOCKER is not set:

```xml
<step id="build-image" 
      category='podman'
      type="build_image"
      subcommand="">
    <description>build podman image</description>
    <skipwhen>
        <flag name="NODOCKER"/>
    </skipwhen>
</step>
```

### Action go_tidy

```xml
	<podmanactiondefinition id="go_tidy">
		<image>docker.io/library/golang:bookworm</image>
		<username>podman</username>
		<command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/golang:bookworm go mod tidy </command>
		<role>golang</role>
	</podmanactiondefinition>
```

This action executes a `go mod tidy` command in the repo. Here is an example of strp using it:

```xml
<step id="tidy" 
      category='podman'
      type="go_tidy"
      subcommand="">
    <description>compile the project</description>
  
</step>
```


### Action go_lint

```xml
	<podmanactiondefinition id="go_lint">
		<image>192.168.0.20:5000/golang-lint:2.1.6</image>
		<username>podman</username>
		<command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp 192.168.0.20:5000/golang-lint:2.1.6 run</command>
		<role>golang</role>
	</podmanactiondefinition>
```

This action executes a `golangci-lint` command in the repo. Here is an example of strp using it:

```xml
<step id="lint" 
      category='podman'
      type="go_lint"
      subcommand="">
    <description>compile the project</description>
  
</step>
```

### Action go_test

```xml
<podmanactiondefinition id="go_test">
    <image>docker.io/library/golang:bookworm</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/golang:bookworm go test</command>
    <role>golang</role>
</podmanactiondefinition>
```

This action executes a `go test` command in the repo. Here is an example of sterp using it:

```xml
<step id="test" 
      category='podman'
      type="go_test"
      subcommand="">
    <description>compile the project</description>
  
</step>
```

### Action go_test_coverage

```xml
<podmanactiondefinition id="go_test_coverage">
    <image>docker.io/library/golang:bookworm</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/golang:bookworm go test -coverprofile=coverage.out</command>
    <role>golang</role>
</podmanactiondefinition>
```
This action executes a `go test` command in the repo, with generation of the coverage data. Here is an example of sterp using it:

```xml
<step id="test" 
      category='podman'
      type="go_test_coverage"
      subcommand="">
    <description>compile the project</description>
  
</step>
```

### Action go_coverage_to_html

```xml
<podmanactiondefinition id="go_coverage_to_html">
   <image>docker.io/library/golang:bookworm</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/golang:bookworm go tool cover -html=coverage.out -o coverage.html</command>
    <role>golang</role>
</podmanactiondefinition>
```

This action converts the coverage data into an html file. Here is an example of step using it:

```xml
<step id="test" 
      category='podman'
      type="go_coverage_to_html"
      subcommand="">
    <description>generate html coverage file</description>
  
</step>
```


### Action go_compile

```xml
	<podmanactiondefinition id="go_compile">
		<image>docker.io/library/golang:bookworm</image>
		<username>podman</username>
		<command>--name  @{ACTIONID}-@{PROJECT} -v /mnt/yacic/go:/go  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/golang:bookworm go build -o PROJECT MAIN </command>
		<role>golang</role>
	</podmanactiondefinition>
```

This action executes a `go build` command. 
It uses the property MAIN that contains the name of the file with the main entry point.
Here is an example of step using it:

```xml
<step id="test" 
      category='podman'
      type="go_compile"
      subcommand="">
    <description>buildthe executable</description>
  
</step>
```

### Action gcc_compile

```xml
<podmanactiondefinition id="gcc_compile">
    <image>docker.io/library/gcc:bookworm</image>
    <username>podman</username>
    <command>--name  @{ACTIONID}-@{PROJECT} -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp docker.io/library/gcc:bookworm make @{PROGRAM} </command>
    <role>gcc</role>
</podmanactiondefinition>
```

This action compiles a c/cpp program.

### Action rust

```xml
<podmanactiondefinition id="rust">
    <image>docker.io/library/rust:1.87.0-alpine3.20</image>
    <username>podman</username>
    <command>--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR}  -v "$HOME/.cargo:~/.cargo" -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp @{IMAGE} </command>
    <role>rust</role>
</podmanactiondefinition>

```

This action is used to build a Rust program.


### Action rust

```xml
<podmanactiondefinition id="zig">
    <image>192.168.0.20:5000/zig:0.15</image>
    <username>podman</username>
    <command>--name @{ACTIONID}-@{PROJECT}-@{BRANCHDIR}  -v @{DATAAREA}/@{PROJECT}:/usr/src/myapp -w /usr/src/myapp @{IMAGE} </command>
    <role>zig</role>
</podmanactiondefinition>

```

This action is used to build a Zig program.

### Action build_deb

```xml
<podmanactiondefinition id="build_deb" mode="host">
    <username>podman</username>
    <command>cd @{DATAAREA/@{PROJECT}; ./build_deb.sh</command>
    <role>golang</role>
</podmanactiondefinition>
```

This action build a .deb file. It is run on the host, that must therefore be debian-based.



[previous](02-installation.md)
[toc](_toc.md)
[next](04-images.md)


Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
