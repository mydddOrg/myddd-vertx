rootProject.name = "myddd-vertx"

include("myddd-vertx-ioc:myddd-vertx-ioc-api")
include("myddd-vertx-ioc:myddd-vertx-ioc-guice")

include("myddd-vertx-domain")

include("myddd-vertx-repository:myddd-vertx-repository-api")
include("myddd-vertx-repository:myddd-vertx-repository-hibernate")


include("myddd-vertx-querychannel:myddd-vertx-querychannel-api")
include("myddd-vertx-querychannel:myddd-vertx-querychannel-hibernate")

//oauth2.0的实现
include("myddd-vertx-oauth2:myddd-vertx-oauth2-domain")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-infra")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-provider")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-api")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-application")