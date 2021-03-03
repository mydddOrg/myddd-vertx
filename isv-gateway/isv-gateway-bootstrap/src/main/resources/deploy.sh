#!/bin/sh

redoc-cli bundle ./isv_gateway.yml

scp /Users/lingen/Developer/Myddd/myddd-vertx/isv-gateway/isv-gateway-bootstrap/src/main/resources/redoc-static.html root@pcx.workplus.io:/usr/share/nginx/pcx/isv_gateway.html
