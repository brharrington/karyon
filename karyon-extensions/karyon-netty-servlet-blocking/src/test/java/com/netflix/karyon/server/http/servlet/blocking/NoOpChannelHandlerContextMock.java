package com.netflix.karyon.server.http.servlet.blocking;

import io.netty.channel.Channel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Nitesh Kant
 */
public class NoOpChannelHandlerContextMock extends NoOpChannelHandlerContext {

    private final Channel channel;
    private final InetSocketAddress serverSockAddr;
    private final InetSocketAddress remoteAddr;
    private final InetSocketAddress localAddr;
    private final DefaultAttributeMap attributeMap;
    private final EventExecutor executor;

    public NoOpChannelHandlerContextMock(final String serverAddr, final int serverPort,
                                         final String localAddr, final int localPort,
                                         final String remoteAddr, final int remotePort) {
        serverSockAddr = InetSocketAddress.createUnresolved(serverAddr, serverPort);
        this.localAddr = new InetSocketAddress(localAddr, localPort);
        this.remoteAddr = new InetSocketAddress(remoteAddr, remotePort);
        final ServerSocketChannel parent = new OioServerSocketChannel() {
            @Override
            protected SocketAddress localAddress0() {
                return serverSockAddr;
            }
        };
        channel = new OioSocketChannel() {
            @Override
            public ServerSocketChannel parent() {
                return parent;
            }

            @Override
            public InetSocketAddress localAddress() {
                return NoOpChannelHandlerContextMock.this.localAddr;
            }

            @Override
            public InetSocketAddress remoteAddress() {
                return NoOpChannelHandlerContextMock.this.remoteAddr;
            }
        };
        attributeMap = new DefaultAttributeMap();
        executor = new DefaultEventExecutorGroup(1).next();
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return attributeMap.attr(key);
    }

    @Override
    public EventExecutor executor() {
        return executor;
    }
}
