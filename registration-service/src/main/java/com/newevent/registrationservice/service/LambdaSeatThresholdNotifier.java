package com.newevent.registrationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

/**
 * The real implementation, replacing LoggingSeatThresholdNotifier (Day 4's
 * stub). This is the payoff of building that seam early - RegistrationService
 * itself needed zero changes; only this class is new, plus @Primary so
 * Spring wires this one in instead of the logging stub.
 *
 * Credentials: deliberately NOT using static access keys (no AWS_ACCESS_KEY_ID
 * baked into the app or a K8s secret). LambdaClient.create() uses the AWS SDK's
 * default credential chain, which picks up the EC2 instance's attached IAM
 * role automatically - no long-lived credentials to leak, ever. This is the
 * same least-privilege pattern as the Lambda's own execution role (Day 5) and
 * the ClickHouse insert-only user (Day 8) - worth citing together in the
 * security section as a consistent design philosophy, not a one-off.
 */
@Service
@Primary
public class LambdaSeatThresholdNotifier implements SeatThresholdNotifier {

    private static final Logger log = LoggerFactory.getLogger(LambdaSeatThresholdNotifier.class);

    private final LambdaClient lambdaClient;
    private final String functionName;

    public LambdaSeatThresholdNotifier(
            @Value("${aws.region}") String region,
            @Value("${aws.lambda.function-name}") String functionName) {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.of(region))
                .build();
        this.functionName = functionName;
    }

    @Override
    public void notifyLowSeats(Long eventId, int seatsRemaining) {
        String payload = String.format(
                "{\"eventId\": %d, \"seatsRemaining\": %d}", eventId, seatsRemaining);

        try {
            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);

            if (response.functionError() != null) {
                // Lambda ran but returned an error - log and move on, don't
                // let an analytics/notification failure block registration
                // itself (the registration was already saved by this point).
                log.warn("Lambda function error: {}", response.payload().asUtf8String());
            } else {
                log.info("Lambda invoked successfully for event {}: {}",
                        eventId, response.payload().asUtf8String());
            }
        } catch (Exception e) {
            // Same principle as above - a Lambda invocation failure is a
            // secondary-path problem, not a reason to fail the registration
            // that already succeeded.
            log.error("Failed to invoke seat-threshold Lambda for event {}", eventId, e);
        }
    }
}
