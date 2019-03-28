# xMessenger (API server)
This project is part of <b>xMessenger</b> application ecosystem. Written on SpringBoot it provides
both open & secure RESTful API for those who want to integrate with it. Also, it's free cloud based browser messenger 
hosted on <i>Heroku</i> platform.
### Used technologies:
`java`, `websockets`, `spring-boot`, `postgre_sql`, `travis`, `heroku`, `oauth`, `cors`
### Sample data setup
In order to demo application with some set of data, make sure to go through next steps:<br/>
> Setup PostgreSQL connection and create demo database (e.g. `sampledata`)<br/>

> Restore DB schema with sample data from [sampledata.psql](https://github.com/awesomeandrey/xMessenger/blob/master/data) file:<br/>
```shell
$ psql -U username dbname < sampledata.psql
```
> Configure datasource in [application.properties](https://github.com/awesomeandrey/xmessenger-api/blob/master/src/main/resources/application.properties) to retrieve data from proper DB

### RDBMS & Dyno management
[Heroku Postgres](https://www.heroku.com/postgres) - managed SQL Database as a Service for all developers.<br/>
Commands for maintaining DB on Heroku:
> Push database:<br/>
```shell
$ heroku pg:reset --app xmessenger-app
$ heroku pg:push mylocaldb DATABASE_URL --app xmessenger-app
```
> Pull database:<br/>
```shell
$ heroku pg:pull DATABASE_URL mylocaldb --app xmessenger-app
```
In order to run command on *Heroku* environment, use following syntax:<br/>
```shell
$ heroku run "echo 'Hello World!'"
```
Occasionally, Heroku Dyno can bump into <u>R14 – Memory Quota Exceeded</u> error preventing app form launching.<br/>
The workaround is to restart container:
```shell
$ heroku restart
```
If providing a Postgres user or password for your local DB is necessary, use the appropriate environment variables like this:<br/>
`PGUSER=username PGPASSWORD=password heroku pg:pull DATABASE_URL mylocaldb --app xmessenger-app`

**On Windows 10 you're supposed to run commands through Git Bash terminal.**
**Heroku CLI works stably only in Unix environment**

### Deployment

Continuous Delivery and Continuous Integration is achieved with the help of utility `Travis CI`.<br/>
Luckily, it provides high-quality support and integration with `Heroku` platform with all required options.<br/>
In order to fulfil `.travis.yml` file with proper API token, follow next steps:
1) navigate to Linux environment (for example `Ubunutu 16.04`);
2) install `ruby` package (internally, it'll install `gem` package);
3) install `travis` package using `gem`;
4) run `travis encrypt $(heroku auth:token) --add deploy.api_key` to generate Heroku API key.

The eventual pipeline is as follows: every new push to `GitHub` repo will trigger a new build on `Travis` platform (or it can be initiated manually);<br/>
once the build is successful, the generated metadata gets moved and deployed on `Heroku` in a dedicated `dyno` (platform specific app container).

Aside from that, there are config variables established on `Heroku` platform. Those are accessible via Heroku UI or
CLI commands. They contain app sensitive information (API keys, DB credentials).

From `Heroku` side, the Java buildpack will automatically detect the use of the Spring Boot web framework; 
it will create a web process type with the following command:
```shell
$ java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar
```
These defaults can be overridden or defined using a `Procfile`.
### Built With

* [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/) - Integrated IDE
* [SpringBoot](http://spring.io/) - The backend framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [TravisCI](https://travis-ci.com/) - CI and CD web utility
* [Postgre SQL](https://www.postgresql.org/) - The world's most advanced open source relational database
* [Heroku](https://www.heroku.com/) - Cloud platform that lets companies build, deliver, monitor and scale apps
* [VivifyScrum](https://app.vivifyscrum.com/) - Issue tracking product which allows bug tracking and agile project management

### Versioning

For the versions available, see the [tags on this repository](https://github.com/awesomeandrey/xmessenger-api/tags).

### Authors

* **Andrii Melnichuk** - *Initial work* - [awesomeandrey](https://github.com/awesomeandrey)