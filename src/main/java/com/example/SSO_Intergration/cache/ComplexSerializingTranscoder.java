package com.example.SSO_Intergration.cache;

import net.spy.memcached.transcoders.SerializingTranscoder;

import java.io.*;

public class ComplexSerializingTranscoder extends SerializingTranscoder {

    @Override
    protected Object deserialize(byte[] bytes) {
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        ObjectInputStream in = null;
        try {
            ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bs) {
                @Override
                protected Class<ObjectStreamClass> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                    try {
                        return (Class<ObjectStreamClass>) currentClassLoader.loadClass(objectStreamClass.getName());
                    } catch (Exception e) {
                        return (Class<ObjectStreamClass>) super.resolveClass(objectStreamClass);
                    }
                }
            };
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeStream(in);
        }
    }

    private static void closeStream(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}