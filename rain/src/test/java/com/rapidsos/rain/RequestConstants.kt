package com.rapidsos.rain

/**
 * ###################### Test Responses ######################
 *
 * Used for testing purposes only.
 */
internal const val TEST_BAD_REQUEST_RESPONSE = "{\"detail\": \"Invalid user credentials.\"}"
internal const val TEST_UNAUTHORIZED_RESPONSE = "{\"detail\": \"Invalid user credentials.\"}"
internal const val TEST_TOO_MANY_REQUESTS_RESPONSE = "{\"detail\": \"Too many requests. Please try again later\"}"
internal const val TEST_SUCCESS_AUTH_TOKEN_RESPONSE = "{\n" +
        "    \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYmYiOjE1MTIxNDk" +
        "3ODAsImlkZW50aXR5IjoxMzMsImV4cCI6MTUxMjE1MzM4MCwiaWF0IjoxNTEyMTQ5NzgwfQ.6ysLVirM" +
        "kWGeB0VKCDC9I9BYbKFfKlTLNNr03cb-3MI\",\n" +
        "    \"token_type\": \"BearerToken\",\n" +
        "    \"issued_at\": \"1512149780354\",\n" +
        "    \"expires_in\": \"3599\",\n" +
        "    \"refresh_token\": \"Nqnt4vs0dKblsGzp08FJskm2IyRUfTW9\",\n" +
        "    \"refresh_token_issued_at\": \"1512149780354\",\n" +
        "    \"refresh_token_expires_in\": \"0\"\n" +
        "}"

internal const val TEST_SUCCESS_REGISTERING_RESPONSE = "{\n" +
        "  \"id\": 0,\n" +
        "  \"username\": \"string\",\n" +
        "  \"email\": \"string\",\n" +
        "  \"password\": \"string\",\n" +
        "  \"created\": \"string\",\n" +
        "  \"modified\": \"string\"\n" +
        "}"

internal const val TEST_REGISTRATION_ERROR_USER_EXISTS_RESPONSE = "{\n" +
        "\"detail\": \"User with same username or email already exists.\"\n" +
        "}"

internal const val TOKEN_SUCCESS_RESPONSE = "{\n" +
        "\"access_token\":\"YaF9QgyG5KAKiYNhJXNclhaHwlcU\",\n" +
        "\"api_product_list\":\"\",\n" +
        "\"application_name\":\"\",\n" +
        "\"client_id\":\"\",\n" +
        "\"developer_email\":\"\",\n" +
        "\"expires_in\":\"3599\",\n" +
        "\"issued_at\":\"1512173235997\",\n" +
        "\"organization_name\":\"\",\n" +
        "\"refresh_count\":\"\",\n" +
        "\"refresh_token_expires_in\":\"\",\n" +
        "\"scope\":\"\",\n" +
        "\"status\":\"\",\n" +
        "\"token_type\":\"BearerToken\"\n" +
        "}"

internal const val INVALID_TOKEN_SUCCESS_RESPONSE = "{\n" +
        "\"access_token\":\"\",\n" + // empty access token makes this fail
        "\"api_product_list\":\"\",\n" +
        "\"application_name\":\"\",\n" +
        "\"client_id\":\"\",\n" +
        "\"developer_email\":\"\",\n" +
        "\"expires_in\":\"3599\",\n" +
        "\"issued_at\":\"1512173235997\",\n" +
        "\"organization_name\":\"\",\n" +
        "\"refresh_count\":\"\",\n" +
        "\"refresh_token_expires_in\":\"\",\n" +
        "\"scope\":\"\",\n" +
        "\"status\":\"\",\n" +
        "\"token_type\":\"BearerToken\"\n" +
        "}"

internal const val RESET_PWD_SUCCESS_RESPONSE = "{}"
internal const val RESET_PWD_UNAUTHORIZED_RESPONSE = "{\n" +
        "\"fault\":{\n" +
        "\"faultstring\":\"Invalid access token\",\n" +
        "\"detail\":{\n" +
        "\"errorcode\":\"oauth.v2.InvalidAccessToken\"\n" +
        "}\n" +
        "}\n" +
        "}"