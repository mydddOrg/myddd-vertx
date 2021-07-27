package org.myddd.vertx.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: HealthCheck.proto")
public final class HealthCheckGrpc {

  private HealthCheckGrpc() {}

  public static final String SERVICE_NAME = "org.myddd.vertx.grpc.HealthCheck";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.BoolValue> getHelloMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "hello",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.google.protobuf.BoolValue.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.BoolValue> getHelloMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.BoolValue> getHelloMethod;
    if ((getHelloMethod = HealthCheckGrpc.getHelloMethod) == null) {
      synchronized (HealthCheckGrpc.class) {
        if ((getHelloMethod = HealthCheckGrpc.getHelloMethod) == null) {
          HealthCheckGrpc.getHelloMethod = getHelloMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.BoolValue>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "hello"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.BoolValue.getDefaultInstance()))
              .setSchemaDescriptor(new HealthCheckMethodDescriptorSupplier("hello"))
              .build();
        }
      }
    }
    return getHelloMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static HealthCheckStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HealthCheckStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HealthCheckStub>() {
        @java.lang.Override
        public HealthCheckStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HealthCheckStub(channel, callOptions);
        }
      };
    return HealthCheckStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static HealthCheckBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HealthCheckBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HealthCheckBlockingStub>() {
        @java.lang.Override
        public HealthCheckBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HealthCheckBlockingStub(channel, callOptions);
        }
      };
    return HealthCheckBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static HealthCheckFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<HealthCheckFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<HealthCheckFutureStub>() {
        @java.lang.Override
        public HealthCheckFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new HealthCheckFutureStub(channel, callOptions);
        }
      };
    return HealthCheckFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class HealthCheckImplBase implements io.grpc.BindableService {

    /**
     */
    public void hello(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.BoolValue> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHelloMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getHelloMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.BoolValue>(
                  this, METHODID_HELLO)))
          .build();
    }
  }

  /**
   */
  public static final class HealthCheckStub extends io.grpc.stub.AbstractAsyncStub<HealthCheckStub> {
    private HealthCheckStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HealthCheckStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HealthCheckStub(channel, callOptions);
    }

    /**
     */
    public void hello(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.BoolValue> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHelloMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class HealthCheckBlockingStub extends io.grpc.stub.AbstractBlockingStub<HealthCheckBlockingStub> {
    private HealthCheckBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HealthCheckBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HealthCheckBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.BoolValue hello(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHelloMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class HealthCheckFutureStub extends io.grpc.stub.AbstractFutureStub<HealthCheckFutureStub> {
    private HealthCheckFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected HealthCheckFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new HealthCheckFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.BoolValue> hello(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHelloMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_HELLO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final HealthCheckImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(HealthCheckImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_HELLO:
          serviceImpl.hello((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.BoolValue>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class HealthCheckBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    HealthCheckBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.myddd.vertx.grpc.HealthCheckProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("HealthCheck");
    }
  }

  private static final class HealthCheckFileDescriptorSupplier
      extends HealthCheckBaseDescriptorSupplier {
    HealthCheckFileDescriptorSupplier() {}
  }

  private static final class HealthCheckMethodDescriptorSupplier
      extends HealthCheckBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    HealthCheckMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (HealthCheckGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new HealthCheckFileDescriptorSupplier())
              .addMethod(getHelloMethod())
              .build();
        }
      }
    }
    return result;
  }
}
