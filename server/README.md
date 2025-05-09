# YACIC Server

This is a REST service giving access to the functionalities of the application.

Here are examples of calls using cURL:

to get the list of the projects:

    curl -u vassili:sekret http://localhost:8080/yacic/project/list

to get the list of the pipelines:

    curl -u vassili:sekret http://localhost:8080/yacic/pipelines/list

to run a pipeline (where *example1* is the project name):

    curl -u vassili:sekret http://localhost:8080/yacic/project/run?project=example1
    curl -u vassili:sekret "http://localhost:8080/yacic/project/run?project=example1&branch=feature/initial"
    
to obtain, the log of the run:

    curl -u vassili:sekret http://localhost:8080/yacic/project/log?project=example1
    curl -u vassili:sekret "http://localhost:8080/yacic/project/log?project=example1&branch=feature/initial"
    curl -u vassili:sekret "http://localhost:8080/yacic/project/log?project=example1&branch=feature/initial&pos=2"
    
## Security

The credentials are stored in an unencrypted file `config/security.json`. Authentication is required but no authorization mechanism is implemented.
