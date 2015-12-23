# OpenConext-authorization-admin

[![Build Status](https://travis-ci.org/OpenConext/OpenConext-authorization-admin.svg)](https://travis-ci.org/OpenConext/OpenConext-authorization-admin)
[![codecov.io](https://codecov.io/github/OpenConext/OpenConext-authorization-admin/coverage.svg)](https://codecov.io/github/OpenConext/OpenConext-authorization-admin)

Admin client for the Oauth2 server for the OpenConext platform.

## Development

Connect to your local mysql database: `mysql -uroot`

Execute the following:

```sql
CREATE DATABASE authzserver DEFAULT CHARACTER SET latin1;
create user 'travis'@'localhost';
grant all on authzserver.* to 'travis'@'localhost';
```

In any other environment other then local we connect to the database controlled by [OpenConext-authorization-server](https://github.com/OpenConext/OpenConext-authorization-server) as described in the [README](https://github.com/OpenConext/OpenConext-authorization-server/blob/master/README.md).

In the application.properties we enable Flyway and override this behaviour with ansible for other environments.

To run locally:

`mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"`

Or use the shortcut:

`./start.sh`

We use sass to ease the pain of CSS development. We recommend you install sass using ruby. Best is to manage your rubies
with [rbenv](https://github.com/sstephenson/rbenv). After installing rbenv ```cd``` into this directory and run:

    gem install sass

Then run

    sass --watch src/main/sass/application.sass:src/main/resources/static/css/application.css

Or use the shortcut:

    ./watch.sh

## Production

Since only users with any of the roles defined in the property ```allowed_roles``` are allowed to connect to this app
  we need to query the voot service. The vootservice is registered with resource_id **groups** in the ```OpenConext-authorization-server```

This admin app needs to be registered out-of-band in the OpenConext-authorization-server using the following SQL:

```sql
INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri)
VALUES ('authz-admin', 'groups', '$2a$10$MUw.vpbP4PMUBXHcZagPBe292hwzPI4qjXi1u8e6ON8PJmCnQ8U0S', 'read','authorization_code', 'http://localhost:8081');
```
