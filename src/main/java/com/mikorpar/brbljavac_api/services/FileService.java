package com.mikorpar.brbljavac_api.services;

import com.mikorpar.brbljavac_api.data.models.File;
import com.mikorpar.brbljavac_api.exceptions.files.FileNotFoundException;
import com.mikorpar.brbljavac_api.exceptions.files.UserNotFileOwnerException;
import com.mikorpar.brbljavac_api.utils.LoggedUserFetcher;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final GridFsOperations operations;
    private final GridFsTemplate gridFsTemplate;
    private final LoggedUserFetcher userFetcher;

    public String storeFile(MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("uploader", new ObjectId(getCurrUsrId()));

        return gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                metadata
        ).toString();
    }

    public File getFile(String id) throws FileNotFoundException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            throw new FileNotFoundException(String.format("File with id '%s' does not exist", id));
        }

        File image = new File();
        image.setFilename(file.getFilename());
        image.setData(IOUtils.toByteArray(operations.getResource(file).getContent()));
        if (file.getMetadata() != null) image.setFiletype(file.getMetadata().get("_contentType").toString());

        return image;
    }

    public void deleteFile(String id) throws FileNotFoundException, UserNotFileOwnerException {
        Query query = new Query(Criteria.where("_id").is(id));
        GridFSFile file = gridFsTemplate.findOne(query);

        if (file == null) {
            throw new FileNotFoundException(String.format("File with id '%s' does not exist", id));
        }
        if (file.getMetadata() == null || !file.getMetadata().get("uploader").equals(new ObjectId(getCurrUsrId()))) {
            throw new UserNotFileOwnerException("Current user is not file owner");
        }

        gridFsTemplate.delete(query);
    }

    private String getCurrUsrId() {
        return userFetcher.getPrincipal().getId();
    }
}
