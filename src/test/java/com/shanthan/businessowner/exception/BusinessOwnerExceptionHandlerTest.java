package com.shanthan.businessowner.exception;

import com.shanthan.businessowner.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Stream;

import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.METHOD_ARG_TYPE_MISMATCH;
import static com.shanthan.businessowner.testutil.BusinessOwnerTestConstants.MISSING_PATH_VARIABLE;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class BusinessOwnerExceptionHandlerTest {

    @InjectMocks
    private BusinessOwnerExceptionHandler subject;

    @Mock
    private MissingPathVariableException missingPathVariableException;

    @Mock
    private MethodArgumentTypeMismatchException methodArgumentTypeMismatchException;

    private String exceptionMessage;


    @BeforeEach
    void setUp() {
        openMocks(this);
        exceptionMessage = "someRuntimeException";
    }

    private static Stream<HttpStatus> badStatuses() {
        return of(BAD_REQUEST, NOT_FOUND, INTERNAL_SERVER_ERROR);
    }

    @ParameterizedTest
    @MethodSource(value = "badStatuses")
    void givenBusinessOwnerException_whenHandled_returnsErrorResponseWithGivenHttpStatus(HttpStatus httpStatus) {
        BusinessOwnerException businessOwnerException = new BusinessOwnerException(exceptionMessage, httpStatus);
        ResponseEntity<ErrorResponse> actualResponse = subject.handleBusinessOwnerException(businessOwnerException);

        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getBody());

        assertEquals(httpStatus, actualResponse.getStatusCode());
        assertEquals(exceptionMessage, actualResponse.getBody().getExceptionMessage());
    }

    @Test
    void givenInvalidBoNumber_whenHandledMissingPathVariableException_returnsErrorResponseWithBadRequestHttpStatus()  {
        when(missingPathVariableException.getMessage()).thenReturn(MISSING_PATH_VARIABLE);
        ResponseEntity<ErrorResponse> actualResponse =
                subject.handleMissingPathVariableException(missingPathVariableException);

        assertNotNull(actualResponse);
        assertEquals(BAD_REQUEST, actualResponse.getStatusCode());

        assertNotNull(actualResponse.getBody());
        assertEquals(BAD_REQUEST, actualResponse.getBody().getHttpStatus());
        assertNotNull(actualResponse.getBody().getExceptionMessage());
    }

    @Test
    void givenInvalidBoNumber_whenHandledMethodArgTypeMismatch_returnsErrorResponseWithBadRequestHttpStatus()  {
        when(methodArgumentTypeMismatchException.getMessage()).thenReturn(METHOD_ARG_TYPE_MISMATCH);
        ResponseEntity<ErrorResponse> actualResponse =
                subject.handleMethodArgTypeMismatch(methodArgumentTypeMismatchException);

        assertNotNull(actualResponse);
        assertEquals(BAD_REQUEST, actualResponse.getStatusCode());

        assertNotNull(actualResponse.getBody());
        assertEquals(BAD_REQUEST, actualResponse.getBody().getHttpStatus());
        assertNotNull(actualResponse.getBody().getExceptionMessage());
    }
}