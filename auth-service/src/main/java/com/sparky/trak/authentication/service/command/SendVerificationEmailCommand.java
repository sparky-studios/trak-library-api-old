package com.sparky.trak.authentication.service.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * The {@link SendVerificationEmailCommand} is a simple {@link HystrixCommand} that is used to asynchronously
 * dispatch a request to the email service to send a verification email with the given verification code to the
 * specified email address. When the command is dispatched, the command doesn't care about the current state of
 * the service, if the email is not sent, normal verification and user process will continue. It will just require
 * the user to request an additional email to be sent with verification details.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@Slf4j
public class SendVerificationEmailCommand extends HystrixCommand<Object> {

    private final RestTemplate restTemplate;
    private final String emailAddress;
    private final short verificationCode;

    /**
     * Constructor for the {@link SendVerificationEmailCommand}. When invoked, the command is assigned to the "EmailService"
     * hystrix command group.
     *
     * @param restTemplate The {@link RestTemplate} used to dispatch the verification request to the email service.
     * @param emailAddress The email address to dispatch the verification email to.
     * @param verificationCode The verification code currently associated with the the requested {@link com.sparky.trak.authentication.domain.User}.
     */
    public SendVerificationEmailCommand(RestTemplate restTemplate, String emailAddress, short verificationCode) {
        super(HystrixCommandGroupKey.Factory.asKey("EmailService"));
        this.restTemplate = restTemplate;
        this.emailAddress = emailAddress;
        this.verificationCode = verificationCode;
    }

    /**
     * Runs the behaviour associated with this command. The behaviour for the {@link SendVerificationEmailCommand} is to
     * invoke the verification email end-point on the email service through the user of the supplied {@link RestTemplate}.
     * Although the method specifies a return type of type {@link Object}, it will always return <code>null</code> as the
     * email service end-point is a void PUT request.
     *
     * If an exception is thrown when invoking the end-point, the {@link SendVerificationEmailCommand#getFallback()} method
     * is invoked.
     *
     * @return An {@link Object} that is always set to <code>null</code>.
     */
    @Override
    protected Object run() {
        restTemplate.put("http://trak-email-server/v1/emails/verification?email-address={emailAddress}&verification-code={verificationCode}",
                null, emailAddress, verificationCode);

        // We want to return null here, the return doesn't need to be used and the email verification end-point returns a 204.
        return null;
    }

    /**
     * The fallback method that is caused when the {@link SendVerificationEmailCommand#run()} throws an exception. Similar to
     * {@link SendVerificationEmailCommand#run()}, although it specifies a return type of {@link Object}, all the method will do
     * is log the exception, and return <code>null</code>, as there is no return type from the email service end-point.
     *
     * @return An {@link Object} that is always set to <code>null</code>.
     */
    @Override
    protected Object getFallback() {
        log.error("Failed to send verification email.");
        // We don't want the fallback bringing the service down, just log it as an error.
        return null;
    }
}
