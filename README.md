## Overview

This package is an example Java Lambda package for using with CDK Pipeline. It doesn't have an API Gateway definition
associated with it. It's most useful when you just want to deploy a lambda function, perhaps for use as a stream consumer,
or invoked by SQS, SNS, or CloudWatch.

In a nutshell, it's just a normal Java package with additional configurations for
BATS transformation. It makes use of HappyTrails's `ht.include.cfg` property to copy the file
`aws_lambda/lambda-transform.yml` to the build output. BATS requires this file to know how to transform this
package into a deployable Lambda package. If you customize your build system, don't forget this requirement. Read more
about [BATS Lambda Transformer here](https://builderhub.corp.amazon.com/docs/bats/user-guide/transformers-lambda.html).

This package does not contain any deployment logic, they are defined in CDK Package.

## Integrating with existing CDK package

If your CDK package does not have any stages or stacks yet, follow [our guides](https://builderhub.corp.amazon.com/docs/native-aws/developer-guide/cdk-pipeline.html#application-stacks)
to add them to your setup.

Once you have your stack ready, add the sample Lambda function using this snippet:

```
  new lambda.Function(this, 'Calculator', {
    code: new BrazilPackageLambdaCode({
      brazilPackage: BrazilPackage.fromString('-1.0'),
      componentName: 'Calculator',
    }),
    handler: 'com.amazon.grayjayreportvalidationautomation.lambda.calculator.Calculator::add',
    memorySize: 512,
    timeout: cdk.Duration.seconds(30),
    runtime: lambda.Runtime.JAVA_11
  });
```

## General Workflow

For testing with this Lambda package, here's our current recommendation:

1. Unit tests. Run good old fashioned unit tests against your code.
1. Deploy to your personal stack and validate the functionalities there. This needs to be done in three steps:
   1. Run `brazil-build` in this package.
   1. Run `brazil-build deploy:assets $StackName` in your CDK package.
   1. Run `brazil-build cdk deploy $StackName` in your CDK package.
1. CR and Push. Run integration tests in your pipeline for your function.
