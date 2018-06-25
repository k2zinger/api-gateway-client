# api-gateway-client
[![CircleCI](https://circleci.com/gh/k2zinger/api-gateway-client.svg?style=svg)](https://circleci.com/gh/k2zinger/api-gateway-client)

An HTTP request library with [AWS Signature v4](http://docs.aws.amazon.com/general/latest/gr/signing_aws_api_requests.html) signed requests.
 generic client library that connects to [AWS Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html), without [generating an SDK for the API](https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-generate-sdk.html).

This API was inspired by [awscurl](https://github.com/okigan/awscurl)

## Installation
### Maven
```
<dependency>
  <groupId>tk.k2zinger</groupId>
  <artifactId>api-gateway-client</artifactId>
  <version>0.1</version>
</dependency>
```

## Examples

HTTP GET
```
        ApiGatewayResponse response = ApiGatewayClientBuilder.standard()
            .withUri("https://xxxxxx.execute-api.us-east-1.amazonaws.com/yyyyyy")
            .build()
            .sendRequest();

        System.out.println(response.getResponseCode());
        System.out.println(response.getContent());
        
        JsonNode responseJson = response.getContentAsJson();
```

HTTP POST
```
        ApiGatewayResponse response = ApiGatewayClientBuilder.standard()
            .withMethod("POST")
            .withUri("https://xxxxxx.execute-api.us-east-1.amazonaws.com/yyyyyy")
            .withBody("{\"key1\": \"value1\",\"key2\": \"value2\"}")
            .build()
            .sendRequest();

        System.out.println(response.getResponseCode());
        System.out.println(response.getContent());
        
        JsonNode responseJson = response.getContentAsJson();
```


## Usage

`withUri`: the url to connect to

`withMethod`: currently GET and POST are the only supported operations, defaults to GET

`withHeaders`: additional headers to add to the request (note: these headers are overwritten if they have the same name as the headers used to generate the AWS v4 Signature!

`withBody`: the string data to send in POST requests

`withAccessKey`: AWS access key

`withSecretKey`: AWS secret key

`withSecurityToken`: interchangable with withSessionToken, populates the x-amz-security-token header and adds it to the AWS v4 Signature

`withSessionToken`: see withSecurityToken

`withApiKey`: adds the header x-api-key

`withRegion`: see [AWS Regions and Endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html), defaults to us-east-1

`withProfile`: which profile to use within the credentials profiles file, see [Named Profiles](https://docs.aws.amazon.com/cli/latest/userguide/cli-multiple-profiles.html)

`withService`: AWS service, defaults to execute-api