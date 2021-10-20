package org.myddd.vertx.grpc

import io.grpc.Attributes
import io.grpc.EquivalentAddressGroup
import io.grpc.NameResolver
import io.grpc.NameResolverProvider
import java.net.SocketAddress
import java.net.URI
import java.util.stream.Collectors

class MultiAddressNameResolverFactory internal constructor(addresses: List<SocketAddress>) :
    NameResolverProvider() {

    val addresses: List<EquivalentAddressGroup>

    init {
        this.addresses = addresses.stream()
            .map { EquivalentAddressGroup(it) }
            .collect(Collectors.toList())
    }

    override fun newNameResolver(notUsedUri: URI, args: NameResolver.Args): NameResolver {
        return object : NameResolver() {
            override fun getServiceAuthority(): String {
                return "fakeAuthority"
            }

            override fun start(listener: Listener2) {
                listener.onResult(
                    ResolutionResult.newBuilder().setAddresses(addresses).setAttributes(Attributes.EMPTY).build()
                )
            }

            override fun shutdown() {
                println("shutdown")
            }
        }
    }

    override fun getDefaultScheme(): String {
        return "multiaddress"
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun priority(): Int {
        return 0
    }


}