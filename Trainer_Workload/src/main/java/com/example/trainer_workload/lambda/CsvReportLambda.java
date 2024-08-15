package com.example.trainer_workload.lambda;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Iterator;

public class CsvReportLambda implements RequestHandler<Object, String> {

    private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final String tableName = "TrainerInfo";
    private final String bucketName = "ketevankhacha-bucket";

    @Override
    public String handleRequest(Object input, Context context) {
        Table table = dynamoDB.getTable(tableName);
        ScanSpec scanSpec = new ScanSpec();
        Iterator<Item> iterator = table.scan(scanSpec).iterator();

        StringWriter out = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("Trainer First Name", "Trainer Last Name", "Trainings Duration Summary"))) {
            while (iterator.hasNext()) {
                Item item = iterator.next();
                String status = item.getString("TraineeStatus");
                int trainingDuration = item.getInt("TrainingsDurationSummary");

                if (!status.equals("Inactive") || trainingDuration > 0) {
                    printer.printRecord(item.getString("TrainerFirstName"), item.getString("TrainerLastName"), trainingDuration);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error generating CSV";
        }

        LocalDate now = LocalDate.now();
        String fileName = String.format("Trainers_Trainings_summary_%d_%02d.csv", now.getYear(), now.getMonthValue());
        s3Client.putObject(bucketName, fileName, out.toString());

        return "CSV report generated and uploaded to S3";
    }
}
