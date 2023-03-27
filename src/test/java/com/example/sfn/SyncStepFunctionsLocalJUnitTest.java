package com.example.sfn;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.example.sfn.StepFunctionsConstants.LOCAL_STACK_LOCAL_IMAGE;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class SyncStepFunctionsLocalJUnitTest {
    private static final Logger log = LoggerFactory.getLogger(SyncStepFunctionsLocalJUnitTest.class);
    private static AmazonS3 s3;
    private static String stateMachineArn;

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse(String.valueOf(LOCAL_STACK_LOCAL_IMAGE))).withServices(LocalStackContainer.Service.S3);

    @BeforeAll
    static void setup() {

        s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString(),
                                localStack.getRegion()
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(localStack.getAccessKey(), localStack.getSecretKey())
                        )
                )
                .build();


    }

    @Test
    @DisplayName("Check Container is running")
    void testContainerRunning() {
        assertTrue(localStack.isRunning());
    }

    @Test
    @DisplayName("Test Happy Path Scenario")
    void testHappyPath() throws InterruptedException {
        String executionName = "happyPathExecution";
        Bucket bucket = s3.createBucket("hello-test-for-docker");


        // IMP: Wait until above execution completes in docker
        Thread.sleep(2000);

        assertNotNull(bucket);

        System.out.println(bucket.getName());

        assertEquals(bucket.getName(), "hello-test-for-docker");

    }

}
