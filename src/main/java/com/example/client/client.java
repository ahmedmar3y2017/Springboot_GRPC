package com.example.client;

import com.example.StudentRequest;
import com.example.StudentResponse;
import com.example.StudentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class client {
    static Logger logger = LoggerFactory.getLogger(client.class);

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

        StudentServiceGrpc.StudentServiceStub studentServiceStub = StudentServiceGrpc.newStub(managedChannel);

        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        do {
            System.out.println(
                    "Press : 1 - > noStreaming , 2 - > serverSideStreaming , 3 - > clientSideStreaming ,4 - > BiDirectionalStreaming "
            );
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    noStreaming(managedChannel);

                    break;
                case 2:
                    // Server Side streaming
                    serverSideStreaming(studentServiceStub);

                    break;
                case 3:
                    // client Side Streaming
                    clientSideStreaming(studentServiceStub);

                    break;
                case 4:
                    // BiDirectional Streaming
                    BiDirectionalStreaming(studentServiceStub);

                    break;
            }

        } while (choice != -1);


    }

    private static void noStreaming(ManagedChannel managedChannel) {
        StudentServiceGrpc.StudentServiceBlockingStub studentServiceBlockingStub = StudentServiceGrpc.newBlockingStub(managedChannel);

        StudentResponse student = studentServiceBlockingStub.getStudent(StudentRequest.newBuilder().setId(1).build());

        logger.info("Student Name : " + student.getName() + "   Age : " + student.getAge());
    }

    private static void BiDirectionalStreaming(StudentServiceGrpc.StudentServiceStub studentServiceStub) {
        StreamObserver<StudentRequest> studentRequestStreamObserver1 = studentServiceStub.clientSideStreaming(new StreamObserver<StudentResponse>() {
            @Override
            public void onNext(StudentResponse studentResponse) {
                logger.info("new Student Response : " + studentResponse.getName() + "  " + studentResponse.getAge());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        for (int i = 0; i < 50; i++) {
            studentRequestStreamObserver1.onNext(StudentRequest.newBuilder().setId(50).build());
        }
        studentRequestStreamObserver1.onCompleted();
    }

    private static void clientSideStreaming(StudentServiceGrpc.StudentServiceStub studentServiceStub) {
        StreamObserver<StudentRequest> studentRequestStreamObserver = studentServiceStub.clientSideStreaming(new StreamObserver<StudentResponse>() {
            @Override
            public void onNext(StudentResponse studentResponse) {
                logger.info("new Student Response : " + studentResponse.getName() + "  " + studentResponse.getAge());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().build());

        for (int i = 0; i < 10; i++) {
            studentRequestStreamObserver.onNext(StudentRequest.newBuilder().build());
        }
        studentRequestStreamObserver.onCompleted();
    }

    private static StudentServiceGrpc.StudentServiceStub serverSideStreaming(StudentServiceGrpc.StudentServiceStub studentServiceStub) {


        studentServiceStub.serverSideStreamingStudent(StudentRequest.newBuilder().setId(50).build(), new StreamObserver<StudentResponse>() {
            @Override
            public void onNext(StudentResponse studentResponse) {
                logger.info("new Student Response : " + studentResponse.getName() + "  " + studentResponse.getAge());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });
        return studentServiceStub;
    }
}
