rootProject.name = "myddd-vertx"

include("myddd-vertx-base:myddd-vertx-base-api")
include("myddd-vertx-base:myddd-vertx-base-provider")

include("myddd-vertx-ioc:myddd-vertx-ioc-api")
include("myddd-vertx-ioc:myddd-vertx-ioc-guice")

include("myddd-vertx-domain")

include("myddd-vertx-repository:myddd-vertx-repository-api")
include("myddd-vertx-repository:myddd-vertx-repository-hibernate")


include("myddd-vertx-querychannel:myddd-vertx-querychannel-api")
include("myddd-vertx-querychannel:myddd-vertx-querychannel-hibernate")

include("myddd-vertx-i18n:myddd-vertx-i18n-api")
include("myddd-vertx-i18n:myddd-vertx-i18n-provider")

include("myddd-vertx-web")


//oauth2.0的实现
include("myddd-vertx-oauth2:myddd-vertx-oauth2-domain")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-infra")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-provider")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-api")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-application")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-bootstrap")

//isv-gateway项目
include("isv-gateway:isv-gateway-domain")
include("isv-gateway:isv-gateway-api")
include("isv-gateway:isv-gateway-application")
include("isv-gateway:isv-gateway-infra")
include("isv-gateway:isv-gateway-bootstrap")


