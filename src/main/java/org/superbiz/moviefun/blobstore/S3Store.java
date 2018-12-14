package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;



public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String bucket;
    private final Logger logger = LoggerFactory.getLogger(S3Store.class);

    public S3Store (AmazonS3Client s3Client, String photoStorageBucket){
        this.s3Client=s3Client;
        this.bucket=photoStorageBucket;
    }
    @Override
    public void put(Blob blob) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.getContentType());
        s3Client.putObject(bucket,blob.getName(), blob.getInputStream(),metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        if (s3Client.doesObjectExist(bucket,name)){
            S3Object s3Object = s3Client.getObject(bucket,name);
            logger.info("Image is Present in S3Amazon Client");
            Blob blob = new Blob(s3Object.getKey(),s3Object.getObjectContent(),s3Object.getObjectMetadata().getContentType());
            return Optional.of(blob);
        }
        return Optional.empty();
    }

    public void delete(String name){

        s3Client.deleteObject(bucket,name);
    }
    @Override
    public void deleteAll() {
        ObjectListing objList = s3Client.listObjects(bucket);
        Iterator<S3ObjectSummary> s3ObjItr = objList.getObjectSummaries().iterator();
        while(s3ObjItr.hasNext()){
            s3Client.deleteObject(bucket,s3ObjItr.next().getKey());
        }

    }

}
