package com.traklibrary.authentication.service.dto;

import com.google.common.base.Strings;
import com.traklibrary.authentication.domain.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * The {@link CheckedResponse} is a simple wrapper object that is used by some of the /users end-points. Its
 * purpose is to return the data associated with the given request and any error messages that may have occurred
 * during the request. Its' primary purpose is during {@link User} creation, as the callee will have no
 * knowledge if any accounts have already been registered with a supplied username or email address.
 *
 * @param <T> The response type of the checked response.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@Data
@Setter(AccessLevel.NONE)
public class CheckedResponse<T> {

    private T data;

    private boolean error;

    private String errorMessage;

    /**
     * Constructor that is used to specify the data within the {@link CheckedResponse}. If this constructor
     * is invoked, it is assumed that no errors have been created during the processing of the data supplied
     * to this object.
     *
     * @param data The data within the {@link CheckedResponse}.
     */
    public CheckedResponse(T data) {
        this.data = data;
        this.errorMessage = "";
    }

    /**
     * Constructor that is used to specify the data within the {@link CheckedResponse} and any errors that
     * have been created during the processing. If the error message supplied to the {@link CheckedResponse}
     * is a valid and non-empty string, the {@link CheckedResponse#isError()} will be set to <code>true</code>.
     *
     * @param data The data within the {@link CheckedResponse}.
     * @param errorMessage The error message associated with the data.
     */
    public CheckedResponse(T data, String errorMessage) {
        this(data);
        this.errorMessage = errorMessage;
        this.error = !Strings.isNullOrEmpty(this.errorMessage);
    }
}
