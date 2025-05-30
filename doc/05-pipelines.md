# Pipelines

[previous](04-images.md)
[toc](_toc.md)
[next](06-projects.md)

Currently, only a singler type of pipeline are defined, where the steps are executed sequentially.

Pipeles are described in, XML files, with the following DTD:

```DTD
<!DOCTYPE pipeline[

<!ELEMENT pipeline (description, step*)>
<!ATTLIST pipeline
          id ID #REQUIRED>
          
<!ELEMENT description (#PCDATA)>

<!ELEMENT step (description, onlywhen?, skipwhen?>
<!ATTLIST step
          id ID #REQUIRED
          category (podman) #REQUIRED
        	 type CDATA #REQUIRED
          subcommand CDATA {IMPLIED>

<!ELEMENT onlywhen (flag*)>
<!ELEMENT skipwhen (flag*)>

<!ELEMENT flag   EMPTY>
<!ATTLIST flag
          name   CDATA #RTEQUIRED>   

]>

```

The elements are :

- `pipeline`: described the pipeline.Has a mandatory identifier `id` .
- `description`: description of a pipelineorstep
- `step`: described a step;the steps are executed sequentially.
- `onlywhen`: indicates that the step should be executed only when one or several flag are set (when all the flags must be set)
- `onlywhen`: indicates that the step should be skipped when one or several flag are set (when one oftyhe flag is set)
- `flag`: the flag, to be found in the project configuration or in the local properties.

The attributes of the `set` elements are:

- `id`: the identifier of the step
- `category`: currently always *podman*
- `type`: identifier of the action to be executed
- `subcommand`: the text to be added to the command specified in the action.

## Actions ans steps

Let's consider the action `maven`:

```xml
<podmanactiondefinition id="maven">
    <image>maven:3.9.9-amazoncorretto-21-alpine</image>
    <username>podman</username>
    <command>--name build-@{PROJECT} -v "$HOME/.m2:/root/.m2" -v "@{DATAAREA}/PROJECT}":/usr/src/@{PROJECT} -w /usr/src/@{PROJECT} maven:3.9.9-amazoncorretto-21-alpine</command>
    <role>java</role>
</podmanactiondefinition>
```

and a step that uses it:

```xml
  <step id="build" 
        category='podman'
        type="maven"
        subcommand="mvn -Dsha1=.@{BUILDID} -Dchangelist=  clean package">
     <description>build package using maven</description>
  
  </step>
```

The process to run the step is as follows:

- the *command* property of the action and the *subcommand* property of the step are addedtogether and to the command podman
- the property keys are substituted by their values (here *PROJECT*, *DATAAREA* and *BUILDID*)
- the role of the action is used to find the host on which the command will be executed
- the resulting command is executed on the host using ssh (axcept when it is *localhost*, in which case it is executed locally)


[previous](04-images.m)
[toc](_toc.md)
[next](06-projects.md)


## Default configuration

The pipelines defined in the defaultconfiguration are:

- `gcc-build`: to build a c/cpp program using Make
- `go-build`: to build a golang app
- `java-build`: to build a java artifact using maven -java 21)
- `java11-build`: to build a java artifact using maven -java 11)
- `java-release`: to build a java artifact in release mode using maven
- `java-graalvm`: to build a java artifact using maven with graalvm
- `rust-build`: to build a Rust program using cargo
- `zig-build`: to build a Zig program

[previous](04-images.m)
[toc](_toc.md)
[next](06-projects.md)


Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

