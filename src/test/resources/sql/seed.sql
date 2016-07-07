INSERT INTO `oauth_client_details` (`client_id`, `resource_ids`, `client_secret`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`)
VALUES
	('authz-admin', 'groups', '$2a$10$AuFCL562n3Fwepdtzj8N9OublcRuFkjM1KwTsgYFIZ/RmdfVGgpqG', 'groups', 'authorization_code,implicit', 'http://localhost:8081,https://localhost:8081', '', NULL, NULL, '{}', 'true'),
	('authz-playground', 'groups,foo', '$2a$10$CesdRHvXTJV.6zSPnwS9quBAbXWu2lXfg1k2Tp76SIgglBRmqYHSe', 'groups', 'authorization_code,implicit,client_credentials', 'http://authz-playground-local:8089/redirect,http://authz-playground-local:8089/redirect2', '', NULL, NULL, '{}', ''),
	('engineblock', 'groups', '$2a$10$M6S8fqrRTJPKob95SFoEYe0jrht0sZ53KJbCj5fB3AdonpVLU71qW', 'groups', 'client_credentials', '', '', NULL, NULL, '{}', ''),
	('vootservice', '', '$2a$10$O5wlBM2dJHapLZAQhpkjxuKeKcwslMXIWvBXTjaOOfZua6NK519gu', '', 'resource_server', NULL, 'ROLE_TOKEN_CHECKER', NULL, NULL, '{}', '');
