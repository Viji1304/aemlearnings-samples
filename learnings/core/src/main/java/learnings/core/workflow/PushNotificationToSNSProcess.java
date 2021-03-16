package learnings.core.workflow;

import java.util.Optional;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.AuthorizationErrorException;
import software.amazon.awssdk.services.sns.model.EndpointDisabledException;
import software.amazon.awssdk.services.sns.model.InternalErrorException;
import software.amazon.awssdk.services.sns.model.InvalidParameterException;
import software.amazon.awssdk.services.sns.model.InvalidParameterValueException;
import software.amazon.awssdk.services.sns.model.InvalidSecurityException;
import software.amazon.awssdk.services.sns.model.KmsAccessDeniedException;
import software.amazon.awssdk.services.sns.model.KmsDisabledException;
import software.amazon.awssdk.services.sns.model.KmsInvalidStateException;
import software.amazon.awssdk.services.sns.model.KmsNotFoundException;
import software.amazon.awssdk.services.sns.model.KmsOptInRequiredException;
import software.amazon.awssdk.services.sns.model.KmsThrottlingException;
import software.amazon.awssdk.services.sns.model.NotFoundException;
import software.amazon.awssdk.services.sns.model.PlatformApplicationDisabledException;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Component(service = WorkflowProcess.class, immediate = true, property = { "process.label=Push Notification to SNS" })
public class PushNotificationToSNSProcess implements WorkflowProcess {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private String awsAccessKey = "";
	private String awsSecretKey = "";
	private final String AWS_SNS_TOPIC_ARN = "arn:aws:sns:us-east-1:963253518673:Learnings-PayloadInformation";

	@Activate
	protected void activate(ComponentContext context) {
		/* Retrieve Credentials from System properties - Starts */
		awsAccessKey = context.getBundleContext().getProperty("aws.accessKeyId");
		awsSecretKey = context.getBundleContext().getProperty("aws.secretKey");
		/* Retrieve Credentials from System properties - Ends */
	}

	@Override
	public void execute(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
			throws WorkflowException {
		/* Retrieve workflow payload */
		String workflowPayload = Optional.of(workItem.getWorkflowData().getPayload().toString()).get();
		/* Frame message to send to SNS topic */
		String message = "Workflow Payload " + workflowPayload;

		try {
			/* Instantiate SNS client */
			AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
			SnsClient snsClient = SnsClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds))
					.build();
			/* Publish message Request */
			PublishRequest request = PublishRequest.builder().message(message).topicArn(AWS_SNS_TOPIC_ARN).build();
			/* Push message */
			PublishResponse response = snsClient.publish(request);
			LOG.debug("SNS Notification message Id={}", response.messageId());
		} catch (InvalidParameterException e) {
			LOG.error("InvalidParameterException={}", e.getMessage());
		} catch (InvalidParameterValueException e) {
			LOG.error("InvalidParameterValueException={}", e.getMessage());
		} catch (InternalErrorException e) {
			LOG.error("InternalErrorException={}", e.getMessage());
		} catch (NotFoundException e) {
			LOG.error("NotFoundException={}", e.getMessage());
		} catch (EndpointDisabledException e) {
			LOG.error("EndpointDisabledException={}", e.getMessage());
		} catch (PlatformApplicationDisabledException e) {
			LOG.error("PlatformApplicationDisabledException={}", e.getMessage());
		} catch (AuthorizationErrorException e) {
			LOG.error("AuthorizationErrorException={}", e.getMessage());
		} catch (KmsDisabledException e) {
			LOG.error("KmsDisabledException={}", e.getMessage());
		} catch (KmsInvalidStateException e) {
			LOG.error("KmsInvalidStateException={}", e.getMessage());
		} catch (KmsNotFoundException e) {
			LOG.error("KmsNotFoundException={}", e.getMessage());
		} catch (KmsOptInRequiredException e) {
			LOG.error("KmsOptInRequiredException={}", e.getMessage());
		} catch (KmsThrottlingException e) {
			LOG.error("KmsThrottlingException={}", e.getMessage());
		} catch (KmsAccessDeniedException e) {
			LOG.error("KmsAccessDeniedException={}", e.getMessage());
		} catch (InvalidSecurityException e) {
			LOG.error("InvalidSecurityException={}", e.getMessage());
		} catch (SnsException e) {
			LOG.error("SnsException={}", e.getMessage());
		} catch (AwsServiceException e) {
			LOG.error("AwsServiceException={}", e.getMessage());
		} catch (SdkClientException e) {
			LOG.error("SdkClientException={}", e.getMessage());
		} catch (Exception e) {
			LOG.error("Generic Exception={}", e.getMessage());
		}

	}

}