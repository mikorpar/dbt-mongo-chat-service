package com.mikorpar.brbljavac_api.config.mongodb;

import com.mikorpar.brbljavac_api.data.models.Group;
import com.mikorpar.brbljavac_api.data.models.Message;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.stream.Collectors;

@WritingConverter
public class GroupWriteConverter implements Converter<Group, Document> {
    @Override
    public Document convert(Group group) {
        Document doc = new Document();
        if (group.getId() != null) doc.put("_id", new ObjectId(group.getId()));

        doc.put("admin", new ObjectId(group.getAdmin()));
        doc.put("users", group.getUsers().stream().map(ObjectId::new).collect(Collectors.toList()));
        doc.put("last_message_seeners", group.getLastMessageSeeners().stream().map(ObjectId::new).collect(Collectors.toList()));
        doc.put("name", group.getName());
        doc.put("created_at", group.getCreatedAt());
        doc.put("updated_at", group.getUpdatedAt());
        doc.put("messages", group.getMessages().stream().map(this::convertMsg).collect(Collectors.toList()));

        return doc;
    }

    private Document convertMsg(Message msg) {
        Document doc = new Document();
        if (msg.getId() == null) msg.setId(new ObjectId().toHexString());

        doc.put("_id", new ObjectId(msg.getId()));
        doc.put("user_id", new ObjectId(msg.getUserId()));
        doc.put("text", msg.getText());
        doc.put("created_at", msg.getCreatedAt());

        if (msg.getRepliedOn() != null) doc.put("replied_on", new ObjectId(msg.getRepliedOn()));
        if (msg.getFileId() != null) doc.put("file_id", msg.getFileId());

        return doc;
    }
}
