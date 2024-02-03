package com.example.server;

import com.example.StudentRequest;
import com.example.StudentResponse;
import com.example.StudentServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GRpcService
public class server extends StudentServiceGrpc.StudentServiceImplBase {


    Logger logger = LoggerFactory.getLogger(server.class);

    public void getStudent(com.example.StudentRequest request,
                           io.grpc.stub.StreamObserver<com.example.StudentResponse> responseObserver) {

        logger.info("Request : " + request.getId());
        // bunsiessLogic
        StudentResponse studentResponse = StudentResponse.newBuilder().setAge(1).setName("ahmed").build();

        responseObserver.onNext(studentResponse);
        responseObserver.onCompleted();


    }


    /**
     *
     */
    public void serverSideStreamingStudent(com.example.StudentRequest request,
                                           io.grpc.stub.StreamObserver<com.example.StudentResponse> responseObserver) {

        logger.info("Request : serverSideStreamingStudent " + request.getId());
        // bunsiessLogic
        for (int i = 1; i < 100; i++) {
            StudentResponse studentResponse = StudentResponse.newBuilder().setAge(i).setName("ahmedStreaming " + i).build();
            // stream every response to client
            responseObserver.onNext(studentResponse);

        }
        responseObserver.onCompleted();

    }

    /**
     *
     */
    public io.grpc.stub.StreamObserver<com.example.StudentRequest> clientSideStreaming(
            io.grpc.stub.StreamObserver<com.example.StudentResponse> responseObserver) {

        StreamObserver<StudentRequest> studentResponseStreamObserver = new StreamObserver<StudentRequest>() {
            @Override
            public void onNext(StudentRequest StudentRequest) {
                logger.info("request recieved : " + StudentRequest.getId());

            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("Error occured");

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StudentResponse.getDefaultInstance());
                responseObserver.onCompleted();
                logger.info("Complete all requests ");

            }
        };

        return studentResponseStreamObserver;
    }

    /**
     *
     */
    public io.grpc.stub.StreamObserver<com.example.StudentRequest> biDirectionalSideStreaming(
            io.grpc.stub.StreamObserver<com.example.StudentResponse> responseObserver) {

        StreamObserver<StudentRequest> studentResponseStreamObserver = new StreamObserver<StudentRequest>() {
            @Override
            public void onNext(StudentRequest StudentRequest) {
                logger.info("request recieved : " + StudentRequest.toString());
                StudentResponse studentResponse = StudentResponse.newBuilder().setAge(100).setName("ahmed BiDirectional Streaming " ).build();
                responseObserver.onNext(studentResponse);

            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("Error occured");

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StudentResponse.getDefaultInstance());
                responseObserver.onCompleted();
                logger.info("Complete all requests ");

            }
        };

        return studentResponseStreamObserver;
    }

}
