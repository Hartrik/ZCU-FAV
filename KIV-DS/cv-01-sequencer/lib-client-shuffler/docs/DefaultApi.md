# DefaultApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**shufflerPost**](DefaultApi.md#shufflerPost) | **POST** /shuffler | Accepts financial operation


<a name="shufflerPost"></a>
# **shufflerPost**
> shufflerPost(operation)

Accepts financial operation

### Example
```java
// Import classes:
//import ApiException;
//import DefaultApi;


DefaultApi apiInstance = new DefaultApi();
Operation operation = new Operation(); // Operation | Financial operation to perform
try {
    apiInstance.shufflerPost(operation);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#shufflerPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **operation** | [**Operation**](Operation.md)| Financial operation to perform | [optional]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

