package org.myddd.vertx.grpc;

import static org.myddd.vertx.grpc.HealthCheckGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;


@javax.annotation.Generated(
value = "by VertxGrpc generator",
comments = "Source: HealthCheck.proto")
public final class VertxHealthCheckGrpc {
    private VertxHealthCheckGrpc() {}

    public static HealthCheckVertxStub newVertxStub(io.grpc.Channel channel) {
        return new HealthCheckVertxStub(channel);
    }

    
    public static final class HealthCheckVertxStub extends io.grpc.stub.AbstractStub<HealthCheckVertxStub> {
        private final io.vertx.core.impl.ContextInternal ctx;
        private HealthCheckGrpc.HealthCheckStub delegateStub;

        private HealthCheckVertxStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = HealthCheckGrpc.newStub(channel);
            this.ctx = (io.vertx.core.impl.ContextInternal) io.vertx.core.Vertx.currentContext();
        }

        private HealthCheckVertxStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = HealthCheckGrpc.newStub(channel).build(channel, callOptions);
            this.ctx = (io.vertx.core.impl.ContextInternal) io.vertx.core.Vertx.currentContext();
        }

        @Override
        protected HealthCheckVertxStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new HealthCheckVertxStub(channel, callOptions);
        }

        
        public io.vertx.core.Future<com.google.protobuf.BoolValue> hello(com.google.protobuf.Empty request) {
            return io.vertx.grpc.stub.ClientCalls.oneToOne(ctx, request, delegateStub::hello);
        }

    }

    
    public static abstract class HealthCheckVertxImplBase implements io.grpc.BindableService {
        private String compression;

        /**
         * Set whether the server will try to use a compressed response.
         *
         * @param compression the compression, e.g {@code gzip}
         */
        public HealthCheckVertxImplBase withCompression(String compression) {
            this.compression = compression;
            return this;
        }

        
        public io.vertx.core.Future<com.google.protobuf.BoolValue> hello(com.google.protobuf.Empty request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
                    .addMethod(
                            org.myddd.vertx.grpc.HealthCheckGrpc.getHelloMethod(),
                            asyncUnaryCall(
                                    new MethodHandlers<
                                            com.google.protobuf.Empty,
                                            com.google.protobuf.BoolValue>(
                                            this, METHODID_HELLO, compression)))
                    .build();
        }
    }

    private static final int METHODID_HELLO = 0;

    private static final class MethodHandlers<Req, Resp> implements
            io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
            io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final HealthCheckVertxImplBase serviceImpl;
        private final int methodId;
        private final String compression;

        MethodHandlers(HealthCheckVertxImplBase serviceImpl, int methodId, String compression) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
            this.compression = compression;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                case METHODID_HELLO:
                    io.vertx.grpc.stub.ServerCalls.oneToOne(
                            (com.google.protobuf.Empty) request,
                            (io.grpc.stub.StreamObserver<com.google.protobuf.BoolValue>) responseObserver,
                            compression,
                            serviceImpl::hello);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch (methodId) {
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }

}
