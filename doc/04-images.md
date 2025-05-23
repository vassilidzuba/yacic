# Images


[previous](03-actiondefinitions.md)
[toc](_toc.md)
[next](05-pipelines.md)

Most the actions are performed by podman, and thus require an image.
We list here the images used by the default configuration.

## docker.io/alpine/git

This image is used to execute git.

## maven:3.9.9-amazoncorretto-21-alpine

This image is used to run maven, except when using graalvm orsonar.

## 192.168.0.20:5000/graalvm:24

This image is used to run maven with graalvm. It is built using the following Dockerfile:

```Dockerfile
FROM docker.io/library/ubuntu:25.04

RUN apt update

COPY graalvm-jdk-24.0.1+9.1 /opt/graalvm
ENV JAVA_HOME /opt/graalvm
ENV GRAALVM_HOME /opt/graalvm

COPY apache-maven-3.9.9 /opt/mvn

ENV PATH /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/opt/mvn/bin

RUN apt install -y build-essential zlib1g zlib1g-dev

```

## 192.168.0.20:5000/maven-sonar

This image is used to run maven with the sonar target. It is built using the following Dockerfile;

```Dockerfile
from docker.io/library/maven:3.9.9-amazoncorretto-21-alpine

CMD /usr/bin/mvn -Dsonar.token=$token sonar:sonar
```

It has been created only to be able to use the sonar token stored as a podman secret.

## docker.io/library/golang:bookworm

This image is used to run the *go* command.

## docker.io/golangci/golangci-lint

This image is used to run the ** command.


[previous](03-actiondefinitions.md)
[toc](_toc.md)
[next](05-pipelines.md)


Distributed under license [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)


