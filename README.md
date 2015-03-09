# OpenConext-authorization-admin
Admin client for the Oauth2 server for the OpenConext platform.

## Development

Setup the database controlled by [OpenConext-authorization-server](https://github.com/OpenConext/OpenConext-authorization-server) as described in the [README](https://github.com/OpenConext/OpenConext-authorization-server/blob/master/README.md).

To run locally:

`mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"`

Or use the shortcut:

    ./start.sh

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
