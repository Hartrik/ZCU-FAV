# DefaultApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**sequencerPost**](DefaultApi.md#sequencerPost) | **POST** /sequencer | Accepts financial operation


<a name="sequencerPost"></a>
# **sequencerPost**
> sequencerPost(operation)

Accepts financial operation

### Example
```java
// Import classes:
//import ApiException;
//import DefaultApi;


DefaultApi apiInstance = new DefaultApi();
Operation operation = new Operation(); // Operation | Financial operation to perform
try {
    apiInstance.sequencerPost(operation);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#sequencerPost");
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

