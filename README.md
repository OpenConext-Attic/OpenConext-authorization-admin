# OpenConext-authorization-admin
Admin client for the Oauth2 server for the OpenConext platform.

## Development

To run locally:

`mvn spring-boot:run`

Or use the shortcut:

    ./start.sh

We use sass to ease the pain of CSS development. We recommend you install sass using ruby. Best is to manage your rubies
with [rbenv](https://github.com/sstephenson/rbenv). After installing rbenv ```cd``` into this directory and run:

    gem install sass

Then run

    sass --watch src/main/sass/application.sass:src/main/resources/static/css/application.css

Or use the shortcut:

    ./watch.sh
