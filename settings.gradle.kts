rootProject.name = "myddd-vertx"

include("myddd-vertx-ioc:myddd-vertx-ioc-api")
include("myddd-vertx-ioc:myddd-vertx-ioc-guice")

include("myddd-vertx-domain")

include("myddd-vertx-repository:myddd-vertx-repository-api")
include("myddd-vertx-repository:myddd-vertx-repository-hibernate")


include("example:comment-domain")
include("example:comment-infra")