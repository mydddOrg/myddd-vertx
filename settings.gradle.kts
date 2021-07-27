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

include("myddd-vertx-media:myddd-vertx-media-domain")
include("myddd-vertx-media:myddd-vertx-media-infra")
include("myddd-vertx-media:myddd-vertx-media-storage:myddd-vertx-media-storage-local")
include("myddd-vertx-media:myddd-vertx-media-storage:myddd-vertx-media-storage-qcloud")


include("myddd-vertx-cache:myddd-vertx-cache-api")
include("myddd-vertx-cache:myddd-vertx-cache-sharedata")

include("myddd-vertx-web")

//oauth2.0的实现
include("myddd-vertx-oauth2:myddd-vertx-oauth2-domain")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-infra")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-provider")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-api")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-application")
include("myddd-vertx-oauth2:myddd-vertx-oauth2-bootstrap")

//grpc
include("myddd-vertx-grpc:myddd-vertx-grpc-api")
include("myddd-vertx-grpc:myddd-vertx-grpc-local")
include("myddd-vertx-grpc:myddd-vertx-grpc-cluster")
include("myddd-vertx-grpc:myddd-vertx-grpc-servicetype")


//example
include("example:document-domain")
include("example:document-api")
include("example:document-application")
include("example:document-infra")
include("example:document-bootstrap")