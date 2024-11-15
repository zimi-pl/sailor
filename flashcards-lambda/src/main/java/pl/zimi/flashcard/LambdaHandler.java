package pl.zimi.flashcard;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import pl.sailor.aws.ServerlessServer;
import pl.zimi.client.Request;
import pl.zimi.client.Response;
import pl.zimi.flashcards.flashcard.FlashcardRepository;
import pl.zimi.flashcards.flashcard.FlashcardService;
import pl.zimi.http.HttpMethod;
import pl.zimi.http.Server;
import pl.zimi.repository.DynamoDbPort;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;
import java.util.HashMap;

public class LambdaHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Processing question from " + request, LogLevel.INFO);
        try {
            return handle(request);
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            logger.log(errors.toString(), LogLevel.ERROR);
            throw new RuntimeException(ex);
        }
    }

    private ApiGatewayProxyResponse handle(ApiGatewayRequest apiGatewayRequest) {
        pl.zimi.context.Context context = pl.zimi.context.Context.create();
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        context.register(FlashcardRepository.class, DynamoDbPort.port(FlashcardRepository.class, client, "flashcards-dev-"));
        context.register(Clock.class, Clock.systemUTC());
        FlashcardService flashcardService = context.getBean(FlashcardService.class);

        Server server = new ServerlessServer()
                .setupService(flashcardService);

        Request serverRequest = new Request(HttpMethod.valueOf(apiGatewayRequest.getHttpMethod()), apiGatewayRequest.getPath(), apiGatewayRequest.getBody());
        Response response = server.handleRequest(serverRequest);
        ApiGatewayProxyResponse apiGatewayProxyResponse = new ApiGatewayProxyResponse();
        apiGatewayProxyResponse.setBody(response.body());
        apiGatewayProxyResponse.setStatusCode(200);
        apiGatewayProxyResponse.setHeaders(new HashMap<>());
        return apiGatewayProxyResponse;
    }

}
