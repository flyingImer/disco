/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package software.amazon.disco.agent.reflect.concurrent;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import software.amazon.disco.agent.reflect.UncaughtExceptionHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that the Agent configuration can be overridden when the Agent is present
 */
public class TransactionContextTests {
    private static final String DISCO_NULL_ID = "disco_null_id";

    @Before
    public void before() {
        software.amazon.disco.agent.concurrent.TransactionContext.clear();
    }

    @After
    public void after() {
        software.amazon.disco.agent.concurrent.TransactionContext.clear();
    }

    @Test
    public void testTransactionContextCreate() {
        TransactionContext.create();
        Assert.assertNotEquals(DISCO_NULL_ID, software.amazon.disco.agent.concurrent.TransactionContext.get());
    }

    @Test
    public void testTransactionContextSet() {
        TransactionContext.set("foo");
        TransactionContext.putMetadata("metadata", "value");
        Assert.assertEquals("foo", software.amazon.disco.agent.concurrent.TransactionContext.get());
        Assert.assertEquals("value", software.amazon.disco.agent.concurrent.TransactionContext.getMetadata("metadata"));
    }

    @Test
    public void testTransactionContextGet() {
        TransactionContext.set("foo");
        TransactionContext.putMetadata("metadata", "value");
        Assert.assertEquals(software.amazon.disco.agent.concurrent.TransactionContext.get(), TransactionContext.get());
        Assert.assertEquals(software.amazon.disco.agent.concurrent.TransactionContext.getMetadata("metadata"), TransactionContext.getMetadata("metadata"));
    }

    @Test
    public void testTransactionContextClear() {
        TransactionContext.set("foo");
        TransactionContext.putMetadata("metadata", "value");
        TransactionContext.clear();
        Assert.assertEquals(DISCO_NULL_ID, software.amazon.disco.agent.concurrent.TransactionContext.get());
        Assert.assertNull(software.amazon.disco.agent.concurrent.TransactionContext.getMetadata("metadata"));
    }

    @Test
    public void testCatchIllegalArgumentExceptionWhenPutMetadataCalledWithReservedIdentifierWhenAgentLoaded() {
        AtomicReference<Throwable> caught = new AtomicReference<>(null);
        UncaughtExceptionHandler.install((call, args, thrown)-> {
            caught.set(thrown);
        });

        TransactionContext.putMetadata("$amazon.discoIdentifier", new Object());

        UncaughtExceptionHandler.install(null);
        Assert.assertTrue(caught.get() instanceof IllegalArgumentException);
    }

    @Test
    public void testCatchIllegalArgumentExceptionWhenGetMetadataCalledWithReservedIdentifierWhenAgentLoaded() {
        AtomicReference<Throwable> caught = new AtomicReference<>(null);
        UncaughtExceptionHandler.install((call, args, thrown)-> {
            caught.set(thrown);
        });

        TransactionContext.getMetadata("$amazon.discoIdentifier");

        UncaughtExceptionHandler.install(null);
        Assert.assertTrue(caught.get() instanceof IllegalArgumentException);
    }

    @Test
    public void testNoIllegalArgumentExceptionWhenPutMetadataCalledWithLegalIdentifierWhenAgentLoaded() {
        AtomicReference<Throwable> caught = new AtomicReference<>(null);
        UncaughtExceptionHandler.install((call, args, thrown)-> {
            caught.set(thrown);
        });

        TransactionContext.putMetadata("legalIdentifier", new Object());

        UncaughtExceptionHandler.install(null);
        Assert.assertNull(caught.get());
    }

    @Test
    public void testNoIllegalArgumentExceptionWhenGetMetadataCalledWithLegalIdentifierWhenAgentLoaded() {
        AtomicReference<Throwable> caught = new AtomicReference<>(null);
        UncaughtExceptionHandler.install((call, args, thrown)-> {
            caught.set(thrown);
        });

        TransactionContext.getMetadata("legalIdentifier");

        UncaughtExceptionHandler.install(null);
        Assert.assertNull(caught.get());
    }

    @Test
    public void testTransactionContextSetMetadataTagWhenAgentLoaded() {
        TransactionContext.putMetadata("identifier", "test_value");
        TransactionContext.setMetadataTag("identifier", "custom_tag");

        final Map<String, Object> metadata = TransactionContext.getMetadataWithTag("custom_tag");

        Assert.assertNotNull(metadata);
        Assert.assertEquals("test_value", metadata.get("identifier"));
    }

    @Test
    public void testGetUninitializedTransactionContextValueWhenAgentLoaded() {
        Assert.assertEquals("disco_null_id", TransactionContext.getUninitializedTransactionContextValue());
    }

    @Test
    public void testIsWithinCreatedContextWhenAgentLoadedWhenOutsideServiceActivity() {
        Assert.assertFalse(TransactionContext.isWithinCreatedContext());
    }

    @Test
    public void testIsWithinCreatedContextWhenAgentLoadedWhenInsideServiceActivity() {
        TransactionContext.create();
        Assert.assertTrue(TransactionContext.isWithinCreatedContext());
    }
}
