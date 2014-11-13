/*
 * Copyright (c) 2014. Vlad Ilyushchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nfsdb.journal.net.mcast;

import com.nfsdb.journal.exceptions.JournalNetworkException;
import com.nfsdb.journal.net.config.NetworkConfig;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class OnDemandAddressPoller extends AbstractOnDemandPoller<InetSocketAddress> {
    public OnDemandAddressPoller(NetworkConfig networkConfig, int inMessageCode, int outMessageCode) throws JournalNetworkException {
        super(networkConfig, inMessageCode, outMessageCode);
    }

    @Override
    protected InetSocketAddress transform(ByteBuffer buf) throws JournalNetworkException {
        if (buf == null) {
            throw new JournalNetworkException("Cannot find NFSdb servers on network");
        }
        try {
            byte[] b = new byte[buf.getInt()];
            buf.get(b);
            InetAddress address = InetAddress.getByAddress(b);
            // skip SSL byte
            buf.get();
            int port = buf.getInt();
            return new InetSocketAddress(address, port);
        } catch (UnknownHostException e) {
            throw new JournalNetworkException(e);
        }
    }
}
