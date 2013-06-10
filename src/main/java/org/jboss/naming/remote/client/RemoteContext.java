/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.naming.remote.client;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.jboss.logging.Logger;
import static org.jboss.naming.remote.client.ClientUtil.isEmpty;
import static org.jboss.naming.remote.client.ClientUtil.namingEnumeration;

/**
 *
 * @author John Bailey
 */
public class RemoteContext implements Context, NameParser {
    private static final Logger log = Logger.getLogger(RemoteContext.class);

    private static final boolean IBM;

    static {
        IBM = SecurityActions.getProperty("java.vm.vendor","").matches(".*[Ii][Bb][Mm].*");
    }

    private final Name prefix;
    private final Hashtable<String, Object> environment;
    private final RemoteNamingStore namingStore;

    private final List<CloseTask> closeTasks;

    private final AtomicBoolean closed = new AtomicBoolean();
    // Trick to keep IBM from finalizing in the middle of a method call
    private byte preventFinalizer;

    public RemoteContext(final RemoteNamingStore namingStore, final Hashtable<String, Object> environment) {
        this(namingStore, environment, Collections.<CloseTask>emptyList());
    }

    public RemoteContext(final RemoteNamingStore namingStore, final Hashtable<String, Object> environment, final List<CloseTask> closeTasks) {
        this(new CompositeName(), namingStore, environment, closeTasks);
    }

    public RemoteContext(final Name prefix, final RemoteNamingStore namingStore, final Hashtable<String, Object> environment) {
        this(prefix, namingStore, environment, Collections.<CloseTask>emptyList());
    }

    public RemoteContext(final Name prefix, final RemoteNamingStore namingStore, final Hashtable<String, Object> environment, final List<CloseTask> closeTasks) {
        this.prefix = prefix;
        this.namingStore = namingStore;
        this.environment = environment;
        this.closeTasks = closeTasks;
    }

    public Object lookup(final Name name) throws NamingException {
        if (isEmpty(name)) {
            return new RemoteContext(prefix, namingStore, environment);
        }
        try {
            return namingStore.lookup(getAbsoluteName(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public Object lookup(final String name) throws NamingException {
        try {
            return lookup(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void bind(final Name name, final Object object) throws NamingException {
        try {
            namingStore.bind(getAbsoluteName(name), object);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void bind(final String name, final Object object) throws NamingException {
        try {
            bind(parse(name), object);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void rebind(final Name name, final Object object) throws NamingException {
        try {
            namingStore.rebind(name, object);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void rebind(final String name, final Object object) throws NamingException {
        try {
            rebind(parse(name), object);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void unbind(final Name name) throws NamingException {
        try {
            namingStore.unbind(name);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void unbind(final String name) throws NamingException {
        try {
            unbind(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void rename(final Name name, final Name newName) throws NamingException {
        try {
            namingStore.rename(name, newName);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void rename(final String name, final String newName) throws NamingException {
        try {
            rename(parse(name), parse(newName));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        try {
            return namingEnumeration(namingStore.list(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public NamingEnumeration<NameClassPair> list(final String name) throws NamingException {
        try {
            return list(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        try {
            return namingEnumeration(namingStore.listBindings(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public NamingEnumeration<Binding> listBindings(final String name) throws NamingException {
        try {
            return listBindings(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void destroySubcontext(final Name name) throws NamingException {
        try {
            namingStore.destroySubcontext(name);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public void destroySubcontext(final String name) throws NamingException {
        try {
            destroySubcontext(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public Context createSubcontext(final Name name) throws NamingException {
        try {
            return namingStore.createSubcontext(name);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public Context createSubcontext(final String name) throws NamingException {
        try {
            return createSubcontext(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public Object lookupLink(final Name name) throws NamingException {
        try {
            return namingStore.lookupLink(name);
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public Object lookupLink(final String name) throws NamingException {
        try {
            return lookupLink(parse(name));
        } finally {
            if (IBM) preventFinalizer++;
        }
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return this;
    }

    public NameParser getNameParser(String s) throws NamingException {
        return this;
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        final Name result = (Name) prefix.clone();
        result.addAll(name);
        return result;
    }

    public String composeName(String name, String prefix) throws NamingException {
        return composeName(parse(name), parse(prefix)).toString();
    }

    public Object addToEnvironment(String s, Object o) throws NamingException {
        return environment.put(s, o);
    }

    public Object removeFromEnvironment(String s) throws NamingException {
        return environment.remove(s);
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return environment;
    }

    public void close() throws NamingException {
        if(closed.compareAndSet(false, true)) {
            for (CloseTask closeTask : closeTasks) {
                closeTask.close(false);
            }
        }
    }

    public void finalize() {
        if (IBM) preventFinalizer++;

        if(closed.compareAndSet(false, true)) {
            for (CloseTask closeTask : closeTasks) {
                closeTask.close(true);
            }
        }
    }

    public String getNameInNamespace() throws NamingException {
        return prefix.toString();
    }

    public Name parse(final String name) throws NamingException {
        return new CompositeName(name);
    }

    private Name getAbsoluteName(final Name name) throws NamingException {
        if (name.isEmpty()) {
            return composeName(name, prefix);
        }
        return composeName(name, prefix);
    }

    public static interface CloseTask {
        void close(boolean isFinalize);
    }
}
