# myddd-vertx

myddd-vertx是基于Kotlin与Vertx的响应式领域驱动基础框架。它是myddd在后端的实现。

## 快速开始

myddd starter已经开放使用，现在开始，你可以访问[myddd starter](https://starter.myddd.org)以快速开始生成一个myddd项目。

## 核心宗旨

myddd-vertx是myddd在后端的大胆尝试 ，myddd-vertx遵循以下宗旨：

* 以响应式编程为核心。在响应式框架中，选择Vert.x做为核心依赖框架。
* 以Kotlin取代Java，Kotlin比Java更简洁与优雅。

基于myddd-vertx的代码，无论在代码的简洁与优雅，还是在其性能上，都比甚至Java + Spring Boot的组合更甚一筹。

如果你愿意尝试新的模式，你可以尝试`myddd-vertx`

当然，myddd仍然提供了基于Java及Spring Boot的领域驱动实现**[myddd-spring-boot](https://github.com/mydddOrg/myddd-spring-boot)**，你仍然可以使用你最熟悉的Java语言及Spring来编码代码。

## SonarQube质量管控

myddd-vertx遵照TDD测试驱动开发的理念进行开发，使用SonarQube进行质量管控。

当前主干的SonarQube数据为:

![SonarQube质量管控](https://images.taoofcoding.tech/2021/11/sonar-data-of-myddd-vertx-1.3.0-snapshot.png)

## 为什么叫myddd

ddd领域驱动的理念较为复杂，概念较多。包含实体，值对象，仓储，领域服务，领域事件，聚合根，应用服务，查询通道，DTO数据对象等众多要素。

因此大家对于它的理解与争议较多，我个人不是非常喜欢与人争论，将自己对DDD的理解，结合自己十多年在后台，移动端（iOS,Android)，基于Electron的桌面开发以及前端（TypeScript + React）的技术经验的基础上，取之名为myddd，表意为：ddd，我理解，我实现。不与人陷入争议之中。:

## 官网

【myddd官网】: https://myddd.org

【微言码道】官网：https://taoofcoding.tech