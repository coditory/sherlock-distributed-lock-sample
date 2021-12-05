package com.coditory.sandbox.sherlock;

import com.coditory.sherlock.ReactorDistributedLock;
import com.coditory.sherlock.ReactorSherlock;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;

import static com.coditory.sherlock.ReactiveMongoSherlockBuilder.reactiveMongoSherlock;

public class SherlockMongoReactive {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private MongoCollection<Document> locksCollection() {
        String database = "sherlock";
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/" + database);
        return mongoClient
            .getDatabase("sherlock")
            .getCollection("locks");
    }

    void sampleMongoLockUsage() {
        ReactorSherlock sherlock = reactiveMongoSherlock()
            .withClock(Clock.systemDefaultZone())
            .withLockDuration(Duration.ofMinutes(5))
            .withUniqueOwnerId()
            .withLocksCollection(locksCollection())
            .buildWithApi(ReactorSherlock::reactorSherlock);
        // ...or simply
        // ReactorSherlock sherlockWithDefaults = reactiveMongoSherlock(reactiveMongoSherlock(locksCollection()));
        ReactorDistributedLock lock = sherlock.createLock("sample-lock");
        lock.acquireAndExecute(Mono.fromCallable(() -> {
            logger.info("Lock acquired!");
            return true;
        })).block();
    }

    public static void main(String[] args) {
        new SherlockMongoReactive().sampleMongoLockUsage();
    }
}
